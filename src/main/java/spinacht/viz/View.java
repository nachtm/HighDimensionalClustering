package spinacht.viz;

import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;


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

        super();

        this.setPadding(new Insets(10, 10, 10, 10));

        this.mid = new Canvas(500, 500);
        this.top = new Canvas(500, 25);
        this.left = new Canvas(25, 500);

        StackPane midPane = new StackPane(mid);
        StackPane topPane = new StackPane(top);
        StackPane leftPane = new StackPane(left);

        midPane.getStyleClass().add("bordered");
        topPane.getStyleClass().add("bordered");
        leftPane.getStyleClass().add("bordered");

        GridPane canvases = new GridPane();
        canvases.add(leftPane, 0,0);
        canvases.add(topPane, 1, 1);
        canvases.add(midPane, 1, 0);
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

        Slider epsSlider = new Slider(1, 120, 10);
        epsSlider.setOrientation(Orientation.HORIZONTAL);
        epsSlider.setMinWidth(203);
        this.eps = epsSlider.valueProperty();
        FlowPane epsInput = new FlowPane(5, 5, new Label("eps:"), epsSlider);

        Canvas epsPreview = new Canvas(241, 241);

        this.eps.addListener((x_, oldVal, newVal) -> {
            GraphicsContext gc = epsPreview.getGraphicsContext2D();
            gc.clearRect(0, 0, 241, 241);
            gc.fillOval(120 - this.eps.get(), 120 - this.eps.get(), 2*this.eps.get() + 1, 2*this.eps.get() + 1);
        });

        this.eps.setValue(30);

        VBox controls = new VBox(10, buttons, minPtsInput, epsInput, epsPreview);
        controls.setPadding(new Insets(10, 10, 10, 10));
        this.getChildren().add(controls);

        // this.setStyle("-fx-focus-color: transparent;");
        // this.setStyle("-fx-background-color: linear-gradient(to bottom, derive(-fx-text-box-border, -10%), -fx-text-box-border), linear-gradient(from 0px 0px to 0px 5px, derive(-fx-control-inner-background, -9%), -fx-control-inner-background);");
        // this.setStyle("-fx-focus-color: -fx-control-inner-background ; -fx-faint-focus-color: -fx-control-inner-background ;");

    }

}
