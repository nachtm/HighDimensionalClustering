package spinacht.viz;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import spinacht.common.Params;
import spinacht.data.*;
import spinacht.subclu.SUBCLU;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import com.google.common.collect.Iterables;


class VisualizerModel {

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
    private Map<Subspace, List<Subset>> clustering = null;

    static Iterable<Color> COLORS = Iterables.cycle(Color.GREEN, Color.RED, Color.BLUE, Color.CYAN, Color.BROWN, Color.BISQUE, Color.TURQUOISE);

    VisualizerModel(DoubleProperty extEps, IntegerProperty extMinPts) {
        extEps.bindBidirectional(this.eps);
        extMinPts.bindBidirectional(this.minPts);
    }

    void addPoint(double x, double y) {
        this.db.add(new SimplePoint(x, y));
    }

    void clearPoints() {
        this.db.clear();
        this.clustering = null;
    }

    void cluster() {
        this.clustering = new HashMap<Subspace, List<Subset>>();
        System.out.println("WAT " + this.minPts.intValue());
        // SUBCLU.go(new Params(this.eps.doubleValue(), this.minPts.intValue(), this.db))
        SUBCLU.go(new Params(50, 5, this.db))
            .forEachCluster(subspace -> subset -> {
                System.out.println("+ " + Subspace.pprint(subspace));
                for (Point p : subset) {
                    System.out.println("+  " + p.get(0) + " " + p.get(1));
                }
                this.clustering.compute(subspace, (IDONTCARE, curr) -> {
                    if (curr == null) {
                        curr = new ArrayList<>();
                        curr.add(subset);
                    } else {
                        curr.add(subset);
                    }
                    return curr;
                });
            });

    }

    void unCluster() {
        this.clustering = null;
    }

    private static class Counter {
        int value = 0;
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

    void render(Canvas mid, Canvas top, Canvas left, Canvas epsPreview) {
        if (this.clustering == null) {
            for (Point point : this.db) {
                mid.getGraphicsContext2D().fillRect(point.get(0), point.get(1), 4, 4);
                top.getGraphicsContext2D().fillRect(point.get(0), 12, 4, 4);
                left.getGraphicsContext2D().fillRect(12, point.get(1), 4,4);
            }
        } else {
            for (Map.Entry<Subspace, List<Subset>> entry : this.clustering.entrySet()) {
                Subspace subspace = entry.getKey();
                System.out.println("> " + Subspace.pprint(subspace));
                if (iteq(subspace, Subspace.of(0))) {
                    // GraphicsContext gc = top.getGraphicsContext2D();
                } else if (iteq(subspace, Subspace.of(1))) {
                    // GraphicsContext gc = left.getGraphicsContext2D();
                } else {
                    GraphicsContext gc = mid.getGraphicsContext2D();
                    Iterator<Color> colorIt = COLORS.iterator();
                    for (Subset s : entry.getValue()) {
                        Color color = colorIt.next();
                        System.out.println(color);
                        gc.setFill(color);
                        for (Point p : s) {
                            gc.fillRect(p.get(0), p.get(1), 4, 4);
                        }
                    }
                }
            }
        }
    }

}
