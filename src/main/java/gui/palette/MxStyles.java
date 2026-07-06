package gui.palette;

import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

import enums.OperationType;
import gui.theme.Theme;

import java.awt.Color;
import java.util.Hashtable;

public final class MxStyles {

    private MxStyles() {
    }

    public static void registerOperatorCellStyles(mxGraph graph) {
        Hashtable<String, Object> operatorCellStyle = buildOperatorCellStyle();
        for (OperationType operationType : OperationType.values()) {
            graph.getStylesheet().putCellStyle(operationType.displayName, operatorCellStyle);
        }
        registerDefaultEdgeStyle(graph);
    }

    public static void registerDefaultEdgeStyle(mxGraph graph) {
        Hashtable<String, Object> edgeStyle = new Hashtable<>(graph.getStylesheet().getDefaultEdgeStyle());
        edgeStyle.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(Theme.TEXT_MUTED));
        edgeStyle.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
        edgeStyle.put(mxConstants.STYLE_FONTCOLOR, mxUtils.getHexColorString(Theme.TEXT_MUTED));
        graph.getStylesheet().setDefaultEdgeStyle(edgeStyle);
    }

    private static Hashtable<String, Object> buildOperatorCellStyle() {
        Hashtable<String, Object> style = new Hashtable<>();
        style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.getHexColorString(Color.WHITE));
        style.put(mxConstants.STYLE_STROKEWIDTH, 1.5);
        style.put(mxConstants.STYLE_STROKECOLOR, mxUtils.getHexColorString(new Color(0, 0, 170)));
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
        style.put(mxConstants.STYLE_PERIMETER, mxConstants.PERIMETER_ELLIPSE);
        return style;
    }
}
