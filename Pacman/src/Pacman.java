import java.awt.Color;
import java.awt.Graphics;

public class Pacman {
    private int x, y;
    private int cellSize;
    private char direction; // 'U' for up, 'D' for down, 'L' for left, 'R' for right
    private boolean mouthOpen;
    private boolean invincible;
    private long invincibilityEndTime;
    private boolean speedBoost;
    private boolean doublePoints;
    private Grid grid;

    public Pacman(int startX, int startY, int cellSize, Grid grid) {
        this.x = startX;
        this.y = startY;
        this.cellSize = cellSize;
        this.direction = 'R';
        this.mouthOpen = true;
        this.invincible = false;
        this.invincibilityEndTime = 0;
        this.speedBoost = false;
        this.doublePoints = false;
        this.grid = grid;
    }

    public void move() {
        int speed = speedBoost ? 2 : 1;
        switch (direction) {
            case 'U':
                y = Math.max(0, y - speed);
                break;
            case 'D':
                y = Math.min(grid.getRows() - 1, y + speed);
                break;
            case 'L':
                x = Math.max(0, x - speed);
                break;
            case 'R':
                x = Math.min(grid.getCols() - 1, x + speed);
                break;
        }
        mouthOpen = !mouthOpen;

        if (invincible && System.currentTimeMillis() > invincibilityEndTime) {
            invincible = false;
        }
    }

    public void setDirection(char direction) {
        this.direction = direction;
    }

    public char getDirection() {
        return direction;
    }

    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        g.setColor(Color.YELLOW);
        if (mouthOpen) {
            g.fillOval(offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize);
        } else {
            int startAngle = 0;
            switch (direction) {
                case 'U':
                    startAngle = 135;
                    break;
                case 'D':
                    startAngle = -45;
                    break;
                case 'L':
                    startAngle = 225;
                    break;
                case 'R':
                    startAngle = 45;
                    break;
            }
            g.fillArc(offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize, startAngle, 270);
        }

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
        if (invincible) {
            invincibilityEndTime = System.currentTimeMillis() + 3000;
        }
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setSpeedBoost(boolean speedBoost) {
        this.speedBoost = speedBoost;
    }

    public boolean hasSpeedBoost() {
        return speedBoost;
    }

    public void setDoublePoints(boolean doublePoints) {
        this.doublePoints = doublePoints;
    }

    public boolean hasDoublePoints() {
        return doublePoints;
    }
}
