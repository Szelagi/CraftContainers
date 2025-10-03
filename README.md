# CraftContainers

A framework that allows creating isolated areas with their own logic, state,
and map within a Minecraft server, ideal for building minigames, dungeons,
and other instances that operate independently while remaining part of the same server.

## Key Features

### Modular Design
- Build separate modules for specific features (e.g., lobby system, boss fights, game logic).
- Reuse modules across multiple projects to speed up development and keep code clean and organized.
### Collaboration & Community
- Modules are cross‑project compatible, making it easy to share ready‑made solutions.
- Encourages teamwork and efficient co‑development of games.
### Instance-Based Architecture
- Each game runs in its own dynamic instance with an independent state and map.
- Create unlimited instances of the same game, all running autonomously.
### In‑Game Minigames
- Full access to player states, existing plugins, and server features.
- Run multiple games simultaneously without the overhead of separate server instances.

## Important Links

* [Official Documentation](https://szelagi.github.io/CraftContainers/) – Comprehensive, up-to-date resource for using and
  understanding SessionAPI, covering all key functionalities and advanced features.

* [Discord Server](https://discord.com/invite/za2pYfGWRN) – Community for support, questions, and sharing projects.


## Installation

### Project Dependency
1. **Add CraftContainers** as a dependency in your project.
2. Set the dependency scope to **compileOnly** or **provided**.

#### Maven

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/Szelagi/CraftContainers</url>
  </repository>
</repositories>

<dependencies>
    <dependency>
      <groupId>pl.szelagi</groupId>
      <artifactId>craftcontainers</artifactId>
        <!-- change to the latest version -->
      <version>2.3.4-SNAPSHOT</version>
      <scope>provided</scope>
    </dependency>
</dependencies>
```

#### Gradle
```groovy
repositories {
    maven {
        name = "github"
        url = uri("https://maven.pkg.github.com/Szelagi/CraftContainers")
    }
}

dependencies {
    // change to the latest version
    compileOnly  'pl.szelagi:craftcontainers:2.3.4-SNAPSHOT'
}
```
3. Add the entry **depend: [CraftContainers]** to the **resources/plugin.yml** file.

### Minecraft Server

1. Download the plugins:
  - [CraftContainers](https://github.com/Szelagi/CraftContainers/releases)
  - [FastAsyncWorldEdit](https://intellectualsites.github.io/download/fawe.html)
2. Place the files **CraftContainers.jar** and **FastAsyncWorldEdit.jar** in the **plugins** directory.