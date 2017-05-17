package spinacht.dbscan;

import java.util.Set;

/**
 * Created by nachtm on 5/15/17.
 */
public interface DBSCANNER {

    Iterable<Cluster> dbscan(Subspace space, Set<Point> setOfPoints);

}
