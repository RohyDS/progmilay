
-- ==========================================
-- Séquences
-- ==========================================
CREATE SEQUENCE seq_point START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_route START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_obstacle START WITH 1 INCREMENT BY 1;

-- ==========================================
-- Table point
-- ==========================================
CREATE TABLE point (
    id NUMBER PRIMARY KEY,
    nom VARCHAR2(50),
    x NUMBER NOT NULL,
    y NUMBER NOT NULL
);

-- ==========================================
-- Table route
-- ==========================================
CREATE TABLE route (
    id NUMBER PRIMARY KEY,
    nom VARCHAR2(50),
    idpointdepart NUMBER NOT NULL,
    idpointdesti NUMBER NOT NULL,
    distance NUMBER(6,2),
    largeur NUMBER(5,2),
    CONSTRAINT fk_route_point_depart FOREIGN KEY (idpointdepart) REFERENCES point(id),
    CONSTRAINT fk_route_point_desti FOREIGN KEY (idpointdesti) REFERENCES point(id)
);

-- ==========================================
-- Table sous_lalana
-- ==========================================


-- ==========================================
-- Table obstacle
-- ==========================================
CREATE TABLE obstacle (
    id NUMBER PRIMARY KEY,
    taux_ralentissement NUMBER(5,2),
    km_depart NUMBER(7,2),
    km_arrivee NUMBER(7,2),
    idroute NUMBER NOT NULL,
    CONSTRAINT fk_obstacle_route FOREIGN KEY (idroute) REFERENCES route(id),
    CONSTRAINT chk_taux CHECK (taux_ralentissement > 0 AND taux_ralentissement <= 100),
    CONSTRAINT chk_km CHECK (km_arrivee > km_depart)
);

-- ==========================================
-- Table voiture (Postgres ou Oracle avec sequence)     PGSQL
-- ==========================================
CREATE TABLE voiture (
    id NUMBER PRIMARY KEY,
    vitesse_max NUMBER(5,2),
    largeur NUMBER(4,2),
    longueur NUMBER(4,2),
    nom VARCHAR2(100),
    type VARCHAR2(30)
);




-- ==========================================
-- Insertion points
-- ==========================================
INSERT INTO point (id, nom, x, y) VALUES (seq_point.NEXTVAL, 'Antananarivo', 10, 20);  
INSERT INTO point (id, nom, x, y) VALUES (seq_point.NEXTVAL, 'Antsirabe', 50, 80);   
INSERT INTO point (id, nom, x, y) VALUES (seq_point.NEXTVAL, 'Sambaina', 100, 120);  
INSERT INTO point (id, nom, x, y) VALUES (seq_point.NEXTVAL, 'Ampefy', 150, 60);     

-- ==========================================
-- Insertion routes
-- ==========================================
-- ==========================================
-- Insertion routes (IDs points +1)
-- ==========================================
INSERT INTO route (id, nom, idpointdepart, idpointdesti, distance, largeur) 
VALUES (seq_route.NEXTVAL, 'Antananarivo-Sambaina', 2, 4, 200, 3.50);

INSERT INTO route (id, nom, idpointdepart, idpointdesti, distance, largeur) 
VALUES (seq_route.NEXTVAL, 'Sambaina-Antsirabe', 4, 3, 120, 4.00);

INSERT INTO route (id, nom, idpointdepart, idpointdesti, distance, largeur) 
VALUES (seq_route.NEXTVAL, 'Antananarivo-Ampefy', 2, 5, 163, 3.00);

INSERT INTO route (id, nom, idpointdepart, idpointdesti, distance, largeur) 
VALUES (seq_route.NEXTVAL, 'Ampefy-sambaina', 5, 4, 88, 3.75);






-- ==========================================
-- Insertion obstacles
-- ==========================================
INSERT INTO obstacle (id, taux_ralentissement, km_depart, km_arrivee, idroute) VALUES (seq_obstacle.NEXTVAL, 11, 56, 70, 3);


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

-- Si vous devez recréer la table complètement :
/*
DROP TABLE IF EXISTS voiture CASCADE;

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

-- Insertion des données avec reservoir et consommation
INSERT INTO voiture (vitesse_max, largeur, longueur, nom, type, reservoir, consommation) 
VALUES (120, 1.8, 4.2, 'Toyota Corolla', 'voiture', 32, 6.5);

INSERT INTO voiture (vitesse_max, largeur, longueur, nom, type, reservoir, consommation) 
VALUES (90, 2.5, 6.0, 'Camion Renault', 'camion', 100, 15.0);

INSERT INTO voiture (vitesse_max, largeur, longueur, nom, type, reservoir, consommation) 
VALUES (100, 2.0, 4.5, 'Bus Mercedes', 'bus', 120, 20.0);

INSERT INTO voiture (vitesse_max, largeur, longueur, nom, type, reservoir, consommation) 
VALUES (180, 1.7, 4.0, 'Porsche 911', 'sportive', 64, 12.0);

INSERT INTO voiture (vitesse_max, largeur, longueur, nom, type, reservoir, consommation) 
VALUES (110, 1.9, 4.5, 'Peugeot 308', 'voiture', 45, 5.8);
*/



drop sequence seq_point;
drop sequence seq_route;
drop sequence seq_obstacle;

drop table obstacle;
drop table route;
drop table point;


CREATE SEQUENCE seq_pause START WITH 1 INCREMENT BY 1;
CREATE TABLE pause (
    id NUMBER PRIMARY KEY,
    id_route NUMBER NOT NULL,
    position_km NUMBER(10,2),
    heure_debut VARCHAR2(5),  -- Format HH:mm
    heure_fin VARCHAR2(5),    -- Format HH:mm
    FOREIGN KEY (id_route) REFERENCES route(id)
);

INSERT INTO pause (id,id_route, position_km, heure_debut, heure_fin) 
VALUES (seq_pause.NEXTVAL, 2, 70, '13:00', '13:30');
INSERT INTO pause (id,id_route, position_km, heure_debut, heure_fin) 
VALUES (seq_pause.NEXTVAL, 3,60, '14:00', '15:00');
INSERT INTO pause (id,id_route, position_km, heure_debut, heure_fin) 
VALUES (seq_pause.NEXTVAL,4, 30, '10:30', '10:45');
INSERT INTO pause (id,id_route, position_km, heure_debut, heure_fin) 
VALUES (seq_pause.NEXTVAL,5, 80, '15:00', '15:20');