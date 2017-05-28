package spinacht.viz;

import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;


class View extends HBox {

    final Canvas mid;
    final Canvas top;
    final Canvas left;

    final DoubleProperty eps;
    final ReadOnlyObjectProperty<Integer> minPts;

    final BooleanProperty isClustered;
    final Button clearButton;
    final Button saveButton;
    final Button loadButton;

    View() {

        super(10);

        this.setId("root");

        this.setPadding(new Insets(10, 10, 10, 10));

        this.mid = new Canvas(500, 500);
        this.top = new Canvas(500, 25);
        this.left = new Canvas(25, 500);

        StackPane midPane = new StackPane(mid);
        StackPane topPane = new StackPane(top);
        StackPane leftPane = new StackPane(left);

        GridPane canvases = new GridPane();
        canvases.add(leftPane, 0,0);
        canvases.add(topPane, 1, 1);
        canvases.add(midPane, 1, 0);
        canvases.setGridLinesVisible(true);
        this.getChildren().add(canvases);

        ToggleButton clusterToggle = new ToggleButton("Cluster");
        this.isClustered = clusterToggle.selectedProperty();
        this.clearButton = new Button("Clear");
        this.saveButton = new Button("Save");
        this.loadButton = new Button("Load");
        HBox buttons = new HBox(5, clusterToggle, this.clearButton, this.saveButton, this.loadButton);

        Spinner<Integer> minPtsSpinner = new Spinner<>(1, Integer.MAX_VALUE, 5, 1);
        minPtsSpinner.setEditable(true);
        this.minPts = minPtsSpinner.valueProperty();
        FlowPane minPtsInput = new FlowPane(10, 10, new Label("minPts:"), minPtsSpinner);

        int epsMax = 150;
        Slider epsSlider = new Slider(1, epsMax, 10);
        epsSlider.setOrientation(Orientation.HORIZONTAL);
        epsSlider.setMinWidth(2*epsMax + 1);
        epsSlider.setMaxWidth(2*epsMax + 1);
        this.eps = epsSlider.valueProperty();

        Canvas epsPreview = new Canvas(2*epsMax + 1, 2*epsMax + 1);

        this.eps.addListener((x_, oldVal, newVal) -> {
            GraphicsContext gc = epsPreview.getGraphicsContext2D();
            gc.clearRect(0, 0, 2*epsMax + 1, 2*epsMax + 1);
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, 2*epsMax + 1, 2*epsMax + 1);
            gc.setFill(Color.BLACK);
            gc.fillOval(epsMax - this.eps.get(), epsMax - this.eps.get(), 2*this.eps.get() + 1, 2*this.eps.get() + 1);
        });

        this.eps.setValue(30);

        VBox controls = new VBox(10, buttons, minPtsInput, epsPreview, epsSlider);
        this.getChildren().add(controls);

        // this.setStyle("-fx-focus-color: transparent;");
        // this.setStyle("-fx-background-color: linear-gradient(to bottom, derive(-fx-text-box-border, -10%), -fx-text-box-border), linear-gradient(from 0px 0px to 0px 5px, derive(-fx-control-inner-background, -9%), -fx-control-inner-background);");
        // this.setStyle("-fx-focus-color: -fx-control-inner-background ; -fx-faint-focus-color: -fx-control-inner-background ;");

    }

}
