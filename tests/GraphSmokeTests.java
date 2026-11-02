import java.util.*;

/** Run with assertions enabled: java -ea -cp src:tests GraphSmokeTests */
public class GraphSmokeTests {
    public static void main(String[] args) {
        CampusGraph g = new AdjacencyListCampusGraph();
        g.addConnection("A", "B"); g.addConnection("B", "C"); g.addConnection("C", "D");
        assert g.shortestPath("A", "D").equals(List.of("A", "B", "C", "D"));
        assert g.shortestPathAvoiding("A", "D", Set.of("B")).isEmpty();
        assert g.connectedComponents().size() == 1;
        assert g.criticalBuildings().equals(Set.of("B", "C"));
        g.removeConnection("B", "C");
        assert g.connectedComponents().size() == 2;
        System.out.println("Smoke tests passed. Add your own edge-case tests.");
    }
}
