# Kalorien Tracker

Ein nativer Kalorien- und Nährstofftracker für den Desktop, geschrieben in Java/JavaFX
mit einem Design im Apple-Stil (helle & dunkle Farbwelt, abgerundete Karten, Systemfarben).

## Funktionen

- **Profil & Empfehlung**: Alter, Größe, Gewicht, Geschlecht und Aktivitätslevel eingeben
  und automatisch Grundumsatz (BMR), Gesamtumsatz (TDEE) sowie ein tägliches Kalorien-
  und Makronährstoffziel (Protein/Kohlenhydrate/Fett) berechnen lassen (Mifflin-St-Jeor-Formel).
- **Tagesübersicht ("Heute")**: Kalorienring, Fortschrittsbalken für Makronährstoffe und
  eine Übersicht über Mikronährstoffe (Ballaststoffe, Zucker, Natrium, Kalium, Calcium,
  Eisen, Vitamin C, Vitamin A) im Vergleich zu Richtwerten.
- **Automatische Nährstofferkennung**: Beim Eintragen eines Lebensmittels (z. B. "Apfel")
  wird aus einer eingebauten Datenbank mit ca. 70 Lebensmitteln automatisch die passende
  Nährwerttabelle erkannt und auf die eingegebene Menge (in Gramm) skaliert.
- **Kalender**: Monatsansicht mit farbcodierten Tagen (unter/im/über dem Kalorienziel) und
  Detailansicht der geloggten Mahlzeiten für jeden Tag.
- **Dark Mode**: Umschaltbar in den Einstellungen, wirkt sofort auf die gesamte App.
- **Lokale Speicherung**: Alle Daten (Profil, Tagebuch, Einstellungen) werden ausschließlich
  lokal unter `~/.calorietracker/data.json` gespeichert – keine Cloud, kein Internet nötig.

## Projekt in VS Code öffnen

1. Ordner `calorie-tracker` in VS Code öffnen (`Datei → Ordner öffnen…`).
2. Die Erweiterung **"Extension Pack for Java"** (`vscjava.vscode-java-pack`) installieren,
   falls noch nicht vorhanden – VS Code schlägt sie beim Öffnen automatisch vor
   (siehe `.vscode/extensions.json`).
3. Warten, bis VS Code das Maven-Projekt automatisch importiert hat (Fortschritt unten rechts).
4. Die Datei `src/main/java/com/calorietracker/CalorieTrackerApp.java` öffnen und über den
   `Run`-Codelens oberhalb der `main`-Methode starten, **oder** die vorbereitete
   Debug-Konfiguration "Run Calorie Tracker" (`.vscode/launch.json`) über den
   Ausführen-und-Debuggen-Tab starten.

## Voraussetzungen

- **Java 17** oder neuer (JDK, nicht nur JRE).
- **Apache Maven** (nur nötig, falls über die Kommandozeile statt VS Code gestartet wird).

JavaFX wird automatisch als Maven-Abhängigkeit heruntergeladen – es muss kein separates
JavaFX SDK installiert werden.

## Über die Kommandozeile starten

```bash
mvn javafx:run
```

## Eine ausführbare JAR-Datei bauen

```bash
mvn package
java -jar target/calorie-tracker.jar
```

## Projektstruktur

```
calorie-tracker/
├── pom.xml                          # Maven-Projektdefinition (Java 17, JavaFX 21, Gson)
├── .vscode/                         # VS Code Launch-Konfiguration & Erweiterungs-Empfehlungen
└── src/main/
    ├── java/com/calorietracker/
    │   ├── Main.java                # Einstiegspunkt
    │   ├── CalorieTrackerApp.java   # JavaFX Application, lädt Theme & Hauptfenster
    │   ├── model/                   # Datenmodelle (Profil, Lebensmittel, Tagebucheintrag, ...)
    │   ├── service/                 # Fachlogik (Speicherung, Nährstoffdatenbank, BMR/TDEE-Berechnung)
    │   └── ui/                      # Controller & UI-Komponenten (Dialoge, Toggle-Switch)
    └── resources/
        ├── data/foods.json          # Eingebaute Nährwertdatenbank (~70 Lebensmittel)
        └── com/calorietracker/
            ├── fxml/                # Bildschirme: Heute, Kalender, Profil, Einstellungen
            └── css/                 # Apple-Stil: base.css + light.css / dark.css

```

## Hinweis zu den Nährwertangaben

Die eingebaute Lebensmitteldatenbank enthält allgemeine Näherungswerte pro 100 g
(basierend auf gängigen Referenzwerten) und dient der Orientierung. Sie ersetzt keine
professionelle Ernährungsberatung.
