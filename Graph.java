package graph;
import java.util.*;

/**
 * <b>Graph</b> represents a mutable directed labeled graph constituted of nodes connected by edges.
 * Each edge connects two nodes, and is directed from parent node to child node. Each edge contains
 * a unique label. No two nodes store entirely equal data. Graph takes in two generic types, one for
 * node and one for edge label. Type for label needs to be comparable.
 *
 * Each Graph can be described by a list of nodes [n_1, n_2, n_3 ... n_k],
 * and a map [n_1 : [n_2 : [l_1, l_2 .. l_k]], [n_3 : [n_4 : [l_1]...]] that maps a
 * parent node to a map that maps a child node to to a set of unique labels for this edge.
 * Graph can also be described constructively, with the append operation ":", such that G:[n_1] is
 * adding a node to the end of the list of nodes for Graph g, and G:[[n_1 : [n_2 :[l_1]]] is adding a label
 * to existing nodes inside Graph G.
 */

public class Graph<T, E extends Comparable<E>>{
    private boolean DEBUG = false;
    /**
     * Nodes is a mutable set of Strings that represents the Nodes that make up the graph.
     */
    private Set<T> Nodes;

    /**
     * Edges is a mutable map that maps a parent node to a map of child node that maps to a set of unique labels
     * that make up the edges.
     */
    private Map<T, Map<T, Set<E>>> Edges;

    //Abstract Function: A typical Graph object is represented by a list of Nodes [n0, n1, n2 .. nk], and a map
    //that maps an edge to a list of labels that the edge contains [[n1,n2] -> [l1, l2 .. lk], [n2, n3] -> [l4]..].
    //An empty map is represented by an empty list of Nodes [] and an empty map [].
    //
    //Representation Invariant: graph != null && list != null && map!= null && for every
    //node in the list and every parent, child and label in map is also not null
    //&& for every node in the map it also exists in the list of Nodes
    private void checkRep() {
        assert(Nodes != null);
        assert(Edges != null);
        if(!DEBUG) {
            for (T parent : Edges.keySet()) {
                assert (parent != null);
                assert (Nodes.contains(parent));
                Map<T, Set<E>> children = Edges.get(parent);
                assert (children != null);
                for (T c : children.keySet()) {
                    assert (c != null);
                    assert (Nodes.contains(c));
                    Set<E> labels = children.get(c);
                    assert (labels != null);
                    for (E label : labels) {
                        assert (label != null);
                    }
                }
            }
        }
    }

    /**
     * @spec.effects Constructs a new empty Graph with no nodes, edges or labels.
     */
    public Graph() {
        Nodes = new HashSet<T>();
        Edges = new HashMap<T, Map<T, Set<E>>>();
        checkRep();
    }

    /**
     * @param node a string that represents a node to be added into the graph
     * @spec.requires {@code node != null}
     * @spec.effects Constructs a new Graph with one node
     */
    public Graph(T node) {
        Nodes = new HashSet<T>();
        Edges = new HashMap<T, Map<T, Set<E>>>();
        Nodes.add(node);
        checkRep();
    }

    /**
     * Returns the size of the graph, which is represented by the number of nodes
     * inside the graph.
     *
     * @spec.requires {@code this != null}
     * @return int size
     */
    public int size() {
        return Nodes.size();
    }

    /**
     * Add one node into the graph
     *
     * @param node a node to be added into the graph. If node already exists in
     *             the graph, do nothing.
     * @spec.requires {@code this!= null && node != null}
     * @spec.modifies this
     * @spec.effects this_post:[node]
     * @return true if node is successfully added into the graph, false otherwise
     */
    public boolean addNode(T node) {
        return Nodes.add(node);
    }

    /**
     * Checks if a given node already exists in the graph. Each node in the graph
     * stores unique data.
     *
     * @param node the node to check if already exists in the graph
     * @spec.requires {@code this != null && node != null}
     * @return true if the given node exists in the graph, false otherwise
     */
    public boolean nodeExists(T node) {
        return Nodes.contains(node);
    }

    /**
     * Delete a given node from the graph
     *
     * @param node a node to be deleted from the graph
     * @spec.requires {@code this != null && node != null}
     * @spec.modifies this
     * @spec.effects If this = G:[node] then this_post = G
     * @throws IllegalArgumentException if the given node is not in the graph
     */
    public void deleteNode(T node) {
        if(!nodeExists(node)) {
            throw new IllegalArgumentException();
        } else {
            Nodes.remove(node);
        }
    }

