import java.io.*;
import java.util.*;

/**
 * @author Wah Loon Keng
 */
/** This class compiles and run the project */
public class P3 {
	// private Builder builder;
	private PrintWriter printer, printerA, printerT;

	/** The main method, calls initialize() and run() */
	public static void main(String[] args) throws Exception{
		P3 sheep = new P3();
		sheep.initialize();

		long startTime = System.nanoTime();
		// sheep.runAndTime();
		sheep.runAndAnalyze();
		long stopTime = System.nanoTime();

		double time = (double)(stopTime - startTime) /(1000000000.);
		System.out.println("Time taken " + time);
	}

	/**
	 * Initialize the project and prepare I/O
	 */
	public void initialize() throws Exception {
		String outFile = "mapping.txt";
		String outFileA = "analysis.txt";
		// String outFileT = "timing.txt";
		printer = new PrintWriter(new FileWriter(outFile));
		printerA = new PrintWriter(new FileWriter(outFileA));
		// printerT = new PrintWriter(new FileWriter(outFileT));
	}




	/**
	 * Run and write the formatted project output to "mapping.txt"
	 * Analyze performance and print result to "analysis.txt"
	 */
	public void runAndAnalyze() throws Exception {
		Builder builder = new Builder();

		// do for each given data file
		for(int file = 1; file < 28; file++) {
			System.out.println("Batch: " + file);
			printer.println("Batch: " + file);

			// build NNGraph and analyze
			builder.build(file);
			// index data
			printerA.print(file + " ");
			analyze(builder);
			// print the paths
			printPaths(builder);

		}
		// close printerA after all analysis
		printer.close();
		printerA.close();
		
	}


	/**
	 * Run and write the formatted project output to "mapping.txt"
	 * Print runtime to "timing.txt"
	 */
	public void runAndTime() throws Exception {
		Builder builder = new Builder();

		// do for each given data file
		for(int file = 1; file < 28; file++) {
			System.out.println("Batch: " + file);
			printer.println("Batch: " + file);

			// print time to build and run
			builder.buildGraph(file);
			long startTime = System.nanoTime();
			// builder.build(file);
			builder.runNN();
			long stopTime = System.nanoTime();
			builder.formatOutput();
			double time = stopTime - startTime;
			printerT.println(file + " " + time);

			// print all paths for each batch
			printPaths(builder);

		}
		// close printers after all batches are done
		printer.close();
		printerT.close();
	}




	/**
	 * Method to run analysis on each batch of data (paths):
	 * <minSize> <maxSize> <avgSize> <minDist> <maxDist> <avgDist>
	 * where Size is the number of stations the path crosses, and Dist is the distance of the path
	 * @param builder Of an NNGraph.
	 */
	protected void analyze(Builder builder) {
		// variables for calculations
		int minSize, maxSize, minDist, maxDist,
		totSize, totDist, count;
		minSize = minDist = Integer.MAX_VALUE;
		maxSize = maxDist = totSize = totDist = count = 0;
		double avgSize = 0, avgDist = 0;

		for (ArrayList<Integer> tmp : builder.reducedPaths) {
			int size = tmp.size()-1;
			int dist = tmp.get(size);

			// update min max
			if (size < minSize)
				minSize = size;
			if (size > maxSize)
				maxSize = size;
			if (dist < minDist)
				minDist = dist;
			if (dist > maxDist)
				maxDist = dist;

			// update tot and count for avg
			count++;
			totSize += size;
			totDist += dist;
		}

		// finally, calculate averages
		avgSize = (double) totSize/count;
		avgDist = (double) totDist/count;

		// then print all 6 numbers
		printerA.println(minSize + " " + maxSize + " " + avgSize + " " + minDist + " " + maxDist + " " + avgDist);

	}



	/**
	 * Print the paths as formatted for project requirement:
	 * <depot> <station> <station> ... <station> <path distance>
	 * @param builder Of an NNGraph.
	 */
	protected void printPaths(Builder builder) {
		for (ArrayList<Integer> tmp : builder.reducedPaths) {
			for (int i : tmp) {
				// print key of each vertex
				System.out.print(i + " ");
				printer.print(i + " ");
			}
			System.out.print("\n");
			printer.print("\n");
		}
		System.out.print("\n");
		printer.print("\n");
	}




}


