package spinacht.subclu;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import spinacht.Params;
import spinacht.data.*;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Test for spinacht.subclu.SUBCLU
 */
public class SUBCLUTest extends TestCase {

    public SUBCLUTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(SUBCLUTest.class);
    }

    /**
     * Runs SUBCLU on a test data set against a naive implementation.
     * SUBCLU is not deterministic because DBSCAN isn't.
     * DBSCAN always clumps core points together, but there are boundary points that could be in more than one cluster.
     * This test ensures that the two runs differ only in where they assign boundary points.
     * @throws Exception
     */
    public void test() throws Exception {

        // We have 10-dimensional data too, but that takes to long with the naive control.
        int[ ] ndimss = new int[]{4, 5};

        for (Integer ndims : ndimss) {

            String fname = "elki/subspaces-" + ndims + "d.csv";
            SimpleLabeledDatabase<String> db = DataUtil.fromELKIFile(ndims, Paths.get(getClass().getClassLoader().getResource(fname).getFile()));

            // Cluster using both algorithms.
            InMemoryClustering fast = SUBCLU.go(new Params(.05, 10, db)).collect();
            InMemoryClustering slow = DumbSUBCLU.go(new Params(.05, 10, db)).collect();

            // Clusters missing from the naive run.
            List<Subset> missing = new ArrayList<>();

            fast.forEachCluster(subspace -> subset -> {
                Set<Subset> subsets = slow.get(subspace);
                if (!subsets.remove(subset)) {
                    missing.add(subset);
                }
            });

            // Clusters only in the naive run.
            List<Subset> leftover = new ArrayList<>();

            for (Map.Entry<TransparentSubspace, Set<Subset>> entry : slow.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    leftover.addAll(entry.getValue());
                }
            }

            System.err.println("ndims: " + ndims);

            Subset missings = new Subset();
            for (Subset s : missing) {
                System.err.println("missing: " + s.size());
                missings.addAll(s);
            }
            Subset leftovers = new Subset();
            for (Subset s : leftover) {
                System.err.println("leftover: " + s.size());
                leftovers.addAll(s);
            }

            // Make sure the two runs only differed in boundary points.
            assert missings.size() == leftovers.size();
        }

    }

}
