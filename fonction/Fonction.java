package fonction;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import model.Obstacle;
import model.Pause;
import model.Route;
import model.Voiture;
import model.ResultatTrajet;

public class Fonction {
    
    /**
     * Calcule la durée de trajet sur une route (sans les pauses)
     */
    public static double calculerDuree(Voiture voiture, Route route, List<Obstacle> obstacles) {
        double duree = 0.0;
        double vitesseNormale = voiture.getVitesseMoyenne();
        double longueurRoute = route.getDistance();

        obstacles.sort((o1, o2) -> Double.compare(o1.getKilometreDepart(), o2.getKilometreDepart()));

        double kmCourant = 0.0;
        for (Obstacle o : obstacles) {
            if (o.getIdRoute() != route.getId()) continue;

            if (o.getKilometreDepart() > kmCourant) {
                double distanceLibre = o.getKilometreDepart() - kmCourant;
                duree += distanceLibre / vitesseNormale;
                kmCourant = o.getKilometreDepart();
            }

            double obstacleStart = Math.max(kmCourant, o.getKilometreDepart());
            double obstacleEnd = Math.min(o.getKilometreArrivee(), longueurRoute);
            if (obstacleEnd > obstacleStart) {
                double distanceObstacle = obstacleEnd - obstacleStart;
                double vitesseObstacle = vitesseNormale * (100.0 - o.getTauxRetablissement()) / 100.0;
                duree += distanceObstacle / vitesseObstacle;
                kmCourant = obstacleEnd;
            }
        }

        if (kmCourant < longueurRoute) {
            double distanceRestante = longueurRoute - kmCourant;
            duree += distanceRestante / vitesseNormale;
        }

        return duree;
    }

    /**
     * Calcule l'heure d'arrivée à une position km donnée sur une route
     */
    private static LocalTime calculerHeureAPositionKm(
            LocalTime heureDebutRoute,
            double positionKm,
            Voiture voiture,
            Route route,
            List<Obstacle> obstacles) {
        
        if (positionKm <= 0) return heureDebutRoute;
        if (positionKm >= route.getDistance()) {
            double dureeRoute = calculerDuree(voiture, route, obstacles);
            return heureDebutRoute.plusMinutes((long)(dureeRoute * 60));
        }

        // Calculer le temps pour atteindre positionKm
        double vitesse = voiture.getVitesseMoyenne();
        double duree = 0.0;
        double kmCourant = 0.0;

        // Trier les obstacles
        List<Obstacle> obstaclesRoute = new ArrayList<>();
        for (Obstacle o : obstacles) {
            if (o.getIdRoute() == route.getId()) obstaclesRoute.add(o);
        }
        obstaclesRoute.sort((o1, o2) -> Double.compare(o1.getKilometreDepart(), o2.getKilometreDepart()));

        for (Obstacle o : obstaclesRoute) {
            // Si on a déjà atteint la position, arrêter
            if (kmCourant >= positionKm) break;

            // Portion avant obstacle
            if (o.getKilometreDepart() > kmCourant) {
                double distanceLibre = Math.min(o.getKilometreDepart(), positionKm) - kmCourant;
                if (distanceLibre > 0) {
                    duree += distanceLibre / vitesse;
                    kmCourant += distanceLibre;
                }
            }

            // Si on a atteint la position, arrêter
            if (kmCourant >= positionKm) break;

            // Portion avec obstacle
            double obstacleStart = Math.max(kmCourant, o.getKilometreDepart());
            double obstacleEnd = Math.min(o.getKilometreArrivee(), Math.min(positionKm, route.getDistance()));
            
            if (obstacleEnd > obstacleStart) {
                double distanceObstacle = obstacleEnd - obstacleStart;
                double vitesseObstacle = vitesse * (100.0 - o.getTauxRetablissement()) / 100.0;
                duree += distanceObstacle / vitesseObstacle;
                kmCourant = obstacleEnd;
            }
        }

        // Portion finale (après tous les obstacles)
        if (kmCourant < positionKm) {
            double distanceRestante = positionKm - kmCourant;
            duree += distanceRestante / vitesse;
        }

        return heureDebutRoute.plusMinutes((long)(duree * 60));
    }

