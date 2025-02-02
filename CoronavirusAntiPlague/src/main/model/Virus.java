package model;

public class Virus {
    private int spreadRate;  
    private int severity;    
    private boolean active;  

    public Virus(int spreadRate, int severity) {
        this.spreadRate = spreadRate;
        this.severity = severity;
        this.active = true;
    }

    public int getSpreadRate() {
        return spreadRate;
    }

    public int getSeverity() {
        return severity;
    }

    public boolean isActive() {
        return active;
    }

    public void mutate() {
        spreadRate += 5;
        severity += 2;
    }

    public void stop() {
        active = false;
    }
}
