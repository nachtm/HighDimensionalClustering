package spinacht.data;

import com.google.common.collect.Iterators;
import spinacht.Params;
import spinacht.subclu.SUBCLU;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * A database where each data point has a label of type T.
 * @param <T> The type of each point's label
 */
public class SimpleLabeledDatabase<T> extends ArrayList<SimpleLabeledDatabase.SimpleLabeledPoint<T>> implements Database<SimpleLabeledDatabase.SimpleLabeledPoint<T>> {

    private final int ndims;

    /**
     * A point with some kind of label. Hashes and considers equality by considering each dimension and the label.
     * @param <T> The type of the label to give to this point.
     */
    public static class SimpleLabeledPoint<T> implements Point {

        final private double[] point;
        final private T label;

        /**
         * Initialize the point with a given array of data and a label.
         * @param point The data this point will hold
         * @param label The label for this point
         */
        public SimpleLabeledPoint(double[] point, T label) {
            this.point = point;
            this.label = label;
        }

        @Override
        public double get(int i) {
            return point[i];
        }

        /**
         * Gets the label corresponding to this point.
         * @return The label this point holds.
         */
        public T getLabel() {
            return this.label;
        }

        @Override
        public String toString() {
            return "[" + Arrays.stream(this.point).mapToObj(Double::toString).collect(Collectors.joining(", ")) + "] " + this.label;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (other instanceof SimpleLabeledPoint) {
                SimpleLabeledPoint other_ = (SimpleLabeledPoint) other;
                for (int i = 0; i < this.point.length; i++) {
                    if (this.get(i) != other_.get(i)) {
                        return false;
                    }
                }
                return this.getLabel().equals(other_.getLabel());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int x = 0;
            for (Double d : this.point) {
                x ^= Double.hashCode(d);
            }
            x ^= this.label.hashCode();
            return x;
        }

    }

    /**
     * Initialize an empty database with ndims dimensions.
     * @param ndims The number of dimensions.
     */
    public SimpleLabeledDatabase(int ndims) {
        super();
        this.ndims = ndims;
    }

    /**
     * Initialize a database with some data.
     * @param ndims The number of dimensions of each point.
     * @param points The data to include.
     */
    public SimpleLabeledDatabase(int ndims, Iterator<SimpleLabeledPoint<T>> points) {
        this(ndims);
        Iterators.addAll(this, points);
    }

    @Override
    public int getDimensionality() {
        return ndims;
    }

}
