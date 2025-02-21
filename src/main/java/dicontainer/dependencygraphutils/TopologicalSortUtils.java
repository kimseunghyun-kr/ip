package dicontainer.dependencygraphutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides graph traversal Utilities to the DependencyInjectionContainer class
 */
public class TopologicalSortUtils {
    /**
     * Perform a Kahn's Algorithm topological sort to detect cycles and produce an order.
     */
    public static List<Class<?>> topologicalSortDfs(Set<Class<?>> nodes,
                                                    Map<Class<?>, Set<Class<?>>> graph) {
        // 'graph[A]' = set of classes that A depends on

        List<Class<?>> sortedList = new ArrayList<>();
        Set<Class<?>> visited = new HashSet<>(); // permanent mark
        Set<Class<?>> visiting = new HashSet<>(); // temporary mark (detect cycles)

        for (Class<?> node : nodes) {
            if (!visited.contains(node)) {
                dfsVisit(node, graph, visited, visiting, sortedList);
            }
        }
        // The result is in "reverse" topological order if you append after visiting.
        // But we want dependencies first, so we can either reverse at the end or insert at the front.
        // Let's reverse at the end:
        Collections.reverse(sortedList);
        return sortedList;
    }

    /**
     * Perform a Depth first Search Algorithm as part of topological sort.
     */
    static void dfsVisit(Class<?> current,
                          Map<Class<?>, Set<Class<?>>> graph,
                          Set<Class<?>> visited,
                          Set<Class<?>> visiting,
                          List<Class<?>> sortedList) {
        if (visiting.contains(current)) {
            // We found a back edge => cycle
            throw new RuntimeException("Cycle detected in dependency graph at: " + current.getName());
        }
        if (visited.contains(current)) {
            // Already fully processed this node
            return;
        }

        // Mark 'current' as in progress
        visiting.add(current);

        // Visit all dependencies (the classes that 'current' depends on)
        Set<Class<?>> dependencies = graph.getOrDefault(current, Collections.emptySet());
        for (Class<?> dep : dependencies) {
            if (!visited.contains(dep)) {
                dfsVisit(dep, graph, visited, visiting, sortedList);
            }
        }

        // Mark 'current' as fully visited
        visiting.remove(current);
        visited.add(current);

        // Add to the result list. We'll reverse later.
        sortedList.add(current);
    }
}
