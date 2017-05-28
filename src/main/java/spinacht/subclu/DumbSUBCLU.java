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
 * Created by nachtm on 5/25/17.
 */
public class DumbSUBCLU {

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

    private static void forEachSubspace(int ndims, Consumer<LinkedList<Integer>> consumer) {
        LinkedList<Integer> xs = new LinkedList<>();
        forEachPrefix(ndims, xs, () -> {
            if (!xs.isEmpty()) {
                consumer.accept(xs);
            }
        });
    }

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
