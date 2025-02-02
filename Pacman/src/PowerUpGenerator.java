import java.util.Random;

public class PowerUpGenerator implements Runnable {
    private Grid grid;
    private Random random;

    public PowerUpGenerator(Grid grid) {
        this.grid = grid;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(5000);
                if (random.nextInt(100) < 25) {
                    int x, y;
                    do {
                        x = random.nextInt(grid.getCols() - 2) + 1;
                        y = random.nextInt(grid.getRows() - 2) + 1;
                    } while (grid.getGrid()[y][x] != 'P');
                    PowerUp.Type[] powerUpTypes = PowerUp.Type.values();
                    PowerUp.Type randomType = powerUpTypes[random.nextInt(powerUpTypes.length)];
                    PowerUp powerUp = new PowerUp(x, y, grid.getCellSize(), randomType, grid);
                    grid.addPowerUp(powerUp);
                    new Thread(() -> {
                        try {
                            Thread.sleep(10000);
                            grid.removePowerUp(powerUp);
                            grid.repaint();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    grid.repaint();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
