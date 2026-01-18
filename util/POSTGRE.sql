// postgresql

-- Création de la table
CREATE TABLE lalana(
    id_lalana SERIAL PRIMARY KEY,
    nom VARCHAR(100),
    largeur INTEGER,
    distance_km INTEGER
);

CREATE TABLE voiture (
    id SERIAL PRIMARY KEY,
    vitesse_max DECIMAL(10,2) NOT NULL,
    largeur DECIMAL(10,2) NOT NULL,
    longueur DECIMAL(10,2) NOT NULL,
    nom VARCHAR(100) NOT NULL,  
    type VARCHAR(50) NOT NULL,
    reservoir DECIMAL(10,2) NOT NULL DEFAULT 50.0,
    consommation DECIMAL(10,2) NOT NULL DEFAULT 7.0
);

-- Insertion des données
INSERT INTO lalana (nom, largeur, distance_km) VALUES
('RN2', 20, 200),
('RN44', 18, 150),
('RN1', 25, 350),
('RN7', 22, 400);

-- Vérifier les données
SELECT * FROM lalana;

-- ==========================================
-- Insertion voitures     PGSQL
-- ==========================================
INSERT INTO voiture (vitesse_max, largeur, longueur, nom, type) 
VALUES (120, 1.8, 4.2, 'Toyota Corolla', 'voiture');

INSERT INTO voiture (vitesse_max, largeur, longueur, nom, type) 
VALUES (90, 2.5, 6.0, 'Camion Renault', 'camion');

INSERT INTO voiture (vitesse_max, largeur, longueur, nom, type) 
VALUES (100, 2.0, 4.5, 'Bus Mercedes', 'bus');





-- Ajout des colonnes reservoir et consommation à la table voiture
ALTER TABLE voiture ADD COLUMN reservoir DECIMAL(10,2) DEFAULT 50.0;
ALTER TABLE voiture ADD COLUMN consommation DECIMAL(10,2) DEFAULT 7.0;

-- Mise à jour des voitures existantes avec des valeurs réalistes
UPDATE voiture 
SET reservoir = 50, consommation = 10
WHERE nom = 'Toyota Corolla';

UPDATE voiture 
SET largeur = 0.1
WHERE nom = 'Toyota Corolla';

UPDATE voiture 
SET largeur = 0.3
WHERE nom = 'Camion Renault';

UPDATE voiture 
SET largeur = 0.2
WHERE nom = 'Bus Mercedes';

UPDATE voiture 
SET reservoir = 100, consommation = 15.0 
WHERE nom = 'Camion Renault';

UPDATE voiture 
SET reservoir = 250, consommation = 20.0 
WHERE nom = 'Bus Mercedes';
//