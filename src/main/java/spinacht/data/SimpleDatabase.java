package spinacht.data;

import com.google.common.collect.Iterators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * A simple database, which contains points of the same dimensionality.
 */
public class SimpleDatabase extends ArrayList<Point> implements Database<Point> {

    private final int ndims;

    /**
     * A simple class that implements point.
     */
    public static class SimplePoint implements Point {

        final private double[] point;

        /**
         * Create a point out of the dimensions listed as arguments.
         * @param point The dimensions to include in this point, in order.
         */
        public SimplePoint(double... point) {
            this.point = point;
        }

        @Override
        public double get(int i) {
            return point[i];
        }

        @Override
        public String toString() {
            return "[" + Arrays.stream(this.point).mapToObj(Double::toString).collect(Collectors.joining(", ")) + "]";
        }

    }

    /**
     * Initialize a SimpleDatabase with ndims dimensions.
     * @param ndims The number of dimensions each point will have.
     */
    public SimpleDatabase(int ndims) {
        super();
        this.ndims = ndims;
    }

    /**
     * Initialize a SimpleDatabase, with the data it will hold.
     * @param ndims The number of dimensions of each point.
     * @param points The points to add to this database.
     */
    public SimpleDatabase(int ndims, Iterator<SimplePoint> points) {
        this(ndims);
        Iterators.addAll(this, points);
    }

    @Override
    public int getDimensionality() {
        return ndims;
    }

}
