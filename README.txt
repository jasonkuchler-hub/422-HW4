Assignment #4 - Campus Navigation (BFS/DFS)

Compile and run
----------------
From the project root:

    javac src/*.java tests/*.java
    java -cp src CampusNav data/ut_austin_campus_100plus.txt
    java -ea -cp src:tests GraphSmokeTests
    java -ea -cp src:tests GraphTests

On Windows, replace the classpath colon (:) with a semicolon (;), e.g.:

    java -ea -cp src;tests GraphTests

The -ea flag enables Java assertions. Without it, the assert statements in
GraphSmokeTests and GraphTests silently do nothing and the programs will
appear to "pass" without actually checking anything.

Design summary
--------------
The graph is stored as a single adjacency list: a Map<String, Set<String>>,
where each building name maps to the set of buildings directly connected to
it. A HashSet is used for each building's neighbor list (rather than a List)
because the spec requires that duplicate edges never be stored, and a Set
enforces that automatically rather than requiring a manual duplicate check
on every insertion.

shortestPath and shortestPathAvoiding both use a standard BFS: a queue, a
visited set, and a predecessor map that records "how did I reach this
building" for each newly-discovered node. The path is reconstructed by
walking backward through the predecessor map from the end building to the
start building, then reversing the result. shortestPathAvoiding is the same
algorithm with one added condition: a building already known to be closed is
never added to the queue, so BFS can never traverse through it.

connectedComponents uses a recursive DFS helper (dfsCollect) that, given a
starting building, marks everything reachable from it as visited and
collects it into one set. connectedComponents loops over every building in
the graph; any building not yet visited starts a brand-new component.

criticalBuildings reuses dfsCollect directly: to check whether a given
building is an articulation point, the visited set is pre-seeded with that
one building before running the same component-counting loop used by
connectedComponents. Since dfsCollect only recurses into unvisited
neighbors, pre-marking a building as "visited" causes DFS to skip over it
entirely, exactly simulating "this building does not exist" without ever
mutating the real graph. If excluding a building increases the resulting
component count compared to the graph's normal baseline, that building is
an articulation point.

Questions
---------
1. Why does BFS find a minimum-edge path?

BFS explores the graph in strict distance order: it first visits every
building reachable in exactly one edge from the start, then every building
reachable in exactly two edges, and so on, one full "layer" at a time. A
building is only ever marked visited the first time it is discovered, which
is guaranteed to be via the shortest possible number of edges, since BFS
never reaches a building through a longer path before reaching it through
a shorter one. The first time the end building is dequeued, the predecessor
chain recorded up to that point is therefore guaranteed to be a
minimum-edge path.

2. How does DFS identify components?

Starting from any unvisited building, DFS recursively visits every building
reachable from it, marking each as visited along the way. Because the graph
is undirected, "reachable from X" and "connected to X" are the same
relationship, so everything DFS visits in one call forms exactly one
connected component. Looping over every building in the graph, and starting
a brand-new DFS only when a building is found still unvisited, guarantees
every building is placed into exactly one component: buildings already
visited during an earlier component's DFS are skipped, and any building
left over after all existing components are explored must belong to a
component that has not been discovered yet.

3. Why does removing a vertex sometimes increase component count?

A vertex increases the component count when it is an articulation point:
the only connection between two otherwise-separate parts of the graph. If
every other building's path to the rest of the graph goes through that one
vertex, removing it severs those parts from each other, splitting one
connected component into two or more. A vertex does not increase the count
if an alternate path exists around it (as in a cycle), or if it was a "dead
end" whose removal only shrinks the graph without disconnecting anything
else (such as either endpoint of a simple chain).

4. Asymptotic complexity

Let V = number of buildings and E = number of connections.

- BFS (shortestPath, shortestPathAvoiding): O(V + E). Each building is
  enqueued and dequeued at most once, and each connection is examined at
  most twice (once from each endpoint), giving linear time in the size of
  the graph.

- DFS (connectedComponents): O(V + E), for the same reason as BFS - every
  building is visited once and every connection is examined at most twice
  across the whole method, regardless of how many separate components the
  graph has.

- criticalBuildings: O(V * (V + E)). For each of the V candidate buildings,
  the implementation performs one full DFS-based component count over the
  (nearly) whole graph, which costs O(V + E) per candidate. This is the
  straightforward approach the assignment explicitly permits; a single-pass
  DFS low-link (Tarjan-style) algorithm would find the same answer in
  O(V + E) total, but was not required and was not implemented here.