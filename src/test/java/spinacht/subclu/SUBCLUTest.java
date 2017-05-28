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


public class SUBCLUTest extends TestCase {

    public SUBCLUTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(SUBCLUTest.class);
    }

    public void test() throws Exception {

        int[ ] ndimss = new int[]{4, 5};

        for (Integer ndims : ndimss) {

            String fname = "elki/subspaces-" + ndims + "d.csv";
            SimpleLabeledDatabase<String> db = DataUtil.fromELKIFile(ndims, Paths.get(getClass().getClassLoader().getResource(fname).getFile()));

            InMemoryClustering fast = SUBCLU.go(new Params(.05, 10, db)).collect();
            InMemoryClustering slow = DumbSUBCLU.go(new Params(.05, 10, db)).collect();

            List<Subset> missing = new ArrayList<>();

            fast.forEachCluster(subspace -> subset -> {
                Set<Subset> subsets = slow.get(subspace);
                if (!subsets.remove(subset)) {
                    missing.add(subset);
                }
            });

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

            assert missings.size() == leftovers.size();
        }

    }

}
