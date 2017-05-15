package spinacht;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nachtm on 5/14/17.
 */
public class Visualizer extends Application {

    public static final String outpath = "points.txt";

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();
        Canvas canv = new Canvas(500, 500);
        Button btn = new Button("Save points");
        root.setCenter(canv);
        root.setBottom(btn);

        List<Point2D.Double> points = new ArrayList<>();
        canv.setOnMouseClicked(e -> {
            double x = e.getX();
            double y = e.getY();
            double minX = x - 2 > 0 ? x - 2 : 0;
            double minY = y - 2 > 0 ? y - 2 : 0;
            GraphicsContext gc = canv.getGraphicsContext2D();
            gc.fillRect(minX, minY, 4, 4);
            points.add(new Point2D.Double(x, y));
            System.out.println(x + " " + y);
        });

        btn.setOnMouseClicked((MouseEvent e) -> {
            File f = new File(outpath);
            try (FileWriter fw = new FileWriter(f)){
                for(Point2D.Double p : points){
                    fw.write(p.getX() + " " + p.getY() + "\n");
                }
            } catch (Exception err){
                System.err.println(err);
            }
            System.out.println("Points saved!");
        });

        primaryStage.setScene(new Scene(root, 500,600));
        primaryStage.show();
    }
}
