import java.util.*;

/** Run with assertions enabled: java -ea -cp src:tests GraphTests
 *  (On Windows, replace the classpath colon with a semicolon.)
 */
public class GraphTests {

    public static void main(String[] args) {
        testChain();
        testCycle();
        testDisconnectedComponents();
        testIsolatedBuilding();
        testUnknownEndpoint();
        testRestrictedShortestPath();
        testBuildingsIsImmutable();
        testDuplicateEdgeIsIdempotent();
        System.out.println("All GraphTests passed.");
    }

    // 1. Chain: A-B-C-D-E. Every "middle" building should be critical.
    private static void testChain() {
        CampusGraph g = new AdjacencyListCampusGraph();
        g.addConnection("A", "B");
        g.addConnection("B", "C");
        g.addConnection("C", "D");
        g.addConnection("D", "E");

        List<String> path = g.shortestPath("A", "E");
        assert path.size() == 5 : "chain A-E should be 4 edges (5 buildings)";
        assertValidPath(g, path, "A", "E");

        assert g.criticalBuildings().equals(Set.of("B", "C", "D"))
                : "every middle building of a chain should be critical";
        System.out.println("testChain passed");
    }

    // 2. Cycle: a pure triangle has no critical buildings; adding a bridge
    //    off the triangle makes exactly the bridge's triangle-side endpoint critical.
    private static void testCycle() {
        CampusGraph triangle = new AdjacencyListCampusGraph();
        triangle.addConnection("A", "B");
        triangle.addConnection("B", "C");
        triangle.addConnection("C", "A");
        assert triangle.criticalBuildings().isEmpty()
                : "a pure triangle should have no critical buildings";

        CampusGraph withBridge = new AdjacencyListCampusGraph();
        withBridge.addConnection("A", "B");
        withBridge.addConnection("B", "C");
        withBridge.addConnection("C", "A");
        withBridge.addConnection("C", "D");
        assert withBridge.criticalBuildings().equals(Set.of("C"))
                : "only C (the bridge's triangle-side endpoint) should be critical";
        System.out.println("testCycle passed");
    }

    // 3. Multiple disconnected components: A-B-C and D-E are separate.
    private static void testDisconnectedComponents() {
        CampusGraph g = new AdjacencyListCampusGraph();
        g.addConnection("A", "B");
        g.addConnection("B", "C");
        g.addConnection("D", "E");

        List<Set<String>> comps = g.connectedComponents();
        assert comps.size() == 2 : "should find exactly 2 components";

        Set<String> union = new HashSet<>();
        for (Set<String> c : comps) {
            union.addAll(c);
        }
        assert union.equals(g.buildings()) : "every building must appear in exactly one component";
        assert g.shortestPath("A", "D").isEmpty() : "A and D are in different components, unreachable";
        System.out.println("testDisconnectedComponents passed");
    }

    // 4. Isolated building: Z has no connections at all.
    private static void testIsolatedBuilding() {
        CampusGraph g = new AdjacencyListCampusGraph();
        g.addConnection("A", "B");
        g.addConnection("B", "C");
        g.addBuilding("Z");

        assert g.connectedComponents().size() == 2 : "the isolated building forms its own component";
        assert g.shortestPath("A", "Z").isEmpty() : "an isolated building is unreachable from anywhere else";
        assert g.criticalBuildings().equals(Set.of("B"))
                : "B still connects A and C; the isolated Z is never critical";
        System.out.println("testIsolatedBuilding passed");
    }

    // 5. Unknown endpoint: queries return empty rather than throwing.
    private static void testUnknownEndpoint() {
        CampusGraph g = new AdjacencyListCampusGraph();
        g.addConnection("A", "B");

        assert g.shortestPath("A", "NOPE").isEmpty() : "unknown end should yield empty list, not throw";
        assert g.shortestPath("NOPE", "A").isEmpty() : "unknown start should yield empty list, not throw";
        assert g.shortestPathAvoiding("A", "NOPE", Set.of()).isEmpty();
        System.out.println("testUnknownEndpoint passed");
    }

    // 6. Restricted shortest path: a diamond where closing one route forces a detour.
    private static void testRestrictedShortestPath() {
        CampusGraph g = new AdjacencyListCampusGraph();
        g.addConnection("A", "B");
        g.addConnection("A", "C");
        g.addConnection("B", "D");
        g.addConnection("C", "D");

        List<String> unrestricted = g.shortestPath("A", "D");
        assert unrestricted.size() == 3 : "diamond shortest path should be 2 edges (3 buildings)";
        assertValidPath(g, unrestricted, "A", "D");

        List<String> restricted = g.shortestPathAvoiding("A", "D", Set.of("B"));
        assert restricted.size() == 3 : "closing B should still leave the equally-short route through C";
        assert !restricted.contains("B") : "restricted path must never include a closed building";
        assertValidPath(g, restricted, "A", "D");

        List<String> impossible = g.shortestPathAvoiding("A", "D", Set.of("B", "C"));
        assert impossible.isEmpty() : "closing both middle buildings makes D unreachable from A";
        System.out.println("testRestrictedShortestPath passed");
    }

    // buildings() must not expose a mutable view of internal state.
    private static void testBuildingsIsImmutable() {
        CampusGraph g = new AdjacencyListCampusGraph();
        g.addConnection("A", "B");
        Set<String> result = g.buildings();
        boolean threw = false;
        try {
            result.add("Z");
        } catch (UnsupportedOperationException e) {
            threw = true;
        }
        assert threw : "buildings() must return an unmodifiable collection";
        System.out.println("testBuildingsIsImmutable passed");
    }

    // adding the same edge twice must not change behavior or create duplicates.
    private static void testDuplicateEdgeIsIdempotent() {
        CampusGraph g = new AdjacencyListCampusGraph();
        g.addConnection("A", "B");
        g.addConnection("A", "B"); // duplicate, should be a no-op
        assert g.buildings().size() == 2 : "duplicate edge should not create extra buildings";
        assert g.shortestPath("A", "B").size() == 2 : "A and B should still be directly connected once";
        System.out.println("testDuplicateEdgeIsIdempotent passed");
    }

    /**
     * Confirms a returned path starts at 'start', ends at 'end', and that every
     * consecutive pair is actually directly connected in the graph. Checking
     * direct connectivity via shortestPath(a, b).size() == 2 keeps this
     * validation entirely within the public CampusGraph interface, rather than
     * reaching into the implementation's internals.
     */
    private static void assertValidPath(CampusGraph g, List<String> path, String start, String end) {
        assert !path.isEmpty() : "path should not be empty";
        assert path.get(0).equals(start) : "path must start at the requested start";
        assert path.get(path.size() - 1).equals(end) : "path must end at the requested end";
        for (int i = 0; i < path.size() - 1; i++) {
            List<String> direct = g.shortestPath(path.get(i), path.get(i + 1));
            assert direct.size() == 2 : "consecutive path entries must be directly connected";
        }
    }
}