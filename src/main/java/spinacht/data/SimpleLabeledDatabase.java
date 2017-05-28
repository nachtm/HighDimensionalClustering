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


public class SimpleLabeledDatabase<T> extends ArrayList<SimpleLabeledDatabase.SimpleLabeledPoint<T>> implements Database<SimpleLabeledDatabase.SimpleLabeledPoint<T>> {

    private final int ndims;

    public static class SimpleLabeledPoint<T> implements Point {

        final private double[] point;
        final private T label;

        public SimpleLabeledPoint(double[] point, T label) {
            this.point = point;
            this.label = label;
        }

        public double get(int i) {
            return point[i];
        }

        public T getLabel() {
            return this.label;
        }

        public String toString() {
            return "[" + Arrays.stream(this.point).mapToObj(Double::toString).collect(Collectors.joining(", ")) + "] " + this.label;
        }

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

        public int hashCode() {
            int x = 0;
            for (Double d : this.point) {
                x ^= Double.hashCode(d);
            }
            x ^= this.label.hashCode();
            return x;
        }

    }

    public SimpleLabeledDatabase(int ndims) {
        super();
        this.ndims = ndims;
    }

    public SimpleLabeledDatabase(int ndims, Iterator<SimpleLabeledPoint<T>> points) {
        this(ndims);
        Iterators.addAll(this, points);
    }

    public int getDimensionality() {
        return ndims;
    }

}
