import java.util.*;

/**
 * @author Wah Loon Keng
 */
/**
 * The Nearest-Neighbor Graph class; is essentially the Graph class, except with special find-path method.
 */
public class NNGraph<K, E> extends Graph<K, E> {
	protected ArrayList<K> stationsToCompute, tmpPath;
	protected ArrayList<ArrayList<K>> listOfPaths, buffer, reducedPaths;

	/** Default constructor */
	NNGraph() {
		super();
		stationsToCompute = new ArrayList<K>();
		listOfPaths = new ArrayList<ArrayList<K>>();
		reducedPaths = new ArrayList<ArrayList<K>>();
		buffer = new ArrayList<ArrayList<K>>();
	}

	/**
	 * Add a node to the graph; if type is station(-1); add the key to the list stationsToCompute.
	 * @param  k    Key of node.
	 * @param  e    Element of node.
	 * @param  type Type of node: 1: depot; -1: station; 0 unknown.
	 * @return true If successful; false otherwise;
	 */
	@Override
	public boolean addNode(K k, E e, int type) {
		boolean added = super.addNode(k, e, type);
		if (added && type == -1)
			addToCompute(k);
		return added;
	}
	/**
	 * Add keys of all stations to the list stationsToCompute.
	 * @param  k The key of station to be computed
	 * @return true If successful; false otherwise.
	 */
	private boolean addToCompute(K k) {
		return stationsToCompute.add(k);
	}




	/**
	 * Run the Nearest-Neighbor algorithm (see report).
	 * @return The paths in a Nearest-Neighbor Graph.
	 */
	public ArrayList<ArrayList<K>> runNN() {
		computeAllStations();
		return pathReduce();
	}


	/**
	 * Compute the path length in a path.
	 * @param  path Given
	 * @return pathlength Of the path.
	 */
	public int pathLength(ArrayList<K> path) {
		int distance = 0;
		K k1 = null, k2 = null;
		Iterator<K> itr = path.iterator();
		// initialize
		if (itr.hasNext())
			k1 = itr.next();
		// get edge weight for each adjacent pair
		while(itr.hasNext()) {
			k2 = itr.next();
			distance += getEdge(k1, k2);
			// move on, reset k1 = k2
			k1 = k2;
		}

		return distance;
	}




	/**
	 * The primary method to call for optimization after paths are computed.
	 * Find and list the reducedPaths, i.e. the nearest-neighborhood paths, from stations to depot (inverse), such that the total distance traveled is minimized, and the shortest-path from depot to station is ensured.
	 * For proof of algorithm, refer to the report.
	 * @return reducedPaths i.e. The nearest neighbors paths: the reduced paths optimized from many possibly overlapping shortest paths.
	 */
	public ArrayList<ArrayList<K>> pathReduce() {
		while(!listOfPaths.isEmpty()){
			pollPathsOfSameDepot();
			dumpSublist();
			addToReducedPaths();
		}
		// remove isolated points, i.e. non-paths
		removeIsolated();
		return reducedPaths;
	}




	/**
	 * Remove empty paths, i.e. isolated station, from reducedPaths
	 */
	protected void removeIsolated() {
		Iterator<ArrayList<K>> itr = reducedPaths.iterator();
		while(itr.hasNext()) {
			if (itr.next().size() < 2) {
				// System.out.println("removing isolated");
				itr.remove();
			}
		}
	}




	/**
	 * From the listOfPaths, the list of all searched shortest paths, poll all paths that end at the same depot.
	 * @return buffer The list of paths ending at the same depot, to ready for optimization via dumpSublist()
	 */
	protected ArrayList<ArrayList<K>> pollPathsOfSameDepot() {
		// fail-safe control
		if(!listOfPaths.isEmpty()){
			Iterator<ArrayList<K>> itr = listOfPaths.iterator();
			// remove first path from listOfPaths, identify the depot
			ArrayList<K> headPath = itr.next();
			K depotKey = endPtOf(headPath);
			itr.remove();
			// insert to an empty buffer
			insertToBuffer(headPath);

			// scan all next members in listOfPaths
			while(itr.hasNext()) {
				ArrayList<K> tmp = itr.next();
				// if has same depot endPt, remove and insert to buffer
				if (sameEnd(tmp, depotKey)) {
					insertToBuffer(tmp);
					itr.remove();
				}
			}
		}
		return buffer;
	}


	/**
	 * Insert a path of the same endpoint (depot) to buffer, in decreasing list length. Uses Insertion sort.
	 * @param newPath The path to be inserted
	 */
	private void insertToBuffer(ArrayList<K> newPath) {
		int newSize = newPath.size();
		int index = 0;
		for (ArrayList<K> tmp : buffer) {
			if (newSize < tmp.size())
				index++;
			else
				break;
		}
		buffer.add(index, newPath);
	}

	/**
	 * The key method to optimize for reducedPaths. Takes a buffer (paths ending at the same depot), reduce any paths that are sublists of some bigger paths.
	 * See the proof of lemma in report for how it works.
	 * @return buffer The reduced buffer with no redundant paths.
	 */
	protected ArrayList<ArrayList<K>> dumpSublist() {
		int index = 0;

		while (!buffer.isEmpty()) {
			// the next biggest path
			ArrayList<K> currentPath = buffer.get(index);
			// iterator starts here at the path
			ListIterator<ArrayList<K>> itr = buffer.listIterator(index);
			// move forward on step
			itr.next();
			// start checking the remaining paths behind it
			while (itr.hasNext()) {
				// if is sublist, remove from buffer
				if (isSublist(currentPath, itr.next()))
					itr.remove();					
			}
			// repeat for next unremoved path
			index++;
			// break whileloop after the last element
			if (index >= buffer.size())
				break;
			
		}

		return buffer;
	}


