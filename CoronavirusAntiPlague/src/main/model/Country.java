package model;

import java.awt.*;

public class Country {
    private String name;
    private int population;
    private Point location;
    private boolean infected;
    private boolean bordersClosed;
    private int infectedPopulation;
    private boolean isolated;
    private int trainShutdownThreshold;
    private int shipShutdownThreshold;
    private int planeShutdownThreshold;
    private boolean transportActive;
    private boolean transportMovement; 
    private String transportStatus;
    private boolean bordersOpen;
    private boolean areBordersClosed;

    public Country(String name, int population, Point location) {
        this.name = name;
        this.population = population;
        this.location = location;
        this.infected = false;
        this.bordersClosed = false;
        this.infectedPopulation = 0;
        this.isolated = false;
        this.trainShutdownThreshold = 30;  
        this.shipShutdownThreshold = 50;   
        this.planeShutdownThreshold = 70;  
        this.transportMovement = false; 
        this.transportStatus = "Transport Stopped";
    }

    

    public void setBordersOpen(boolean isOpen) {
        this.areBordersClosed = !isOpen;  
        System.out.println(this.name + " borders set to open: " + !this.areBordersClosed);
    }

    public boolean areBordersOpen() {
        return this.bordersOpen;
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public Point getLocation() {
        return location;
    }

    public boolean isInfected() {
        return infected;
    }

    public void infect(int amount) {
        if (!bordersClosed) {
            infected = true;
            infectedPopulation += amount;
        }
    }

    public boolean areBordersClosed() {
        return this.areBordersClosed;  
    }

    public void setBordersClosed(boolean isClosed) {
        this.areBordersClosed = isClosed;  
        System.out.println(this.name + " borders set to closed: " + this.areBordersClosed);
    }


    public void toggleBorders() {
        this.bordersClosed = !this.bordersClosed;  
    }

    public void openBorders() {
        if (!bordersClosed) return;
        bordersClosed = false;
        System.out.println(name + " borders have been opened.");
    }

    public void closeBorders() {
        if (bordersClosed) return;
        bordersClosed = true;
        System.out.println(name + " borders have been closed.");
    }

    public int getInfectedPopulation() {
        return infectedPopulation;
    }

    public boolean isIsolated() {
        return isolated;
    }

    public void setIsolated(boolean isolated) {
        this.isolated = isolated;
    }

    public boolean shouldShutdownTrains() {
        return (infectedPopulation / (double) population) * 100 >= trainShutdownThreshold;
    }

    public boolean shouldShutdownShips() {
        return (infectedPopulation / (double) population) * 100 >= shipShutdownThreshold;
    }

    public boolean shouldShutdownPlanes() {
        return (infectedPopulation / (double) population) * 100 >= planeShutdownThreshold;
    }

    public void lowerShutdownThreshold(String transportType) {
        switch (transportType.toLowerCase()) {
            case "train":
                lowerTrainShutdownThreshold();
                break;
            case "ship":
                lowerShipShutdownThreshold();
                break;
            case "plane":
                lowerPlaneShutdownThreshold();
                break;
            default:
                System.out.println("Unknown transport type: " + transportType);
        }
    }

    public void lowerTrainShutdownThreshold() {
        if (trainShutdownThreshold > 0) {
            trainShutdownThreshold -= 5; 
            System.out.println("Train shutdown threshold for " + name + " lowered to " + trainShutdownThreshold);
        }
    }

    public void lowerShipShutdownThreshold() {
        if (shipShutdownThreshold > 0) {
            shipShutdownThreshold -= 5; 
            System.out.println("Ship shutdown threshold for " + name + " lowered to " + shipShutdownThreshold);
        }
    }

    public boolean getTransportMovement() {
        return transportMovement;
    }

    public void setTransportMovement(boolean transportMovement) {
        this.transportMovement = transportMovement;
    }

    public String getTransportStatus() {
        return transportStatus;
    }

    public void setTransportStatus(String transportStatus) {
        this.transportStatus = transportStatus;
    }

    public boolean isTransportActive() {
        return transportActive;
    }

    public void lowerPlaneShutdownThreshold() {
        if (planeShutdownThreshold > 0) {
            planeShutdownThreshold -= 5; 
            System.out.println("Plane shutdown threshold for " + name + " lowered to " + planeShutdownThreshold);
        }
    }


    
    public Color getCountryColor() {
        double infectionRate = getInfectionRate();
        if (infectionRate >= 100) {
            return new Color(139, 0, 0); 
        } else if (infectionRate >= 80) {
            return new Color(125, 44, 0); 
        } else if (infectionRate >= 60) {
            return new Color(255, 165, 0); 
        } else if (infectionRate >= 40) {
            return new Color(128, 0, 128); 
        } else if (infectionRate >= 20) {
            return Color.BLUE; 
        } else {
            return Color.GREEN; 
        }
    }

    public double getInfectionRate() {
        if (population == 0) return 0;
        return (double) infectedPopulation / population * 100;
    }

    public void heal(int amount) {
        int healed = Math.min(amount, infectedPopulation);
        infectedPopulation -= healed;
        if (infectedPopulation <= 0) {
            infected = false;
            infectedPopulation = 0;
        }
        model.UpgradeManager.getInstance().addPoints(healed);
        view.GameView.getInstance().updatePointsInfo("Country " + name + " healed by " + healed + " people.");
    }
}
