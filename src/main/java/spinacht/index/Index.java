package spinacht.index;

import spinacht.data.Database;
import spinacht.data.Point;
import spinacht.data.Subset;
import spinacht.data.Subspace;

import java.lang.reflect.Array;

/**
 * Non-specialized indexing structure used in SUBCLU.
 * DBSCAN itself could use an indexing structure that looks at every dimension at once, but, as discussed
 * in both the original DBSCAN paper and our own paper, this is not feasible with SUBCLU because SUBCLU
 * would require a seperate such structure for each subspace, which is not worth the construction cost.

 */
public class Index {

    // One Range Tree for each dimension.
    private RangeTree<Point>[] trees;

    public Index(Database db) {
        int ndims = db.getDimensionality();
        this.trees = (RangeTree<Point>[]) Array.newInstance(RangeTree.class, ndims);
        for (int i = 0; i < ndims; i++) {
            final int j = i;
            this.trees[i] = new RangeTree<Point>(db, p -> p.get(j));
        }
    }

    /**
     * Return the epsilon neighborhood of the given point in the given subspace when restricted to the given subset by
     * doing a separate range query on each dimension, and then filtering the results to take all dimensions in the
     * subspace into account at once, and also making sure all points are in the given subset.
     * @param eps Epsilon
     * @param p Point around which to search
     * @param subspace Subspace to which the search is restricted
     * @param subset Subset to which the search is restricted
     */
    public Subset epsNeighborhood(double eps, Point p, Subspace subspace, Subset subset) {
        Subset curr = subset;
        for (Integer i : subspace) {
            final double x = p.get(i);
            final Subset last = curr;
            final Subset next = new Subset();
            trees[i].forEachInRange(x - eps, x + eps, y -> {
                if (last.contains(y) && !next.contains(y)) {
                    double dsum = 0;
                    for (Integer dim : subspace) {
                        dsum += Math.pow(p.get(dim) - y.get(dim), 2);
                    }
                    if (Math.sqrt(dsum) <= eps) {
                        next.add(y);
                    }
                }
            });
            curr = next;
        }
        return curr;
    }

}
