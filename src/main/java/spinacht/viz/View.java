package spinacht.viz;

import javafx.beans.property.*;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

class View extends FlowPane {

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

        super(Orientation.HORIZONTAL);

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
        canvases.add(leftPane, 0, 1);
        canvases.add(topPane, 1, 0);
        canvases.add(midPane, 1, 1);
        this.getChildren().add(canvases);

        ToggleButton clusterToggle = new ToggleButton("Cluster");
        this.isClustered = clusterToggle.selectedProperty();
        this.clearButton = new Button("Clear");
        this.saveButton = new Button("Save");
        this.loadButton = new Button("Load");
        FlowPane buttons = new FlowPane(clusterToggle, this.clearButton, this.saveButton, this.loadButton);
        buttons.setHgap(5);

        Spinner ptSpinner = new Spinner<>(1, Integer.MAX_VALUE, 5, 1);
        ptSpinner.setEditable(true);
        this.minPts = ptSpinner.valueProperty();
        FlowPane numPoints = new FlowPane(new Label("minPts:"), ptSpinner);

        Slider epsSlider = new Slider(1, 150, 10);
        epsSlider.setOrientation(Orientation.VERTICAL);
        this.eps = epsSlider.valueProperty();
        Canvas epsilonPreview = new Canvas(300, 300);
        FlowPane epsilonSetter = new FlowPane(epsSlider, epsilonPreview);

        this.eps.addListener((IDONTCARE, oldVal, newVal) -> {
            GraphicsContext gc = epsilonPreview.getGraphicsContext2D();
            gc.clearRect(0, 0, 300, 300);
            gc.fillOval(0, 0, 2*this.eps.get() + 1, 2*this.eps.get() + 1);
        });

        FlowPane controls = new FlowPane(Orientation.VERTICAL, buttons, numPoints, epsilonSetter);
        controls.setVgap(10);
        this.getChildren().add(controls);

        this.setStyle("-fx-focus-color: transparent;");
        // this.setStyle("-fx-background-color: linear-gradient(to bottom, derive(-fx-text-box-border, -10%), -fx-text-box-border), linear-gradient(from 0px 0px to 0px 5px, derive(-fx-control-inner-background, -9%), -fx-control-inner-background);");
        // this.setStyle("-fx-focus-color: -fx-control-inner-background ; -fx-faint-focus-color: -fx-control-inner-background ;");

    }

}
