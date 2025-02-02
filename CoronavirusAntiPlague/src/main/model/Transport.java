package model;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import model.Country;

public class Transport {
    private Point currentLocation;
    private Point startLocation;
    private Point destination;
    private int speed;
    private boolean active;
    private boolean returning;
    private boolean infectedOnce; 
    private ImageIcon icon;
    private Country startCountry;
    private Country endCountry;
    private List<Country> allCountries; 
    private List<Point> route; 
    private int currentRouteIndex; 
    private String type;
    private double infectionRate;
    private List<Transport> transports = new CopyOnWriteArrayList<>();

    
    public Transport(Point startLocation, Point destination, int speed, String type, Country startCountry, Country endCountry, List<Point> route, List<Country> allCountries) {
        this.currentLocation = new Point(startLocation);
        this.startLocation = startLocation;
        this.destination = destination;
        this.currentLocation = new Point(startLocation);
        this.speed = speed;
        this.active = true;
        this.returning = false;
        this.infectedOnce = false;
        this.startCountry = startCountry;
        this.endCountry = endCountry;
        this.route = route; 
        this.allCountries = allCountries; 

        
        String imagePath = "src/main/resources/images/";
        switch (type) {
            case "plane" -> imagePath += "plane.png";
            case "train" -> imagePath += "train.png";
            case "ship" -> imagePath += "ship.png";
        }
        icon = new ImageIcon(imagePath);
    }

    
    public void move() {
        if (!active) return;
        if (route == null || route.isEmpty()) return;

        
        Point target = route.get(currentRouteIndex);

        
        if (!canMove()) {
            System.out.println("Cannot move: Borders are closed.");
            return;  
        }

        if (currentLocation.distance(target) > speed) {
            
            int dx = target.x - currentLocation.x;
            int dy = target.y - currentLocation.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            currentLocation.translate((int) (speed * dx / distance), (int) (speed * dy / distance));
        } else {
            
            currentLocation.setLocation(target);

            
            if (!returning) {
                currentRouteIndex++;
                if (currentRouteIndex >= route.size()) {
                    returning = true; 
                    currentRouteIndex--;
                }
            } else {
                currentRouteIndex--;
                if (currentRouteIndex < 0) {
                    returning = false;
                    currentRouteIndex = 0; 
                    infectedOnce = false; 
                }
            }
        }
    }

    
    public boolean canMove() {
        
        for (Country country : allCountries) {
            if (currentLocation.distance(country.getLocation()) < 15 && country.areBordersClosed()) {
                return false;  
            }
        }
        return true;  
    }

    
    public boolean isActive() {
        return active;
    }

    
    public Point getCurrentLocation() {
        return currentLocation;
    }

    
    public ImageIcon getIcon() {
        return icon;
    }

    
    public boolean canInfect() {
        return !infectedOnce;
    }

    
    public void setInfectedOnce() {
        this.active = false;  
    }

    
    public void deactivate() {
        this.active = false;
    }

    
    public Country getStartCountry() {
        return startCountry;
    }

    public Country getEndCountry() {
        return endCountry;
    }

    
    public List<Point> getRoute() {
        return route;
    }

    public String getType() {
        return type;
    }

    
    public void setDifficulty(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy" -> infectionRate = 0.5; 
            case "medium" -> infectionRate = 1.0;
            case "hard" -> infectionRate = 1.5; 
            default -> infectionRate = 1.0;
        }
    }

    
    public void infectNearbyCountries() {
        for (Country country : allCountries) { 
            if (!country.isInfected() && currentLocation.distance(country.getLocation()) < 15) {
                country.infect(100); 
                System.out.println(country.getName() + " has been infected by transport!");
                break;
            }
        }
    }

    
    public void moveAndInfect() {
        move();
        if (Math.random() < infectionRate) { 
            infectNearbyCountries();
        }
    }
}
