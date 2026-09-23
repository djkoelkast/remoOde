# moOde Remote

Minimale Android-app: een fullscreen WebView die je moOde-webinterface laadt,
zodat je moOde niet via de telefoonbrowser hoeft te openen.

## Bouwen zonder Android Studio (GitHub Actions)
Deze map bevat ook `.github/workflows/build-apk.yml`. Als je dit project
naar een GitHub-repo pusht, bouwt GitHub automatisch een APK voor je:
1. Maak een (gratis, mag privé) repo op GitHub en push deze map erheen.
2. Ga naar het tabblad **Actions** van de repo — de workflow "Build APK"
   start vanzelf.
3. Als hij groen is: open de run → onderaan bij **Artifacts** staat
   `moode-remote-debug-apk` (zip met de `.apk` erin) → downloaden.
4. APK naar je telefoon sturen en installeren (installatie uit
   onbekende bronnen moet je mogelijk eenmalig toestaan).

## Bouwen in Android Studio
1. Open deze map in Android Studio (`File → Open`).
2. Laat Android Studio de Gradle-wrapper aanvullen als hij daarom vraagt
   (dit project bevat geen `gradlew`/`gradle-wrapper.jar`, die genereert
   Android Studio zelf bij het syncen).
3. `Build → Build Bundle(s) / APK(s) → Build APK(s)`.
4. APK staat daarna in `app/build/outputs/apk/debug/`.

## Gebruik
- Bij eerste start vraagt de app om het IP-adres van je moOde-Pi
  (bijv. `192.168.1.50`, met of zonder `http://`).
- Via het tandwiel-icoon rechtsboven kun je het IP later altijd wijzigen.
- Hardware terug-knop: gaat eerst terug binnen de webinterface, sluit
  daarna de app.
- Er zijn bewust geen aparte vooruit/achteruit-knoppen toegevoegd — de
  moOde-UI is een single-page app, dus browsergeschiedenis navigeren
  heeft daar weinig nut. Alleen een dunne laadbalk bovenin toont
  wanneer de pagina nog laadt.

## Aanpassen
- App-naam / package: `app/build.gradle.kts` (`applicationId`) en
  `AndroidManifest.xml`.
- `usesCleartextTraffic="true"` staat aan omdat moOde standaard over
  gewoon `http://` op je lokale netwerk draait.
