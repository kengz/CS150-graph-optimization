import java.util.*;
import java.io.*;

/**
 * @author Wah Loon Keng
 */
/** A class that manages data-import */
public class Importer {
	private Scanner sc;
	private String filename = "filename.txt";
	private ArrayList<String> fileList;
	protected ArrayList<Integer> depotList, stationList, edgeList;

	/**
	 * Default constructor
	 */
	Importer() {
	}


	public static void main(String[] args) throws Exception{
		Importer sheep = new Importer();
		sheep.importGraph(1);
	}


	/**
	 * The only method called from this class: A shorthand to import graph, indexed 1-27; ordered, of files small-1,2,3, ..., large-1,2,3, and easy-hard on each.
	 * @param  single Which graph, of some size and difficulty, to import
	 */
	public void importGraph(int single) throws IOException {
		int batch = (single-1)/3;
		int nodeFile = (batch)*4 + (single-1)%3+1;
		int edgeFile = (batch+1)*4;
		// System.out.println(single + " " + nodeFile + " " + edgeFile);
		importGraph(nodeFile, edgeFile);
	}


	/**
	 * Import the nodes and edges needed for a graph.
	 * @param  nodeFile    Index on the fileList specifying the node file.
	 * @param  edgeFile    Index on the fileList specifying the edge file.
	 */
	private void importGraph(int nodeFile, int edgeFile) throws IOException{
		listFile();
		importNode(nodeFile);
		importEdge(edgeFile);
	}

	/**
	 * Get the file paths for all the data to be read
	 */
	protected void listFile() throws IOException {
		fileList = new ArrayList<String>();
		sc = new Scanner(new FileReader(filename));
		while(sc.hasNext()) {
			fileList.add(sc.nextLine());
		}
		sc.close();
	}

	/**
	 * Return the file path of data from fileList, index start from 1 for convenience.
	 * @param  index Of the path in fileList
	 * @return path Of the data location
	 */
	private String file(int index) {
		return fileList.get(index-1);
	}

	/**
	 * Import the nodes of a graph, of type depot or station; add to the corresponding list.
	 * @param  whichFile   Which file of the fileList to import from.
	 */
	protected void importNode(int whichFile) throws IOException {
		depotList = new ArrayList<Integer>();
		stationList = new ArrayList<Integer>();
		sc = new Scanner(new FileReader(file(whichFile)));

		while(sc.hasNext()) {
			String str = sc.next();
			if (str.equals("depot")) {
				// System.out.println("depot " + sc.nextInt());
				depotList.add(sc.nextInt());
			}
			else {
				// System.out.println("station " + sc.nextInt());
				stationList.add(sc.nextInt());
			}
		}
		sc.close();
	}


	/**
	 * Import the edge of a graph whenever valid, i.e. nodes exist; must be called after importNode()
	 * Edges are added in triples to edgeList: <node1>, <node2>, <weight>
	 * @param  whichFile Which file of the fileList to import from.
	 */
	protected void importEdge(int whichFile) throws IOException {
		edgeList = new ArrayList<Integer>();
		sc = new Scanner(new FileReader(file(whichFile)));
		while(sc.hasNext()) {
			int n1 = sc.nextInt();
			String flush = sc.next();
			int n2 = sc.nextInt();
			int weight = sc.nextInt();
			// if edge is valid, add to edgeList in triplets
			if (edgeValid(n1, n2)) {
				edgeList.add(n1);
				edgeList.add(n2);
				edgeList.add(weight);
				// System.out.println(n1 + " - " + n2 + " " + weight);
			}
		}
		sc.close();
	}


	/**
	 * Check if edge is valid
	 * @param  k1 Key of first node
	 * @param  k2 Key of second node
	 * @return true If both nodes present; false otherwise.
	 */
	private boolean edgeValid(int k1, int k2) {
		boolean has1 = (depotList.contains(k1) || stationList.contains(k1));
		boolean has2 = (depotList.contains(k2) || stationList.contains(k2));
		return (has1 && has2);
	}





}