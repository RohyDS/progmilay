package fonction;

import java.util.*;

import model.Point;
import model.Route;

public class DfsService {

    private static void dfsTousChemins(
            Point courant,
            Point destination,
            Set<Point> visites,
            List<Route> cheminCourant,
            List<List<Route>> tousLesChemins
    ) {
        if (courant.equals(destination)) {
            tousLesChemins.add(new ArrayList<>(cheminCourant));
            return;
        }

        visites.add(courant);

        // 🔁 Parcours bidirectionnel
        for (Route r : courant.getRoutes()) {

            Point suivant;

            if (r.getDepart().equals(courant)) {
                suivant = r.getArrivee();     // sens normal
            } else if (r.getArrivee().equals(courant)) {
                suivant = r.getDepart();      // sens inverse
            } else {
                continue;
            }

            if (!visites.contains(suivant)) {
                cheminCourant.add(r);
                dfsTousChemins(suivant, destination, visites, cheminCourant, tousLesChemins);
                cheminCourant.remove(cheminCourant.size() - 1);
            }
        }

        visites.remove(courant);
    }

    public static List<List<Route>> trouverTousLesChemins(Point depart, Point arrivee) {
        List<List<Route>> tousLesChemins = new ArrayList<>();
        dfsTousChemins(depart, arrivee, new HashSet<>(), new ArrayList<>(), tousLesChemins);
        return tousLesChemins;
    }
}
