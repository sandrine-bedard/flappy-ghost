import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * Controller for MVC model. Connects View and Model together.
 */
public class Controller {
    public final static float START_SPEED = 120;
    public final static float SPEED_ACC = 15;
    public final static double OBTSACLE_COOLDOWN = 3;
    public final static int OBSTACLE_TO_PASS = 2;
    public final static int POINTS_PER_OBSTACLE = 5;
    public final static int POINTS_BETWEEN_BOSSES = 25;

    public final static String BONUS_PASSWORD = "TWADO";

    private FlappyGhost view;

    private boolean reset;
    private boolean pause;
    private boolean debug;
    private boolean jumpReady;
    private boolean shootReady;
    private boolean bossFight;
    private boolean twado;

    private Background background;
    private Player ghost;
    private Boss queen;
    private ArrayList<Obstacle> obstacles;
    private Bullet bullet;

    private Text scoreText;
    private int score;
    private int scoredObstacles;
    private int scoreForBoss;
    private float xSpeed;
    private double obstacleCooldown;

    private int passwordProgress;

    // Getters
    public boolean getPause() {
        return this.pause;
    }

    public Background getBackground() {
        return this.background;
    }

    public Player getGhost() {
        return this.ghost;
    }

    public ArrayList<Obstacle> getObstacles() {
        return this.obstacles;
    }

    public Bullet getBullet() {
        return bullet;
    }

    public Text getScoreText() {
        return this.scoreText;
    }

    // Setters
    public void setDebug(boolean debug) {
        this.debug = debug;

        // Obstacle debug
        for (Obstacle o : obstacles) {
            if (debug) {
                o.getImageView().setImage(null);
            } else {
                o.getImageView().setImage(o.getImage());
            }
        }

        // Player debug
        if (debug) {
            this.ghost.getImageView().setImage(null);
            this.view.showObstaclesDebug();
            this.view.showPlayerDebug();
        } else {
            this.view.clearDebug();
            this.ghost.getImageView().setImage(this.ghost.getImage());
        }
    }

    /**
     * Controller Constructor
     *
     * @param view View from MVC model
     */
    public Controller (FlappyGhost view) {
        this.view = view;

        this.reset = false;
        this.pause = false;
        this.debug = false;
        this.jumpReady = true;
        this.shootReady = true;
        this.bossFight = false;

        this.background = new Background();
        this.ghost = new Player();
        this.queen = new Boss();
        this.obstacles = new ArrayList<>();

        this.score = 0;
        this.scoreText = new Text("Score : " + this.score);
        this.scoredObstacles = 0;
        this.scoreForBoss = POINTS_BETWEEN_BOSSES;
        this.xSpeed = START_SPEED;
        this.obstacleCooldown = OBTSACLE_COOLDOWN;

        // BONUS
        this.passwordProgress = 0;
    }

    /**
     * Advances the game 1 frame.
     * Moves the background, the player and the obstacles.
     *
     * @param deltaTime time between frames in nanoseconds
     */
    public void step(double deltaTime) {
        if (this.score >= this.scoreForBoss) {
            bossFight = true;
            this.scoreForBoss += POINTS_BETWEEN_BOSSES;
        }

        if (!bossFight) {
            this.obstacleCooldown -= deltaTime;

            // Generate new obstacle
            if (this.obstacleCooldown <= 0) {
                int type = (int) (Math.random() * 3);

                Obstacle obstacle = new SinusObstacle(this.debug);
                switch (type) {
                    case 0:
                        obstacle = new SimpleObstacle(this.debug);
                        break;
                    case 1:
                        obstacle = new SinusObstacle(this.debug);
                        break;
                    case 2:
                        obstacle = new QuanticObstacle(this.debug);
                        break;
                }

                this.obstacles.add(obstacle);
                this.view.getPane().getChildren().add(obstacle.getImageView());

                this.obstacleCooldown = OBTSACLE_COOLDOWN;
            }
        } else {
            queen.step(xSpeed, deltaTime);
            if(this.collision(queen)) {
                this.reset = true;
            }
            if (queen.getX() < - queen.getRadius()) {
                queen.reset();
                this.bossFight = false;
            }
        }
        // Bullet step
        if (this.bullet != null) {
            this.bullet.step();
            if (this.bullet.hit(queen)) {
                queen.hurt();
                if (queen.getHp() <= 0) {
                    this.bossFight = false;
                    queen.reset();
                }
                this.bullet = null;
            } else if (this.bullet.getX() >= FlappyGhost.GAME_WIDTH) {
                this.bullet = null;
            }
        }

        // Background Step
        this.background.step(xSpeed * deltaTime);

        // Player Step
        this.ghost.step(deltaTime);

        // Obstacles Step
        ArrayList<Obstacle> obstaclesToRemoves = new ArrayList<>();
        for (Obstacle o : obstacles) {
            o.step(xSpeed, deltaTime);

            if (this.collision(o) && !this.debug) {
                this.reset = true;
            }

            if (o.getX() <= -o.getRadius()) {
                obstaclesToRemoves.add(o);
            }

            this.score(o);
        }

        for (Obstacle o : obstaclesToRemoves) {
            obstacles.remove(o);
        }

        if (this.bullet != null) {
            this.view.showBullet();
        }

        obstaclesToRemoves.clear();

        // Collision
        if (this.debug) { // Debug visuals if debug is selected
            this.view.showObstaclesDebug();
            this.view.showPlayerDebug();
        } else if (this.reset) { // Game Reset if debug isn't selected
            this.reset();
        }
    }

