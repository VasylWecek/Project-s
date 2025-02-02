import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;

class Enemy {
    private int x, y;
    private int cellSize;
    private char direction;
    private Random random;
    private Color color;
    private Grid grid;
    private boolean frozen;

    public Enemy(int rows, int cols, int cellSize, Grid grid) {
        this.cellSize = cellSize;
        this.random = new Random();
        this.x = random.nextInt(cols - 2) + 1;
        this.y = random.nextInt(rows - 2) + 1;
        this.direction = 'R';
        this.color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        this.grid = grid;
        this.frozen = false;
    }


    public void move() {
        if (frozen) {
            return;
        }

        if (random.nextInt(10) < 2) {
            changeDirection();
        }

        int nextX = x, nextY = y;
        switch (direction) {
            case 'U':
                nextY--;
                break;
            case 'D':
                nextY++;
                break;
            case 'L':
                nextX--;
                break;
            case 'R':
                nextX++;
                break;
        }

        if (grid.isWalkable(nextX, nextY)) {
            x = nextX;
            y = nextY;
        } else {
            changeDirection();
        }
    }

    public void changeDirection() {
        switch (random.nextInt(4)) {
            case 0:
                direction = 'U';
                break;
            case 1:
                direction = 'D';
                break;
            case 2:
                direction = 'L';
                break;
            case 3:
                direction = 'R';
                break;
        }
    }

    public char getDirection() {
        return direction;
    }

    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(color);
        int bodyHeight = cellSize * 2 / 3;
        g2d.fillArc(offsetX + x * cellSize, offsetY + y * cellSize, cellSize, bodyHeight, 0, 180);
        g2d.fillRect(offsetX + x * cellSize, offsetY + y * cellSize + bodyHeight / 2, cellSize, bodyHeight / 2);

        int footHeight = cellSize / 6;
        int footWidth = cellSize / 3;
        int footY = offsetY + y * cellSize + bodyHeight;
        g2d.fillArc(offsetX + x * cellSize, footY, footWidth, footHeight, 0, 180);
        g2d.fillArc(offsetX + x * cellSize + footWidth, footY, footWidth, footHeight, 0, 180);
        g2d.fillArc(offsetX + x * cellSize + 2 * footWidth, footY, footWidth, footHeight, 0, 180);

        g2d.setColor(Color.WHITE);
        int eyeSize = cellSize / 4;
        int eyeOffsetX = cellSize / 6;
        int eyeOffsetY = cellSize / 6;
        int leftEyeX = offsetX + x * cellSize + eyeOffsetX;
        int rightEyeX = offsetX + x * cellSize + 2 * eyeOffsetX;
        int eyesY = offsetY + y * cellSize + eyeOffsetY;
        g2d.fillOval(leftEyeX, eyesY, eyeSize, eyeSize);
        g2d.fillOval(rightEyeX, eyesY, eyeSize, eyeSize);

        g2d.setColor(Color.BLACK);
        int pupilSize = cellSize / 8;
        int pupilOffsetX = 0;
        int pupilOffsetY = 0;

        switch (direction) {
            case 'U':
                pupilOffsetY = -eyeOffsetY / 2;
                break;
            case 'D':
                pupilOffsetY = eyeOffsetY / 2;
                break;
            case 'L':
                pupilOffsetX = -eyeOffsetX / 2;
                break;
            case 'R':
                pupilOffsetX = eyeOffsetX / 2;
                break;
        }

        g2d.fillOval(leftEyeX + pupilOffsetX, eyesY + pupilOffsetY, pupilSize, pupilSize);
        g2d.fillOval(rightEyeX + pupilOffsetX, eyesY + pupilOffsetY, pupilSize, pupilSize);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public boolean isFrozen() {
        return frozen;
    }
}
