package spinacht.data;

import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The result of a run of a run of a clustering algorithm. A clustering is a grouping of points into subsets. In the case
 * of SUBCLU, clusterings can happen in different spaces, so a clustering is actually a grouping of points into subsets, in
 * a particular subspace. It just happens to be the case that the clusterings of many other algorithms have subspaces
 * equal to the entire space.
 */
public interface Clustering {

    /**
     * Takes a function from subspaces to a consumer of subsets. Semantically, these can usually be read as "for each
     * subspace, for each subset, do..."
     * @param f The function from subspace -> consumer of subsets.
     */
    void forEachCluster(Function<Subspace, Consumer<Subset>> f);

    /**
     * Gathers the clustering into an InMemoryClustering, in order to perform other operations on it.
     * @return The InMemoryClustering version of this.
     */
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

    /**
     * Prints out each cluster nicely.
     */
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
