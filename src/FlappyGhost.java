import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;

/**
 * View for MVC model. Contains main method of the game.
 */
public class FlappyGhost extends Application {
    public final static int WINDOW_WIDTH = 640;
    public final static int WINDOW_HEIGHT = 440;
    public final static int GAME_WIDTH = 640;
    public final static int GAME_HEIGHT = 400;

    private Controller controller;
    private Scene scene;
    private Pane pane;
    private MediaPlayer music;

    private Canvas canvas;
    private GraphicsContext debugContext;

    // Getters
    public MediaPlayer getMusic() {
        return music;
    }

    public Pane getPane() {
        return pane;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * Main method. Calls the launch method from the Application class.
     *
     * @param args Console arguments
     */
    public static void main(String[] args) {
        FlappyGhost.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Flappy Ghost");
        primaryStage.getIcons().add(new Image("/ghost.png"));
        primaryStage.setResizable(false);

        // Music
        URL resource = getClass().getResource("music.wav");
        this.music = new MediaPlayer(new Media(resource.toString()));
        music.setVolume(0.2f);
        music.setAutoPlay(true);
        music.setCycleCount(MediaPlayer.INDEFINITE);
        music.play();

        // Controller
        this.controller = new Controller(this);

        // Screen
        BorderPane root = new BorderPane();
        this.scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        this.scene.setOnKeyPressed((event) -> {
            this.controller.keyboardInput(event.getCode());
        });

        this.scene.setOnKeyReleased((event) -> {
            this.controller.keyboardReleased(event.getCode());
        });

        // Middle Screen
        this.pane = new Pane();
        root.setCenter(this.pane);

        // Debug Canvas
        this.canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
        this.debugContext = canvas.getGraphicsContext2D();

        // Menu
        HBox menu = new HBox();
        menu.setAlignment(Pos.CENTER);
        menu.setSpacing(10);
        menu.setPadding(new Insets(8));

        // Pause button
        Button pauseButton = new Button("Pause");
        menu.getChildren().add(pauseButton);

        pauseButton.setOnAction((event) -> {
            this.controller.pause();
            canvas.requestFocus();
        });

        // Debug Checkbox
        CheckBox debugCheckBox = new CheckBox("Mode debug");
        menu.getChildren().add(debugCheckBox);

        debugCheckBox.setOnAction((event) -> {
            this.controller.setDebug(debugCheckBox.isSelected());
            canvas.requestFocus();
        });

        // Score text
        Text scoreText = this.controller.getScoreText();
        menu.getChildren().add(scoreText);

        // Mute button
        Button muteButton = new Button("Mute");
        muteButton.setOnAction((event) -> {
            if (this.music.getVolume() != 0) {
                this.getMusic().setVolume(0);
            } else {
                this.music.setVolume(0.2f);
            }
            canvas.requestFocus();
        });
        menu.getChildren().add(muteButton);

        // Bottom Screen
        root.setBottom(menu);

        // Get focus on screen
        Platform.runLater(() -> {
            canvas.requestFocus();
        });

        // Focus on screen when clicked on
        scene.setOnMouseClicked((event) -> {
            canvas.requestFocus();
        });

        // Initialize
        this.controller.init();

        // Bullets thickness
        debugContext.setLineWidth(Bullet.BULLET_THIC);

        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {

                if (lastTime == 0 || controller.getPause()) {
                    lastTime = now;
                    return;
                }

                double deltaTime = (now - lastTime) * 1e-9;

                debugContext.clearRect(0,0,GAME_WIDTH,GAME_HEIGHT);

                controller.step(deltaTime);

                lastTime = now;
            }
        };
        timer.start();

        // Show
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Clears the screen, useful when called from the controller.
     */
    public void clearDebug() {
        this.debugContext.clearRect(0,0,GAME_WIDTH,GAME_HEIGHT);
    }

    /**
     * Used for debug.
     * Shows the player as a black circle.
     */
    public void showPlayerDebug() {
        Player ghost = this.controller.getGhost();

        this.debugContext.setFill(Color.BLACK);
        this.debugContext.fillOval(
                ghost.getX() - Player.RADIUS,
                ghost.getY() - Player.RADIUS,
                Player.RADIUS * 2,
                Player.RADIUS * 2
        );
    }

    /**
     * Used for debug.
     * Shows obstacles circles as yellow, or red if there is a collision with the player.
     */
    public void showObstaclesDebug () {
        ArrayList<Obstacle> obstacles = this.controller.getObstacles();

        for (Obstacle o : obstacles) {
            // If it collides with the player, show it red
            if (this.controller.collision(o)) {
                debugContext.setFill(Color.RED);
            } else { // Otherwise show it yellow
                debugContext.setFill(Color.YELLOW);
            }
            debugContext.fillOval(
                    o.getX() - o.getRadius(),
                    o.getY() - o.getRadius(),
                    o.getRadius() * 2,
                    o.getRadius() * 2
            );
        }
    }

    /**
     * Displays bullet on screen.
     */
    public void showBullet() {
        Bullet bullet = this.controller.getBullet();

        debugContext.strokeLine(
                bullet.getX(),
                bullet.getY(),
                bullet.getX() + Bullet.BULLET_LENGTH,
                bullet.getY()
        );
    }
}
