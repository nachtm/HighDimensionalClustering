package spinacht.visualizer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * GUI component containing the entire Visualizer interface.
 * This class exists to separate the yucky GUI initialization logic from the actual application logic.
 */
class View extends HBox {

    // The canvases corresponding to each subspace (2-space and its 2 projections)
    final Canvas mid;
    final Canvas top;
    final Canvas left;

    // Mutable parameters for DBSCAN
    final DoubleProperty eps;
    final ReadOnlyObjectProperty<Integer> minPts;

    // Various buttons
    final Button clearButton;
    final Button saveButton;
    final Button loadButton;
    final Button snapshotButton;
    final BooleanProperty isClustered;
    final BooleanProperty isErasing;
    final BooleanProperty hideNeighborhoods;

    final private Node superspace;
    final private Node allSubspaces;

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
        ToggleButton eraseToggle = new ToggleButton("Erase");
        this.isErasing = eraseToggle.selectedProperty();
        ToggleButton neighborhoodsToggle = new ToggleButton("Hide Neighborhoods");
        this.hideNeighborhoods = neighborhoodsToggle.selectedProperty();
        this.clearButton = new Button("Clear");
        this.saveButton = new Button("Save");
        this.loadButton = new Button("Load");
        this.snapshotButton = new Button("Snapshot");
        HBox toggles = new HBox(5, clusterToggle, eraseToggle, neighborhoodsToggle);
        HBox buttons = new HBox(5, this.clearButton, this.saveButton, this.loadButton, this.snapshotButton);

        Spinner<Integer> minPtsSpinner = new Spinner<>(1, Integer.MAX_VALUE, 5, 1);
        minPtsSpinner.setEditable(true);
        this.minPts = minPtsSpinner.valueProperty();
        FlowPane minPtsInput = new FlowPane(10, 10, new Label("minPts:"), minPtsSpinner);

        TextField epsValue= new TextField();
        epsValue.setEditable(false);
        FlowPane epsInput = new FlowPane(10, 10, new Label("eps:"), epsValue);

        int epsMax = 150;
        Slider epsSlider = new Slider(1, epsMax, 10);
        epsSlider.setOrientation(Orientation.HORIZONTAL);
        epsSlider.setMinWidth(2*epsMax + 1);
        epsSlider.setMaxWidth(2*epsMax + 1);
        epsSlider.setMajorTickUnit(15);
        epsSlider.setShowTickMarks(true);
        this.eps = epsSlider.valueProperty();

        Canvas epsPreview = new Canvas(2*epsMax + 1, 2*epsMax + 1);

        this.eps.addListener((x_, oldVal, newVal) -> {
            GraphicsContext gc = epsPreview.getGraphicsContext2D();
            gc.clearRect(0, 0, 2*epsMax + 1, 2*epsMax + 1);
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, 2*epsMax + 1, 2*epsMax + 1);
            gc.setFill(Color.BLACK);
            gc.fillOval(epsMax - this.eps.get(), epsMax - this.eps.get(), 2*this.eps.get() + 1, 2*this.eps.get() + 1);
            epsValue.setText(newVal.toString());
        });

        this.eps.setValue(30);

        VBox controls = new VBox(10, buttons, toggles, minPtsInput, epsInput, epsPreview, epsSlider);
        this.getChildren().add(controls);

        this.superspace = mid;
        this.allSubspaces = canvases;

    }

    /**
     * Take snapshot of 2-space.
     * @param params Parameters for Node.snapshot
     * @param wi Writable image for Node.snapshot
     * @return Result of Node.snapshot
     */
    WritableImage snapshotSuperspace(SnapshotParameters params, WritableImage wi) {
        return this.superspace.snapshot(params, wi);
    }

    /**
     * Take snapshot of all 3 subspaces.
     * @param params Parameters for Node.snapshot
     * @param wi Writable image for Node.snapshot
     * @return Result of Node.snapshot
     */
    WritableImage snapshotAllSubspaces(SnapshotParameters params, WritableImage wi) {
        return this.allSubspaces.snapshot(params, wi);
    }

}
