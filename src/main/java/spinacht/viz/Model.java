package spinacht.viz;

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
                case 0: return x;
                case 1: return y;
                default: throw new IndexOutOfBoundsException();
            }
        }
    }

    private class SimpleDatabase extends HashSet<Point> implements Database  {
        @Override
        public int getDimensionality() {
            return 2;
        }
    }

    private final Database db = new SimpleDatabase();
    private Map<Subspace, Set<Subset>> clustering = null;
    private double lastEps = -1;
    private int lastMinPts = -1;

    private View view = null;

    static Iterable<Color> COLORS = Iterables.cycle(Color.GREEN, Color.BLUE, Color.CYAN, Color.YELLOW, Color.PINK);

    static Color background(Color c) {
        return new Color(1 - c.getRed(), 1 - c.getGreen(), 1 - c.getBlue(), .5);
    }

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
        this.lastEps = this.view.eps.doubleValue();
        this.lastMinPts = this.view.minPts.get();
        this.clustering = DumbSUBCLU.go(new Params(this.lastEps, this.lastMinPts, this.db));
//        this.clustering = SUBCLU.go(new Params(this.view.eps.doubleValue(), this.minPts.get(), this.db)).collect();
    }

    void unCluster() {
        this.clustering = null;
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
                view.mid.getGraphicsContext2D().fillOval(point.get(0) - 2, point.get(1) - 2,5, 5);
                view.top.getGraphicsContext2D().fillOval(point.get(0) - 2, 12, 5,5);
                view.left.getGraphicsContext2D().fillOval(12, point.get(1) - 2, 5,5);
            }

        } else {

            for (Map.Entry<Subspace, Set<Subset>> entry : this.clustering.entrySet()) {

                Subspace subspace = entry.getKey();
                Iterator<Color> colorIt = COLORS.iterator();

                if (Iterables.elementsEqual(subspace, Subspace.of(0))) {
                    GraphicsContext gc = view.top.getGraphicsContext2D();
                    for (Subset s : entry.getValue()) {
                        gc.setFill(colorIt.next());
                        for (Point p : s) {
                            gc.fillRect(p.get(0), 12, 4, 4);
                        }
                    }
                } else if (Iterables.elementsEqual(subspace, Subspace.of(1))) {
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
                        gc.setFill(background(colorIt.next()));
                        for (Point p : s) {
                            gc.fillOval(p.get(0) - this.lastEps, p.get(1) - this.lastEps, 2*this.lastEps + 1, 2*this.lastEps + 1);
                        }
                    }
                    colorIt = COLORS.iterator();
                    for (Subset s : entry.getValue()) {
                        gc.setFill(colorIt.next());
                        for (Point p : s) {
                            gc.fillRect(p.get(0) - 1, p.get(1) - 1, 4, 4);
                        }
                    }
                }

            }

        }

    }

}
