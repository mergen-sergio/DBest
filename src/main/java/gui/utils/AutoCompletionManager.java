package gui.utils;

import enums.OperationType;

/**
 * Manages auto-completion functionality for the text editor.
 */
public class AutoCompletionManager {
    private static final int MAX_DISTANCE_THRESHOLD = 2; // Maior distância aceitável
    private static final int MIN_PREFIX_LENGTH = 2; // Tamanho mínimo da palavra a ser completada

    /**
     * Finds the closest matching operation template for a given prefix.
     * 
     * @param prefix The partial operation name typed by the user
     * @return The complete template string or null if no good match found
     */
    public static String findClosestMatch(String prefix) {
        if (prefix == null || prefix.trim().length() < MIN_PREFIX_LENGTH) {
            return null;
        }

        String normalizedPrefix = prefix.trim().toLowerCase();

        // Busca a melhor correspondência entre todas as operações disponíveis
        String bestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        // Operações vêm do enum OperationType
        for (OperationType operation : OperationType.values()) {
            String normalizedOperationName = operation.name.trim().toLowerCase();
            int distance = calculatePrefixLevenshteinDistance(normalizedPrefix, normalizedOperationName);

            if (distance < minDistance && distance <= MAX_DISTANCE_THRESHOLD) {
                minDistance = distance;
                bestMatch = operation.dslSyntax;
            }
        }

        return bestMatch;
    }

    /**
     * Calculates prefix Levenshtein distance between two strings.
     * 
     * @param prefix The input prefix being typed
     * @param target The target operation name to compare against
     * @return The edit distance between the prefix and target prefix
     */
    private static int calculatePrefixLevenshteinDistance(String prefix, String target) {
        if (prefix.isEmpty()) return 0;
        if (target.isEmpty()) return prefix.length();

        int compareLength = Math.min(prefix.length(), target.length());
        String targetPrefix = target.substring(0, compareLength);

        // Algoritmo de programação dinâmica para calcular distância de edição
        int[][] dp = new int[prefix.length() + 1][compareLength + 1];

        for (int i = 0; i <= prefix.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= compareLength; j++) {
            dp[0][j] = j;
        }

        // Preenche a matriz calculando o custo mínimo de edição
        for (int i = 1; i <= prefix.length(); i++) {
            for (int j = 1; j <= compareLength; j++) {
                if (prefix.charAt(i - 1) == targetPrefix.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                        Math.min(dp[i - 1][j], dp[i][j - 1]),
                        dp[i - 1][j - 1]
                    );
                }
            }
        }

        return dp[prefix.length()][compareLength];
    }
}
