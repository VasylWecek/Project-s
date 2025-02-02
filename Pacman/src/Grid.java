import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

class Grid extends JPanel {
    private int rows;
    private int cols;
    private int cellSize;
    private char[][] grid;
    private Pacman pacman;
    private List<Enemy> enemies;
    private List<PowerUp> powerUps;
    private List<Point> points;
    private int score;
    private int lives;
    private long startTime;
    private JLabel scoreLabel;
    private JLabel timeLabel;
    private JLabel livesLabel;

    private Thread pacmanThread;
    private List<Thread> enemyThreads;
    private Random random;

    private HighScoreManager highScoreManager;

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cellSize = 20;
        this.grid = new char[rows][cols];
        this.score = 0;
        this.lives = 3;
        this.startTime = System.currentTimeMillis();
        this.random = new Random();
        this.points = new ArrayList<>();
        this.highScoreManager = new HighScoreManager();

        initializeGrid();

        scoreLabel = new JLabel("Score: 0");
        timeLabel = new JLabel("Time: 0");
        livesLabel = new JLabel("Lives: 3");

        setLayout(new BorderLayout());
        JPanel infoPanel = new JPanel();
        infoPanel.add(scoreLabel);
        infoPanel.add(timeLabel);
        infoPanel.add(livesLabel);
        add(infoPanel, BorderLayout.NORTH);


        pacman = new Pacman(cols / 2, rows / 2, cellSize, this);

