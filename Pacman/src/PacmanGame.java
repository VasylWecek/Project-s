import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PacmanGame extends JFrame {

    public PacmanGame() {
        setTitle("Pacman Game");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(new MainMenuPanel(this));
    }

    public void showGridSizeSelection() {
        JFrame gridSizeFrame = new JFrame("Select Grid Size");
        gridSizeFrame.setSize(400, 300);
        gridSizeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gridSizeFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Choose grid size:");

        String[] gridSizes = {"Small (15x15)", "Medium (17x17)", "Large (20x20)", "Extra Large (25x25)", "Huge (30x30)"};
        JComboBox<String> gridSizeComboBox = new JComboBox<>(gridSizes);

        JButton startButton = new JButton("Start Game");

        panel.add(label);
        panel.add(gridSizeComboBox);
        panel.add(startButton);

        gridSizeFrame.add(panel);
        gridSizeFrame.setVisible(true);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedSize = (String) gridSizeComboBox.getSelectedItem();
                startGameWithGridSize(selectedSize);
                gridSizeFrame.dispose();
            }
        });
    }

    private void startGameWithGridSize(String gridSize) {
        System.out.println("Starting game with grid size: " + gridSize);

        int rows, cols;
        switch (gridSize) {
            case "Small (15x15)":
                rows = cols = 15;
                break;
            case "Medium (17x17)":
                rows = cols = 17;
                break;
            case "Large (20x20)":
                rows = cols = 20;
                break;
            case "Extra Large (25x25)":
                rows = cols = 25;
                break;
            case "Huge (30x30)":
                rows = cols = 30;
                break;
            default:
                rows = cols = 10;
                break;
        }

        Grid gameGrid = new Grid(rows, cols);
        setContentPane(gameGrid);
        revalidate();
    }

    public void showHighScores() {
        System.out.println("Showing high scores...");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PacmanGame().setVisible(true);
            }
        });
    }
}
