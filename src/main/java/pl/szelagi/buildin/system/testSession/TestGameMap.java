/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system.testSession;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.CraftContainers;
import pl.szelagi.allocator.Allocators;
import pl.szelagi.allocator.ISpaceAllocator;
import pl.szelagi.component.container.Container;
import pl.szelagi.fawe.ISchematic;
import pl.szelagi.fawe.Schematics;
import pl.szelagi.component.GameMap;
import pl.szelagi.marker.IMarkers;
import pl.szelagi.marker.Markers;
import pl.szelagi.transform.Degree;
import pl.szelagi.transform.RotAxis;
import pl.szelagi.transform.Rotation;

import java.io.File;
import java.util.*;

public class TestGameMap extends GameMap {
    private final static Random RANDOM = new Random();
    // Liczba pokoi, które będą wygenerowane na mapie.
    private final int ROOM_COUNT = 15;
    // Lista nazw dostępnych pokoi.
    private final static List<String> ROOM_NAMES = List.of("room1", "room2", "room3");
    // Lista plików schematów odpowiadających pokojom.
    private final static List<File> ROOM_SCHEMATIC_FILES = new ArrayList<>();
    // Lista plików markerów odpowiadających pokojom.
    private final static List<File> ROOM_MARKER_FILES = new ArrayList<>();
    // Zbiór wczytanych schematów, które należy później oczyścić.
    private Set<ISchematic<?>> loadedSchematics = new HashSet<>();
    // Połączony obiekt kluczowych lokacji ze wszystkich schematów.
    private IMarkers<?> globalMarkers = null;
    // Obecny kierunek rotacji przy generowaniu mapy.
    private CurrentRotation currentRotation = CurrentRotation.FORWARD;

    static {
        // Wczytanie plików schematów i markerów dla wszystkich nazw pokoi
        ROOM_NAMES.forEach(roomName -> {
            ROOM_SCHEMATIC_FILES.add(ISchematic.getFile(CraftContainers.instance(), roomName));
            ROOM_MARKER_FILES.add(IMarkers.getFile(CraftContainers.instance(), roomName));
        });
    }

    public TestGameMap(@NotNull Container container, ISpaceAllocator allocator) {
        super(container, allocator);
    }

    public TestGameMap(@NotNull Container container) {
        super(container, Allocators.defaultRecyclingAllocator());
    }

    // Losuje indeks pokoju z listy dostępnych nazw.
    private int randomRoomIndex() {
        return RANDOM.nextInt(ROOM_NAMES.size());
    }

    // Tworzy schemat pokoju na podstawie indeksu i punktu początkowego.
    private ISchematic<?> schematicByIndex(int index, Location origin) {
        var file = ROOM_SCHEMATIC_FILES.get(index);
        return Schematics.newMassive(file, space(), origin);
    }

    // Tworzy markery pokoju na podstawie indeksu i punktu początkowego.
    private IMarkers<?> markersByIndex(int index, Location origin) {
        var file = ROOM_MARKER_FILES.get(index);
        return Markers.read(file, origin);
    }

    // Określa kolejny kierunek rotacji schematu w zależności od obecnego kierunku.
    private CurrentRotation nextRotation() {
        switch (currentRotation) {
            case FORWARD -> {
                return currentRotation = RANDOM.nextBoolean() ? CurrentRotation.LEFT : CurrentRotation.RIGHT;
            }
            case LEFT, RIGHT -> {
                return currentRotation = CurrentRotation.FORWARD;
            }
            default -> throw new IllegalStateException();
        }
    }

    @Override
    protected void generate() {
        // Tworzymy globalne markery mapy (center() nie jest dostępne w konstruktorze)
        globalMarkers = new Markers(center());

        var origin = space().getCenter();

        for (int i = 0; i < ROOM_COUNT; i++) {
            // Wylosuj pokój
            var index = randomRoomIndex();
            var schematic = schematicByIndex(index, origin);
            var markers = markersByIndex(index, origin);

            // Losowa rotacja schematu (nie cofamy względem początkowego kierunku)
            if (RANDOM.nextBoolean()) {
                var rotation = nextRotation();
                if (rotation != CurrentRotation.FORWARD) {
                    var direction = rotation == CurrentRotation.LEFT ? Rotation.COUNTER : Rotation.CLOCKWISE;
                    schematic = schematic.rotate(Degree.DEG_90, direction, RotAxis.YAW_Y);
                    markers = markers.rotate(Degree.DEG_90, direction, RotAxis.YAW_Y);
                }
            }

            // Załaduj schemat i dodaj do zbioru w celu późniejszego czyszczenia
            schematic.load();
            loadedSchematics.add(schematic);

            // Dodaj markery schematu do globalnych markerów mapy
            globalMarkers.createFrom(markers);

            // Ustaw punkt wklejania następnego pokoju na "connector" obecnego schematu
            origin = markers.requireOneLocationByName("connector");
        }
    }

