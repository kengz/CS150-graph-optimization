import java.util.*;

/**
 * @author Wah Loon Keng
 */
/** 
 * This is a basic graph class, with built-in shortest-path method based on Dijkstra's algorithm.
 * The class for an undirected graph, each vertex having a key and value.
 * This can easily be modified into a directed graph by changing the addEdge() method.
 */
public class Graph<K, E> {
	protected int sizeE;
	protected int pathLength = Integer.MAX_VALUE;
	protected ArrayList<Node<K,E>> pathFound;
	/** The adjacency matrix, entry = weight of edge */
	protected LinkedList<Node<K, E>> listV;

	/**
	 * Default constructor
	 * Initializes listV, the list of all vertices in the graph.
	 */
	Graph() {
		listV = new LinkedList<Node<K, E>>();
		pathFound = new ArrayList<Node<K,E>>();
	}

	/**
	 * Constructor, with first vertex added to the graph.
	 */
	Graph(K k, E e) {
		listV = new LinkedList<Node<K, E>>();
		pathFound = new ArrayList<Node<K,E>>();
		addNode(k, e);
	}

	/**
	 * @return size The number of vertices in graph
	 */
	public int getSizeV() {
		return listV.size();
	}
	/**
	 * @return sizeE The number of edges in graph
	 */
	public int getSizeE() {
		return sizeE;
	}

	/**
	 * @return iterator Of the adjacency list of this vertex.
	 */
	public Iterator<Node<K,E>> getItr() {
		return listV.listIterator(0);
	}



	/**
	 * add a new vertex with key k, allows no duplicates.
	 * @param  k Key of the vertex
	 * @param  e Element of the vertex
	 * @return true If successful; false otherwise(if duplicates found).
	 */
	public boolean addNode(K k, E e) {
		return addNode(k, e, 0);
	}

	/**
	 * add a new vertex with key k and type, allows no duplicates.
	 * @param  k Key of the vertex
	 * @param  e Element of the vertex
	 * @param  type Of the vertex: -1: station; 1: depot; 0 = unknown;
	 * @return true If successful; false otherwise(if duplicates found).
	 */
	public boolean addNode(K k, E e, int type) {
		if (getNode(k) != null) {
			return false;
		}
		else {
			Node<K, E> tmp = new Node<K, E>(k, e);
			tmp.setType(type);
			return listV.offer(tmp);
		}
	}

	/**
	 * Get the vertex with the specified key
	 * @param  k The key
	 * @return vertex With the key; null if not found.
	 */
	public Node<K,E> getNode(K k) {
		// search algo can be improved
		for (Node<K, E> tmp : listV) {
			if (tmp.getKey() == k) {
				return tmp;
			}
		}
		return null;
	}


	/**
	 * add a weighted edge from node with key k1 to node with key k2.
	 * @param  k1 node from
	 * @param  k2 node to
	 * @param  w  Weight of the edge added
	 * @return true If successful; false otherwise(if duplicates found)
	 */
	public boolean addEdge(K k1, K k2, int w) {
		Node<K,E> source, target;
		// search the graph for node containing the keys
		source = getNode(k1);
		target = getNode(k2);
		// if find vertices with k1 and k2, then add edge (two ways)
		if (source != null && target != null) {
			// modify this for directed graph
			boolean added = (source.addEdge(target, w) && target.addEdge(source, w) );
			// reverse path, directed
			// boolean added = (target.addEdge(source, w) );
			if (added) {
				// update number of edges if added
				sizeE++;
			}
			return added;
		}
		else {
			// invalid specification of source and target
			return false;
		}
	}

	/**
	 * Get the edge weight between vertices specified
	 * @param  k1 Key of source vertex
	 * @param  k2 Key of target adjacent vertex
	 * @return weight If edge exists; -1 otherwise.
	 */
	public int getEdge(K k1, K k2) {
		Node<K,E> source, target;
		source = getNode(k1);
		target = getNode(k2);
		for (Edge<K,E> tmp : source.adj) {
			if (tmp.target.isDuplicate(target)) {
				// return weight if edge found
				return tmp.weight;
			}
		}
		// if no edge found, return -1
		return -1;
	}


