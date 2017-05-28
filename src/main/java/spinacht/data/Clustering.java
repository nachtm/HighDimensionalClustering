package spinacht.data;

import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;


public interface Clustering {

    void forEachCluster(Function<Subspace, Consumer<Subset>> f);

    default InMemoryClustering collect() {
        InMemoryClustering collected = new InMemoryClustering();
        this.forEachCluster(subspace -> subset ->
                collected.compute(new SubspaceWrapper(subspace), (IDONTCARE, curr) -> {
                    if (curr == null) {
                        curr = new HashSet<>();
                        curr.add(subset);
                    } else {
                        curr.add(subset);
                    }
                    return curr;
                })
        );
        return collected;
    }

    default void pprint() {
        this.forEachCluster(subspace -> {
            System.out.print("SUBSPACE: " + Subspace.pprint(subspace));
            System.out.println();
            return subset -> {
                System.out.println("  SUBSET");
                for (Point p : subset) {
                    System.out.println("    " + p);
                }
            };
        });
    }

}
