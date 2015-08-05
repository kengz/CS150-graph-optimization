import junit.framework.TestCase;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;


public class TestBuilder extends TestCase {
  private Builder builder;
  
  
  public void setUp() {
    builder = new Builder();
  }
  
  
  public void testBuildGraph() throws Exception {
//    test build the first 3 graphs
    for(int i = 1; i < 4; i++) {
    NNGraph<Integer, Integer> graph = builder.buildGraph(i);
//    ensure a new graph is built, and all lists are not empty
    Assert.assertTrue(graph.getSizeV() > 0);
    Assert.assertTrue(graph.stationsToCompute.size() > 0);
    Assert.assertTrue(graph.getSizeE() > 0);
    }
  }
  
  
}
