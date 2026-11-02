import java.io.IOException;
import java.util.*;

public class CampusNav {
    public static void main(String[] args) throws IOException {
        String filename = args.length > 0 ? args[0] : "data/ut_austin_campus_100plus.txt";
        CampusGraph graph = CampusGraphLoader.load(filename);
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("\n1 Shortest path | 2 Avoid buildings | 3 Components | 4 Critical buildings | 5 Close walkway | 6 Exit");
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.print("Start: "); String a = in.nextLine().trim();
                    System.out.print("End: "); String b = in.nextLine().trim();
                    System.out.println(graph.shortestPath(a, b)); break;
                case "2":
                    System.out.print("Start: "); String s = in.nextLine().trim();
                    System.out.print("End: "); String t = in.nextLine().trim();
                    System.out.print("Closed buildings (space-separated): ");
                    String raw = in.nextLine().trim();
                    Set<String> closed = raw.isEmpty() ? Set.of() : new HashSet<>(Arrays.asList(raw.split("\\s+")));
                    System.out.println(graph.shortestPathAvoiding(s, t, closed)); break;
                case "3": System.out.println(graph.connectedComponents()); break;
                case "4": System.out.println(graph.criticalBuildings()); break;
                case "5":
                    System.out.print("Walkway endpoint 1: "); String u = in.nextLine().trim();
                    System.out.print("Walkway endpoint 2: "); String v = in.nextLine().trim();
                    graph.removeConnection(u, v); System.out.println("Removal requested."); break;
                case "6": return;
                default: System.out.println("Invalid option");
            }
        }
    }
}
