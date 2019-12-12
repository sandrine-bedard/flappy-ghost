import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Users's controllable character (i.e. ghost).
 */
public class Player {
    public final static float RADIUS = 30;
    public final static float START_GRAVITY = 500;
    public final static float GRAVITY_STEP = 15;
    public final static float MAX_VY = 300;
    public final static float JUMP_VY = 300;

    private final Image image = new Image("/ghost.png");
    private ImageView imageView;

    private float vy;
    private float x, y;
    private float gravity;

    // Getters
    public Image getImage() {
        return image;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getGravity() {
        return gravity;
    }

    // Setter
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    // Constructor
    public Player() {
        this.x = (float) FlappyGhost.GAME_WIDTH / 2;
        this.y = (float) FlappyGhost.GAME_HEIGHT / 2;

        this.imageView = new ImageView(this.image);
        this.imageView.setX(this.x - RADIUS);
        this.imageView.setY(this.y - RADIUS);

        this.vy = 0;
        this.gravity = START_GRAVITY;
    }

    /**
     * Calculates next player position. Prevents out of bounds, and switches the player's y-velocity.
     *
     * @param deltaTime time between frames in nanoseconds
     */
    public void step(double deltaTime) {
        this.vy += (float) deltaTime * this.gravity;
        float nextY = (float) (this.y + deltaTime * vy);

        if (nextY - RADIUS < 0 || nextY + RADIUS > FlappyGhost.GAME_HEIGHT) {
            this.vy *= -1;
            if (nextY < RADIUS) {
                this.y = RADIUS;
            } else {
                this.y = FlappyGhost.GAME_HEIGHT - RADIUS;
            }
        } else {
            this.y = nextY;
        }

        if (vy > MAX_VY) {
            vy = MAX_VY;
        } else if (vy < -MAX_VY) {
            vy = -MAX_VY;
        }

        this.imageView.setY(this.y - RADIUS);
    }

    /**
     * Updates the player's y-velocity when the player goes up.
     */
    public void jump() {
        this.vy = -JUMP_VY;
    }

    /**
     * Resets player.
     */
    public void reset() {
        this.y = (float) FlappyGhost.GAME_HEIGHT / 2;
        this.vy = 0;
        this.gravity = START_GRAVITY;
    }
}