    /**
     * Test if a given edge with a given label exists in the graph
     *
     * @param parentNode a parent node object
     * @param childNode a child node object
     * @param label the label for an edge
     * @spec.requires {@code this != null && parentNode != null && childNode != null && label != null}
     * @return true if edge with given label between the given nodes exists in the graph, false otherwise
     */
    public boolean edgeExists(T parentNode, T childNode, E label) {
        if(Edges.containsKey(parentNode) && Edges.get(parentNode).containsKey(childNode)
            && Edges.get(parentNode).get(childNode).contains(label)) {
                    return true;
        }
        return false;
    }


    /**
     * Add an edge for a node pair into the graph
     *
     * @param parentNode a parent node object
     * @param childNode a child node object
     * @param label the label for an edge
     * @spec.requires {@code this != null && n1 != null && n2 != null && label != null}
     * @spec.modifies this
     * @spec.effects Constructs a new edge between specified Nodes in the List, G:[[n_1]: [n_2 : [l_1]]]
     * @throws IllegalArgumentException if either parent node or child node are not in the Graph, or there is
     * an existing edge that contains the given label
     */
    public void addEdge(T parentNode, T childNode, E label) {
        if(!nodeExists(parentNode) || !nodeExists(childNode)) {
            throw new IllegalArgumentException("Node doesn't exist in graph.");
        }
        if(edgeExists(parentNode, childNode, label)) {
            throw new IllegalArgumentException("Edge already exists");
        }
        if (Edges.containsKey(parentNode) && Edges.get(parentNode).containsKey(childNode)){
            Edges.get(parentNode).get(childNode).add(label);
        } else if (Edges.containsKey(parentNode)){
            Set<E> labels = new HashSet<>();
            labels.add(label);
            Edges.get(parentNode).put(childNode, labels);
        } else {
            Map<T, Set<E>> edge= new HashMap<>();
            Set<E> labels = new HashSet<>();
            labels.add(label);
            edge.put(childNode, labels);
            Edges.put(parentNode, edge);
        }
    }

    /**
     * Delete an edge from a given node pair with a specified label from graph
     *
     * @param parentNode a parent node object
     * @param childNode a child node object
     * @param label the label for an edge
     * @spec.requires {@code this != null && path != null && path.size() = 2}
     * @spec.modifies this
     * @spec.effects Deletes an edge with specified label from the given parent to child node from the graph
     * @throws IllegalArgumentException if either parent node or child node are not in the Graph, or there is no
     * edge between the given parent and child node
     */
     public void deleteEdge(T parentNode, T childNode, E label) {
        if(!nodeExists(parentNode) || !nodeExists(childNode)) {
            throw new IllegalArgumentException("Provided Node not in graph.");
        }
        if(!edgeExists(parentNode, childNode, label)) {
            throw new IllegalArgumentException("No such edge exists in the graph.");
        } else {
            if(Edges.get(parentNode).get(childNode).size() == 1) {
                Edges.remove(parentNode);
            } else {
                Edges.get(parentNode).get(childNode).remove(label);
            }
        }
    }

    /**
     * returns a set of labels ordered alphabetically contained by an edge. Do not modify
     * the labels in the returned list.
     *
     * @param parentNode a parent node object
     * @param childNode a child node object
     * @return a set of labels contained by an edge from parentNode to childNode ordered by the natural
     * ordering of elements
     */
    public Set<E> getLabel(T parentNode, T childNode) {
        if(Edges.containsKey(parentNode)) {
            return new TreeSet<E>(Edges.get(parentNode).get(childNode));
        }
        return new TreeSet<E>();
    }

    /**
     * Returns list of Node for all the node contained in the graph. Do not modify
     * the nodes in the returned list.
     *
     * @return a list that contains all the nodes in the graph
     */
    public List<T> ListNodes() {
        List<T> copy = new ArrayList<T>(Nodes);
        return copy;
    }

    /**
     * Returns a list of children nodes for a given parent node. Do not modify
     * the nodes in the returned list.
     *
     * @param parentNode the Node of which to find children Nodes for
     * @spec.requires {parentNode is in the graph, and @code parentNode != null}
     * @return a list that contains nodes that are children to the given node, meaning nodes that have
     * a directed edge pointing to from the parentNode.
     */
    public List<T> ListChildren(T parentNode) {
        if(Edges.containsKey(parentNode)) {
            return new ArrayList<T>(Edges.get(parentNode).keySet());
        }
        return new ArrayList<T>();
    }
}
