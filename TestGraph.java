import junit.framework.TestCase;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class TestGraph extends TestCase {
  private Graph<Integer, Integer> graph, graphAnew;
  private int start = 1, end = 10, size = end-start;
  
  public void setUp() {
    graphAnew = new Graph<Integer, Integer>();
    graph = new Graph<Integer, Integer>();
    for (int i = start; i < end; i++) {
//      add vertices
      graph.addNode(i, -i);
    }
    
//    System.out.println("mehehe");
    
  }
  
  
  public void testaddNode() {
    for (int i = start; i < end; i++) {
//      add a new node to the graph, of key i, element -i
      Assert.assertTrue(graphAnew.addNode(i, -i));
//      assure type is unassigned = 0
      Assert.assertTrue(graphAnew.getNode(i).type() == 0);
//      allows no duplicates, shall return false
      Assert.assertFalse(graphAnew.addNode(i, -i));
      
//      and check the number of vertices is correct
      Assert.assertTrue(graphAnew.getSizeV() == i);
    }
    
  }
  
  
  public void testaddNodewithType() {
    for (int i = start; i < end; i++) {
//      add a new node to the graph, of key i, element -i, type 1 = depot
      Assert.assertTrue(graphAnew.addNode(i, -i, 1));
      Assert.assertTrue(graphAnew.getNode(i).type() == 1);
      
//      add a new node to the graph, of key -i, element -i, type -1 = station      
      Assert.assertTrue(graphAnew.addNode(-i, -i, -1));
      Assert.assertTrue(graphAnew.getNode(-i).type() == -1);
      
//      allows no duplicates, shall return false
      Assert.assertFalse(graphAnew.addNode(i, -i));
    }
    
  }
  
  
  
  public void testGetNode() {
    for (int i = start; i < end; i++) {
//      check node is gotten with the key is correct, i.e. it exists; with the correct element value
      Assert.assertTrue(graph.getNode(i).getEle() == -i);
//      check null is returned if no such node exists
      Assert.assertTrue(graph.getNode(-i) == null);
    }
  }
  
  
  
  public void testaddEdge() {
//    System.out.println(graph.getSizeV());
    for (int i = start; i < end-1; i++) {
//      add edge for vertices in graph, and check the size of graph is correctly updated
      Assert.assertTrue(graph.addEdge(i, i+1, 2*i) );
      Assert.assertTrue(graph.getSizeE() == i);
      
//      allows no duplicates; thus shall return false
      Assert.assertFalse(graph.addEdge(i, i+1, 2*i) );
//      specifying inexistent vertices return false too
      Assert.assertFalse(graph.addEdge(null, null, 2*i) );
     
//      Note, edge replacement is tested next in getEdge()
    }
    
  }
  
  
  
  
  public void testGetEdge() {
    for (int i = start; i < end-1; i++) {
//      add edge for vertices
      Assert.assertTrue(graph.addEdge(i, i+1, 2*i) );
//      retrieve and check is of the correct length indeed
      Assert.assertTrue(graph.getEdge(i, i+1) ==  2*i );
      
//      Test edge replacement:
//      add new edge that's longer; shall just be ignored, return false
      Assert.assertFalse(graph.addEdge(i, i+1, 3*i) );
//      check is ignored indeed.
      Assert.assertTrue(graph.getEdge(i, i+1) ==  2*i );
      
//      add a new edge that is shorter, shall be replaced
      Assert.assertTrue(graph.addEdge(i, i+1, i) );
//      check if indeed replaced
      Assert.assertTrue(graph.getEdge(i, i+1) ==  i );
      
    }
    
    
  }
  
  
  
  
  
//  3 cases tested: path exist, path doesnt exist, shortest path exist out of many paths
  public void testFindShortestPath() {
//    two cases tested below: path exist, path doesnt exist. Now test the correct return values
    
//    test the valid case
    for (int i = start; i < end-1; i++) {
//      add simple edges, length 1 for increasing keyvalues, i.e. forming a line
      Assert.assertTrue(graph.addEdge(i, i+1, 1) );
      
//      verify pathFound and pathLength are updated correctly each time
//      shortest path is the sum of edges from 1 to i, has i nodes
      Assert.assertTrue(graph.findShortestPath(1, i).size() == i);
//      and i-1 edges, sum = i-1
      Assert.assertTrue(graph.pathLength() == i-1);
    }
    
    
//    test the no-path case
    graph.addNode(-1, 1);
    for(int i = start; i < end - 2; i++) {
//      ensure the list of path(nodes) for no-path has only the target node.
       Assert.assertTrue(graph.findShortestPath(i, -1).size() == 1);
       Assert.assertTrue(graph.findShortestPath(i, -1).get(0).getKey() == -1);
//       and the path distance is max-val indeed
       int expDist = Integer.MAX_VALUE;
       int dist = graph.pathLength();
       Assert.assertTrue(dist == expDist);
      
    }
      
      
    
  }
  
  
  
  
  
  public void testComputePaths() {
//    path exists: add edges first
    testaddEdge();
    for(int i = start; i < end - 2; i++) {
    int dist = graph.computePaths(graph.getNode(i), graph.getNode(i+2));
//    The expected shortest distance the way graph is set up
    int expDist = 2*i+2*(i+1);
//    verify they agree
    Assert.assertTrue(dist == expDist);
    }
    
//    path does not exist
//    add a special node not connected to anything via edges
    graph.addNode(-1, 1);
    for(int i = start; i < end - 2; i++) {
    int dist =  graph.computePaths(graph.getNode(i), graph.getNode(-1));
//    no path exist, should give infinity (max value)
    int expDist = Integer.MAX_VALUE;
    Assert.assertTrue(dist == expDist);
    
    }
    
  }
  
  
  
  public void testShortestPathTo() {
//    note, normal path distance tested above. here tests only shortest path
    
    for (int i = start+1; i < end ; i++) {
//    add shortcut, from source to all, distance = 1; shortest path to all should yield 1
      Assert.assertTrue(graph.addEdge(1, i, 1) );
      graph.computePaths(graph.getNode(1), graph.getNode(i));
      int size = graph.shortestPathTo(graph.getNode(i)).size();
      
//      path arraylist size shd be 2, contains only source and target
      Assert.assertTrue(size == 2);
//      distance shall be 1, the shortcut
      int dist = graph.getNode(i).minDistance;
      Assert.assertTrue(dist == 1);
      
    }
    
  }
  
  
  
  
  public void testIsDuplicate() {
    for(int i = start; i < end-1; i++) {
//      test two vertices (drawn from separate graphs) are equal by their key values
      graphAnew.addNode(i,0);
      Assert.assertTrue(graph.getNode(i).isDuplicate(graphAnew.getNode(i)));
      
//      and different by key values (one is negative)
      graphAnew.addNode(-i,0);
      Assert.assertFalse(graph.getNode(i).isDuplicate(graphAnew.getNode(-i)));
      
  }
    
  }
  
  
  
  
  public void testGetAdjNode() {
//    call the previous method to add edge
    testaddEdge();
    
    for(int i = start; i < end-1; i++) {
//      get the adjacent node, verify correct by checking its element
      int adjEle = graph.getNode(i).getAdjNode(i+1).getEle();
      Assert.assertTrue(adjEle == -(i+1));
    }
    
  }
  
  
  
  
  
  
  
 
  
}
