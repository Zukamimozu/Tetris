
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * For future use when we need to use a GUI.
 *
 * @author kell-gigabyte
 */
public class MainViewFX extends Application {

    // Specific panes
    private FlowPane tetrisPane;
    private FlowPane colorPane;

    // Pre-set color codes for easy modification
    private final String hexHbox = "4A444B";
    private final String hexButtonBox = "BA0101";
    private final String hexColorBox = "FFFFF0";
    private final String hexDelayBox = "78866B";
    private final String hexSliderBox = "90AFFF";
    private final String tetronimoDefaultColor = "FFFFFF";
    private final String tetronimoBackgroundColor = "80BFFF";

    // Width and height of tetris grid
    private static int width, height;

    // Main game object
    public static Game myGame;

    // Main Scene object
    private Scene mainScene;

    // Rectangle representation of the tetris grid
    private Rectangle[][] tetronimos;
    private final int RECTANGLE_SIZE = 50;

    private static long autoFall, lastFall;

    /**
     * Launches the GUI window.
     *
     * @param args
     * @param width
     * @param height
     * @param autoFall
     */
    public void init(String[] args, int width, int height, int autoFall) {
        myGame = new Game(width, height);
        MainViewFX.autoFall = autoFall;
        MainViewFX.width = width;
        MainViewFX.height = height;
        launch(args);

    }

    /**
     * Starts the JavaFX application window, creating the panes for view
     *
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {

        // Task to update the GUI and also cause the block to fall down every [MainViewFX.autoFall] Milliseconds.
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                int i = 0;
                while (true) {
                    final int finalI = i;
                    Platform.runLater(() -> {
                        //System.out.println("AutoFall!");
                        myGame.tick(false, -1);
                        updateRectangles();
                    });
                    i++;
                    Thread.sleep(MainViewFX.autoFall);
                }
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

        primaryStage.setTitle("Tetris V01");

        // The root pane, has a basic layout allowing other panes to go on top
        BorderPane root = new BorderPane();

        // Sets panes on top of the root pane
        root.setRight(addScorePane(null));
        root.setLeft(addNextBlockPane());
        root.setCenter(addTetrisPane());

        // Creates a scene, which is what is actually displayed. Uses the root pane.
        mainScene = new Scene(root);

        //setupKeyboard(mainScene);
        mainScene.setOnKeyPressed(keyPressed);

        // Sets the scene, and shows it to the user.
        primaryStage.setScene(mainScene);

        primaryStage.show();
    }

    /**
     * Generates the flowPane acting as the tetris window
     */
    private FlowPane addTetrisPane() {
        // Creates a new, empty pane
        FlowPane tetrisPane = new FlowPane();
        // Sets the packground color
        tetrisPane.setStyle("-fx-background-color: #" + this.tetronimoBackgroundColor + ";");

        // Sets the spacing for the Rectangle objects within
        tetrisPane.setPadding(new Insets(5, 10, 5, 10));
        tetrisPane.setVgap(4);
        tetrisPane.setHgap(4);
        tetrisPane.setPrefWrapLength(MainViewFX.width * (this.RECTANGLE_SIZE + 4)); // preferred width allows for two columns

        //System.out.println(this.height + " " + this.width);
        this.tetronimos = new Rectangle[MainViewFX.height][MainViewFX.width];

        // Generates the Rectangle Matrix with default colors
        for (int outer = 0; outer < this.tetronimos.length; outer++) {
            for (int inner = 0; inner < this.tetronimos[outer].length; inner++) {
                Rectangle rect = new Rectangle(this.RECTANGLE_SIZE, this.RECTANGLE_SIZE);
                rect.setFill(Color.web(this.tetronimoDefaultColor));
                tetrisPane.getChildren().add(rect);
                this.tetronimos[outer][inner] = rect;
            }
        }

        return tetrisPane;
    }

    /**
     * Generates a pane for displaying the next 3 tetronimos
     *
     * @return Vbox pane
     */
    private VBox addNextBlockPane() {
        return new VBox();
    }

    /**
     * Generates a pane with a scoreboard for high scores, displaying top 10,
     * and current score. Can either be given null, for no saved high scores, or
     * given a file location as a string to load scores from.
     *
     * @return Flowpane pane
     */
    private FlowPane addScorePane(String highScoreFile) {
        if (highScoreFile != null) {

        }
        return new FlowPane();
    }

    /**
     * Calls the tick method of the Game object to move down the current block.
     */
    public void drop() {
        System.out.println("Block Drop!");
        myGame.tick(false, 0);
    }

    public void updateRectangles() {
        //System.out.println("Rectangle Update!");
        Block[] blocks = MainViewFX.myGame.getArrayBlocks();

        // Reset all colors to default
        for (Rectangle[] rectArr : this.tetronimos) {
            for (Rectangle rect : rectArr) {
                if (rect.getFill().toString().equals(("0x" + this.tetronimoDefaultColor + "FF").toLowerCase())) {

                } else {
                    rect.setFill(Color.web(this.tetronimoDefaultColor));
                    System.out.println("Setting a rect color to def " + rect.getFill().toString() + "   " + ("0x" + this.tetronimoDefaultColor + "FF").toLowerCase());
                }
            }
        }

        // Add any Block colors to the Rectangle Array
        for (Block block : blocks) {
            if (block != null) {
                if (this.tetronimos[block.getPositionY()][block.getPositionX()].getFill() != Color.web(this.tetronimoDefaultColor)) {
                    // Error printout broken, do not uncomment, it'll print even if nothing is wrong
                    // System.err.println("Block overlap detected! " + block.getPositionX() + "X, " + block.getPositionY() + "Y");
                }
                this.tetronimos[block.getPositionY()][block.getPositionX()].setFill(block.getColor());
            }
        }

    }

    /**
     * Handles Keyboard events.
     */
    private EventHandler<KeyEvent> keyPressed = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
            //System.out.println(myGame.toString());

            // Gets the name of the key, aka the unicode character
            String keyName = event.getCode().getName();
            //System.out.println(event.getCode().getName());

            if (keyName.equals("S")) {
                //System.out.println("mov down");
                myGame.tick(false, 0);
                updateRectangles();
            }

            if (keyName.equals("A")) {
                //System.out.println("mov left");
                myGame.tick(true, 1);
                updateRectangles();
            }

            if (keyName.equals("D")) {
                //System.out.println("mov right");
                myGame.tick(true, 2);
                updateRectangles();
            }

            if (keyName.equals("Q")) {
                //System.out.println("rot left");
                myGame.tick(true, 3);
                updateRectangles();
            }

            if (keyName.equals("E")) {
                //System.out.println("rot right");
                myGame.tick(true, 4);
                updateRectangles();
            }
        }
    };
}
