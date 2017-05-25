package spinacht.viz;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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

    public static final String outpath = "outfiles/points.txt";

    @Override
    public void start(Stage primaryStage) throws Exception {

//        BorderPane root = new BorderPane();
        GridPane root = new GridPane();

        StackPane canvasPane = new StackPane();
        StackPane xOnlyPane = new StackPane();
        StackPane yOnlyPane = new StackPane();

        Canvas canv = new Canvas(500, 500);
        Canvas xOnly = new Canvas(500, 25);
        Canvas yOnly = new Canvas(25, 500);
        Button btn = new Button("Save points");

        Pane[] panes = {canvasPane, xOnlyPane, yOnlyPane};
        Node[] nodes = {canv, xOnly, yOnly};
        addNodesAndStyle(panes, nodes, "bordered");

        root.add(yOnlyPane, 0, 1);
        root.add(xOnlyPane, 1, 0);
        root.add(canvasPane, 1, 1);
        root.add(btn, 1, 2);

        List<Point2D.Double> points = new ArrayList<>();
        canv.setOnMouseClicked(e -> {
            double x = e.getX();
            double y = e.getY();
            double minX = x - 2 > 0 ? x - 2 : 0;
            double minY = y - 2 > 0 ? y - 2 : 0;
            canv.getGraphicsContext2D().fillRect(minX, minY, 4,4);
            yOnly.getGraphicsContext2D().fillRect(12, minY, 4,4);
            xOnly.getGraphicsContext2D().fillRect(minX, 12, 4, 4);
//            GraphicsContext gc = canv.getGraphicsContext2D();
//            gc.fillRect(minX, minY, 4, 4);
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

        Scene s = new Scene(root);
        s.getStylesheets().add("borders.css");
        primaryStage.setScene(s);
        primaryStage.show();
    }

    private void addNodesAndStyle(Pane[] panes, Node[] nodes, String style){
        if(panes.length != nodes.length) {throw new IllegalArgumentException("Nodes and Panes must be same length");}
        for(int i = 0; i < panes.length; i++){
            panes[i].getChildren().add(nodes[i]);
            panes[i].getStyleClass().add(style);
        }
    }
}
