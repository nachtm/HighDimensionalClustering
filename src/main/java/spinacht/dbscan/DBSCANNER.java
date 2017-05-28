package spinacht.dbscan;

import spinacht.data.Subset;
import spinacht.data.Subspace;

import java.util.Collection;

/**
 * Created by nachtm on 5/15/17.
 */
public interface DBSCANNER {

    Collection<Subset> dbscan(Subspace space, Subset setOfPoints);

}
