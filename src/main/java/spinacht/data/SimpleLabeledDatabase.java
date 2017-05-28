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


public class SimpleLabeledDatabase<T> extends ArrayList<SimpleLabeledDatabase.LabeledPoint<T>> implements Database<SimpleLabeledDatabase.LabeledPoint<T>> {

    private final int ndims;

    public static class LabeledPoint<T> implements Point {

        final private double[] point;
        final private T label;

        public LabeledPoint(double[] point, T label) {
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

    }

    public SimpleLabeledDatabase(int ndims) {
        super();
        this.ndims = ndims;
    }

    public SimpleLabeledDatabase(int ndims, Iterator<LabeledPoint<T>> points) {
        this(ndims);
        Iterators.addAll(this, points);
    }

    public int getDimensionality() {
        return ndims;
    }

}
