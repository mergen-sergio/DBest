package gui.palette;

import controllers.ConstantController;
import enums.OperationType;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;


public final class OperatorCatalog {

    private static final String DESCRIPTION_KEY_PREFIX = "palette.operator.";
    private static final String DESCRIPTION_KEY_SUFFIX = ".description";
    private static final String TAGS_KEY_SUFFIX = ".tags";
    private static final String TAGS_SEPARATOR = ",";

    public static final List<OperatorMetadata> ALL = buildCatalog();

    private OperatorCatalog() {
    }

    public static List<OperatorMetadata> byCategory(OperatorCategory category) {
        List<OperatorMetadata> filtered = new ArrayList<>();
        for (OperatorMetadata operator : ALL) {
            if (operator.category() == category) {
                filtered.add(operator);
            }
        }
        return filtered;
    }

    public static List<OperatorMetadata> matching(String query) {
        if (query == null || query.isBlank()) {
            return ALL;
        }
        String normalizedQuery = normalize(query);
        List<OperatorMetadata> matches = new ArrayList<>();
        for (OperatorMetadata operator : ALL) {
            if (matchesAnyField(operator, normalizedQuery)) {
                matches.add(operator);
            }
        }
        return matches;
    }

    private static boolean matchesAnyField(OperatorMetadata operator, String normalizedQuery) {
        if (normalize(operator.displayName()).contains(normalizedQuery)) return true;
        if (normalize(operator.description()).contains(normalizedQuery)) return true;
        if (normalize(operator.category().englishFallback).contains(normalizedQuery)) return true;
        if (operator.symbol().equals(normalizedQuery)) return true;
        for (String tag : operator.searchTags()) {
            if (normalize(tag).contains(normalizedQuery)) return true;
        }
        return false;
    }

    private static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String stripped = Normalizer.normalize(text, Normalizer.Form.NFD)
                                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return stripped.toLowerCase();
    }

    private static List<OperatorMetadata> buildCatalog() {
        List<OperatorMetadata> entries = new ArrayList<>();
        for (OperationType type : OperationType.values()) {
            entries.add(new OperatorMetadata(
                type,
                categoryFor(type),
                type.symbol,
                type.displayName,
                descriptionFor(type),
                tagsFor(type)
            ));
        }
        return List.copyOf(entries);
    }

    private static String descriptionFor(OperationType type) {
        String key = DESCRIPTION_KEY_PREFIX + type.name + DESCRIPTION_KEY_SUFFIX;
        try {
            return ConstantController.getString(key);
        } catch (MissingResourceException missing) {
            return type.displayName;
        }
    }

    private static List<String> tagsFor(OperationType type) {
        String key = DESCRIPTION_KEY_PREFIX + type.name + TAGS_KEY_SUFFIX;
        try {
            String raw = ConstantController.getString(key);
            if (raw == null || raw.isBlank()) {
                return List.of();
            }
            return Arrays.stream(raw.split(TAGS_SEPARATOR))
                         .map(String::trim)
                         .filter(s -> !s.isEmpty())
                         .toList();
        } catch (MissingResourceException missing) {
            return List.of();
        }
    }

    private static OperatorCategory categoryFor(OperationType type) {
        return switch (type) {

            case FILTER, PROJECTION, SELECT_COLUMNS, LIMIT, RENAME, SORT,
                 DUPLICATE_REMOVAL, HASH_DUPLICATE_REMOVAL, EXPLODE, AUTO_INCREMENT,
                 CARTESIAN_PRODUCT
                -> OperatorCategory.ALGEBRA;

            case NESTED_LOOP_JOIN, MERGE_JOIN, HASH_JOIN,
                 NESTED_LOOP_LEFT_SEMI_JOIN, MERGE_LEFT_SEMI_JOIN, MERGE_RIGHT_SEMI_JOIN,
                 HASH_LEFT_SEMI_JOIN, HASH_RIGHT_SEMI_JOIN,
                 NESTED_LOOP_LEFT_ANTI_JOIN, MERGE_LEFT_ANTI_JOIN, MERGE_RIGHT_ANTI_JOIN,
                 HASH_LEFT_ANTI_JOIN, HASH_RIGHT_ANTI_JOIN,
                 NESTED_LOOP_LEFT_OUTER_JOIN, NESTED_LOOP_RIGHT_OUTER_JOIN,
                 MERGE_LEFT_OUTER_JOIN, MERGE_RIGHT_OUTER_JOIN, MERGE_FULL_OUTER_JOIN,
                 HASH_LEFT_OUTER_JOIN, HASH_RIGHT_OUTER_JOIN, HASH_FULL_OUTER_JOIN
                -> OperatorCategory.JOINS;

            case APPEND, UNION, HASH_UNION, INTERSECTION, HASH_INTERSECTION,
                 DIFFERENCE, HASH_DIFFERENCE,
                 UNILATERAL_EXISTENCE, BILATERAL_EXISTENCE
                -> OperatorCategory.SETS;

            case GROUP, HASH_GROUP, AGGREGATION
                -> OperatorCategory.AGGREGATION;

            case REFERENCE, SCAN, HASH, MEMOIZE, MATERIALIZATION,
                 AND, OR, XOR, CONDITION, IF
                -> OperatorCategory.OTHER;
        };
    }
}
