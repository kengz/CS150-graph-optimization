import junit.framework.TestCase;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;


public class TestImporter extends TestCase {
  private Importer importer;
  
  public void setUp() throws Exception {
    importer = new Importer();
    importer.listFile();
  }
  
//  Note that this class handles IO exclusively, 
//  thus correctness of methods is tested manually by comparison with input data
//  tests here are to ensure no basic error, i.e. ensure there is indeed import done
  
  
//  Note this test on all 27 data files takes some time
  public void testImportGraph() throws Exception {
//    test all 27 data files
    for(int i = 1; i <= 27; i++) {
    importer.importGraph(i);
//    ensure there are nodes and edges imported
    Assert.assertTrue(importer.depotList.size() > 0);
    Assert.assertTrue(importer.stationList.size() > 0);
    Assert.assertTrue(importer.edgeList.size() > 0);
    }
  }
  
  
  public void testImportNode() throws Exception {
    importer.importNode(1);
//    ensure there are nodes imported
    Assert.assertTrue(importer.depotList.size() > 0);
    Assert.assertTrue(importer.stationList.size() > 0);
  }
  
  
  public void testImportEdge() throws Exception {
//    import nodes
    testImportNode();
    importer.importEdge(4);
//    ensure there are edges imported
    Assert.assertTrue(importer.edgeList.size() > 0);
  }
  
  
  
}
