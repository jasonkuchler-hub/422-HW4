import java.util.List;
import java.util.Set;

/** Undirected, unweighted campus graph. Building codes are case-sensitive. */
public interface CampusGraph {
    void addBuilding(String building);
    void addConnection(String from, String to);
    void removeConnection(String from, String to);
    Set<String> buildings();
    List<String> shortestPath(String start, String end);
    List<String> shortestPathAvoiding(String start, String end, Set<String> closedBuildings);
    List<Set<String>> connectedComponents();
    Set<String> criticalBuildings();
}
