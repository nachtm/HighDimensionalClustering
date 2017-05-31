package spinacht.subclu;

import spinacht.Params;
import spinacht.data.*;
import spinacht.dbscan.DBSCANNER;
import spinacht.dbscan.PaperDBScanner;
import spinacht.index.Index;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * Naive subspace clustering algorithm that has the same output as SUBCLU.
 * Used for testing purposes.
 * Created by nachtm on 5/25/17.
 */
public class DumbSUBCLU {

    /**
     * Cluster using DBSCAN in each subspace, just like SUBCLU, with the given SUBCLU parameters,
     * by naively clustering in every possible subspace. O(2^n), where n is the number of dimensions.
     * @param params
     * @return
     */
    public static InMemoryClustering go(Params params){
        Index index = new Index(params.getDatabase());
        DBSCANNER dbscanner = new PaperDBScanner(params.getEps(), params.getMinPts(), index);
        Subset everything = new Subset(params.getDatabase());
        InMemoryClustering clustering = new InMemoryClustering();
        forEachSubspace(params.getDatabase().getDimensionality(), xs -> {
            clustering.put(new SubspaceWrapper(xs.descendingIterator()), new HashSet<>(dbscanner.dbscan(new SubspaceWrapper(xs), everything)));
        });
        return clustering;
    }

    /**
     * Feed a linked list corresponding to every possible subsequence of (0, 1, ..., ndims - 1) to the given function.
     * @param ndims Exclusive ceiling
     * @param consumer Function to receive each subsequence
     */
    private static void forEachSubspace(int ndims, Consumer<LinkedList<Integer>> consumer) {
        LinkedList<Integer> xs = new LinkedList<>();
        forEachPrefix(ndims, xs, () -> {
            if (!xs.isEmpty()) {
                consumer.accept(xs);
            }
        });
    }

    /**
     * Helper for forEachSubspace that mutates the given list to contain each subsequence, invoking the given Runnable
     * each time the state of the list changes.
     */
    private static void forEachPrefix(int x, LinkedList<Integer> xs, Runnable go) {
        if (x == 0) {
            go.run();
        } else {
            xs.addFirst(x - 1);
            forEachPrefix(x - 1, xs, go);
            xs.removeFirst();
            forEachPrefix(x - 1, xs, go);
        }
    }

}
