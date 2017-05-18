package spinacht.subclu;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import spinacht.data.Database;
import spinacht.data.SimpleDatabase;
import spinacht.data.Point;
import spinacht.common.Params;


public class SUBCLUTest extends TestCase {

    public SUBCLUTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(SUBCLUTest.class);
    }

    public void test() throws Exception {

      try {
        Database db = new SimpleDatabase(new File(getClass().getClassLoader().getResource("test.csv").getFile()));
        SUBCLU.go(new Params(1.1, 1, db)).forEachCluster(subspace -> {
          System.out.print("SUBSPACE:");
          for (Integer dim : subspace) {
            System.out.print(" " + dim);
          }
          System.out.println();
          return subset -> {
            System.out.println("  SUBSET");
            for (Point p : subset) {
              System.out.println("    " + p);
            }
          };
        });
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
