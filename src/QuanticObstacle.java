/**
 * Quantic obstacle. Teleports randomly.
 */
public class QuanticObstacle extends Obstacle {
    public static final double QUANTIC_COOLDOWN = 0.2;
    public static final float QUANTIC_RANGE = 30;

    private double quanticStepTimer;

    // Constructor
    public QuanticObstacle (boolean debug) {
        super(debug);
        this.quanticStepTimer = 0;
        this.setY((float) (Math.random() * (FlappyGhost.GAME_HEIGHT - 2 * this.getRadius())) + this.getRadius());
    }

    /**
     * Obstacle's displacement.
     *
     * @param xSpeed : speed in x-direction
     * @param deltaTime : time between frames in nanoseconds
     */
    public void step (float xSpeed, double deltaTime) {
        float vx = (float) deltaTime * xSpeed;

        this.quanticStepTimer += deltaTime;

        if (this.quanticStepTimer >= QUANTIC_COOLDOWN) {
            this.quanticStepTimer = 0;

            // Prevent out of bound
            float leftRange = this.getX() - QUANTIC_RANGE;
            float rightRange = this.getX() + QUANTIC_RANGE;
            float topRange = Math.max(this.getY() - QUANTIC_RANGE, this.getRadius());
            float bottomRange = Math.min(this.getY() + QUANTIC_RANGE, FlappyGhost.GAME_HEIGHT - this.getRadius());

            // Set up next position
            this.setX((float) (Math.random() * (rightRange - leftRange) + leftRange));

            this.setY((float) (Math.random() * (topRange - bottomRange) + bottomRange));

        }

        // X
        this.setX(this.getX() - vx);
    }
}

