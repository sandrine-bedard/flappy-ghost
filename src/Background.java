import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Game's whole background : 2 moving images.
 */
public class Background {

    private ImageView bg1, bg2;

    private float x;

    // Getters
    public ImageView getBg1() {
        return bg1;
    }

    public ImageView getBg2() {
        return bg2;
    }

    // Constructor
    public Background() {
        this.x = FlappyGhost.GAME_WIDTH;

        Image image = new Image("/bg.png");
        this.bg1 = new ImageView(image);
        this.bg2 = new ImageView(image);
        this.bg2.setX(x);
    }

    /**
     * Moves the background to the left.
     * If the first background is outside of the screen, resets to be fully on the screen.
     *
     * @param vx Amount of pixel(s) it needs to move left per frame
     */
    public void step(double vx) {
        this.x -= vx;

        if (this.x <= 0) {
            this.x = FlappyGhost.GAME_WIDTH;
        }

        this.bg1.setX(x - FlappyGhost.GAME_WIDTH);
        this.bg2.setX(x);
    }

    /**
     * Puts both backgrounds to the starting position
     */
    public void reset() {
        this.x = FlappyGhost.GAME_WIDTH;
        this.bg1.setX(x - FlappyGhost.GAME_WIDTH);
        this.bg2.setX(x);
    }
}
