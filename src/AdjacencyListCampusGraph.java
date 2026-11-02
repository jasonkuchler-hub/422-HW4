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

    @Override public List<String> shortestPath(String start, String end) { throw new UnsupportedOperationException("TODO");
    }
    @Override public List<String> shortestPathAvoiding(String start, String end, Set<String> closedBuildings) { throw new UnsupportedOperationException("TODO"); }
    @Override public List<Set<String>> connectedComponents() { throw new UnsupportedOperationException("TODO"); }
    @Override public Set<String> criticalBuildings() { throw new UnsupportedOperationException("TODO"); }
}
