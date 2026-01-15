package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour stocker le résultat d'un calcul de trajet
 * avec obstacles et pauses
 */
public class ResultatTrajet {
    public double dureeTrajet;      // Durée de conduite en heures
    public double dureePauses;      // Durée totale des pauses en heures
    public double dureeTotal;       // Durée totale (trajet + pauses) en heures
    public String heureArrivee;     // Heure d'arrivée (format HH:mm)
    public List<String> detailsPauses = new ArrayList<>();
    
    public ResultatTrajet() {
    }
    
    /**
     * Retourne un résumé formaté du trajet
     */
    public String getResume() {
        int heuresTrajet = (int) dureeTrajet;
        int minutesTrajet = (int) ((dureeTrajet - heuresTrajet) * 60);
        
        int heuresPauses = (int) dureePauses;
        int minutesPauses = (int) ((dureePauses - heuresPauses) * 60);
        
        int heuresTotal = (int) dureeTotal;
        int minutesTotal = (int) ((dureeTotal - heuresTotal) * 60);
        
        return String.format(
            "⏱️ Durée de conduite : %dh %02dmin\n" +
            "☕ Durée des pauses : %dh %02dmin\n" +
            "🕐 Durée totale : %dh %02dmin\n" +
            "🏁 Heure d'arrivée : %s",
            heuresTrajet, minutesTrajet,
            heuresPauses, minutesPauses,
            heuresTotal, minutesTotal,
            heureArrivee
        );
    }
}