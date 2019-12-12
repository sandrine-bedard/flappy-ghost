/**
 * Simple obstacle.
 */
public class SimpleObstacle extends Obstacle{

    // Constructor
    public SimpleObstacle (boolean debug) {
        super(debug);
        this.setY((float) (Math.random() * (FlappyGhost.GAME_HEIGHT - 2 * this.getRadius())) + this.getRadius());
    }

    /**
     * Simple obstacle's displacement.
     *
     * @param xSpeed : speed in x-direction
     * @param deltaTime : time between frames in nanoseconds
     */
    public void step (float xSpeed, double deltaTime) {
        float vx = (float) deltaTime * xSpeed;

        this.setX(this.getX() - vx);
    }
}
