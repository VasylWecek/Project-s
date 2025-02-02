package view;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.*;
import model.Country;
import model.UpgradeManager;
import model.Country;
import model.UpgradeManager;
import model.Transport;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import model.Upgrade;
import model.HighScore;
import view.MainMenuView;
import java.util.concurrent.CopyOnWriteArrayList;
import view.HighScoreView;
import controller.DifficultyManager;

public class GameView extends JFrame {

    private String difficulty;
    private JLabel scoreLabel;
    private JLabel populationLabel;
    private JLabel pointsInfoLabel;
    private JLabel timerLabel;
    private JLabel virusStatusLabel;
    private JPanel mapPanel;
    private JPanel upgradePanel;
    private int score = 0;
    private int timeElapsed = 0;
    private Timer gameTimer;
    private CopyOnWriteArrayList<Country> countries = new CopyOnWriteArrayList<>();
    private UpgradeManager upgradeManager;
    private ArrayList<HighScore> highScores = new ArrayList<>();
    private static GameView instance;
    private boolean infectionInitialized = false;
    private int totalInfected = 0;
    private JLabel infectedLabel;
    private JPanel infoPanel;
    private CopyOnWriteArrayList<Transport> transports = new CopyOnWriteArrayList<>();
    private boolean allBordersClosedMessageShown = false;
    private boolean isMessageDisplayed = false;
    private boolean transportMovementAllowed = true; 
    private boolean areBordersClosed = false;  
    private boolean gameEnded = false;
    private int finalScore;

