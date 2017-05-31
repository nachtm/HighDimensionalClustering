package spinacht.dbscan;

import spinacht.data.Subset;
import spinacht.data.Subspace;

import java.util.Collection;

/**
 * An interface that will run some form of the DBSCAN algorithm on a dataset.
 * Created by nachtm on 5/15/17.
 */
public interface DBSCANNER {

    /**
     * DBSCANs throught setOfPoints in order to find clusters in the relevant subspace
     * @param space the dimensions we should use to calculate distance
     * @param setOfPoints the set of points we are clustering
     * @return The collection of subsets that represent a single-subspace clustering
     */
    Collection<Subset> dbscan(Subspace space, Subset setOfPoints);

}
