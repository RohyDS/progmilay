# progmilay
javac -d . -cp ".;lib/*" (Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })    
java -cp ".;lib/*" persistance.FenetreSimulation    