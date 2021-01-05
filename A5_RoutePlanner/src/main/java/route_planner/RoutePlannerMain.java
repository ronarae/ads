package route_planner;

import graphs.DirectedGraph;

public class RoutePlannerMain {

    public static void main(String[] args) {
        System.out.println("Welcome to the HvA RoutePlanner");

        // load the small map from csv files
        RoadMap.reSeedRandomizer(19670427L);
        RoadMap roadMap0 = new RoadMap("Junctions0.csv", "Roads0.csv");
        System.out.println(roadMap0);

        // provide a map into the target classpath
        roadMap0.svgDrawMap("RoadmapAMS.svg", null);

        // Run various types of searches
        doPathSearches(roadMap0, "Oostzaan", "Ouder-Amstel");

        // load the complete map from csv files
        RoadMap.reSeedRandomizer(19670427L);
        RoadMap roadMap = new RoadMap("Junctions.csv", "Roads.csv");

        // provide a map into the target classpath
        roadMap.svgDrawMap("RoadmapNL.svg", null);

        // Run various types of searches
        final String FROM_ID = "Amsterdam";
        final String TO_ID = "Staphorst";
        doPathSearches(roadMap, FROM_ID, TO_ID);

        // now we have an accident between Diemen and Weesp...
        roadMap.getVertexById("Diemen").getEdges().stream().filter(e -> e.getTo().equals(roadMap.getVertexById("Weesp"))).findFirst().get().setMaxSpeed(5);

        // find the fastest route avoiding the accident
        DirectedGraph.DGPath path =
                roadMap.dijkstraShortestPathByAStar(FROM_ID, TO_ID,
                        e -> e.getLength() * e.getMaxSpeed()
                );
        System.out.println("DijkstraByAStar-accident-Weesp: " + path);
        roadMap.svgDrawMap(String.format("DSPACC-%s-%s.svg", FROM_ID, TO_ID), path);
    }

    private static void doPathSearches(RoadMap roadMap, String fromId, String toId) {
        System.out.printf("\nResults from path searches from %s to %s:\n", fromId, toId);
        RoadMap.DGPath path;

        // find the routes by depth-first-search
        path = roadMap.depthFirstSearch(fromId, toId);
        System.out.println("Depth-first-search: " + path);
        roadMap.svgDrawMap(String.format("DFS-%s-%s.svg", fromId, toId), path);
        path = roadMap.depthFirstSearch(toId, fromId);
        System.out.println("Depth-first-search return: " + path);

        // find the routes by breadth-first-search with minimum number of hops
        path = roadMap.breadthFirstSearch(fromId, toId);
        System.out.println("Breadth-first-search: " + path);
        roadMap.svgDrawMap(String.format("BFS-%s-%s.svg", fromId, toId), path);
        path = roadMap.breadthFirstSearch(toId, fromId);
        System.out.println("Breadth-first-search return: " + path);

        // find the routes by dijkstra-Shortest-Path with minimum total length
        path = roadMap.dijkstraShortestPath(fromId, toId,
                Road::getLength);
        System.out.println("Dijkstra-Shortest-Path: " + path);
        roadMap.svgDrawMap(String.format("DSP-%s-%s.svg", fromId, toId), path);
        path = roadMap.dijkstraShortestPath(toId, fromId,
                Road::getLength);
        System.out.println("Dijkstra-Shortest-Path return: " + path);

        // find the routes by A* Shortest Path with minimum total length
        path = roadMap.aStarShortestPath(fromId, toId,
                Road::getLength,
                (e, i) -> Math.sqrt(Math.pow(e.getLocationX() - i.getLocationX(), 2) + Math.pow(e.getLocationY() - i.getLocationY(), 2))
        );
        System.out.println("AStar-Shortest-Path: " + path);
        roadMap.svgDrawMap(String.format("ASSP-%s-%s.svg", fromId, toId), path);
        path = roadMap.aStarShortestPath(toId, fromId,
                Road::getLength,
                (e, i) -> Math.sqrt(Math.pow(e.getLocationX() - i.getLocationX(), 2) + Math.pow(e.getLocationY() - i.getLocationY(), 2))
        );
        System.out.println("AStar-Shortest-Path return: " + path);

        // find the routes by A* Shortest Path with minimum total travel time
        path = roadMap.aStarShortestPath(fromId, toId,
                e -> e.getLength() * e.getMaxSpeed(),
                (e, i) -> Math.sqrt(Math.pow(e.getLocationX() - i.getLocationX(), 2) + Math.pow(e.getLocationY() - i.getLocationY(), 2)) * 120
        );
        System.out.println("AStar-Fastest-Route: " + path);
        roadMap.svgDrawMap(String.format("ASFR-%s-%s.svg", fromId, toId), path);

        System.out.println();
    }


}