    /**
     * Calcule la pause effective basée sur POSITION KM + HEURE
     */
    private static double calculerPauseAPositionKm(
            LocalTime heureDebutRoute,
            Voiture voiture,
            Route route,
            List<Obstacle> obstacles,
            Pause pause) {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime pauseDebut = LocalTime.parse(pause.getHeureDebut(), formatter);
        LocalTime pauseFin = LocalTime.parse(pause.getHeureFin(), formatter);

        // Calculer l'heure d'arrivée à la position km de la pause
        LocalTime heureArriveePause = calculerHeureAPositionKm(
            heureDebutRoute, 
            pause.getPositionKm(), 
            voiture, 
            route, 
            obstacles
        );

        // Vérifier si on arrive pendant la plage horaire de la pause
        if (heureArriveePause.isBefore(pauseDebut)) {
            return 0.0;
        }

        if (heureArriveePause.isAfter(pauseFin) || heureArriveePause.equals(pauseFin)) {
            return 0.0;
        }

        // Arrivée pendant la pause - dure de l'arrivée jusqu'à la fin
        long minutes = Duration.between(heureArriveePause, pauseFin).toMinutes();
        return minutes / 60.0;
    }

    /**
     * Calcule l'heure d'arrivée en tenant compte des obstacles et des pauses
     */
    public static ResultatTrajet calculerHeureArrivee(
            Voiture voiture,
            List<Route> chemin,
            List<Obstacle> tousObstacles,
            List<Pause> toutesPauses,
            String heureDepart) {

        ResultatTrajet resultat = new ResultatTrajet();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime heureActuelle = LocalTime.parse(heureDepart, formatter);

        double dureeTrajetTotal = 0.0;
        double dureePausesTotal = 0.0;  // ✅ Cumul global des pauses

        for (Route r : chemin) {
            // Récupérer obstacles de cette route
            List<Obstacle> obstaclesRoute = new ArrayList<>();
            for (Obstacle o : tousObstacles) {
                if (o.getIdRoute() == r.getId()) {
                    obstaclesRoute.add(o);
                }
            }

            // Calculer durée de conduite sur cette route (sans pauses)
            double dureeRoute = calculerDuree(voiture, r, obstaclesRoute);
            dureeTrajetTotal += dureeRoute;

            // Heure de début sur cette route
            LocalTime debutRoute = heureActuelle;

            // Récupérer et trier les pauses de cette route par position
            List<Pause> pausesRoute = new ArrayList<>();
            for (Pause p : toutesPauses) {
                if (p.getIdRoute() == r.getId()) {
                    pausesRoute.add(p);
                }
            }
            pausesRoute.sort((p1, p2) -> Double.compare(p1.getPositionKm(), p2.getPositionKm()));

            // ✅ Calculer les pauses et cumuler AVANT d'avancer l'heure
            double dureePausesRoute = 0.0;
            
            for (Pause p : pausesRoute) {
                double pauseEffective = calculerPauseAPositionKm(
                    debutRoute, 
                    voiture, 
                    r, 
                    obstaclesRoute, 
                    p
                );
                
                if (pauseEffective > 0) {
                    dureePausesRoute += pauseEffective;
                    
                    // Calculer l'heure d'arrivée à la position de la pause
                    LocalTime heureArriveePause = calculerHeureAPositionKm(
                        debutRoute, p.getPositionKm(), voiture, r, obstaclesRoute
                    );
                    
                    resultat.detailsPauses.add(String.format(
                        "☕ Pause au km %.1f de %s : arrivée à %s, pause %s → %s (%.0f min)",
                        p.getPositionKm(),
                        r.getNom(),
                        heureArriveePause.format(formatter),
                        p.getHeureDebut(),
                        p.getHeureFin(),
                        pauseEffective * 60
                    ));
                }
            }

            // ✅ Cumuler les pauses au total global
            dureePausesTotal += dureePausesRoute;

            // ✅ Avancer l'heure : durée route + pauses de cette route
            heureActuelle = debutRoute.plusMinutes((long) ((dureeRoute + dureePausesRoute) * 60));
        }

        // ✅ Affecter directement les valeurs calculées
        resultat.dureeTrajet = dureeTrajetTotal;
        resultat.dureePauses = dureePausesTotal;
        resultat.dureeTotal = dureeTrajetTotal + dureePausesTotal;
        resultat.heureArrivee = heureActuelle.format(formatter);

        return resultat;
    }

    /**
     * Formate une durée en heures au format "Xh YYmin"
     */
    public static String formaterDuree(double heures) {
        int h = (int) heures;
        int min = (int) ((heures - h) * 60);
        return String.format("%dh %02dmin", h, min);
    }
}