    /**
     * Checks if the target obstacle has to be scored.
     * If the most right part of the obstacle is past the left part of the ghost
     * and if it has not been scored before, the obstacle is scored.
     *
     * @param o Obstacle to be compared with the player's position.
     */
    public void score(Obstacle o) {
        if (!o.isScored() && o.getX() + o.getRadius() < this.getGhost().getX() - Player.RADIUS) {
            // Update Score
            this.score += POINTS_PER_OBSTACLE;

            // Prevent counting it twice
            o.setScored(true);

            // Speed up / Increase the gravity
            if (++this.scoredObstacles == OBSTACLE_TO_PASS) {
                this.scoredObstacles = 0;

                // Speed
                this.xSpeed += SPEED_ACC;

                // Gravity Increase
                this.ghost.setGravity(this.ghost.getGravity() + Player.GRAVITY_STEP);
            }

            // Score text update
            this.scoreText.setText("Score : " + this.score);
        }
    }

    /**
     * Switches the pause value from true to false, or false to true.
     */
    public void pause() {
        this.pause = !this.pause;
        if (this.pause) {
            this.view.getMusic().pause();
        } else {
            this.view.getMusic().play();
        }
    }

    /**
     * Initialises the game
     *
     * Sets up the background, and the player.
     */
    public void init() {
        // Background
        this.view.getPane().getChildren().add(this.background.getBg1());
        this.view.getPane().getChildren().add(this.background.getBg2());

        // Player
        this.view.getPane().getChildren().add(this.ghost.getImageView());

        // Boss
        this.view.getPane().getChildren().add(this.queen.getImageView());
        // Debug context
        this.view.getPane().getChildren().add(this.view.getCanvas());
    }

    public void reset () {
        this.reset = false;

        this.xSpeed = START_SPEED;
        this.score = 0;
        this.scoredObstacles = 0;
        this.scoreForBoss = POINTS_BETWEEN_BOSSES;
        this.obstacleCooldown = OBTSACLE_COOLDOWN;

        this.passwordProgress = 0;

        // Music reset
        this.view.getMusic().stop();
        this.view.getMusic().play();

        // Background reset
        this.background.reset();

        // Obstacles reset
        obstacles.clear();
        this.view.getPane().getChildren().clear();

        // Player reset
        this.getGhost().reset();
        this.init();

        // Boss reset
        this.queen.reset();
        this.bossFight = false;

        // Score text update
        this.scoreText.setText("Score : " + this.score);
    }

    /**
     * Creates the desired effect depending on the pressed key in the "View"
     *
     * @param keycode Code related to the key pressed
     */
    public void keyboardInput(KeyCode keycode) {
        if (!this.pause) {
            // Next required character to progress toward bonus effect
            String bonusNextChar = BONUS_PASSWORD.substring(this.passwordProgress, this.passwordProgress + 1);
            switch(keycode) {
                case SPACE :
                    if (jumpReady) {
                        this.ghost.jump();
                        this.jumpReady = false;
                    }
                    break;
                case ENTER :
                    if (this.bullet == null && shootReady) {
                        this.shoot();
                    }
                    this.shootReady = false;
                    break;
                default :
                    if (keycode.getChar().equals(bonusNextChar)) { // Bonus char
                        if (++this.passwordProgress == BONUS_PASSWORD.length()) { // Completed the password
                            this.flipView();
                            this.passwordProgress = 0;
                        }
                    } else {
                        this.passwordProgress = 0;
                    }
                    break;
            }
        }
    }

    /**
     * Creates the desired effect depending on the pressed key in the "View"
     *
     * @param keycode Code related to the key pressed
     */
    public void keyboardReleased(KeyCode keycode) {
        if (!this.pause) {
            switch (keycode) {
                case SPACE :
                    this.jumpReady = true;
                    break;
                case ENTER :
                    this.shootReady = true;
                    break;
            }
        }
    }

    /**
     * Detects collision between wanted Obstacle and the player's ghost
     *
     * @param o Obstacle to compare with player's position
     * @return if there is collision
     */
    public boolean collision (Obstacle o) {
        float diffX = this.ghost.getX() - o.getX();
        float diffY = this.ghost.getY() - o.getY();
        float dist = (float) Math.sqrt(diffX * diffX + diffY * diffY);

        return dist < Player.RADIUS + o.getRadius();
    }

    /**
     * Same function than before, but for collision with boss instead
     *
     * @param b boss to compare with player's position
     * @return if there is a collision
     */
    public boolean collision (Boss b) {
        float diffX = this.ghost.getX() - (b.getX() + Boss.X_OFFSET);
        float diffY = this.ghost.getY() - b.getY();
        float dist = (float) Math.sqrt(diffX * diffX + diffY * diffY);

        return dist < Player.RADIUS + b.getRadius();
    }

    /**
     * For twado bonus. Reverses y-axis
     */
    public void flipView() {
        this.twado = !this.twado;
        if (twado) {
            this.view.getPane().setScaleY(-1);
        } else {
            this.view.getPane().setScaleY(1);
        }
    }

    /**
     * Initialises a bullet to the player's location
     */
    public void shoot() {
        this.bullet = new Bullet(this.ghost);
    }
}