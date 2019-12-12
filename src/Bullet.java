/**
 * Check BONUS.txt
 * Used for the weapon of the player, to attack the boss.
 */
public class Bullet {
    public static final float BULLET_LENGTH = 20;
    public static final float BULLET_THIC = 5;
    public static final float BULLET_SPEED = 10;

    private float x;
    private float y;

    // Getters
    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    // Constructor
    public Bullet(Player ghost) {
        this.y = ghost.getY();
        this.x = FlappyGhost.GAME_WIDTH >> 1;
    }

    /**
     * Bullet's displacement
     */
    public void step() {
        this.x += BULLET_SPEED;
    }

    /**
     * Detects collision between wanted Obstacle and the player's ghost
     *
     * @param b : boss to compare with bullet's position
     * @return : if there is a collision
     */
    public boolean hit(Boss b) {
        float diffX = this.x + Bullet.BULLET_LENGTH - b.getX() - Boss.X_OFFSET;
        float diffY = this.y - b.getY();
        float dist = (float) Math.sqrt(diffX * diffX + diffY * diffY);

        return dist < b.getRadius();
    }
}