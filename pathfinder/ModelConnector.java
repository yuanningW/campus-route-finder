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

package pathfinder;

import pathfinder.datastructures.Path;
import pathfinder.datastructures.Point;
import Graph.java;
import pathfinder.parser.CampusBuilding;
import pathfinder.parser.CampusPath;
import pathfinder.parser.CampusPathsParser;
import pathfinder.textInterface.Pathfinder;

import java.util.*;

/*
In the pathfinder homework, the text user interface calls these methods to talk
to your model. In the campuspaths homework, your graphical user interface
will ultimately make calls to these methods (through a web server) to
talk to your model the same way.

This is the power of the Model-View-Controller pattern, two completely different
user interfaces can use the same model to display and interact with data in
different ways, without requiring a lot of work to change things over.
*/

/**
 * This class represents the connection between the view/controller and the model
 * for the pathfinder and campus paths applications.
 */
public class ModelConnector {
  // a map that maps campus buildings short names to long names
  private Map<String, String> shortToLongNames;

  // a map that maps campus buildings short names to the building's location
  private Map<String, Point> locations;

  // a graph that contains campus paths from buildings to buildings
  private Graph<Point, Double> campusPaths;

  // This class does not represent an ADT because it only stores data and interacts
  // with the data.

  /**
   * Creates a new {@link ModelConnector} and initializes it to contain data about
   * pathways and buildings or locations of interest on the campus of the University
   * of Washington, Seattle. When this constructor completes, the dataset is loaded
   * and prepared, and any method may be called on this object to query the data.
   */
  public ModelConnector() {
    shortToLongNames = new HashMap<>();
    locations = new HashMap<>();
    campusPaths = new Graph<>();
    storeNamesAndLocations();
    storeGraph();
  }

  // Parse campus buildings and store buildings' short names and long names
  // in map
  private void storeNamesAndLocations() {
    List<CampusBuilding> buildings = CampusPathsParser.parseCampusBuildings();
    for(CampusBuilding b : buildings) {
      shortToLongNames.put(b.getShortName(), b.getLongName());
      Point b1 = new Point(b.getX(), b.getY());
      locations.put(b.getShortName(), b1);
    }
  }

  // Parse campus paths and store points the the weights between the points in
  // the graph
  private void storeGraph() {
    List<CampusPath> paths = CampusPathsParser.parseCampusPaths();
    for(CampusPath p : paths) {
      Point p1 = new Point(p.getX1(), p.getY1());
      Point p2 = new Point(p.getX2(), p.getY2());
      Double distance = p.getDistance();
      if(!campusPaths.nodeExists(p1)) {
        campusPaths.addNode(p1);
      }
      if(!campusPaths.nodeExists(p2)) {
        campusPaths.addNode(p2);
      }
      campusPaths.addEdge(p1, p2, distance);
    }
  }

  /**
   * @param shortName The short name of a building to query.
   * @return {@literal true} iff the short name provided exists in this campus map.
   */
  public boolean shortNameExists(String shortName) {
    return shortToLongNames.containsKey(shortName);
  }

  /**
   * @param shortName The short name of a building to look up.
   * @return The long name of the building corresponding to the provided short name.
   * @throws IllegalArgumentException if the short name provided does not exist.
   */
  public String longNameForShort(String shortName) {
    if(!shortNameExists(shortName)) {
      throw new IllegalArgumentException();
    }
    return shortToLongNames.get(shortName);
  }

  /**
   * @return The mapping from all the buildings' short names to their long names in this campus map.
   */
  public Map<String, String> buildingNames() {
    return new HashMap<String, String>(shortToLongNames);
  }

  /**
   * Finds the shortest path, by distance, between the two provided buildings.
   *
   * @param startShortName The short name of the building at the beginning of this path.
   * @param endShortName   The short name of the building at the end of this path.
   * @return A path between {@code startBuilding} and {@code endBuilding}, or {@literal null}
   * if none exists.
   * @throws IllegalArgumentException if {@code startBuilding} or {@code endBuilding} are
   *                                  {@literal null}, or not valid short names of buildings in
   *                                  this campus map.
   */
  public Path<Point> findShortestPath(String startShortName, String endShortName) {
    if(startShortName == null || endShortName == null || !shortNameExists(startShortName)
        || !shortNameExists(endShortName)) {
      throw new IllegalArgumentException();
    }
    Point p1 = locations.get(startShortName);
    Point p2 = locations.get(endShortName);
    Path<Point> result = Pathfinder.dijkstraFindPath(p1, p2, campusPaths);
    return result;
  }

}
