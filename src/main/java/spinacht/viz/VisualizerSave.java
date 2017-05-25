package spinacht.viz;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nachtm on 5/14/17.
 */
public class VisualizerSave extends Application {

    public static final String outpath = "outfiles/points.txt";

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();
        Canvas canv = new Canvas(500, 500);
        Button btn = new Button("Save points");
        root.setCenter(canv);
        root.setBottom(btn);

        VisualizerModel model = new VisualizerModel(new SimpleDoubleProperty(200), new SimpleIntegerProperty(2));

        List<Point2D.Double> points = new ArrayList<>();
        canv.setOnMouseClicked(e -> {
            double x = e.getX();
            double y = e.getY();
            model.addPoint(x, y);
            model.render(canv, null, null, null);
        });

        btn.setOnMouseClicked((MouseEvent e) -> {
            model.cluster();
            model.render(canv, null, null, null);
        });

        primaryStage.setScene(new Scene(root, 500,600));
        primaryStage.show();
    }
}
