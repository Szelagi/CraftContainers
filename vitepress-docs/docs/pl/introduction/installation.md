# Instalacja
## Serwer Minecraft
1. Pobierz pluginy:
   - [CraftContainers](https://github.com/Szelagi/SessionAPI/releases)
   - [FastAsyncWorldEdit](https://intellectualsites.github.io/download/fawe.html)
3. Umieść pliki `CraftContainers.jar` oraz `FastAsyncWorldEdit.jar` w katalogu `plugins`.

## Zależność projektu
1. Dodaj `CraftContainers` jako zależność w swoim projekcie.
2. Ustaw zależność jako *compileOnly* lub *provided*.

**Maven**
```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/Szelagi/SessionAPI</url>
  </repository>
</repositories>

<dependencies>
    <dependency>
      <groupId>pl.szelagi</groupId>
      <artifactId>sessionapi</artifactId>
        <!-- change to the latest version -->
      <version>2.3.0-SNAPSHOT</version>
      <scope>provided</scope>
    </dependency>
</dependencies>
```

**Gradle**
```groovy
repositories {
    maven {
        name = "github"
        url = uri("https://maven.pkg.github.com/Szelagi/SessionAPI")
    }
}

dependencies {
    // change to the latest version
    compileOnly  'pl.szelagi:sessionapi:2.3.0-SNAPSHOT'
}
```
3. Dodaj wpis `depend: [SessionAPI]` do pliku `resources/plugin.yml`.