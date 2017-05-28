package spinacht.data;

import com.google.common.collect.Iterators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;


public class SimpleDatabase extends ArrayList<Point> implements Database<Point> {

    private final int ndims;

    public static class SimplePoint implements Point {

        final private double[] point;

        public SimplePoint(double... point) {
            this.point = point;
        }

        // public SimplePoint(double[] point) {
            // this.point = point;
        // }

        public double get(int i) {
            return point[i];
        }

        public String toString() {
            return "[" + Arrays.stream(this.point).mapToObj(Double::toString).collect(Collectors.joining(", ")) + "]";
        }

    }

    public SimpleDatabase(int ndims) {
        super();
        this.ndims = ndims;
    }

    public SimpleDatabase(int ndims, Iterator<SimplePoint> points) {
        this(ndims);
        Iterators.addAll(this, points);
    }

    public int getDimensionality() {
        return ndims;
    }

}