    @Override
    protected void degenerate() {
        // Jeżeli alokator nie wymaga czyszczenia, zakończ metodę
        if (!space().requiresCleanup()) return;

        for (var schematic : loadedSchematics)
            schematic.clean();

        loadedSchematics.clear();
    }

//    private final static String NAME = "MyGameMap";
//    private final static File SCHEMATIC_FILE = ISchematic.getFile(CraftContainers.instance(), NAME);
//    private final static File MARKERS_FILE = IMarkers.getFile(CraftContainers.instance(), NAME);
//
//    private ISchematic<?> schematic;
//    private Markers markers;
//
//    public TestGameMap(@NotNull Container container, ISpaceAllocator allocator) {
//        super(container, allocator);
//    }
//
//    public TestGameMap(@NotNull Container container) {
//        super(container, Allocators.defaultRecyclingAllocator());
//    }
//
//    @Override
//    protected void generate() {
//        schematic = Schematics.newMassive(SCHEMATIC_FILE, space(), center());
//        schematic.load();
//
//        markers = Markers.read(MARKERS_FILE, center());
//    }
//
//    @Override
//    protected void degenerate() {
//        // Allocator przydziela przestrzeń dla GameMap.
//        // Jeśli ponownie używa tego samego obszaru, musisz zadbać o jego wyczyszczenie.
//        // Aby tego uniknąć, zdefinuj na sztywno allocator, który automatycznie czyści przestrzeń.
//
//        // Sprawdzamy czy allokator zamuje się czyszczeniem przestrzeni
//        if (!space().requiresCleanup()) return;
//
//        if (schematic != null)
//            schematic.clean();
//    }
//
//    @Override
//    public void onComponentInit(ComponentConstructor event) {
//        // Pobieramy unikalny marker dla generatora przedmiotów.
//        var itemGeneratorMarker = markers.requireOneByName("itemGenerator");
//
//        // Pobieramy wszystkie lokalizacje oznaczone jako "chest" i ustawiamy tam skrzynie.
//        var chestLocations = markers.requireAnyLocationsByName("chest");
//        for (var chestLocation : chestLocations)
//            chestLocation.getBlock().setType(Material.CHEST);
//    }



//    private final List<ISchematic<?>> schematics = new ArrayList<>();
//
//    public TestGameMap(Container container) {
//        super(container);
//    }
//
//    @Override
//    protected void generate() {
//        final int rooms = 4;
//        var sapi = SessionAPI.instance();
//        var spawnSchemFile = ISchematic.getFile(sapi, "droom3");
//        var spawnMarkerFile = IMarkers.getFile(sapi, "droom3");
//
//        var spawnSchematic = Schematics.newMassive(spawnSchemFile, space(), center());
//        var spawnMarkers = Markers.read(spawnMarkerFile, center());
//        var connector = spawnMarkers.getByName("connector").getFirst().getLocation();
//
//
//        for (int i = 0; i < rooms; i++) {
//            schematics.add(spawnSchematic);
//            spawnSchematic.load();
//            connector = spawnMarkers.getByName("connector").getFirst().getLocation();
//
//            final Location c = connector;
//            Scheduler.runAndWait(() -> {
//                c.getBlock().setType(Material.BEDROCK);
//            });
//
//            spawnMarkers = spawnMarkers.translateAbsoluteTo(connector).rotate(Degree.DEG_90, Rotation.CLOCKWISE, RotAxis.YAW_Y);
//            spawnSchematic = spawnSchematic.translateAbsoluteTo(connector).rotate(Degree.DEG_90, Rotation.CLOCKWISE, RotAxis.YAW_Y);
//
//        }
//    }
//
//    @Override
//    protected void degenerate() {
//        schematics.forEach(ISchematic::clean);
//    }
}
