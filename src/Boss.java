import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Check BONUS.txt
 * Queen of England.
 */
public class Boss {
    public static final int START_HP = 5;
    public static final float X_OFFSET = 80;

    private final Image image = new Image("/boss.png");
    private ImageView imageView;

    private float radius, x, y;
    private int hp;

    // Getters
    public ImageView getImageView() {
        return imageView;
    }

    public float getRadius() {
        return radius;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getHp() {
        return hp;
    }

    // Setters
    public void setX(float x) {
        this.x = x;
        this.getImageView().setX(this.getX() - this.getRadius());
    }

    // Constructor
    public Boss () {
        this.imageView = new ImageView(this.image);

        this.radius = FlappyGhost.GAME_HEIGHT >> 1;
        this.y = FlappyGhost.GAME_HEIGHT >> 1;
        this.x = FlappyGhost.GAME_WIDTH + this.radius;

        this.imageView.setX(this.x - this.radius);
        this.imageView.setY(this.y - this.radius);

        this.imageView.setFitHeight(this.radius * 2);
        this.imageView.setFitWidth(this.radius * 2);

        this.hp = START_HP;
    }

    /**
     * Boss' displacement
     * @param xSpeed : speed in x-direction
     * @param deltaTime : time lapse
     */
    public void step(float xSpeed, double deltaTime) {
        float vx = (float) deltaTime * xSpeed / 2;

        this.setX(this.getX() - vx);
    }

    /**
     * When the boss is attacked
     */
    public void hurt() {
        this.hp--;
    }

    /**
     * When the boss dies or is outside of screen
     */
    public void reset() {
        this.setX(FlappyGhost.GAME_WIDTH + this.radius);
        this.hp = START_HP;
    }
}