	/**
	 * Add the optimized, reduced buffer to reducedPaths, and clear the content.
	 */
	private void addToReducedPaths() {
		for (ArrayList<K> tmp : buffer) {
			reducedPaths.add(tmp);
		}
		// reset buffer for next operation
		buffer = new ArrayList<ArrayList<K>>();
	}




	/**
	 * Determine if a path is a sublist of another.
	 * @param  bigPath   The potential super-path (larger list-length)
	 * @param  smallPath The potential sub-path, (smaller list-length)
	 * @return true If smallPath is a sublist of bigPath; false otherwise.
	 */
	protected boolean isSublist(ArrayList<K> bigPath, ArrayList<K> smallPath) {
		boolean isSub = true;
		for (K tmp : smallPath) {
			if (!bigPath.contains(tmp)) 
				isSub = false;
		}
		return isSub;
	}

	// check if path ends at the same depot
	/**
	 * Check if a path ends at a depot
	 * @param  path     The path to check.
	 * @param  depotKey Key of the depot.
	 * @return true If indeed; false otherwise.
	 */
	private boolean sameEnd(ArrayList<K> path, K depotKey) {
		return (endPtOf(path) == depotKey);
	}

	/**
	 * Return the end point of a path found; can detect error in given data, i.e. isolated stations.
	 * @param  path A path that ends at a depot
	 * @return key Of the depot (at the end of list); null If station is isolated.
	 */
	protected K endPtOf(ArrayList<K> path) {
		if (!path.isEmpty())
			return path.get(path.size()-1);
		else
			return null;
		
	}





	/**
	 * The primary method called to compute paths from all stations.
	 * While stationsToCompute is not empty, poll from it, find the path to closest depot, add the path to listOfPaths, and remove from stationsToCompute the stations that are visited in the path.
	 * @return listOfPaths The list of all short paths from station to the closest depot.
	 */
	public ArrayList<ArrayList<K>> computeAllStations() {
		// reset
		listOfPaths = new ArrayList<ArrayList<K>>();

		while (!stationsToCompute.isEmpty()) {
			K sourceKey = stationsToCompute.get(0);
			Node<K,E> source = getNode(sourceKey);
			Node<K,E> closestDepot = findClosestDepot(source);
			ArrayList<K> path = pathToDepot(closestDepot);
			addPathToList(path);
		}

		return listOfPaths;
	}


	/**
	 * Given a source station, perform Dijkstra's search and terminate at the first (closest) depot found.
	 * Dijkstra's shortest path algorithm
	 * @see <a href="http://www.algolist.com/code/java/Dijkstra's_algorithm">Premodified algorithm</a>
	 * @param station The station vertex to compute path from.
	 * @return  closestDepot The closest depot to this station
	 */
	protected Node<K,E> findClosestDepot(Node<K,E> station) {
		PriorityQueue<Node<K,E>> vertexQ = new PriorityQueue<Node<K,E>>();
		// resets for each method run		
		for (Node<K,E> v : listV) {
			v.checked = false;
			v.previous = null;
			v.minDistance = Integer.MAX_VALUE;
		}
		// initialize
		station.minDistance = 0;
		vertexQ.add(station);
		Node<K, E> u = new Node<K,E>();

		while(!vertexQ.isEmpty()) {
			u = vertexQ.poll();
			u.check();
			// control: compute until the first (closest) depot
			if (typeTask(u))
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

		// System.out.println("\nshortest dist bet " + station.getKey() + " and " + aim.getKey() + " is " + aim.minDistance);
		// return the first nearest depot; ensure is indeed a depot
		if (u.type() == 1)
			return u;
		else
			return null;
	}

	/**
	 * Called within findClosestDepot().
	 * The helper method to perform task based on node type:
	 * If u is station; remove from stationsToCompute its key and return false; if is depot, return true; false otherwise.
	 * @param u The node polled from vertexQ in compute
	 * @return true If u is a depot; false otherwise.
	 */
	protected boolean typeTask(Node<K, E> u) {
		int type = u.type();
		// if is station, remove from stationsToCompute
		if (type == -1) {
			stationsToCompute.remove(u.getKey());
			return false;
		}
		// if is depot; return true to break computation
		else if (type == 1)
			return true;
		// if type unknown
		else
			return false;
	}


	/**
	 * Called immediately after findClosestDepot(station).
	 * Once the closest depot to a station is found, record the path in terms of key of the nodes.
	 * @param closestDepot The closest depot to a station; given from findClosestDepot()
	 * @return path The path from station to the closest depot; as a list of the keys of vertices visited.
	 */
	protected ArrayList<K> pathToDepot(Node<K,E> closestDepot) {
		tmpPath = new ArrayList<K>();
		Node<K,E> tmp = closestDepot;
		while(tmp != null) {
			// System.out.println("added previous");
			tmpPath.add(tmp.getKey());
			tmp = tmp.previous;
		}
		// reverse the path to be correct
		Collections.reverse(tmpPath);
		return tmpPath;
	}

	/**
	 * Called immediately after pathToDepot().
	 * Once the path to closest depot is constructed, add it to the listOfPaths.
	 * @param  path The path from a station to a closest depot.
	 * @return boolean If successful; false otherwise.
	 */
	protected boolean addPathToList(ArrayList<K> path) {
		return listOfPaths.add(path);
	}






}