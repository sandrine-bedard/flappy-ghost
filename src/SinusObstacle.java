/**
 * Sinus obstacle. Moves in a sinusoïdal fashion.
 */
public class SinusObstacle extends Obstacle {
    public static final float SIN_SIZE = 50;
    public static final float CYCLE_PER_STEP = 1f / 32f;

    private float startY;
    private float currentCycle;

    // Constructor
    public SinusObstacle (boolean debug) {
        super(debug);

        float distanceFromEdge = this.getRadius() + SIN_SIZE;
        this.setY((float) (Math.random() * (FlappyGhost.GAME_HEIGHT - 2 * (distanceFromEdge)) + distanceFromEdge));

        this.startY = this.getY();

        this.currentCycle = 0;
    }

    /**
     * Sinus obstacle's displacement
     *
     * @param xSpeed : speed in x-direction
     * @param deltaTime : time between frames in nanoseconds
     */
    public void step (float xSpeed, double deltaTime) {
        float vx = (float) deltaTime * xSpeed;

        // X
        this.setX(this.getX() - vx);

        // Y
        this.currentCycle += CYCLE_PER_STEP;
        this.currentCycle %= 2;

        this.setY(this.startY + (float) Math.sin(Math.PI * this.currentCycle) * SIN_SIZE);
    }
}