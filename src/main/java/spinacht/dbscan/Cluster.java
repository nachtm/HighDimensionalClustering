package spinacht.dbscan;

/**
 * Created by nachtm on 5/15/17.
 */
public interface Cluster extends Iterable<Point>{

    int size();

    Point get(int i);
}
