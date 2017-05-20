package spinacht.dbscan;

import spinacht.data.*;

import java.util.Collection;

/**
 * Created by nachtm on 5/15/17.
 */
public interface DBSCANNER {

    Collection<Subset> dbscan(Subspace space, Subset setOfPoints);

}
