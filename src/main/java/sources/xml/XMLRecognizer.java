package sources.xml;

import entities.Column;
import enums.ColumnDataType;
import exceptions.InvalidXMLException;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

/**
 * XMLRecognizer is responsible for parsing XML files and converting hierarchical
 * data into a tabular format suitable for DBest.
 */
public class XMLRecognizer {

    private final File xmlFile;
    private final String rootElement;
    private final String recordElement;
    private final FlatteningStrategy strategy;

    public enum FlatteningStrategy {
        NESTED_COLUMNS,    // Convert nested elements to dot-notation columns (parent.child)
        SEPARATE_TABLES,   // Create separate tables for nested structures
        JSON_COLUMNS       // Store complex nested data as JSON strings
    }

    public XMLRecognizer(String filePath, String rootElement, String recordElement, FlatteningStrategy strategy) throws InvalidXMLException {
        this.xmlFile = new File(filePath);
        this.rootElement = rootElement;
        this.recordElement = recordElement;
        this.strategy = strategy != null ? strategy : FlatteningStrategy.NESTED_COLUMNS;

        if (!xmlFile.exists()) {
            throw new InvalidXMLException("XML file not found: " + filePath);
        }
    }

    public XMLRecognizer(String filePath) throws InvalidXMLException {
        this(filePath, null, null, FlatteningStrategy.NESTED_COLUMNS);
    }

    /**
     * Analyzes the XML structure and returns metadata about the file
     */
    public XMLAnalysisResult analyzeStructure() throws InvalidXMLException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            Element root = document.getDocumentElement();

            // Auto-detect structure if not specified
            String detectedRoot = rootElement != null ? rootElement : root.getNodeName();
            String detectedRecord = recordElement;

            if (detectedRecord == null) {
                detectedRecord = detectRecordElement(root);
            }

            // Analyze schema
            List<Column> columns = analyzeSchema(document, detectedRecord);
            List<Map<String, String>> sampleData = extractSampleData(document, detectedRecord, 10);

            return new XMLAnalysisResult(
                detectedRoot,
                detectedRecord,
                columns,
                sampleData,
                strategy,
                countRecords(document, detectedRecord)
            );

        } catch (Exception e) {
            throw new InvalidXMLException("Error analyzing XML structure: " + e.getMessage());
        }
    }

    /**
     * Extracts all data from the XML file in tabular format
     */
    public XMLData extractData() throws InvalidXMLException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            XMLAnalysisResult analysis = analyzeStructure();

            List<Map<String, String>> allData = extractAllData(document, analysis.getRecordElement());

            return new XMLData(
                analysis.getColumns(),
                allData,
                analysis.getRootElement(),
                analysis.getRecordElement()
            );

        } catch (Exception e) {
            throw new InvalidXMLException("Error extracting XML data: " + e.getMessage());
        }
    }

    private String detectRecordElement(Element root) {
        // Find the most common child element that likely represents records
        Map<String, Integer> childCounts = new HashMap<>();
        NodeList children = root.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = child.getNodeName();
                childCounts.put(nodeName, childCounts.getOrDefault(nodeName, 0) + 1);
            }
        }

        // Return the element with highest count (most likely to be records)
        return childCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(root.getFirstChild() != null ? root.getFirstChild().getNodeName() : "record");
    }

    private List<Column> analyzeSchema(Document document, String recordElement) {
        Set<String> allColumns = new LinkedHashSet<>();
        Map<String, ColumnDataType> columnTypes = new HashMap<>();

        NodeList records = document.getElementsByTagName(recordElement);

        // Analyze first few records to determine schema
        int maxRecordsToAnalyze = Math.min(100, records.getLength());

        for (int i = 0; i < maxRecordsToAnalyze; i++) {
            Element record = (Element) records.item(i);
            Map<String, String> flatRecord = flattenElement(record, "");

            for (Map.Entry<String, String> entry : flatRecord.entrySet()) {
                String columnName = entry.getKey();
                String value = entry.getValue();

                allColumns.add(columnName);

                // Infer data type
                ColumnDataType inferredType = inferDataType(value);
                ColumnDataType existingType = columnTypes.get(columnName);

                if (existingType == null) {
                    columnTypes.put(columnName, inferredType);
                } else if (existingType != inferredType && existingType != ColumnDataType.STRING) {
                    // If types conflict, default to STRING
                    columnTypes.put(columnName, ColumnDataType.STRING);
                }
            }
        }

        // Convert to Column objects
        List<Column> columns = new ArrayList<>();
        for (String columnName : allColumns) {
            ColumnDataType type = columnTypes.getOrDefault(columnName, ColumnDataType.STRING);
            Column column = new Column(columnName, "xml", type, false, false);
            columns.add(column);
        }

        return columns;
    }

    private ColumnDataType inferDataType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return ColumnDataType.STRING;
        }

        // Try integer
        try {
            Integer.parseInt(value);
            return ColumnDataType.INTEGER;
        } catch (NumberFormatException ignored) {}

        // Try double
        try {
            Double.parseDouble(value);
            return ColumnDataType.DOUBLE;
        } catch (NumberFormatException ignored) {}

        // Default to string
        return ColumnDataType.STRING;
    }

    private Map<String, String> flattenElement(Element element, String prefix) {
        Map<String, String> result = new HashMap<>();

        // Handle attributes
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attr = attributes.item(i);
            String attrName = prefix.isEmpty() ? "@" + attr.getNodeName() : prefix + ".@" + attr.getNodeName();
            result.put(attrName, attr.getNodeValue());
        }

        // Handle child elements and text content
        NodeList children = element.getChildNodes();
        boolean hasElementChildren = false;
        StringBuilder textContent = new StringBuilder();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                hasElementChildren = true;
                Element childElement = (Element) child;
                String childPrefix = prefix.isEmpty() ? child.getNodeName() : prefix + "." + child.getNodeName();

                if (strategy == FlatteningStrategy.NESTED_COLUMNS) {
                    result.putAll(flattenElement(childElement, childPrefix));
                } else {
                    // For other strategies, we might handle differently
                    result.put(childPrefix, childElement.getTextContent());
                }
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getNodeValue().trim();
                if (!text.isEmpty()) {
                    textContent.append(text);
                }
            }
        }

        // If element has no child elements, use its text content
        if (!hasElementChildren && textContent.length() > 0) {
            String columnName = prefix.isEmpty() ? element.getNodeName() : prefix;
            result.put(columnName, textContent.toString());
        }

        return result;
    }

    private List<Map<String, String>> extractSampleData(Document document, String recordElement, int maxSamples) {
        List<Map<String, String>> sampleData = new ArrayList<>();
        NodeList records = document.getElementsByTagName(recordElement);

        int samplesToExtract = Math.min(maxSamples, records.getLength());

        for (int i = 0; i < samplesToExtract; i++) {
            Element record = (Element) records.item(i);
            Map<String, String> flatRecord = flattenElement(record, "");
            sampleData.add(flatRecord);
        }

        return sampleData;
    }

    private List<Map<String, String>> extractAllData(Document document, String recordElement) {
        List<Map<String, String>> allData = new ArrayList<>();
        NodeList records = document.getElementsByTagName(recordElement);

        for (int i = 0; i < records.getLength(); i++) {
            Element record = (Element) records.item(i);
            Map<String, String> flatRecord = flattenElement(record, "");
            allData.add(flatRecord);
        }

        return allData;
    }

    private int countRecords(Document document, String recordElement) {
        NodeList records = document.getElementsByTagName(recordElement);
        return records.getLength();
    }
}
