@echo off
SETLOCAL EnableDelayedExpansion

echo ============================================================
echo      DEPLOIEMENT ET EXECUTION DU PROJET PROGMILAY
echo ============================================================

:: 1. Nettoyage des anciens fichiers .class
echo [1/3] Nettoyage des anciens fichiers...
del /s /q *.class >nul 2>&1

:: 2. Compilation
echo [2/3] Compilation des fichiers Java (UTF-8)...
:: Recherche de tous les fichiers .java et compilation
dir /s /b *.java > sources.txt
javac -encoding UTF-8 -d . -cp ".;lib/*" @sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo [ERREUR] La compilation a echoue.
    del sources.txt
    pause
    exit /b %ERRORLEVEL%
)
del sources.txt
echo [OK] Compilation reussie.

:: 3. Execution
echo [3/3] Lancement de l'application (UTF-8)...
echo.
java -Dfile.encoding=UTF-8 -cp ".;lib/*" persistance.FenetreSimulation

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERREUR] Une erreur est survenue lors de l'execution.
    pause
)

ENDLOCAL
