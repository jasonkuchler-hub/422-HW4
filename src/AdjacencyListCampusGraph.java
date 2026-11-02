import java.util.*;

/** Implement this class using an adjacency list. Do not use graph libraries. */
public class AdjacencyListCampusGraph implements CampusGraph {

    private final Map<String, Set<String>> adjacency = new HashMap<>();
    private void validateBuildingName(String building) {
        if(building == null){
            throw new IllegalArgumentException("building name is null");
        }
        else if (building.isBlank()){
            throw new IllegalArgumentException("building name is blank");
        }
    }

    @Override public void addBuilding(String building) {
        validateBuildingName(building);
        adjacency.computeIfAbsent(building, k -> new HashSet<>());
    }

    @Override public void addConnection(String from, String to) {
        validateBuildingName(from);
        validateBuildingName(to);
        if (from.equals(to)){throw new IllegalArgumentException("buildings are equal");}
        adjacency.computeIfAbsent(from, k -> new HashSet<>()).add(to);
        adjacency.computeIfAbsent(to, k -> new HashSet<>()).add(from);
    }

    @Override public void removeConnection(String from, String to) {
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

    @Override public Set<String> buildings() {
        return Set.copyOf(adjacency.keySet());
    }

    @Override public List<String> shortestPath(String start, String end) {
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
    @Override public List<String> shortestPathAvoiding(String start, String end, Set<String> closedBuildings) { throw new UnsupportedOperationException("TODO"); }
    @Override public List<Set<String>> connectedComponents() { throw new UnsupportedOperationException("TODO"); }
    @Override public Set<String> criticalBuildings() { throw new UnsupportedOperationException("TODO"); }
}
