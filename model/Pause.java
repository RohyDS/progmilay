package model;

public class Pause {
    private int id;
    private int idRoute;
    private double positionKm;      // Position de la pause sur la route (en km)
    private String heureDebut;      // Heure de début de la pause (format HH:mm)
    private String heureFin;        // Heure de fin de la pause (format HH:mm)
    
    public Pause() {
    }
    
    public Pause(int id, int idRoute, double positionKm, String heureDebut, String heureFin) {
        this.id = id;
        this.idRoute = idRoute;
        this.positionKm = positionKm;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }
    
    /**
     * Calcule la durée de la pause en heures
     */
    public double getDureeHeures() {
        try {
            String[] debut = heureDebut.split(":");
            String[] fin = heureFin.split(":");
            
            int heuresDebut = Integer.parseInt(debut[0]);
            int minutesDebut = Integer.parseInt(debut[1]);
            
            int heuresFin = Integer.parseInt(fin[0]);
            int minutesFin = Integer.parseInt(fin[1]);
            
            int totalMinutesDebut = heuresDebut * 60 + minutesDebut;
            int totalMinutesFin = heuresFin * 60 + minutesFin;
            
            // Gérer le cas où la pause traverse minuit
            if (totalMinutesFin < totalMinutesDebut) {
                totalMinutesFin += 24 * 60;
            }
            
            int dureeMinutes = totalMinutesFin - totalMinutesDebut;
            return dureeMinutes / 60.0; // Convertir en heures
            
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * Calcule la durée de la pause en minutes
     */
    public int getDureeMinutes() {
        return (int)(getDureeHeures() * 60);
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdRoute() {
        return idRoute;
    }
    
    public void setIdRoute(int idRoute) {
        this.idRoute = idRoute;
    }
    
    public double getPositionKm() {
        return positionKm;
    }
    
    public void setPositionKm(double positionKm) {
        this.positionKm = positionKm;
    }
    
    public String getHeureDebut() {
        return heureDebut;
    }
    
    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }
    
    public String getHeureFin() {
        return heureFin;
    }
    
    public void setHeureFin(String heureFin) {
        this.heureFin = heureFin;
    }
    
    @Override
    public String toString() {
        return String.format("Pause au km %.2f (%s → %s, durée: %d min)", 
            positionKm, heureDebut, heureFin, getDureeMinutes());
    }
}