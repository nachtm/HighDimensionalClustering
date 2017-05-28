package spinacht.index;

import demo.SimpleDatabase;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import spinacht.data.Database;
import spinacht.data.Point;
import spinacht.data.Subset;
import spinacht.data.Subspace;

import java.io.File;


public class IndexTest extends TestCase {

    public IndexTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(IndexTest.class);
    }

    public void test() throws Exception {

      try {
        Database db = new SimpleDatabase(new File(getClass().getClassLoader().getResource("test.csv").getFile()));
        Index index = new Index(db);
        Subset subset = index.epsNeighborhood(1.5, db.iterator().next(), Subspace.of(1, 2), new Subset(db));
        System.out.println("N_eps");
        for (Point p : subset) {
          System.out.println("    " + p);
        }
        System.out.println();
      } catch (Exception e) {
        e.printStackTrace(System.out);
        throw e;
      } catch (Error e) {
        e.printStackTrace(System.out);
        throw e;
      }

    }

}
