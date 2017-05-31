package spinacht.index;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import spinacht.data.*;

import java.io.File;
import java.nio.file.Paths;

/**
 * Tests for spinacht.index.Index
 */
public class IndexTest extends TestCase {

    public IndexTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(IndexTest.class);
    }

    /**
     * Just makes sure nothing crashes.
     * @throws Exception
     */
    public void test() throws Exception {

        Database<Point> db = DataUtil.fromLameFile(3, Paths.get(getClass().getClassLoader().getResource("test.csv").getFile()));
        Index index = new Index(db);
        Subset subset = index.epsNeighborhood(1.5, db.iterator().next(), new SubspaceWrapper(1, 2), new Subset(db));
        System.out.println("N_eps");
        for (Point p : subset) {
            System.out.println("    " + p);
        }
        System.out.println();

    }

}
