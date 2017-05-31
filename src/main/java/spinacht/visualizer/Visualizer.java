package spinacht.visualizer;

import com.google.common.collect.Iterables;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import spinacht.Params;
import spinacht.data.*;
import spinacht.subclu.DumbSUBCLU;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by nachtm on 5/14/17.
 */
public class Visualizer extends Application {

    private Database<Point> db = new SimpleDatabase(2);
    private View view = new View();

    @Override
    public void start(Stage primaryStage) {

        this.view.mid.setOnMouseClicked(ev -> {
            double x = ev.getX();
            double y = ev.getY();
            if (this.view.isErasing.get()) {
                int eps = 5;
                this.db.removeIf(p -> Math.pow(p.get(0) - x, 2) + Math.pow(p.get(1) - y, 2) < eps*eps);
            } else {
                this.db.add(new SimpleDatabase.SimplePoint(x, y));
            }
            this.render();
        });

        this.view.eps.addListener(x_ -> this.render());
        this.view.minPts.addListener(x_ -> this.render());

        view.isClustered.addListener(x_ -> this.render());
        view.hideNeighborhoods.addListener(x_ -> this.render());

        this.view.clearButton.setOnMouseClicked(x_ -> {
            this.view.isClustered.set(false);
            this.db.clear();
            this.render();
        });

        FileChooser chooser = new FileChooser();

        this.view.saveButton.setOnMouseClicked(x_ -> {
            chooser.setTitle("Save Points");
            File file = chooser.showSaveDialog(primaryStage);
            if (file != null) {
                try {
                    toFile(file, this.db);
                    chooser.setInitialDirectory(file.getParentFile());
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        });

        this.view.loadButton.setOnMouseClicked(x_ -> {
            chooser.setTitle("Load Points");
            File file = chooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    this.db = fromFile(file);
                    this.render();
                    chooser.setInitialDirectory(file.getParentFile());
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        });

        FileChooser snapshotChooser = new FileChooser();

        this.view.snapshotButton.setOnMouseClicked(x_ -> {
            snapshotChooser.setTitle("Save Snapshot");
            File file = snapshotChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try {
                   WritableImage image = this.view.snapshotAllSubspaces(new SnapshotParameters(), null);
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File(file.toString() + ".png"));
                    snapshotChooser.setInitialDirectory(file.getParentFile());
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        });

        this.render();

        Scene scene = new Scene(this.view);
        primaryStage.setTitle("spinacht - SUBCLU Visualizer");
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
            InMemoryClustering clustering = DumbSUBCLU.go(new Params(eps, minPts, this.db)).collect();
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

            if (!this.view.hideNeighborhoods.get()) {
                for (Subset s : subsets) {
                    gc.setFill(background(colors.next()));
                    for (Point p : s) {
                        drawRegionIn(p, eps, subspace);
                    }
                }
                colors = COLORS.iterator();
            }

            for (Subset s : subsets) {
                gc.setFill(colors.next());
                notNoise.addAll(s);
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

    static void toFile(File file, Database<Point> db) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (Point p : db) {
            writer.write(IntStream.range(0, db.getDimensionality()).boxed().map(i -> Double.toString(p.get(i))).collect(Collectors.joining(" ")));
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

    static Database fromFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Database<Point> db = new SimpleDatabase(2);
        reader.lines()
                .map(line -> line.trim().split("\\s+"))
                .map(row -> new SimpleDatabase.SimplePoint(new double[]{Double.parseDouble(row[0]), Double.parseDouble(row[1])}))
                .forEach(db::add);
        reader.close();
        return db;
    }

}
