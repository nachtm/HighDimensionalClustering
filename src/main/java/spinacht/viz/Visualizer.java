package spinacht.viz;

import java.util.*;
import java.util.function.Function;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import com.google.common.collect.Iterables;

import spinacht.common.Params;
import spinacht.data.*;
import spinacht.subclu.DumbSUBCLU;
import spinacht.subclu.SUBCLU;

/**
 * Created by nachtm on 5/14/17.
 */
public class Visualizer extends Application {

    private final Database db = new SimpleDatabase();
    private View view = new View();

    @Override
    public void start(Stage primaryStage) {

        this.view.mid.setOnMouseClicked(e -> {
            double x = e.getX();
            double y = e.getY();
            this.db.add(new SimplePoint(x, y));
            this.render();
        });

        this.view.eps.addListener(x_ -> this.render());
        this.view.minPts.addListener(x_ -> this.render());

        view.isClustered.addListener(x_ -> this.render());

        this.view.clearButton.setOnMouseClicked(e -> {
            this.view.isClustered.set(false);
            this.db.clear();
            this.render();
        });

        Scene s = new Scene(this.view, 1000, 600);
        s.getStylesheets().add("borders.css");
        primaryStage.setScene(s);
        primaryStage.show();

    }

    private void clearCanvases() {
        view.mid.getGraphicsContext2D().clearRect(0,0,500,500);
        view.top.getGraphicsContext2D().clearRect(0,0,500,25);
        view.left.getGraphicsContext2D().clearRect(0,0,25,500);
    }

    private void render() {
        if (this.view.isClustered.get()) {
            double eps = view.eps.doubleValue();
            int minPts = view.minPts.get();
            InMemoryClustering clustering = SUBCLU.go(new Params(eps, minPts, this.db)).collect();
            this.renderClustering(eps, clustering);
        } else {
            this.renderPoints();
        }
    }

    private void renderPoints() {

        clearCanvases();

        GraphicsContext mid = view.mid.getGraphicsContext2D();
        GraphicsContext top = view.top.getGraphicsContext2D();
        GraphicsContext left = view.left.getGraphicsContext2D();

        mid.setFill(Color.BLACK);
        top.setFill(Color.BLACK);
        left.setFill(Color.BLACK);

        for (Point point : this.db) {
            mid.fillOval(point.get(0) - 2, point.get(1) - 2, 5, 5);
            top.fillOval(point.get(0) - 2, 11, 5, 5);
            left.fillOval(11, point.get(1) - 2, 5, 5);
        }

    }

    private boolean subspaceIs(Subspace subspace, Integer... cmp) {
        return Iterables.elementsEqual(subspace, Arrays.asList(cmp));
    }

    private Canvas getCanvasOf(Subspace subspace) {
        return subspaceIs(subspace, 0) ? view.top : subspaceIs(subspace, 1) ? view.left : view.mid;
    }

    private void drawPointIn(Point p, Subspace subspace) {
        if (subspaceIs(subspace, 0)) {
            view.top.getGraphicsContext2D().fillOval(p.get(0) - 2, 11, 5, 5);
        } else if (subspaceIs(subspace, 1)) {
            view.left.getGraphicsContext2D().fillOval(11,p.get(1) - 2, 5,5);
        } else {
            System.out.println(Subspace.pprint(subspace));
            view.mid.getGraphicsContext2D().fillOval(p.get(0) - 2, p.get(1) - 2, 5, 5);
        }
    }

    private void drawRegionIn(Point p, double eps, Subspace subspace) {
        if (subspaceIs(subspace, 0)) {
            view.top.getGraphicsContext2D().fillOval(p.get(0) - eps, 6, 2 * eps + 1, 14);
        } else if (subspaceIs(subspace,1)) {
            view.left.getGraphicsContext2D().fillOval(6, p.get(1) - eps, 14, 2 * eps + 1);
        } else {
            view.mid.getGraphicsContext2D().fillOval(p.get(0) - eps, p.get(1) - eps, 2 * eps + 1, 2 * eps + 1);
        }
    }

    private void renderClustering(double eps, InMemoryClustering clustering) {

        clearCanvases();

        Iterable<TransparentSubspace> subspaces = Arrays.asList(
            new SubspaceWrapper(0),
            new SubspaceWrapper(1),
            new SubspaceWrapper(1, 0)
        );

        for (TransparentSubspace subspace : subspaces) {

            final Set<Subset> subsets = clustering.getOrDefault(subspace, new HashSet<>());
            final GraphicsContext gc = getCanvasOf(subspace).getGraphicsContext2D();
            final Set<Point> notNoise = new HashSet<>();

            Iterator<Color> colors = COLORS.iterator();

            for (Subset s : subsets) {
                gc.setFill(background(colors.next()));
                notNoise.addAll(s);
                for (Point p : s) {
                    drawRegionIn(p, eps, subspace);
                }
            }

            colors = COLORS.iterator();

            for (Subset s : subsets) {
                gc.setFill(colors.next());
                for (Point p : s) {
                    drawPointIn(p, subspace);
                }
            }

            gc.setFill(Color.LIGHTGREY);

            for (Point p : this.db) {
                if (!notNoise.contains(p)) {
                    drawPointIn(p, subspace);
                }
            }

        }

    }

    private static Iterable<Color> COLORS = Iterables.cycle(Color.GREEN, Color.BLUE, Color.BROWN, Color.CADETBLUE, Color.CHOCOLATE, Color.CORNFLOWERBLUE, Color.DARKCYAN, Color.DARKGREEN, Color.MEDIUMORCHID);

    private static double backgroundComponent(double d) {
        return d + (1 - d)/2;
    }

    private static Color background(Color c) {
        return new Color(
                backgroundComponent(c.getRed()),
                backgroundComponent(c.getGreen()),
                backgroundComponent(c.getBlue()),
                .1
        );
    }

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

}
