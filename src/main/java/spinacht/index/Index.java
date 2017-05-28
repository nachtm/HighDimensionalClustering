package spinacht.index;

import spinacht.data.Database;
import spinacht.data.Point;
import spinacht.data.Subset;
import spinacht.data.Subspace;

import java.lang.reflect.Array;


public class Index {

    private RangeTree<Point>[] trees;

    public Index(Database db) {
        int ndims = db.getDimensionality();
        this.trees = (RangeTree<Point>[]) Array.newInstance(RangeTree.class, ndims);
        for (int i = 0; i < ndims; i++) {
            final int j = i;
            this.trees[i] = new RangeTree<Point>(db, p -> p.get(j));
        }
    }

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
