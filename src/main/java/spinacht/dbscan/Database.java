package spinacht.dbscan;

/**
 * Created by nachtm on 5/15/17.
 */
public interface Database extends Iterable<Point> {

    Point get(int i);

    int size();

}
