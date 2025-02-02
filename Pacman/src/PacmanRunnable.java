class PacmanRunnable implements Runnable {
    private Pacman pacman;
    private Grid grid;

    public PacmanRunnable(Pacman pacman, Grid grid) {
        this.pacman = pacman;
        this.grid = grid;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                grid.movePacman();
                grid.checkCollisions();
                grid.repaint();
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
        }
    }
}
