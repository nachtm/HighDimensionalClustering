package spinacht.viz;

import javafx.beans.property.*;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
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
    // final Button saveButton;

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

        // Button saveButton = new Button("Save points");
        this.clearButton = new Button("Clear points");
        ToggleButton clusterToggle = new ToggleButton("Cluster this!");
        this.isClustered = clusterToggle.selectedProperty();
        FlowPane buttons = new FlowPane(this.clearButton, clusterToggle);
        buttons.setHgap(5);

        Spinner ptSpinner = new Spinner<>(1, Integer.MAX_VALUE, 5, 1);
        ptSpinner.setEditable(true);
        this.minPts = ptSpinner.valueProperty();
        FlowPane numPoints = new FlowPane(new Label("NumPoints:"), ptSpinner);

        Slider epsSlider = new Slider(1, 500, 10);
        epsSlider.setOrientation(Orientation.VERTICAL);
        this.eps = epsSlider.valueProperty();
        Canvas epsilonPreview = new Canvas(300, 300);
        FlowPane epsilonSetter = new FlowPane(epsSlider, epsilonPreview);

        FlowPane controls = new FlowPane(Orientation.VERTICAL, buttons, numPoints, epsilonSetter);
        controls.setVgap(10);
        this.getChildren().add(controls);

    }

}
