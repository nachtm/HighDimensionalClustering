package spinacht.viz;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import spinacht.common.Params;
import spinacht.data.*;
import spinacht.subclu.DumbSUBCLU;

import java.util.*;
import com.google.common.collect.Iterables;


class Model {

    private static class SimplePoint implements Point {
        double x, y;
        SimplePoint(double x, double y) {
            this.x = x;
            this.y = y;
        }
        public double get(int i) {
            switch(i) {
                case 0:
                    return x;
                case 1:
                    return y;
                default:
                    throw new IndexOutOfBoundsException();
            }
        }
    }

    private class SimpleDatabase extends HashSet<Point> implements Database  {
        @Override
        public int getDimensionality() {
            return 2;
        }
    }

    private final DoubleProperty eps = new SimpleDoubleProperty();
    private final IntegerProperty minPts = new SimpleIntegerProperty();
    private final Database db = new SimpleDatabase();
    private Map<Subspace, Set<Subset>> clustering = null;

    private View view = null;

    static Iterable<Color> COLORS = Iterables.cycle(Color.GREEN, Color.RED, Color.BLUE, Color.CYAN, Color.BROWN, Color.BISQUE, Color.TURQUOISE);

    Model(View view) {
        this.view = view;
    }

    void addPoint(double x, double y) {
        this.db.add(new SimplePoint(x, y));
    }

    void resetPoints() {
        this.db.clear();
        this.clustering = null;
        this.view.isClustered.setValue(false);
    }

    void cluster() {
        System.out.println("eps: " + this.view.eps.doubleValue());
        System.out.println("minPts: " + this.view.minPts.get());
//        this.clustering = SUBCLU.go(new Params(this.view.eps.doubleValue(), this.minPts.get(), this.db)).collect();
        this.clustering = DumbSUBCLU.go(new Params(this.view.eps.doubleValue(), this.minPts.get(), this.db));
    }

    void unCluster() {
        this.clustering = null;
    }

    private static <T> boolean iteq(Iterable<T> a, Iterable<T> b) {
        Iterator<T> c = a.iterator();
        Iterator<T> d = b.iterator();
        while (c.hasNext() && d.hasNext()) {
            if (c.next() != d.next()) {
                return false;
            }
        }
        return c.hasNext() == d.hasNext();
    }

    void render() {

        view.mid.getGraphicsContext2D().clearRect(0, 0, 500, 500);
        view.top.getGraphicsContext2D().clearRect(0, 0, 500, 25);
        view.left.getGraphicsContext2D().clearRect(0, 0, 25, 500);

        if (this.clustering == null) {

            view.mid.getGraphicsContext2D().setFill(Color.BLACK);
            view.top.getGraphicsContext2D().setFill(Color.BLACK);
            view.left.getGraphicsContext2D().setFill(Color.BLACK);
            for (Point point : this.db) {
                view.mid.getGraphicsContext2D().fillRect(point.get(0), point.get(1), 4, 4);
                view.top.getGraphicsContext2D().fillRect(point.get(0), 12, 4, 4);
                view.left.getGraphicsContext2D().fillRect(12, point.get(1), 4,4);
            }

        } else {

            for (Map.Entry<Subspace, Set<Subset>> entry : this.clustering.entrySet()) {

                Subspace subspace = entry.getKey();
                Iterator<Color> colorIt = COLORS.iterator();

                if (iteq(subspace, Subspace.of(0))) {
                    GraphicsContext gc = view.top.getGraphicsContext2D();
                    for (Subset s : entry.getValue()) {
                        gc.setFill(colorIt.next());
                        for (Point p : s) {
                            gc.fillRect(p.get(0), 12, 4, 4);
                        }
                    }
                } else if (iteq(subspace, Subspace.of(1))) {
                    GraphicsContext gc = view.left.getGraphicsContext2D();
                    for (Subset s : entry.getValue()) {
                        gc.setFill(colorIt.next());
                        for (Point p : s) {
                            gc.fillRect(12, p.get(1),4, 4);
                        }
                    }
                } else {
                    GraphicsContext gc = this.view.mid.getGraphicsContext2D();
                    for (Subset s : entry.getValue()) {
                        gc.setFill(colorIt.next());
                        for (Point p : s) {
                            gc.fillRect(p.get(0), p.get(1), 4, 4);
                        }
                    }
                }

            }

        }

    }

}
