package spinacht.index;

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

      RangeTree<Double> rt = new RangeTree<Double>(x -> x);

      rt.insert(3.0);
      rt.insert(3.0);
      rt.insert(4.0);
      rt.insert(100.0);
      rt.insert(30.0);
      rt.insert(66.0);
      rt.insert(666.0);
      rt.insert(-32.0);
      rt.insert(0.0);
      rt.insert(1.0);

      rt.forEachInRange(-100.0, 1000.0, System.out::println);
      System.out.println();
      rt.forEachInRange(3.0, 30.0, System.out::println);

    }

}
