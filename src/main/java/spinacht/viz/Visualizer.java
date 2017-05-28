package spinacht.viz;

import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import com.google.common.collect.Iterables;

import spinacht.common.Params;
import spinacht.data.*;
import spinacht.subclu.SUBCLU;
import sun.java2d.pipe.SpanShapeRenderer;

/**
 * Created by nachtm on 5/14/17.
 */
public class Visualizer extends Application {

    private SimpleDatabase db = new SimpleDatabase();
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

        this.view.clearButton.setOnMouseClicked(x_ -> {
            this.view.isClustered.set(false);
            this.db.clear();
            this.render();
        });

        this.view.saveButton.setOnMouseClicked(x_ -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Points");
            chooser.setInitialFileName("WHEREISTHIS");
            File file = chooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    this.db.toFile(file);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        });

        this.view.loadButton.setOnMouseClicked(x_ -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Load Points");
            File file = chooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    this.db = fromFile(file);
                    this.render();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        });

        this.render();

        Scene scene = new Scene(this.view);
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.getStylesheets().add(Visualizer.class.getResource("main.css").toExternalForm());

    }

    private void clearCanvases() {

        view.mid.getGraphicsContext2D().setFill(Color.WHITE);
        view.top.getGraphicsContext2D().setFill(Color.WHITE);
        view.left.getGraphicsContext2D().setFill(Color.WHITE);

        view.mid.getGraphicsContext2D().clearRect(0,0,500,500);
        view.mid.getGraphicsContext2D().fillRect(0,0,500,500);

        view.top.getGraphicsContext2D().clearRect(0,0,500,25);
        view.top.getGraphicsContext2D().fillRect(0,0,500,25);

        view.left.getGraphicsContext2D().clearRect(0,0,25,500);
        view.left.getGraphicsContext2D().fillRect(0,0,25,500);

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

    static class SimpleDatabase extends HashSet<Point> implements Database  {

        @Override
        public int getDimensionality() {
            return 2;
        }

        void toFile(File file) throws IOException {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (Point p : this) {
                System.out.println(p);
                writer.write(p.get(0) + " " + p.get(1));
                writer.newLine();
            }
            writer.flush();
            writer.close();
        }

    }

    static SimpleDatabase fromFile(File file) throws IOException {
        SimpleDatabase db = new SimpleDatabase();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        reader.lines().map(line -> line.trim().split("\\s+")).forEach(row -> {
            System.out.println(row[0]);
            db.add(new SimplePoint(Double.parseDouble(row[0]), Double.parseDouble(row[1])));
        });
        reader.close();
        return db;
    }

}
