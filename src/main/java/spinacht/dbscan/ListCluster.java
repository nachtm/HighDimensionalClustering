package spinacht.dbscan;

import java.util.Iterator;
import java.util.List;

/**
 * Created by nachtm on 5/15/17.
 */
public class ListCluster implements Cluster {

    private List<Point> points;
    public ListCluster(List<Point> points) {
        this.points = points;
    }

    @Override
    public int size() {
        return points.size();
    }

    @Override
    public Point get(int i) {
        return points.get(i);
    }

    @Override
    public Iterator<Point> iterator() {
        return points.iterator();
    }
}
