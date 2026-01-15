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




// postgresql

-- Création de la table
CREATE TABLE lalana(
    id_lalana SERIAL PRIMARY KEY,
    nom VARCHAR(100),
    largeur INTEGER,
    distance_km INTEGER
);

-- Insertion des données
INSERT INTO lalana (nom, largeur, distance_km) VALUES
('RN2', 20, 200),
('RN44', 18, 150),
('RN1', 25, 350),
('RN7', 22, 400);

-- Vérifier les données
SELECT * FROM lalana;
