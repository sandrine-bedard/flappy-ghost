import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Abstract class for all obstacles objects (simple, quantic & sinus).
 */
public abstract class Obstacle {
    private boolean scored;

    private Image image;
    private ImageView imageView;

    private float radius, x, y;

    // Getters
    public boolean isScored() {
        return scored;
    }

    public Image getImage() {
        return image;
    }

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

    // Setters
    public void setScored(boolean scored) {
        this.scored = scored;
    }

    public void setX(float x) {
        this.x = x;
        this.getImageView().setX(this.getX() - this.getRadius());
    }

    public void setY(float y) {
        this.y = y;
        this.getImageView().setY(this.getY() - this.getRadius());
    }

    // Constructor
    public Obstacle (boolean debug) {
        this.scored = false;
        this.radius = (float) Math.random() * 35 + 10;
        this.x = FlappyGhost.GAME_WIDTH + this.radius;

        String path = "/obstacles/" + (int) (Math.random() * 27) + ".png";
        this.image = new Image(path);

        if (debug) {
            this.imageView = new ImageView();
        } else {
            this.imageView = new ImageView(this.image);
        }

        this.imageView.setX(this.x - this.radius);
        this.imageView.setY(this.y - this.radius);

        this.imageView.setFitHeight(this.radius * 2);
        this.imageView.setFitWidth(this.radius * 2);
    }

    /**
     * Abstract method for object's displacement
     *
     * @param xSpeed : speed in x-direction
     * @param deltaTime : time between frames in nanoseconds
     */
    public abstract void step(float xSpeed, double deltaTime);
}