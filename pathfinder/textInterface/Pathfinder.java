/*
 * Copyright ©2019 Dan Grossman.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Autumn Quarter 2019 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

package pathfinder.textInterface;

import graph.Graph;
import pathfinder.ModelConnector;
import pathfinder.datastructures.Path;

import java.util.*;


/**
 * Pathfinder represents a complete application capable of responding to user prompts to provide
 * a variety of information about campus buildings and paths between them.
 */
public class Pathfinder {

    // This class does not represent an ADT.

    /**
     * The main entry point for this application. Initializes and launches the application.
     *
     * @param args The command-line arguments provided to the system.
     */
    public static void main(String[] args) {
        ModelConnector model = new ModelConnector();
        TextInterfaceView view = new TextInterfaceView();
        TextInterfaceController controller = new TextInterfaceController(model, view);
        //
        view.setInputHandler(controller);
        controller.launchApplication();
    }

    /**
     * Dijkstra algorithm to find the path from src to dest with the least cost
     *
     * @param src the start
     * @param dest the end
     * @param graph graph that contains nodes and paths
     * @param <T> type of node
     * @spec.requires {@code src != null && dest != null && graph != null}
     * @return the path from src to dest with the least cost
     */
    public static <T> Path<T> dijkstraFindPath(T src, T dest, Graph<T, Double> graph) {
        PriorityQueue<Path<T>> queue = new PriorityQueue<>(new Comparator<Path<T>>() {
            @Override
            public int compare(Path<T> o1, Path<T> o2) {
                return Double.compare(o1.getCost(), o2.getCost());
            }
        });
        Set<T> finished = new HashSet<>();
        queue.add(new Path<T>(src));
        while (!queue.isEmpty()) {
            Path<T> minPath = queue.remove();
            T minDest = minPath.getEnd();
            if (minDest.equals(dest)) {
                return minPath;
            }
            if (!finished.contains(minDest)) {
                for (T child : graph.ListChildren(minDest)) {
                    if (!finished.contains(child)) {
                        Set<Double> labels = graph.getLabel(minDest, child);
                        Iterator<Double> iter = labels.iterator();
                        Path<T> path = minPath.extend(child, iter.next());
                        queue.add(path);
                    }
                }
                finished.add(minDest);
            }
        }
        return null;
    }
}
