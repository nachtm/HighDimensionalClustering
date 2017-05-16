package spinacht.dbscan;

/**
 * Created by nachtm on 5/15/17.
 */
public interface DBSCANNER {

    Iterable<Cluster> dbscan(Subspace space, Cluster cluster);

}
