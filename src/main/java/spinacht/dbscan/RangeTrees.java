package spinacht.dbscan;

import java.util.List;
import java.util.Set;

/**
 * Created by nachtm on 5/15/17.
 */
public interface RangeTrees {

    Set<Point> getWithinRange(Subspace s, Point p, double epsilon);

    //should return a set including p!!!
    List<Point> getWithinRangeFilteredBy(Subspace s, Point p, double epsilon, Set<Point> superset);

}
