import java.awt.*;

public class PowerUp {
    public enum Type {
        EXTRA_LIFE,
        SPEED_BOOST,
        INVINCIBILITY,
        FREEZE_ENEMIES,
        DOUBLE_POINTS
    }

    private int x, y;
    private int cellSize;
    private Type type;
    private Grid grid;

    public PowerUp(int x, int y, int cellSize, Type type, Grid grid) {
        this.x = x;
        this.y = y;
        this.cellSize = cellSize;
        this.type = type;
        this.grid = grid;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Type getType() {
        return type;
    }

    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        switch (type) {
            case EXTRA_LIFE:
                g.setColor(Color.RED);
                g.fillOval(offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize);
                break;
            case SPEED_BOOST:
                g.setColor(Color.GREEN);
                g.fillOval(offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize);
                break;
            case INVINCIBILITY:
                g.setColor(Color.BLUE);
                g.fillOval(offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize);
                break;
            case FREEZE_ENEMIES:
                g.setColor(Color.CYAN);
                g.fillOval(offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize);
                break;
            case DOUBLE_POINTS:
                g.setColor(Color.MAGENTA);
                g.fillOval(offsetX + x * cellSize, offsetY + y * cellSize, cellSize, cellSize);
                break;
        }
    }
}
