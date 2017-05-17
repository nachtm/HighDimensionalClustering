package spinacht.dbscan;

/**
 * Created by nachtm on 5/15/17.
 */
public interface Point {

    public int getDimensionality();

    public double get(int i);

    public Point addToDimension(int dim, double toAdd);
}
