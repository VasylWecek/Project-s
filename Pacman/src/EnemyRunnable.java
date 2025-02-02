class EnemyRunnable implements Runnable {
    private Enemy enemy;
    private Grid grid;

    public EnemyRunnable(Enemy enemy, Grid grid) {
        this.enemy = enemy;
        this.grid = grid;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                grid.moveEnemy(enemy);
                grid.checkCollisions();
                grid.repaint();
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {

        }
    }
}
