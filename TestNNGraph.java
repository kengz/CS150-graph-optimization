import junit.framework.TestCase;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;


public class TestNNGraph extends TestCase {
  private NNGraph<Integer, Integer> graph, graphAnew;
  Node<Integer, Integer> station1, station2, station3, depot4, depot5;
  ArrayList<ArrayList<Integer>> listOfPaths;
  ArrayList<Integer> p1, p2, p3, p4, p5;
  private int start = 1, end = 10, size = end-start;
  
  public void setUp() {
    graphAnew = new NNGraph<Integer, Integer>();
//    setup a sample graph
    graph = new NNGraph<Integer, Integer>();
//    add stations;
    graph.addNode(1, 1, -1);
    graph.addNode(2, 2, -1);
    graph.addNode(3, 3, -1);
//    depot
    graph.addNode(4, 4, 1);
    graph.addNode(5, 5, 1);
//    add edge: node 1-2-4-3-5
    graph.addEdge(1, 2, 10);
    graph.addEdge(2, 4, 20);
    graph.addEdge(4, 3, 50);
    graph.addEdge(3, 5, 5);
    
    station1 = graph.getNode(1);
    station2 = graph.getNode(2);
    station3 = graph.getNode(3);
    depot4 = graph.getNode(4);
    depot5 = graph.getNode(5);
    
    
//    setup sample pathlists
    listOfPaths = graph.listOfPaths;
    p1 = new ArrayList<Integer>();
    p2 = new ArrayList<Integer>();
    p3 = new ArrayList<Integer>();
    p4 = new ArrayList<Integer>();
    p5 = new ArrayList<Integer>();
    
    for(int i = 1; i < 6; i++) {
      p1.add(-i);
      p2.add(i);
      p3.add(i);
      p4.add(i);
      p5.add(i);
    }
    p3.remove(0);
    p4.remove(0); p4.remove(0);
    p5.remove(0); p5.remove(0); p5.remove(0); p5.remove(0);
    p5.add(0, -1);
    listOfPaths.add(p1); listOfPaths.add(p2); listOfPaths.add(p3); listOfPaths.add(p4); listOfPaths.add(p5);
//    thus the paths that represent all possible scenarios are:
//    p1: -1, -2, -3, -4, -5; is a distinct list
//    p2: 1, 2, 3, 4, 5; is super-list of p2, p3
//    p3: 2, 3, 4, 5; sublist of p1
//    p4: 3, 4, 5; sublist of p1
//    p5: -1, 5; distinct list, has some overlap with p1
  }
  
  
  
  public void testAddNode() {
    for (int i = start; i < end; i++) {
//      add Stations, with key = ele = i
      Assert.assertTrue(graphAnew.addNode(i, i, -1));
      Assert.assertTrue(graphAnew.getNode(i).type() == -1);
//      ensure stationsToCompute is updated correctly (accounted for duplicates too)
      Assert.assertTrue(graphAnew.stationsToCompute.size() == i);
//      allows no duplicates, shall return false
      Assert.assertFalse(graphAnew.addNode(i, i, -1));
      
//      add Depots, with key = ele = -i
      Assert.assertTrue(graphAnew.addNode(-i, -i, 1));
      Assert.assertTrue(graphAnew.getNode(-i).type() == 1);
//      allows no duplicates, shall return false
      Assert.assertFalse(graphAnew.addNode(-i, -i, 1));
      
//      and check the number of vertices is correct
      Assert.assertTrue(graphAnew.getSizeV() == 2*i);
    }
    
  }
  
  
  
  
  