	/**
	 * Prints to System.out the path found in the format:
	 * <source> <nextVertex> ... <target> <pathLength>
	 * @return false If not path found, nothing is printed; true otherwise
	 */
	public boolean printPath() {
		// no path; contains no vertex or just the target vertex
		if (pathFound.size() == 0 || pathFound.size() == 1) {
			System.out.println("No path found");
			return false;
		}
		// print the path in format
		// System.out.print(pathFound.get(0).getKey());
		for (int i = 0; i < pathFound.size()-1; i++) {
			K currentKey, nextKey;
			currentKey = pathFound.get(i).getKey();
			nextKey = pathFound.get(i+1).getKey();
			// System.out.print(" -" + getEdge(currentKey, nextKey) + "- " + nextKey);
			// System.out.print(" " + nextKey);
		}
		// System.out.println(" " + pathLength);

		return true;
	}

	/**
	 * Method to return pathlength; can only be called after findShortestPath()
	 * @return pathLength If there is shortest path; MAX_VALUE otherwise.
	 */
	public int pathLength() {
		return pathLength;
	}



	/**
	 * Dijkstra's algorithm for the shortest path, calls the two private methods computePaths() and shortestPathTo().
	 * @param  k1 Key of the source graph vertex
	 * @param  k2 Key of the target graph vertex 
	 * @return shortest-path If there exists, the ArrayList containing path and vertices along it; from source to target; null otherwise.
	 */
	public ArrayList<Node<K,E>> findShortestPath(K k1, K k2) {
		Node<K,E> source, target;
		source = getNode(k1);
		target = getNode(k2);
		if (source == null || target == null) {
			System.out.println("Specified vertices do not exist");
			return null;
		}
		
		pathLength = computePaths(source, target);
		pathFound = shortestPathTo(target);
		// System.out.println("head is " + pathFound.get(0).getKey());
		printPath();
		return pathFound;
	}

	/**
	 * Dijkstra's shortest path algorithm
	 * @see <a href="http://www.algolist.com/code/java/Dijkstra's_algorithm">Premodified algorithm</a>
	 * @param source The source vertex to compute path from.
	 */
	protected int computePaths(Node<K,E> source, Node<K,E> aim) {
		PriorityQueue<Node<K,E>> vertexQ = new PriorityQueue<Node<K,E>>();
		// resets for each method run		
		for (Node<K,E> v : listV) {
			v.checked = false;
			v.previous = null;
			v.minDistance = Integer.MAX_VALUE;
		}
		// initialize
		source.minDistance = 0;
		vertexQ.add(source);

		while(!vertexQ.isEmpty()) {
			Node<K, E> u = vertexQ.poll();
			u.check();
			// control: method ends at aim-vertex
			if (u.isDuplicate(aim))
				break;

			// do for each adjacent vertex
			for (Edge<K,E> tmp : u.adj) {
				// the other end of edge
				Node<K, E> v = tmp.target;
				if (!v.checked) {
					int distanceThru = u.minDistance + tmp.weight;
					if (distanceThru < v.minDistance) {
						// update minDistance & previous
						vertexQ.remove(v);
						v.minDistance = distanceThru;
						v.previous = u;
						vertexQ.add(v);
					}
				}
			}

		}

		System.out.println("\nshortest dist bet " + source.getKey() + " and " + aim.getKey() + " is " + aim.minDistance);
		// if return finite val, implies path is found
		return aim.minDistance;
		
	}

	/**
	 * Method to call immediately after computePaths()
	 * Picks out the path from the source specified in the preceeding method to the target.
	 * @param target Vertex from the source
	 * @return path The ArrayList containing path and vertices along it; from source to target.
	 */
	// since exists loops, need to add in source as var
	protected ArrayList<Node<K,E>> shortestPathTo(Node<K,E> target) {
		ArrayList<Node<K,E>> path = new ArrayList<Node<K,E>>();
		// starting from target, traverse back to source
		Node<K,E> tmp = target;
		while(tmp != null) {
			path.add(tmp);
			tmp = tmp.previous;
			// System.out.println(tmp.getKey());
			// System.out.println("test inf recur");
		}
		// reverse the path to be correct
		Collections.reverse(path);
		return path;
	}




}






/**
 * The Vertex / Node class for the graph.
 * Implements comparable, ordering by shortest path distance.
 */
