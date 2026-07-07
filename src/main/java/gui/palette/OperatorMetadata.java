package gui.palette;

import enums.OperationType;

import java.util.List;

public record OperatorMetadata(
    OperationType type,
    OperatorCategory category,
    String symbol,
    String displayName,
    String description,
    List<String> searchTags
) {
}
