//oracle 


-- Création de la table
CREATE TABLE voiture(
    id_voiture VARCHAR2(10) PRIMARY KEY,
    nom VARCHAR2(100),
    marque VARCHAR2(100),
    vitesse_max INTEGER,
    largeur INTEGER,
    longueur INTEGER
);

-- Création de la séquence
CREATE SEQUENCE seq_voiture
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

-- Création du trigger pour remplir automatiquement id_voiture
CREATE OR REPLACE TRIGGER trg_voiture_id
BEFORE INSERT ON voiture
FOR EACH ROW
BEGIN
    :NEW.id_voiture := 'V' || TO_CHAR(seq_voiture.NEXTVAL, 'FM000');
END;
/

-- Insertion des données sans préciser l'id_voiture
INSERT INTO voiture (nom, marque, vitesse_max, largeur, longueur) VALUES ('Supra', 'Toyota', 150, 10, 50);
INSERT INTO voiture (nom, marque, vitesse_max, largeur, longueur) VALUES ('Mustang', 'Ford', 170, 10, 80);
INSERT INTO voiture (nom, marque, vitesse_max, largeur, longueur) VALUES ('Civic', 'Honda', 120, 10, 40);
INSERT INTO voiture (nom, marque, vitesse_max, largeur, longueur) VALUES ('Model S', 'Tesla', 160, 10, 45);

-- Vérification
SELECT * FROM voiture;

CREATE SEQUENCE seq_point START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_route START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_obstacle START WITH 1 INCREMENT BY 1;

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
INSERT INTO obstacle (id, taux_ralentissement, km_depart, km_arrivee, idroute) VALUES (seq_obstacle.NEXTVAL, 11, 56, 70, 3);

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
INSERT INTO route (id, nom, idpointdepart, idpointdesti, distance, largeur) 
VALUES (seq_route.NEXTVAL, 'Antananarivo-Sambaina', 2, 4, 200, 3.50);

INSERT INTO route (id, nom, idpointdepart, idpointdesti, distance, largeur) 
VALUES (seq_route.NEXTVAL, 'Sambaina-Antsirabe', 4, 3, 120, 4.00);

INSERT INTO route (id, nom, idpointdepart, idpointdesti, distance, largeur) 
VALUES (seq_route.NEXTVAL, 'Antananarivo-Ampefy', 2, 5, 163, 3.00);

INSERT INTO route (id, nom, idpointdepart, idpointdesti, distance, largeur) 
VALUES (seq_route.NEXTVAL, 'Ampefy-sambaina', 5, 4, 88, 3.75);

CREATE TABLE point (
    id NUMBER PRIMARY KEY,
    nom VARCHAR2(50),
    x NUMBER NOT NULL,
    y NUMBER NOT NULL
);


INSERT INTO point (id, nom, x, y) VALUES (seq_point.NEXTVAL, 'Antananarivo', 10, 20);  
INSERT INTO point (id, nom, x, y) VALUES (seq_point.NEXTVAL, 'Antsirabe', 50, 80);   
INSERT INTO point (id, nom, x, y) VALUES (seq_point.NEXTVAL, 'Sambaina', 100, 120);  
INSERT INTO point (id, nom, x, y) VALUES (seq_point.NEXTVAL, 'Ampefy', 150, 60);    

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







