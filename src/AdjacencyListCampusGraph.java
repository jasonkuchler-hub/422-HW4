import java.util.*;

/** Implement this class using an adjacency list. Do not use graph libraries. */
public class AdjacencyListCampusGraph implements CampusGraph {

    private final Map<String, Set<String>> adjacency = new HashMap<>();

    //checks if the buildings are valid
    private void validateBuildingName(String building) {
        if (building == null) {
            throw new IllegalArgumentException("building name is null");
        } else if (building.isBlank()) {
            throw new IllegalArgumentException("building name is blank");
        }
    }

    //given a starting building, returns every reachable building into a Set
    private void dfsCollect(String building, Set<String> visited, Set<String> component) {
        visited.add(building);
        component.add(building);
        for (String neighbor : adjacency.get(building)) {
            if (!visited.contains(neighbor)) {
                dfsCollect(neighbor, visited, component);
            }
        }
    }

    private int countComponentsExcluding(String excluded) {
        Set<String> visited = new HashSet<>();
        visited.add(excluded); // pre-marking it "visited" means DFS will never traverse into or out of it
        int count = 0;
        for (String building : adjacency.keySet()) {
            if (!visited.contains(building)) {
                Set<String> component = new HashSet<>();
                dfsCollect(building, visited, component);
                count++;
            }
        }
        return count;
    }

    @Override
    public void addBuilding(String building) {
        validateBuildingName(building);
        adjacency.computeIfAbsent(building, k -> new HashSet<>());
    }

    @Override
    public void addConnection(String from, String to) {
        validateBuildingName(from);
        validateBuildingName(to);
        if (from.equals(to)) {
            throw new IllegalArgumentException("buildings are equal");
        }
        adjacency.computeIfAbsent(from, k -> new HashSet<>()).add(to);
        adjacency.computeIfAbsent(to, k -> new HashSet<>()).add(from);
    }

    @Override
    public void removeConnection(String from, String to) {
        validateBuildingName(from);
        validateBuildingName(to);
        Set<String> fromNeighbors = adjacency.get(from);
        if (fromNeighbors != null) {
            fromNeighbors.remove(to);
        }
        Set<String> toNeighbors = adjacency.get(to);
        if (toNeighbors != null) {
            toNeighbors.remove(from);
        }
    }

    @Override
    public Set<String> buildings() {
        return Set.copyOf(adjacency.keySet());
    }

    @Override
    public List<String> shortestPath(String start, String end) {
        if (!adjacency.containsKey(start) || !adjacency.containsKey(end)) {
            //check for unknown buildings
            return new ArrayList<>();
        }
        if (start.equals(end)) {
            //check if the buildings are the same
            return new ArrayList<>(List.of(start));
        }

        Set<String> visited = new HashSet<>();
        Map<String, String> predecessor = new HashMap<>();
        Queue<String> queue = new LinkedList<>();

        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(end)) {
                break;
            }
            for (String neighbor : adjacency.get(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    predecessor.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        if (!visited.contains(end)) {
            return new ArrayList<>(); // never reached end — unreachable
        }

        List<String> path = new ArrayList<>();
        String cur = end;
        while (cur != null) {
            path.add(cur);
            cur = predecessor.get(cur);
        }
        Collections.reverse(path);
        return path;
    }

    @Override
    public List<String> shortestPathAvoiding(String start, String end, Set<String> closedBuildings) {
        if (closedBuildings == null) {
            throw new IllegalArgumentException("closedBuildings is null");
        }
        if (closedBuildings.contains(start) || closedBuildings.contains(end)) {
            return new ArrayList<>();
        }
        if (!adjacency.containsKey(start) || !adjacency.containsKey(end)) {
            return new ArrayList<>();
        }
        if (start.equals(end)) {
            return new ArrayList<>(List.of(start));
        }

        Set<String> visited = new HashSet<>();
        Map<String, String> predecessor = new HashMap<>();
        Queue<String> queue = new LinkedList<>();

        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(end)) {
                break;
            }
            for (String neighbor : adjacency.get(current)) {
                if (!visited.contains(neighbor) && !closedBuildings.contains(neighbor)) {
                    visited.add(neighbor);
                    predecessor.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        if (!visited.contains(end)) {
            return new ArrayList<>();
        }

        List<String> path = new ArrayList<>();
        String cur = end;
        while (cur != null) {
            path.add(cur);
            cur = predecessor.get(cur);
        }
        Collections.reverse(path);
        return path;
    }

    @Override
    public List<Set<String>> connectedComponents() {
        List<Set<String>> components = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        for (String building : adjacency.keySet()) {
            if (!visited.contains(building)) {
                Set<String> component = new HashSet<>();
                dfsCollect(building, visited, component);
                components.add(component);
            }
        }
        return components;
    }

    @Override
    public Set<String> criticalBuildings() {
        int baseline = connectedComponents().size();
        Set<String> critical = new HashSet<>();
        for (String candidate : adjacency.keySet()) {
            int withoutCandidate = countComponentsExcluding(candidate);
            if (withoutCandidate > baseline) {
                critical.add(candidate);
            }
        }
        return critical;
    }
}