class Node<K, E> implements Comparable<Node<K,E>>{
	private K key;
	private E ele;
	/** Type of node: 1 depot; -1 station; 0 unknown */
	private int type;
	/** The adjacency list, entry = weight of edge */
	protected LinkedList<Edge<K, E>> adj;

	// migrate from graph to each node having its searched result
	protected int pathLength = Integer.MAX_VALUE;
	protected ArrayList<Node<K,E>> pathSearched;

	/**
	 * Comparable variables used by PriorityQueue and Dijkstra algorithm
	 */
	protected int minDistance = Integer.MAX_VALUE;
	protected boolean checked = false;
	protected Node<K, E> previous;

	/**
	 * Comparable method for min-heap
	 * @param  other Vertex being compared to
	 * @return minDistance Which minDistance is lower.
	 */
	public int compareTo(Node<K, E> other) {
		return Long.compare(minDistance, other.minDistance);
	}

	/**
	 * Helper method for Dijkstra's algorithm: mark a vertex as checked when polled from the PriorityQueue
	 */
	protected boolean check() {
		checked = true;
		return checked;
	}

	Node() {}

	/**
	 * Constructor.
	 * Creates a vertex with its adjacency list.
	 * @param  k Key of the vertex.
	 * @param  e Element of the vertex.
	 */
	Node(K k, E e) {
		this.key = k;
		this.ele = e;
		adj = new LinkedList<Edge<K,E>>();
	}

	// modular part
	/**
	 * Set the type of node:
	 * @param  type If depot, +1; If station; -1; If unknown, 0.
	 * @return true If successful; false otherwise.
	 */
	public boolean setType(int type) {
		if (type == -1 || type == 1 || type == 0) {
			this.type = type;
			return true;
		}
		else
			return false;
	}

	/**
	 * @return type Of the node.
	 */
	public int type() {
		return type;
	}





	/**
	 * @return key Of this vertex.
	 */
	public K getKey() {
		return key;
	}

	/**
	 * @return ele Of this vertex.
	 */
	public E getEle() {
		return ele;
	}

	/**
	 * @return degree Of this vertex.
	 */
	public int getDegree() {
		return adj.size();
	}

	/**
	 * Determine if two vertices are identical by their key values
	 * @param other Vertex to be compared to.
	 * @return true If keys are identical; false otherwise.
	 */
	public boolean isDuplicate(Node<K,E> other) {
		if (other.getKey() == this.key)
			return true;
		else
			return false;
	}

	/**
	 * Get the adjacent vertex with the specified key
	 * @param  k The key
	 * @return vertex With the key; null if not found.
	 */
	public Node<K,E> getAdjNode(K k) {
		// search algo can be improved
		for (Edge<K, E> tmp : adj) {
			if (tmp.target.getKey() == k) {
				return tmp.target;
			}
		}
		return null;
	}

	/**
	 * @return iterator Of the adjacency list of this vertex.
	 */
	public Iterator<Edge<K,E>> getItr() {
		return adj.listIterator(0);
	}

	/**
	 * add an edge to this vertex, allows no duplicates;
	 * shorter edge replaces the longer edge.
	 * @param  other The other vertex of this edge
	 * @param  w The weight of the edge.
	 * @return true If successful; false If duplicates found.
	 */
	protected boolean addEdge(Node<K, E> other, int w) {
		boolean found = false;
		for (Edge<K, E> tmp : adj) {
			// if existing edge longer, replace it
			if (other.isDuplicate(tmp.target)){
				found = true;
				if (w < tmp.weight) {
					tmp.replace(w);
					return true;
				}
			}
		}
		if (!found) {
			Edge<K,E> tmp = new Edge<K, E>(other, w);
			return adj.offer(tmp);
		}
		else
			return false;

	}


}





/** The Edge helper inner class for the adjacency linked list */
class Edge<K, E>{
	protected Node<K, E> target;
	protected int weight;

	/**
	 * Constructor
	 * @param other The other vertex of this edge
	 * @param  weight Of the edge.
	 */
	Edge(Node<K, E> target, int weight) {
		this.target = target;
		this.weight = weight;
	}

	/**
	 * Replace the edge weight is the added edge is shorter than the current one.
	 * @param  newWeight The new weight added (ensured shorter)
	 * @return weight The updated weight
	 */
	public int replace(int newWeight) {
		this.weight = newWeight;
		return weight;
	}

}

