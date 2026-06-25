package utils;

import java.util.*;
import java.util.function.Function;

public final class TopologicalSort {

    private TopologicalSort() {}

    public static <T> List<T> sort(List<T> nodes, Function<T, List<T>> parents) {
        if (nodes.isEmpty()) return new ArrayList<>();

        Set<T> nodeSet = new HashSet<>(nodes);
        Map<T, List<T>> childrenOf = new HashMap<>();
        Map<T, Integer> inDegree = new HashMap<>();

        for (T node : nodes) {
            inDegree.put(node, 0);
            childrenOf.put(node, new ArrayList<>());
        }

        for (T node : nodes) {
            for (T parent : parents.apply(node)) {
                if (nodeSet.contains(parent)) {
                    childrenOf.get(parent).add(node);
                    inDegree.merge(node, 1, Integer::sum);
                }
            }
        }

        Queue<T> queue = new ArrayDeque<>();
        for (T node : nodes) {
            if (inDegree.get(node) == 0) {
                queue.add(node);
            }
        }

        List<T> result = new ArrayList<>(nodes.size());

        while (!queue.isEmpty()) {
            T node = queue.poll();
            result.add(node);

            for (T child : childrenOf.get(node)) {
                int newDegree = inDegree.merge(child, -1, Integer::sum);
                if (newDegree == 0) {
                    queue.add(child);
                }
            }
        }

        if (result.size() < nodes.size()) {
            Set<T> emitted = new HashSet<>(result);
            for (T node : nodes) {
                if (!emitted.contains(node)) result.add(node);
            }
        }

        return result;
    }
}
