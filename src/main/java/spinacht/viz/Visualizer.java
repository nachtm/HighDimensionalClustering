package spinacht.viz;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nachtm on 5/14/17.
 */
public class Visualizer extends Application {

    public static final String outpath = "outfiles/points.txt";

    @Override
    public void start(Stage primaryStage) {

        View view = new View();
        Model model = new Model(view);

        view.mid.setOnMouseClicked(e -> {
            if (!view.isClustered.getValue()) {
                double x = e.getX();
                double y = e.getY();
                // double minX = x - 2 > 0 ? x - 2 : 0;
                // double minY = y - 2 > 0 ? y - 2 : 0;
                model.addPoint(x, y);
                model.render();
            }
        });

        view.isClustered.addListener((IDONTCARE, oldVal, newVal) -> {
            if (newVal) {
                model.cluster();
            } else {
                model.unCluster();
            }
            model.render();
        });

        view.clearButton.setOnMouseClicked(e -> {
            model.resetPoints();
            model.render();
        });

        Scene s = new Scene(view, 1000, 600);
        s.getStylesheets().add("borders.css");
        primaryStage.setScene(s);
        primaryStage.show();

    }

}
