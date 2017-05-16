package spinacht.dbscan;

import java.util.Comparator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class RangeTreeTest extends TestCase {

    public RangeTreeTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(RangeTreeTest.class);
    }

    public void test() {

      RangeTree rt = new RangeTree(Comparator.naturalOrder());

      rt.insert(3);
      rt.insert(3);
      rt.insert(4);
      rt.insert(100);
      rt.insert(30);
      rt.insert(66);
      rt.insert(666);
      rt.insert(-32);
      rt.insert(0);
      rt.insert(1);

      rt.forEachInRange(-100, 1000, System.out::println);
      System.out.println();
      rt.forEachInRange(3, 30, System.out::println);

    }

}
