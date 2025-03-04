# Instalacja

1. Pobierz najnowszą wersję wtyczki SessionAPI z sekcji [releases](https://github.com/Szelagi/SessionAPI/releases).
2. Dodaj *SessionAPI.jar* do folderu *plugins* na serwerze minecraft.
3. Pobierz najnowszą wersję wtyczki [FAWE](https://intellectualsites.github.io/download/fawe.html).
4. Dodaj *FAWE* do folderu *plugins* na serwerze minecraft.
5. Dodaj plik *SessionAPI.jar* jako zależność w projekcie Java.
6. Ustaw zależność jako *compileOnly* lub *provided*.
7. W pliku *plugin.yml* dodaj następujący wpis: `depend: [SessionAPI]`.

## Github Packages
Alternatywnie, możesz dodać SessionAPI za pomocą Mavena:

```
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/Szelagi/SessionAPI</url>
  </repository>
</repositories>

<dependency>
  <groupId>pl.szelagi</groupId>
  <artifactId>sessionapi</artifactId>
  <version>2.3.0-SNAPSHOT</version>
  <scope>provided</scope>
</dependency>
```