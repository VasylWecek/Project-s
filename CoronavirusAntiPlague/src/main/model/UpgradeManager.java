package model;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import view.GameView;
import model.Upgrade;

public class UpgradeManager {
    private static UpgradeManager instance; 
    private List<Upgrade> upgrades;
    private List<Upgrade> purchasedUpgrades;
    private int points;

    public UpgradeManager() {
        this.upgrades = new ArrayList<>();
        this.purchasedUpgrades = new ArrayList<>();
        this.points = 0;

        
        initializeUpgrades();
    }

    public static UpgradeManager getInstance() {
        if (instance == null) {
            instance = new UpgradeManager();
        }
        return instance;
    }

    private void initializeUpgrades() {
        
        System.out.println("Upgrades size before initialization: " + upgrades.size());

        upgrades.add(new Upgrade("Close Borders", 100, "Close borders for infected countries."));
        upgrades.add(new Upgrade("Fast Healing", 150, "Speeds up healing in infected countries."));
        upgrades.add(new Upgrade("Extra Medical Teams", 200, "Increases resources for treating the infected."));
        upgrades.add(new Upgrade("Lower Train Shutdown Threshold", 50, "Reduces train shutdown threshold by 10%."));
        upgrades.add(new Upgrade("Lower Ship Shutdown Threshold", 50, "Reduces ship shutdown threshold by 10%."));
        upgrades.add(new Upgrade("Lower Plane Shutdown Threshold", 50, "Reduces plane shutdown threshold by 10%."));
        upgrades.add(new Upgrade("Boost Resources", 300, "Adds extra resources to manage outbreaks."));
        upgrades.add(new Upgrade("Mass Disinfection", 250, "Massively heals infected populations."));
        upgrades.add(new Upgrade("Quarantine Centers", 400, "Prevents further spread in isolated regions."));

        
        System.out.println("Upgrades size after initialization: " + upgrades.size());
    }

    public List<Upgrade> getUpgrades() {
        return upgrades;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int amount) {
        points += amount; 
        GameView.getInstance().updateScoreLabel(points); 
    }

    public boolean purchaseUpgrade(Upgrade upgrade) {
        if (points >= upgrade.getCost()) {
            points -= upgrade.getCost();
            purchasedUpgrades.add(upgrade);
            JOptionPane.showMessageDialog(null, "Upgrade " + upgrade.getName() + " purchased!");
            GameView.getInstance().updateScoreLabel(points); 
            return true;
        } else {
            int needed = upgrade.getCost() - points;
            JOptionPane.showMessageDialog(null, "Not enough points to purchase \"" + upgrade.getName() +
                    "\". You need " + needed + " more points.");
            return false;
        }
    }

    public void cancelCloseBordersUpgrade(List<model.Country> countries) {
        System.out.println("Checking if 'Close Borders' upgrade was purchased...");
        if (isUpgradePurchased(new Upgrade("Close Borders", 100, ""))) {
            System.out.println("Close Borders upgrade was purchased.");
            for (model.Country country : countries) {
                country.setBordersOpen(true); 
                System.out.println("Borders for " + country.getName() + " have been reopened.");
            }
        } else {
            System.out.println("Close Borders upgrade has not been purchased.");
        }
    }



    public boolean spendPointsForBorders(int cost) {
        if (points < cost) {
            JOptionPane.showMessageDialog(null, "Not enough points to toggle borders. You need " + (cost - points) + " more points.");
            return false;
        }
        points -= cost;
        JOptionPane.showMessageDialog(null, "Borders toggled for " + cost + " points!");
        GameView.getInstance().updateScoreLabel(points);
        return true;
    }

    public void setPoints(int points) {
        this.points = points;
        GameView.getInstance().updateScoreLabel(this.points);  
    }


    public boolean isUpgradePurchased(Upgrade upgrade) {
        System.out.println("Checking if " + upgrade.getName() + " is purchased.");
        return purchasedUpgrades.contains(upgrade);
    }

}
