# progmilay - Simulation de Trajet

Ce projet est une application Java Swing permettant de simuler des trajets de voitures avec gestion d'obstacles et de consommation de carburant.

## Configuration des Bases de Données

Le projet supporte **PostgreSQL** et **Oracle**. Les scripts d'initialisation se trouvent dans le dossier [util/](file:///g:/ITU/S5/PROG/gits/rohy/progmilay/util).

### 1. PostgreSQL (Configuration par défaut)

- **Base de données** : Créez une base de données nommée `voyage`.
- **Utilisateur** : `postgres`
- **Mot de passe** : `admin`
- **Script** : Exécutez [POSTGRE.sql](file:///g:/ITU/S5/PROG/gits/rohy/progmilay/util/POSTGRE.sql).
- **Code** : Géré par [PostgresBD.java](file:///g:/ITU/S5/PROG/gits/rohy/progmilay/persistance/PostgresBD.java).

### 2. Oracle

- **Service Name** : `ORCL` (sur `localhost:1521`)
- **Utilisateur** : `voyage`
- **Mot de passe** : `voyage123`

#### Création de l'utilisateur Oracle
Pour créer l'utilisateur, connectez-vous d'abord en tant que `SYS` via SQL*Plus :
```bash
sqlplus sys as sysdba
```
*(Entrez le mot de passe défini lors de l'installation d'Oracle)*

Une fois connecté, exécutez les commandes suivantes :
```sql
CREATE USER voyage IDENTIFIED BY voyage123;
GRANT CONNECT, RESOURCE, CREATE VIEW TO voyage;
ALTER USER voyage QUOTA UNLIMITED ON USERS;
```
```bash
cc
```

- **Script d'initialisation** : Exécutez [ORACLE.sql](file:///g:/ITU/S5/PROG/gits/rohy/progmilay/util/ORACLE.sql) après vous être connecté avec l'utilisateur `voyage`.
- **Code** : Géré par [OracleDB.java](file:///g:/ITU/S5/PROG/gits/rohy/progmilay/persistance/OracleDB.java).

## Compilation

Pour compiler le projet, ouvrez un terminal PowerShell à la racine du projet et exécutez :

```powershell
javac -d . -cp ".;lib/*" (Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })
```

## Exécution

Une fois compilé, lancez l'application avec la commande suivante :

```powershell
java -cp ".;lib/*" persistance.FenetreSimulation
```

## Structure du Projet

- **dao/** : Accès aux données (CRUD) pour les obstacles, pauses, points, routes et voitures.
- **model/** : Classes métiers (Voiture, Route, Obstacle, Pause, etc.).
- **persistance/** : Interface graphique (Swing) et classes de connexion ([PostgresBD.java](file:///g:/ITU/S5/PROG/gits/rohy/progmilay/persistance/PostgresBD.java), [OracleDB.java](file:///g:/ITU/S5/PROG/gits/rohy/progmilay/persistance/OracleDB.java)).
- **fonction/** : Logique métier et algorithmes de recherche de chemin (DFS).
- **lib/** : Bibliothèques externes (Pilotes JDBC PostgreSQL/Oracle, Lombok).
- **util/** : Scripts SQL d'initialisation pour les deux types de bases de données.
