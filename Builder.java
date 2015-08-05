import java.util.*;

/**
 * @author Wah Loon Keng
 */
/** The class that builds a graph from given data, and builds its nearest-neighbor paths */
public class Builder {
	private Importer importer;
	protected NNGraph<Integer, Integer> graph;
	protected ArrayList<ArrayList<Integer>> reducedPaths;


	/**
	 * Constructor.
	 */
	Builder() {
		reducedPaths = new ArrayList<ArrayList<Integer>>();
	}


	/**
	 * The single primary method called in the class.
	 * From a specified data, build the graph and its Nearest-Neighbor paths, i.e. run all the methods from importing to building the NNGraph.
	 * @param  whichGraph The index of the data input.
	 * @return reducedPaths The formatted NN-paths with distances at the last entry.
	 */
	public ArrayList<ArrayList<Integer>> build(int whichGraph) throws Exception {
		buildGraph(whichGraph);
		return buildNN();
	}



	/**
	 * Build a specified graph.
	 * @param  whichGraph Index of the graph data.
	 * @return graph Built from the data.
	 */
	protected NNGraph<Integer,Integer> buildGraph(int whichGraph) throws Exception {
		importGraph(whichGraph);
		// reset
		graph = new NNGraph<Integer, Integer>();
		// add depot nodes
		for (int key : importer.depotList)
			graph.addNode(key, key, 1);
		// add station nodes
		for (int key : importer.stationList){
			graph.addNode(key, key, -1);
		}
		// add valid edges
		Iterator<Integer> itr = importer.edgeList.iterator();
		while(itr.hasNext()) {
			int k1 = itr.next();
			int k2 = itr.next();
			int weight = itr.next();
			graph.addEdge(k1, k2, weight);
		}

		return graph;
	}


	/**
	 * Build the NN-Graph and format the output.
	 * @return reducedPaths Formatted paths with distances at the last entry.
	 */
	protected ArrayList<ArrayList<Integer>> buildNN() {
		runNN();
		return formatOutput();
	}


	/**
	 * Build the NN-Graph by calling the runNN() from NNGraph class.
	 * This is the method timed for studying the performance of algorithm.
	 * @return reducedPaths The NN-paths.
	 */
	protected ArrayList<ArrayList<Integer>> runNN() {
		reducedPaths = graph.runNN();
		return reducedPaths;
	}

	/**
	 * Format the output for project requirement: 
	 * <depot> <station> ... <station> <path distance>
	 * append each path distance to the end of the path list.
	 * @return reducedPaths After formatted.
	 */
	protected ArrayList<ArrayList<Integer>> formatOutput() {
		for (ArrayList<Integer> tmpPath : reducedPaths) {
			// reverse path to start from depot
			Collections.reverse(tmpPath);
			// add path distance of end of list
			int distance = graph.pathLength(tmpPath);
			tmpPath.add(distance);
		}
		return reducedPaths;
	}


	/**
	 * Import a specified graph data.
	 * @param  whichGraph Index of the graph data.
	 */
	private void importGraph(int whichGraph) throws Exception {
		importer = new Importer();
		importer.importGraph(whichGraph);
	}


}