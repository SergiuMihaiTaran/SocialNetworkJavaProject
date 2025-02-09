package com.example.demo.Utils;

import java.util.*;


public class Comunities {
    private Map<Long, List<Long>> graph = new HashMap<>();

    public Comunities(Map<Long, List<Long>> graph) {
        this.graph = graph;
    }

    // Method to return the component with the longest path
    public List<Long> getLongestPathComponent() {
        List<List<Long>> components = getComunities();
        List<Long> longestPathComponent = null;
        int maxPathLength = -1;

        for (List<Long> component : components) {
            int longestPathInComponent = findLongestPathInComponent(component);
            if (longestPathInComponent > maxPathLength) {
                maxPathLength = longestPathInComponent;
                longestPathComponent = component;
            }
        }

        return longestPathComponent;
    }

    // Calculate the longest path in a single component
    private int findLongestPathInComponent(List<Long> component) {
        if (component.isEmpty()) return 0;

        long startNode = component.get(0);
        long farthestNode = bfsFindFarthestNode(startNode, component);
        return bfsFindMaxDistance(farthestNode, component);
    }

    // Helper BFS to find the farthest node from the starting node within the component
    private long bfsFindFarthestNode(long start, List<Long> component) {
        Queue<Long> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        long farthestNode = start;

        while (!queue.isEmpty()) {
            long current = queue.poll();
            farthestNode = current;
            for (long neighbor : graph.getOrDefault(current, Collections.emptyList())) {
                if (!visited.contains(neighbor) && component.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return farthestNode;
    }

    // Helper BFS to calculate the maximum distance from the farthest node found
    private int bfsFindMaxDistance(long start, List<Long> component) {
        Queue<Long> queue = new LinkedList<>();
        Map<Long, Integer> distance = new HashMap<>();
        queue.add(start);
        distance.put(start, 0);

        int maxDistance = 0;

        while (!queue.isEmpty()) {
            long current = queue.poll();
            int currentDistance = distance.get(current);

            for (long neighbor : graph.getOrDefault(current, Collections.emptyList())) {
                if (!distance.containsKey(neighbor) && component.contains(neighbor)) {
                    distance.put(neighbor, currentDistance + 1);
                    queue.add(neighbor);
                    maxDistance = Math.max(maxDistance, currentDistance + 1);
                }
            }
        }

        return maxDistance;
    }

    // DFS to find all connected components
    public List<List<Long>> getComunities() {
        Set<Long> visited = new HashSet<>();
        List<List<Long>> connectedComponents = new ArrayList<>();

        for (long node : graph.keySet()) {
            if (!visited.contains(node)) {
                List<Long> component = new ArrayList<>();
                dfs(node, graph, visited, component);
                connectedComponents.add(component);
            }
        }

        return connectedComponents;
    }

    private static void dfs(long node, Map<Long, List<Long>> graph, Set<Long> visited, List<Long> component) {
        visited.add(node);
        component.add(node);

        for (long neighbor : graph.getOrDefault(node, Collections.emptyList())) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, graph, visited, component);
            }
        }
    }
}