  public void testPathReduce() {    
    ArrayList<ArrayList<Integer>> reduced = graph.pathReduce();
//    after reduce, list is populated by the optimal paths p1, p2, p5
    Assert.assertTrue(graph.reducedPaths.size() == 3);
    Assert.assertTrue(reduced.get(0).equals(p1));
    Assert.assertTrue(reduced.get(1).equals(p2));
    Assert.assertTrue(reduced.get(2).equals(p5));
  }
  
  
  
  
  public void testPollPathsOfSameDepot() {
    Assert.assertTrue(listOfPaths.size() == 5);
    
//    first poll, remove first path with depot -5 off the list, add to buffer
    ArrayList<ArrayList<Integer>> buffer1 = graph.pollPathsOfSameDepot();
//    verify path is indeed removed from listOfPaths
    Assert.assertTrue(listOfPaths.size() == 4);
//    ensure the only path polled is indeed p1, with depot (last ele) key = -5
    Assert.assertTrue(buffer1.size() == 1);
    Assert.assertTrue(buffer1.get(0).equals(p1));
    Assert.assertTrue(graph.endPtOf(buffer1.get(0)) == -5);
    
//    reset
    graph.buffer.clear();
    

//    second poll, remove all paths with depot 5 off, add to buffer
    ArrayList<ArrayList<Integer>> buffer2 = graph.pollPathsOfSameDepot();
//    verify paths are indeed removed from listOfPaths
    Assert.assertTrue(listOfPaths.size() == 0);
    Assert.assertTrue(buffer2.size() == 4);
    
//    ensure the paths polled are indeed correct, with depot key = 5    
    Assert.assertTrue(buffer2.get(0).equals(p2));
    Assert.assertTrue(graph.endPtOf(buffer2.get(0)) == 5);
    Assert.assertTrue(buffer2.get(1).equals(p3));
    Assert.assertTrue(graph.endPtOf(buffer2.get(1)) == 5);
    Assert.assertTrue(buffer2.get(2).equals(p4));
    Assert.assertTrue(graph.endPtOf(buffer2.get(2)) == 5);
    Assert.assertTrue(buffer2.get(3).equals(p5));
    Assert.assertTrue(graph.endPtOf(buffer2.get(3)) == 5);
    
  }
  
  
  
  
  public void testDumpSublist() {
//    first poll and dump,
    graph.pollPathsOfSameDepot();
    ArrayList<ArrayList<Integer>> buffer3 = graph.dumpSublist();
//    verify indeed the single path p1 remains
    Assert.assertTrue(buffer3.size() == 1);
    Assert.assertTrue(buffer3.get(0).equals(p1));
    
//    reset
    graph.buffer.clear();
    
    //    second poll and dump,
    graph.pollPathsOfSameDepot();
    buffer3 = graph.dumpSublist();
//    verify indeed the sublists are dumped, only super-lists remain: p2 and p5
    Assert.assertTrue(buffer3.size() == 2);
    Assert.assertTrue(buffer3.get(0).equals(p2));
    Assert.assertTrue(buffer3.get(1).equals(p5));
    
    
  }
  
  
  
  
  
  public void testIsSublist() {
    
//    assert p2 indeed not sublist of p1
    Assert.assertFalse(graph.isSublist(p1, p2));
    
//    but p3, p4 are sublist of p1
    Assert.assertTrue(graph.isSublist(p2, p3));
    Assert.assertTrue(graph.isSublist(p2, p4));
//    p4 sublist of p3
    Assert.assertTrue(graph.isSublist(p3, p4));
    
//    and p5 not sublist of anyone.
    Assert.assertFalse(graph.isSublist(p1, p5));
    Assert.assertFalse(graph.isSublist(p2, p5));
    
  }
  
  
  
  
  
  
  public void testComputeAllStations() {
//    From the way the example is set up, shall have two paths
    Assert.assertTrue(graph.computeAllStations().size() == 2);
  }
  
  
  
  
  
  public void testFindClosestDepot() {
//    given the example graph, depot4 is closest to station1 and station2
    Assert.assertTrue(graph.findClosestDepot(station1).isDuplicate(depot4));
    Assert.assertTrue(graph.findClosestDepot(station2).isDuplicate(depot4));
//    while depot5 is closest to station 3
    Assert.assertTrue(graph.findClosestDepot(station3).isDuplicate(depot5));
  }
  
  
  
  
  
  
  
  public void testPathToDepot() {
//    short path from station1
    depot4 = graph.findClosestDepot(station1);
    ArrayList<Integer> path = graph.pathToDepot(depot4);
//    path goes thru node 1-2-4
    Assert.assertTrue(path.get(0) == 1);
    Assert.assertTrue(path.get(1) == 2);
    Assert.assertTrue(path.get(2) == 4);
    
//    short path from station2
    depot4 = graph.findClosestDepot(station2);
    path = graph.pathToDepot(depot4);
//    path goes thru node 2-4
    Assert.assertTrue(path.get(0) == 2);
    Assert.assertTrue(path.get(1) == 4);
    
//    short path from station3
    depot5 = graph.findClosestDepot(station3);
    path = graph.pathToDepot(depot5);
//    path goes thru node 3-5
    Assert.assertTrue(path.get(0) == 3);
    Assert.assertTrue(path.get(1) == 5);
    
    
  }
  
  
  
  
  
  public void testTypeTask() {
//    If add Station
    Assert.assertTrue(graphAnew.addNode(-2, -2, -1));
//    stationsToCompute size = 1 before removal
    Assert.assertTrue(graphAnew.stationsToCompute.size() == 1);
    Node<Integer, Integer> station = graphAnew.getNode(-2);
//    remove key from stationsToCompute and return false
    Assert.assertFalse(graphAnew.typeTask(station));
//    updated size = previousSize -1
    Assert.assertTrue(graphAnew.stationsToCompute.size() == 0);
    
//    If add Depot
    Assert.assertTrue(graphAnew.addNode(2, 2, 1));
//    Does nothing to stationsToCompute, assert size is still 0
    Assert.assertTrue(graphAnew.stationsToCompute.size() == 0);
    Node<Integer, Integer> depot = graphAnew.getNode(2);
//    ensure return true if depot is passed as parameter
    Assert.assertTrue(graphAnew.typeTask(depot));
    
  }
  
  
}