        enemies = new ArrayList<>();
        enemyThreads = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            enemies.add(new Enemy(rows, cols, cellSize, this));
        }

        powerUps = new ArrayList<>();

        addPoints();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        pacman.setDirection('U');
                        break;
                    case KeyEvent.VK_DOWN:
                        pacman.setDirection('D');
                        break;
                    case KeyEvent.VK_LEFT:
                        pacman.setDirection('L');
                        break;
                    case KeyEvent.VK_RIGHT:
                        pacman.setDirection('R');
                        break;
                    case KeyEvent.VK_ESCAPE:
                        exitToMainMenu();
                        break;
                }
            }
        });

        startThreads();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeGrid();
            }
        });
    }

    private void startThreads() {
        pacmanThread = new Thread(new PacmanRunnable(pacman, this));
        pacmanThread.start();

        for (Enemy enemy : enemies) {
            Thread enemyThread = new Thread(new EnemyRunnable(enemy, this));
            enemyThreads.add(enemyThread);
            enemyThread.start();
        }

        Thread powerUpThread = new Thread(new PowerUpGenerator(this));
        powerUpThread.start();
    }

    private void initializeGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == 0 || i == rows - 1 || j == 0 || j == cols - 1) {
                    grid[i][j] = 'W';
                } else {
                    grid[i][j] = 'P';
                }
            }
        }

        addObstacles();
    }

    private void addObstacles() {
        int[][] obstaclePositions = {
                {1, 1}, {1, 2}, {1, 3}, {2, 1}, {3, 1},
                {rows - 2, cols - 2}, {rows - 2, cols - 3}, {rows - 2, cols - 4}, {rows - 3, cols - 2}, {rows - 4, cols - 2},
                {rows / 2 + 1, cols / 2 + 1}, {rows / 2 + 1, cols / 2 - 1}, {rows / 2 - 1, cols / 2 + 1}, {rows / 2 - 1, cols / 2 - 1},
                {rows / 2 + 2, cols / 2}, {rows / 2 - 2, cols / 2}, {rows / 2, cols / 2 + 2}, {rows / 2, cols / 2 - 2}
        };

        for (int[] pos : obstaclePositions) {
            if (!(pos[0] == rows / 2 && (pos[1] == cols / 2 || pos[1] == cols / 2 + 1 || pos[1] == cols / 2 - 1)) &&
                    !(pos[0] == rows / 2 + 1 && (pos[1] == cols / 2 || pos[1] == cols / 2 + 1 || pos[1] == cols / 2 - 1)) &&
                    !(pos[0] == rows / 2 - 1 && (pos[1] == cols / 2 || pos[1] == cols / 2 + 1 || pos[1] == cols / 2 - 1))) {
                grid[pos[0]][pos[1]] = 'O';
            }
        }
    }

    private void addPoints() {
        int pointCount = (rows * cols) / 5;
        for (int i = 0; i < pointCount; i++) {
            int x, y;
            do {
                x = random.nextInt(cols - 2) + 1;
                y = random.nextInt(rows - 2) + 1;
            } while (grid[y][x] != 'P');
            points.add(new Point(x, y));
        }
    }

    private void resizeGrid() {
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        cellSize = Math.min(panelWidth / cols, panelHeight / rows);
    }

    public synchronized void movePacman() {
        int nextX = pacman.getX();
        int nextY = pacman.getY();
        switch (pacman.getDirection()) {
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
        if (nextY >= 0 && nextY < rows && nextX >= 0 && nextX < cols && grid[nextY][nextX] != 'W' && grid[nextY][nextX] != 'O') {
            pacman.move();
        }
    }

    public synchronized void moveEnemy(Enemy enemy) {
        int nextX = enemy.getX();
        int nextY = enemy.getY();
        switch (enemy.getDirection()) {
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
        if (grid[nextY][nextX] != 'W' && grid[nextY][nextX] != 'O') {
            enemy.move();
        } else {
            enemy.changeDirection();
        }
    }

    public synchronized void checkCollisions() {
        for (Enemy enemy : enemies) {
            if (pacman.getX() == enemy.getX() && pacman.getY() == enemy.getY()) {
                if (!pacman.isInvincible()) {
                    lives--;
                    System.out.println("Lives remaining: " + lives);
                    livesLabel.setText("Lives: " + lives);
                    pacman.setInvincible(true);
                    if (lives <= 0) {
                        System.out.println("Game Over!");
                        handleGameOver(); // Gérer la fin du jeu
                        return;
                    }
                }
            }
        }

        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            if (pacman.getX() == powerUp.getX() && pacman.getY() == powerUp.getY()) {
                iterator.remove();
                applyPowerUp(powerUp.getType());
                int pointsGained = 10;
                if (pacman.hasDoublePoints()) {
                    pointsGained *= 2;
                }
                score += pointsGained;
                System.out.println("Score: " + score);
                scoreLabel.setText("Score: " + score);
            }
        }

        Iterator<Point> pointIterator = points.iterator();
        while (pointIterator.hasNext()) {
            Point point = pointIterator.next();
            if (pacman.getX() == point.x && pacman.getY() == point.y) {
                pointIterator.remove();
                int pointsGained = 5;
                if (pacman.hasDoublePoints()) {
                    pointsGained *= 2;
                }
                score += pointsGained;
                System.out.println("Score: " + score);
                scoreLabel.setText("Score: " + score);

                if (points.isEmpty()) {
                    System.out.println("You Win!");
                    handleVictory();
                    return;
                }
            }
        }
    }

    private void handleVictory() {
        pacmanThread.interrupt();
        for (Thread enemyThread : enemyThreads) {
            enemyThread.interrupt();
        }

        String playerName = JOptionPane.showInputDialog(this, "You Win! Enter your name:", "Victory", JOptionPane.PLAIN_MESSAGE);
        if (playerName != null && !playerName.trim().isEmpty()) {
            highScoreManager.addHighScore(new HighScore(playerName, score));
            highScoreManager.saveHighScores();
        }

        SwingUtilities.invokeLater(this::exitToMainMenu);
    }

    private void handleGameOver() {
        pacmanThread.interrupt();
        for (Thread enemyThread : enemyThreads) {
            enemyThread.interrupt();
        }

        String playerName = JOptionPane.showInputDialog(this, "Game Over! Enter your name:", "Game Over", JOptionPane.PLAIN_MESSAGE);
        if (playerName != null && !playerName.trim().isEmpty()) {
            highScoreManager.addHighScore(new HighScore(playerName, score));
            highScoreManager.saveHighScores();
        }

        SwingUtilities.invokeLater(this::exitToMainMenu);
    }

    private void applyPowerUp(PowerUp.Type type) {
        switch (type) {
            case EXTRA_LIFE:
                lives++;
                livesLabel.setText("Lives: " + lives);
                JOptionPane.showMessageDialog(this, "Power-Up! Extra Life!", "Power-Up", JOptionPane.INFORMATION_MESSAGE);
                break;
            case SPEED_BOOST:
                pacman.setSpeedBoost(true);
                JOptionPane.showMessageDialog(this, "Power-Up! Speed Boost!", "Power-Up", JOptionPane.INFORMATION_MESSAGE);
                new Thread(() -> {
                    try {
                        Thread.sleep(10000);
                        pacman.setSpeedBoost(false);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;
            case INVINCIBILITY:
                pacman.setInvincible(true);
                JOptionPane.showMessageDialog(this, "Power-Up! Invincibility!", "Power-Up", JOptionPane.INFORMATION_MESSAGE);
                new Thread(() -> {
                    try {
                        Thread.sleep(10000);
                        pacman.setInvincible(false);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;
            case FREEZE_ENEMIES:
                for (Enemy enemy : enemies) {
                    enemy.setFrozen(true);
                }
                JOptionPane.showMessageDialog(this, "Power-Up! Enemies Frozen!", "Power-Up", JOptionPane.INFORMATION_MESSAGE);
                new Thread(() -> {
                    try {
                        Thread.sleep(10000);
                        for (Enemy enemy : enemies) {
                            enemy.setFrozen(false);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;
            case DOUBLE_POINTS:
                pacman.setDoublePoints(true);
                JOptionPane.showMessageDialog(this, "Power-Up! Double Points!", "Power-Up", JOptionPane.INFORMATION_MESSAGE);
                new Thread(() -> {
                    try {
                        Thread.sleep(10000);
                        pacman.setDoublePoints(false);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;
        }
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public char[][] getGrid() {
        return grid;
    }

    public int getCellSize() {
        return cellSize;
    }

    public synchronized void addPowerUp(PowerUp powerUp) {
        powerUps.add(powerUp);
    }

    public synchronized void removePowerUp(PowerUp powerUp) {
        powerUps.remove(powerUp);
    }

    public boolean isWalkable(int x, int y) {
        return grid[y][x] != 'W' && grid[y][x] != 'O';
    }

    private void exitToMainMenu() {
        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (mainFrame != null) {
            mainFrame.setContentPane(new MainMenuPanel((PacmanGame) mainFrame));
            mainFrame.revalidate();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        resizeGrid();
        int gridWidth = cols * cellSize;
        int gridHeight = rows * cellSize;
        int offsetX = (getWidth() - gridWidth) / 2;
        int offsetY = (getHeight() - gridHeight) / 2;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 'W') {
                    g.setColor(Color.BLACK);
                } else if (grid[i][j] == 'O') {
                    g.setColor(Color.BLUE);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                g.fillRect(offsetX + j * cellSize, offsetY + i * cellSize, cellSize, cellSize);
                g.setColor(Color.GRAY);
                g.drawRect(offsetX + j * cellSize, offsetY + i * cellSize, cellSize, cellSize);
            }
        }
        pacman.draw(g, offsetX, offsetY, cellSize);

        for (Enemy enemy : enemies) {
            enemy.draw(g, offsetX, offsetY, cellSize);
        }

        List<Point> pointsCopy;
        synchronized (points) {
            pointsCopy = new ArrayList<>(points);
        }
        g.setColor(Color.YELLOW);
        for (Point point : pointsCopy) {
            int pointSize = cellSize / 4;
            g.fillOval(offsetX + point.x * cellSize + (cellSize - pointSize) / 2, offsetY + point.y * cellSize + (cellSize - pointSize) / 2, pointSize, pointSize);
        }

        List<PowerUp> powerUpsCopy;
        synchronized (powerUps) {
            powerUpsCopy = new ArrayList<>(powerUps);
        }
        for (PowerUp powerUp : powerUpsCopy) {
            powerUp.draw(g, offsetX, offsetY, cellSize);
        }

        long currentTime = System.currentTimeMillis();
        int timeElapsed = (int) ((currentTime - startTime) / 1000);
        timeLabel.setText("Time: " + timeElapsed);
    }
}
