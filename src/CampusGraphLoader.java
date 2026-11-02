import java.io.*;
import java.nio.file.*;
import java.util.*;

/** File format: each nonblank noncomment line is two building codes separated by whitespace. */
public final class CampusGraphLoader {
    private CampusGraphLoader() {}
    public static CampusGraph load(String file) throws IOException {
        CampusGraph graph = new AdjacencyListCampusGraph();
        int lineNo = 0;
        for (String raw : Files.readAllLines(Path.of(file))) {
            lineNo++;
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] tokens = line.split("\\s+");
            if (tokens.length != 2) throw new IOException("Invalid edge on line " + lineNo);
            graph.addConnection(tokens[0], tokens[1]);
        }
        return graph;
    }
}