    public GameView(String difficulty, int finalScore) {
        this.difficulty = difficulty;
        instance = this;
        initializeCountries();
        openAllBordersOnStart();
        setTitle("Coronavirus AntiPlague - Game");
        setSize(1200, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        System.out.println("Game started with difficulty: " + difficulty + " and score: " + finalScore);
        infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(1, 3));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scoreLabel = new JLabel("Points: 0");
        timerLabel = new JLabel("Time: 0 s");
        virusStatusLabel = new JLabel("Virus Status: Initial");

        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        virusStatusLabel.setHorizontalAlignment(JLabel.CENTER);

        pointsInfoLabel = new JLabel("Points log will appear here.");
        pointsInfoLabel.setHorizontalAlignment(JLabel.CENTER);
        pointsInfoLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        infoPanel.add(scoreLabel);
        infoPanel.add(timerLabel);
        infoPanel.add(virusStatusLabel);
        infoPanel.add(new JLabel());
        infoPanel.add(pointsInfoLabel);
        infoPanel.add(new JLabel());

        addInfectedLabel();
        startInfectedUpdateTimer();

        add(infoPanel, BorderLayout.NORTH);

        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon mapIcon = new ImageIcon("src/main/resources/images/world_map.png");

                if (mapIcon.getImage() != null) {
                    g.drawImage(mapIcon.getImage(), 0, 0, mapIcon.getIconWidth(), mapIcon.getIconHeight(), this);
                } else {
                    g.setColor(Color.RED);
                    g.drawString("Map not found!", getWidth() / 2 - 50, getHeight() / 2);
                }

                g.setColor(Color.RED);
                for (Country country : countries) {
                    Point location = country.getLocation();
                    if (country.isInfected()) {
                        g.setColor(Color.RED);
                        g.fillOval(location.x, location.y, 12, 12);
                    } else {
                        g.setColor(Color.GREEN);
                        g.fillOval(location.x, location.y, 10, 10);
                    }
                    g.drawString(country.getName(), location.x + 12, location.y + 5);
                }

                drawTransports(g);
            }
        };

        mapPanel.setPreferredSize(new Dimension(900, 600));
        add(mapPanel, BorderLayout.CENTER);
        randomlyInfectCountry();
        startGameTimer();
        upgradeManager = UpgradeManager.getInstance();

        
        int startingPoints = DifficultyManager.getFinalScore(difficulty);
        upgradeManager.addPoints(startingPoints);

        
        scoreLabel.setText("Points: " + upgradeManager.getPoints());

        initializeTransports();
        startTransportAnimation();
        startInfectionTimer();
        refreshPlaneRoutes(10);
        printCountries();
        addUpgradePanel(); 

        this.getRootPane().registerKeyboardAction(
                e -> endGame(false),
                KeyStroke.getKeyStroke("ctrl shift Q"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        setVisible(true);
    }



    
    private void initializeCountries() {
        List<Country> countriesCopy = new ArrayList<>(countries);
        countries.add(new Country("China", 1400000000, new Point(920, 280)));  
        countries.add(new Country("USA", 331000000, new Point(235, 260)));     
        countries.add(new Country("France", 67000000, new Point(580, 242)));  
        countries.add(new Country("Germany", 83000000, new Point(604, 223))); 
        countries.add(new Country("Italy", 60000000, new Point(610, 258)));   
        countries.add(new Country("United Kingdom", 67000000, new Point(568, 216)));  
        countries.add(new Country("India", 1390000000, new Point(846, 337)));  
        countries.add(new Country("Japan", 125000000, new Point(1036, 254)));  
        countries.add(new Country("Brazil", 213000000, new Point(390, 480)));  
        countries.add(new Country("Ukraine", 41000000, new Point(675, 230)));  
    }

    
    private void drawCountries(Graphics g) {
        for (Country country : countries) {
            Point location = country.getLocation();

            
            Color countryColor = country.getCountryColor();
            g.setColor(countryColor);  

            
            g.fillOval(location.x, location.y, 12, 12);
            g.setColor(Color.BLACK);
            g.drawString(country.getName(), location.x + 15, location.y + 5);
        }
    }

    
    private void startGameTimer() {
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeElapsed++;
                timerLabel.setText("Time: " + timeElapsed + " s");
                updateVirusProgress();
                checkEndConditions();  
            }
        }, 1000, 1000);
    }

    private void checkBordersClosed() {
        boolean allClosed = true;
        for (Country country : countries) {
            if (!country.areBordersClosed()) {
                allClosed = false;
                break;
            }
        }

        if (allClosed && !isMessageDisplayed) {
            System.out.println("Cannot move: Borders are closed.");
            isMessageDisplayed = true;
        }
    }


    private void someMethodThatChecksBorders() {
        
        checkBordersClosed();
    }

    
    private void updateVirusProgress() {
        if (timeElapsed % 10 == 0) { 
            UpgradeManager.getInstance().addPoints(10); 
            updateScoreLabel(UpgradeManager.getInstance().getPoints()); 
            updatePointsInfo("+10 points for time bonus");

            
            for (Country country : countries) {
                if (!country.areBordersClosed()) {  
                    country.infect(100);  
                }
            }

            repaint(); 
        }

        
        if (timeElapsed == 30) {
            virusStatusLabel.setText("Virus Status: Growing");
        } else if (timeElapsed == 60) {
            virusStatusLabel.setText("Virus Status: Pandemic");
        }
    }


    
    public void stopGame() {
        gameTimer.cancel();
        JOptionPane.showMessageDialog(this, "Game Over! All countries are infected.");

        dispose();
    }

    
    public static void open(String difficulty, int finalScore) {
        new GameView(difficulty, finalScore);  
    }

    
    private void initializeTransports() {
        transports = new CopyOnWriteArrayList<>();
        Random rand = new Random();

        
        addTrainTransportBetween("Ukraine", "China", 3, rand);
        addTrainTransportBetween("Ukraine", "Germany", 2, rand);
        addTrainTransportBetween("Ukraine", "France", 2, rand);
        addTrainTransportBetween("Ukraine", "Italy", 3, rand);
        addTrainTransportBetween("Ukraine", "India", 3, rand);

        addTrainTransportBetween("Germany", "China", 2, rand);
        addTrainTransportBetween("Germany", "Ukraine", 3, rand);
        addTrainTransportBetween("Germany", "France", 2, rand);
        addTrainTransportBetween("Germany", "Italy", 3, rand);
        addTrainTransportBetween("Germany", "India", 2, rand);

        addTrainTransportBetween("France", "China", 2, rand);
        addTrainTransportBetween("France", "Ukraine", 3, rand);
        addTrainTransportBetween("France", "Germany", 2, rand);
        addTrainTransportBetween("France", "Italy", 3, rand);
        addTrainTransportBetween("France", "India", 3, rand);

        addTrainTransportBetween("Italy", "China", 3, rand);
        addTrainTransportBetween("Italy", "Ukraine", 3, rand);
        addTrainTransportBetween("Italy", "Germany", 3, rand);
        addTrainTransportBetween("Italy", "France", 2, rand);
        addTrainTransportBetween("Italy", "India", 3, rand);

        addTrainTransportBetween("India", "China", 3, rand);
        addTrainTransportBetween("India", "Ukraine", 3, rand);
        addTrainTransportBetween("India", "Germany", 2, rand);
        addTrainTransportBetween("India", "France", 2, rand);
        addTrainTransportBetween("India", "Italy", 3, rand);

        
        addShipTransportBetween("China", "Japan", 4, rand);
        addShipTransportBetween("India", "Japan", 4, rand);
        addShipTransportBetween("USA", "India", 4, rand);
        addShipTransportBetween("USA", "Brazil", 4, rand);
        addShipTransportBetween("USA", "UK", 4, rand);
        addShipTransportBetween("UK", "France", 4, rand);
        addShipTransportBetween("USA", "France", 4, rand);
        addShipTransportBetween("China", "France", 4, rand);
        addShipTransportBetween("Japan", "France", 4, rand);
        addShipTransportBetween("Ukraine", "USA", 4, rand);

        
        initializePlanes(5);  
    }

    
    private void addTrainTransportBetween(String startCountry, String endCountry, int speed, Random rand) {
        
        List<List<Point>> routes = new ArrayList<>();
        routes.add(List.of(new Point(679, 227), new Point(784, 224), new Point(901, 266)));  
        routes.add(List.of(new Point(675, 230), new Point(604, 223)));  
        routes.add(List.of(new Point(675, 230), new Point(580, 242)));  
        routes.add(List.of(new Point(675, 230), new Point(602, 250), new Point(610, 258)));  
        routes.add(List.of(new Point(675, 230), new Point(798, 227), new Point(846, 337)));  

        
        List<Point> route = routes.get(rand.nextInt(routes.size()));

        
        Country start = getCountryByName(startCountry);
        Country end = getCountryByName(endCountry);

        if (start != null && end != null) {
            
            transports.add(new Transport(start.getLocation(), end.getLocation(), speed, "train", start, end, route, countries));
        }
    }

    
    private void addShipTransportBetween(String startCountry, String endCountry, int speed, Random rand) {
        
        List<List<Point>> routes = new ArrayList<>();
        routes.add(List.of(new Point(983, 283), new Point(1016, 267)));  
        routes.add(List.of(new Point(848, 391), new Point(893, 459), new Point(960, 444), new Point(951, 412), new Point(1041, 264)));  
        routes.add(List.of(new Point(318, 283), new Point(600, 608), new Point(859, 521), new Point(847, 392)));  
        routes.add(List.of(new Point(318, 283), new Point(433, 441)));  
        routes.add(List.of(new Point(318, 283), new Point(553, 228)));  

        
        List<Point> route = routes.get(rand.nextInt(routes.size()));

        
        Country start = getCountryByName(startCountry);
        Country end = getCountryByName(endCountry);

        if (start != null && end != null) {
            
            transports.add(new Transport(start.getLocation(), end.getLocation(), speed, "ship", start, end, route, countries));
        }
    }

    
    private void initializePlanes(int numberOfPlanes) {
        Random rand = new Random();

        for (int i = 0; i < numberOfPlanes; i++) {
            
            int randomStartIndex = rand.nextInt(countries.size());
            Country start = countries.get(randomStartIndex);

            
            List<Country> availableCountries = new ArrayList<>(countries);
            availableCountries.remove(start);

            
            int randomEndIndex = rand.nextInt(availableCountries.size());
            Country end = availableCountries.get(randomEndIndex);

            
            List<Point> route = List.of(start.getLocation(), end.getLocation());

            
            transports.add(new Transport(
                    start.getLocation(),
                    end.getLocation(),
                    5, 
                    "plane",
                    start,
                    end,
                    route,
                    countries  
            ));

            
            System.out.println("Plane created: " + start.getName() + " -> " + end.getName());
        }
    }

    

    private void addTrainTransportWithRoute(String startName, String endName, int speed, List<Point> route, List<Country> allCountries) {
        Country start = getCountryByName(startName);
        Country end = getCountryByName(endName);
        if (start != null && end != null) {
            
            transports.add(new Transport(start.getLocation(), end.getLocation(), speed, "train", start, end, route, allCountries));
        }
    }

    private void addShipTransportWithRoute(String startName, String endName, int speed, List<Point> route, List<Country> allCountries) {
        Country start = getCountryByName(startName);
        Country end = getCountryByName(endName);
        if (start != null && end != null) {
            
            transports.add(new Transport(start.getLocation(), end.getLocation(), speed, "ship", start, end, route, allCountries));
        }
    }


    private void addTransportBetweenWithRoute(String startName, String endName, int speed, String type, List<Point> route, List<Country> allCountries) {
        Country start = getCountryByName(startName);
        Country end = getCountryByName(endName);
        if (start != null && end != null) {
            transports.add(new Transport(start.getLocation(), end.getLocation(), speed, type, start, end, route, allCountries));  
        }
    }

    private void addTransportBetween(String startName, String endName, int speed, String type, List<Point> route, List<Country> allCountries) {
        Country start = getCountryByName(startName);
        Country end = getCountryByName(endName);

        if (start != null && end != null) {
            
            if (start.areBordersClosed() || end.areBordersClosed()) {
                System.out.println("Cannot add transport as one of the countries has closed borders.");
                return;
            }

            
            transports.add(new Transport(start.getLocation(), end.getLocation(), speed, type, start, end, route, allCountries));

            
            List<Point> reverseRoute = new ArrayList<>(route);
            Collections.reverse(reverseRoute);

            transports.add(new Transport(end.getLocation(), start.getLocation(), speed, type, end, start, reverseRoute, allCountries));
        }
    }


    private void infectCountriesByNames(List<String> countryNames) {
        for (String name : countryNames) {
            Country country = getCountryByName(name);
            if (country != null && !country.isInfected()) {
                country.infect(1000); 
                System.out.println(name + " is infected at the start of the game!");
            } else if (country != null) {
                System.out.println(name + " is already infected! Skipping.");
            } else {
                System.out.println(name + " not found in the country list!");
            }
        }
    }

    private void randomlyInfectCountry() {
        
        switch (difficulty.toLowerCase()) {
            case "easy" -> infectAndInitialize(List.of("China"), 100);
            case "medium" -> infectAndInitialize(List.of("China", "France", "Italy", "India"), 500);
            case "hard" -> infectAndInitialize(List.of("China", "France", "Italy", "Brazil", "USA", "India", "Germany"), 1000);
            default -> infectAndInitialize(List.of("China"), 100);
        }
        repaint(); 
    }

    
    private void infectAndInitialize(List<String> countryNames, int infectionAmount) {
        infectCountriesByNames(countryNames);
        for (String name : countryNames) {
            Country country = getCountryByName(name);
            if (country != null) {
                country.infect(infectionAmount); 
            }
        }
    }

    
    private boolean isTransportVisible(Transport transport) {
        
        return transport.isActive() && transport.getCurrentLocation() != null;
    }


    private void drawTransports(Graphics g) {
        List<Transport> transportsCopy = new ArrayList<>(transports); 
        for (Transport transport : transportsCopy) {
            
            if (isTransportVisible(transport)) {
                Point loc = transport.getCurrentLocation();
                ImageIcon icon = transport.getIcon();

                
                if (icon != null) {
                    g.drawImage(icon.getImage(), loc.x, loc.y, 20, 20, this);  
                } else {
                    
                    g.setColor(Color.BLUE); 
                    g.fillOval(loc.x, loc.y, 10, 10);  
                }
            }
        }

    }

    
    private void startTransportAnimation() {
        System.out.println("Starting transport animation...");
        Timer transportTimer = new Timer();
        transportTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                boolean repaintNeeded = false;
                if (transportMovementAllowed) {  
                    System.out.println("Transport movement is allowed.");
                    for (Transport transport : transports) {
                        
                        if (areBordersOpen(transport)) {
                            System.out.println("Transport is moving...");
                            transport.move();  
                            repaintNeeded = true;
                        } else {
                            System.out.println("Cannot move: Borders are closed.");
                        }
                    }
                } else {
                    System.out.println("Transport movement is not allowed.");
                }

                checkTransportInfection();
                if (repaintNeeded) {
                    repaint();  
                }
            }
        }, 1000, 50);  
    }


    
    
    private boolean areBordersOpen(Transport transport) {
        Country startCountry = transport.getStartCountry();
        Country endCountry = transport.getEndCountry();

        if (startCountry.areBordersClosed() || endCountry.areBordersClosed()) {
            return false;  
        }
        return true;  
    }


    
    private void openAllBordersOnStart() {
        for (Country country : countries) {
            country.setBordersOpen(true);  
        }
        System.out.println("All borders are open at the start.");
    }

    private synchronized void checkTransportInfection() {
        for (Transport transport : transports) {
            if (!transport.isActive() || !transport.canInfect()) continue;

            for (Country target : countries) {
                
                if (target.isInfected()) continue;

                
                if (target.getLocation().distance(transport.getCurrentLocation()) < 15) {
                    int infectionAmount = switch (difficulty.toLowerCase()) {
                        case "easy" -> 50;  
                        case "medium" -> 200;
                        case "hard" -> 500;  
                        default -> 100;
                    };

                    target.infect(infectionAmount);  
                    transport.setInfectedOnce();  
                    System.out.println(target.getName() + " infected!");
                    break;  
                }
            }
        }
    }

    private void checkEndConditions() {
        boolean allInfected = true;

        for (Country country : countries) {
            
            if (country.getInfectedPopulation() < country.getPopulation()) {
                allInfected = false;
                break;
            }
        }

        if (allInfected) {
            JOptionPane.showMessageDialog(this, "Game Over! All countries are fully infected.");
            endGame(false);  
        }

        
        boolean allSaved = true;
        for (Country country : countries) {
            if (country.getInfectedPopulation() > 0) {
                allSaved = false;
                break;
            }
        }

        if (allSaved) {
            JOptionPane.showMessageDialog(this, "Victory! All countries are saved or isolated.");
            endGame(true);  
        }
    }


    
    public void resumeTransportMovement() {
        System.out.println("Attempting to resume transport movement...");

        if (areBordersClosed) {
            openAllBorders();  
            areBordersClosed = false;  
            System.out.println("Borders have been reopened.");
        }

        transportMovementAllowed = true;  
        System.out.println("Transport movement resumed.");
        startTransportAnimation();  
    }



    private void addUpgradePanel() {
        upgradePanel = new JPanel();
        upgradeManager = UpgradeManager.getInstance();
        upgradePanel.setLayout(new GridLayout(2, 5)); 
        upgradePanel.setPreferredSize(new Dimension(600, 200));
        add(upgradePanel, BorderLayout.SOUTH);

        
        for (Upgrade upgrade : upgradeManager.getUpgrades()) {
            String buttonText = getShortenedButtonText(upgrade); 
            JButton button = new JButton();
            JLabel label = new JLabel(buttonText);

            
            label.setFont(new Font("Arial", Font.PLAIN, 10));
            label.setHorizontalAlignment(SwingConstants.CENTER); 
            label.setVerticalAlignment(SwingConstants.CENTER);   

            button.add(label); 
            button.setPreferredSize(new Dimension(110, 40)); 

            button.setToolTipText(upgrade.getDescription());

            
            button.addActionListener(e -> {
                if (upgradeManager.getPoints() >= upgrade.getCost()) {
                    upgradeManager.purchaseUpgrade(upgrade);
                    applyUpgrade(upgrade);
                    JOptionPane.showMessageDialog(this, "Upgrade \"" + upgrade.getName() + "\" successfully purchased!");
                } else {
                    JOptionPane.showMessageDialog(this, "Not enough points to purchase \"" + upgrade.getName() + "\". You need " + (upgrade.getCost() - upgradeManager.getPoints()) + " more points.");
                }
            });

            upgradePanel.add(button);
        }

        
        
        JButton resumeTransportButton = new JButton("Resume Transport - 30 points");
        resumeTransportButton.setPreferredSize(new Dimension(110, 40)); 
        resumeTransportButton.setFont(new Font("Arial", Font.PLAIN, 10)); 

        
        resumeTransportButton.addActionListener(e -> {
            if (upgradeManager.getPoints() >= 30) {  
                upgradeManager.setPoints(upgradeManager.getPoints() - 30);  
                resumeTransportMovement();  
                openAllBorders();  
                JOptionPane.showMessageDialog(this, "Transport movement has been resumed!");
            } else {
                JOptionPane.showMessageDialog(this, "Not enough points to resume transport.");
            }
        });




        
        upgradePanel.add(resumeTransportButton);

        
        upgradePanel.revalidate();
        upgradePanel.repaint();

    }

    
    private void resumeTransport() {
        
        for (Country country : countries) {
            country.setTransportMovement(true); 
        }
        
    }


    
    private String getShortenedButtonText(Upgrade upgrade) {
        String name = upgrade.getName();
        String[] words = name.split(" ");
        String shortName = (words.length > 3) ? words[0] + " " + words[1] + " " + words[2] : name;
        return shortName + " - " + upgrade.getCost() + " points";  
    }

    private void openAllBorders() {
        for (Country country : countries) {
            country.setBordersOpen(true);  
            System.out.println(country.getName() + " borders have been opened.");
        }

        
        checkBordersClosed();  

        JOptionPane.showMessageDialog(null, "All borders have been opened.");
    }


    private void applyUpgrade(Upgrade upgrade) {
        
        switch (upgrade.getName()) {
            case "Close Borders":
                closeAllBorders();
                break;
            case "Fast Healing":
                fastHealing();
                break;
            case "Extra Medical Teams":
                extraMedicalTeams();
                break;
            case "Lower Train Shutdown Threshold":
                lowerTransportThreshold("train");
                break;
            case "Lower Ship Shutdown Threshold":
                lowerTransportThreshold("ship");
                break;
            case "Lower Plane Shutdown Threshold":
                lowerTransportThreshold("plane");
                break;
            case "Boost Resources":
                boostResources();
                break;
            case "Mass Disinfection":
                massDisinfection();
                break;
            case "Quarantine Centers":
                establishQuarantineCenters();
                break;
            case "Resume Transport":
                resumeTransportMovement();  
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown upgrade: " + upgrade.getName(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public void closeAllBorders() {
        for (Country country : countries) {
            country.setBordersClosed(true);  
            System.out.println(country.getName() + " borders have been closed.");
        }

        checkBordersClosed();  

        JOptionPane.showMessageDialog(null, "All borders have been closed.");
    }



    private void fastHealing() {
        for (Country country : countries) {
            if (country.isInfected()) {
                country.heal(500); 
            }
        }
        System.out.println("Fast healing applied to all infected countries.");
    }

    private void extraMedicalTeams() {
        upgradeManager.addPoints(100); 
        System.out.println("Extra medical teams added.");
    }

    private void lowerTransportThreshold(String transportType) {
        for (Country country : countries) {
            country.lowerShutdownThreshold(transportType);
        }
        System.out.println("Transport shutdown threshold lowered for " + transportType);
    }

    private void boostResources() {
        upgradeManager.addPoints(200); 
        System.out.println("Resources have been boosted by 200 points.");
    }

    private void massDisinfection() {
        for (Country country : countries) {
            if (country.isInfected()) {
                country.heal(1000); 
            }
        }
        System.out.println("Mass disinfection applied.");
    }

    private void establishQuarantineCenters() {
        for (Country country : countries) {
            if (country.isInfected()) {
                country.setIsolated(true);
            }
        }
        System.out.println("Quarantine centers established in all infected countries.");
    }

    public void startGame() {
        
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkForEndGame();
            }
        }, 0, 1); 
    }

    
    private void checkForEndGame() {
        boolean allSaved = true;

        for (Country country : countries) {
            if (country.getInfectedPopulation() > 0) {
                allSaved = false;
                break;
            }
        }

        if (allSaved && !gameEnded) {
            endGame(true);  
        } else if (!allSaved && !gameEnded) {
            endGame(false);  
        }
    }

    
    private void endGame(boolean isWin) {
        if (gameEnded) return;  

        gameEnded = true;  
        gameTimer.cancel();  

        String message = isWin ? "Victory! All countries are saved or isolated." : "Defeat! All countries are infected.";
        JOptionPane.showMessageDialog(this, message + "\nTotal Score: " + score);

        
        String playerName = JOptionPane.showInputDialog(this, "Enter your name for the leaderboard:");

        if (playerName != null && !playerName.trim().isEmpty()) {
            
            HighScoreView.showHighScoreView(score, difficulty, timeElapsed, playerName);
        }

        dispose();  
        new MainMenuView();  
    }



    
    private void endGame() {
        endGame(false);  
    }

    private Country getCountryByName(String name) {
        for (Country country : countries) {
            if (country.getName().equalsIgnoreCase(name)) {
                return country;
            }
        }
        return null;
    }
    private void saveHighScores() {
        try (FileWriter writer = new FileWriter("highscores.txt", true)) {
            for (HighScore hs : highScores) {
                writer.write(hs.toString() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Failed to keep the records straight.");
            e.printStackTrace();
        }
    }

    private void initialize() {
        setTitle("Coronavirus AntiPlague - " + difficulty);
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new BorderLayout());

        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 3)); 
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scoreLabel = new JLabel("Points: 0");
        timerLabel = new JLabel("Time: 0 s");
        virusStatusLabel = new JLabel("Virus Status: Initial");

        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        virusStatusLabel.setHorizontalAlignment(JLabel.CENTER);

        
        pointsInfoLabel = new JLabel("Points log will appear here.");
        pointsInfoLabel.setHorizontalAlignment(JLabel.CENTER);
        pointsInfoLabel.setFont(new Font("Arial", Font.ITALIC, 12)); 

        
        infoPanel.add(scoreLabel);
        infoPanel.add(timerLabel);
        infoPanel.add(virusStatusLabel);

        
        infoPanel.add(new JLabel()); 
        infoPanel.add(pointsInfoLabel);
        infoPanel.add(new JLabel()); 

        
        add(infoPanel, BorderLayout.NORTH);


        
        mapPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                ImageIcon mapIcon = new ImageIcon("src/main/resources/images/world_map.png");
                if (mapIcon.getImage() != null) {
                    g.drawImage(mapIcon.getImage(), 0, 0, mapIcon.getIconWidth(), mapIcon.getIconHeight(), this);
                } else {
                    g.setColor(Color.RED);
                    g.drawString("Map not found!", getWidth() / 2 - 50, getHeight() / 2);
                }

                
                for (Country country : countries) {
                    Point location = country.getLocation();

                    double infectionRate = country.getInfectionRate();
                    if (infectionRate >= 1.0) {
                        g.setColor(new Color(139, 0, 0)); 
                    } else if (infectionRate >= 0.8) {
                        g.setColor(new Color(178, 34, 34)); 
                    } else if (infectionRate >= 0.6) {
                        g.setColor(new Color(255, 140, 0)); 
                    } else if (infectionRate >= 0.4) {
                        g.setColor(new Color(128, 0, 128)); 
                    } else if (infectionRate >= 0.2) {
                        g.setColor(new Color(70, 130, 180)); 
                    } else {
                        g.setColor(Color.GREEN); 
                    }

                    g.fillOval(location.x, location.y, 12, 12);
                    g.setColor(Color.BLACK);
                    g.drawString(country.getName(), location.x + 15, location.y + 5);
                }
            }
        };

        mapPanel.setPreferredSize(new Dimension(900, 600));

        add(infoPanel, BorderLayout.NORTH);
        add(mapPanel, BorderLayout.CENTER);


        this.getRootPane().registerKeyboardAction(
                e -> endGame(false),  
                KeyStroke.getKeyStroke("ctrl shift Q"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        setVisible(true);
    }

    
    private void startInfectionTimer() {
        Timer infectionTimer = new Timer();
        infectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkTransportInfection();
                repaint();  
            }
        }, 5000, 5000);  
    }

    public void updateScoreLabel(int newScore) {
        scoreLabel.setText("Points: " + newScore);
    }

    public static GameView getInstance() {
        return instance;
    }

    private void printCountries() {
        System.out.println("List of countries:");
        for (Country country : countries) {
            System.out.println("- " + country.getName());
        }
    }

    private void refreshPlaneRoutes(int intervalInSeconds) {
        Timer routeRefreshTimer = new Timer();
        routeRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                
                List<Transport> tempTransports = new ArrayList<>(transports);
                for (Transport transport : tempTransports) {
                    if ("plane".equals(transport.getType())) {
                        transports.remove(transport);  
                    }
                }
                initializePlanes(5);  
                System.out.println("Plane routes refreshed!");
            }
        }, intervalInSeconds * 1000, intervalInSeconds * 1000);
    }

    
    public void updatePointsInfo(String message) {
        if (pointsInfoLabel != null) {
            pointsInfoLabel.setText(message);

            
            Timer clearMessageTimer = new Timer();
            clearMessageTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    pointsInfoLabel.setText(""); 
                }
            }, 3000);  
        } else {
            System.err.println("pointsInfoLabel is not initialized!");
        }
    }

    private void reduceTransportEvery60Seconds() {
        Timer reductionTimer = new Timer();
        reductionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                
                if (transports.size() > 1) {
                    
                    int halfSize = transports.size() / 2;
                    for (int i = 0; i < halfSize; i++) {
                        transports.remove(0); 
                    }
                    System.out.println("Number of transports reduced by half.");
                }

                
                repaint();
            }
        }, 60000, 60000);  
    }

    
    private void addInfectedLabel() {
        infectedLabel = new JLabel("Infected: 0");
        infectedLabel.setHorizontalAlignment(JLabel.CENTER);
        infectedLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(infectedLabel);  
    }

    
    private void startInfectedUpdateTimer() {
        Timer infectionUpdateTimer = new Timer();
        infectionUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTotalInfected();  
            }
        }, 0, 100);  
    }

    
    private void updateTotalInfected() {
        totalInfected = 0;  

        
        for (Country country : countries) {
            totalInfected += country.getInfectedPopulation();  
        }

        
        updateInfectedLabel(totalInfected);
    }

    
    private void updateInfectedLabel(int infectedCount) {
        infectedLabel.setText("Infected: " + infectedCount);  
    }

}