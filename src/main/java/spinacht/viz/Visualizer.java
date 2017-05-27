package spinacht.viz;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Function;

import javafx.application.Application;
import javafx.scene.Scene;
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
            top.fillOval(point.get(0) - 2, 12, 5, 5);
            left.fillOval(12, point.get(1) - 2, 5, 5);
        }

    }

    private void renderClustering(double eps, InMemoryClustering clustering) {

        clearCanvases();

        for (Map.Entry<Subspace, Set<Subset>> entry : clustering.entrySet()) {

            Subspace subspace = entry.getKey();
            Set<Point> notNoise = new HashSet<>();
            Iterator<Color> colors = COLORS.iterator();

            GraphicsContext gc = (
                Iterables.elementsEqual(subspace, Subspace.of(0))
                ? view.top
                : Iterables.elementsEqual(subspace, Subspace.of(1))
                ? view.left
                : view.mid
            ).getGraphicsContext2D();

            for (Subset s : entry.getValue()) {
                gc.setFill(background(colors.next()));
                notNoise.addAll(s);
                for (Point p : s) {
                    if (Iterables.elementsEqual(subspace, Subspace.of(0))) {
                        gc.fillOval(p.get(0) - eps, 8, 2 * eps + 1, 14);
                    } else if (Iterables.elementsEqual(subspace, Subspace.of(1))) {
                        gc.fillOval(8, p.get(1) - eps, 14, 2 * eps + 1);
                    } else {
                        gc.fillOval(p.get(0) - eps, p.get(1) - eps, 2 * eps + 1, 2 * eps + 1);
                    }
                }
            }

            colors = COLORS.iterator();

            for (Subset s : entry.getValue()) {
                gc.setFill(colors.next());
                for (Point p : s) {
                    if (Iterables.elementsEqual(subspace, Subspace.of(0))) {
                        gc.fillOval(p.get(0) - 2, 12, 5,5);
                    } else if (Iterables.elementsEqual(subspace, Subspace.of(1))) {
                        gc.fillOval(12,p.get(1) - 2, 5,5);
                    } else {
                        gc.fillOval(p.get(0) - 2, p.get(1) - 2, 5, 5);
                    }
                }
            }

            gc.setFill(Color.LIGHTGREY);

            for (Point p : this.db) {
                if (!notNoise.contains(p)) {
                    if (Iterables.elementsEqual(subspace, Subspace.of(0))) {
                        gc.fillOval(p.get(0) - 2, 12, 5,5);
                    } else if (Iterables.elementsEqual(subspace, Subspace.of(1))) {
                        gc.fillOval(12,p.get(1) - 2, 5,5);
                    } else {
                        gc.fillOval(p.get(0) - 2, p.get(1) - 2, 5, 5);
                    }
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
