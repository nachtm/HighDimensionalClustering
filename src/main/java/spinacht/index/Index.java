package spinacht.index;

import java.lang.Double;
import java.lang.reflect.Array;

import spinacht.data.Database;
import spinacht.data.Subset;
import spinacht.data.Subspace;
import spinacht.data.Point;

import spinacht.index.RangeTree;


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
        if (last.contains(y)) {
          next.add(y);
        }
      });
      curr = next;
    }
    return curr;
  }

}
