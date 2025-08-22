/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.schematic;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import pl.szelagi.util.IncrementalGenerator;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Markers extends AbstractMarkers {
    private final IncrementalGenerator idGenerator;
    private final Map<String, List<Marker>> markersByName;
    private final Map<Integer, Marker> markersById;

    public Markers() {
        this.idGenerator = new IncrementalGenerator();
        this.markersByName = new HashMap<>();
        this.markersById = new HashMap<>();
    }

    protected Markers(IncrementalGenerator idGenerator, Set<Marker> markers) {
        this.idGenerator = idGenerator;
        this.markersByName = new HashMap<>();
        this.markersById = new HashMap<>();
        markers.forEach(this::addMarker);
    }

    protected void addMarker(Marker marker) {
        var id = marker.getId();
        var name = marker.getName();

        markersById.put(id, marker);

        var markerList = markersByName.computeIfAbsent(name, k -> new ArrayList<>());
        markerList.add(marker);
    }

    @Override
    public @NotNull List<Marker> getMarkers() {
        return markersById.values().stream().toList();
    }

    @Override
    public @NotNull Marker create(String name, Location location) {
        var id = (int) idGenerator.next();
        var marker = new Marker(id, name, location);
        addMarker(marker);
        return marker;
    }

    @Override
    public @Nullable Marker getById(int id) {
        return markersById.get(id);
    }

    @Override
    public @Nullable List<Marker> getByName(String name) {
        var list = markersByName.get(name);
        if (list == null) return null;
        return Collections.unmodifiableList(list);
    }

    @Override
    public @NotNull List<Marker> getNearbyMarkers(Location location, double radius) {
        return markersById.values()
                .stream()
                .filter(marker -> marker.getLocation().distance(location) <= radius)
                .toList();
    }

    @Override
    public @NotNull List<Marker> removeNearbyMarkers(Location location, double radius) {
        var markers = getNearbyMarkers(location, radius);
        markers.forEach(marker -> removeById(marker.getId()));
        return markers;
    }

    @Override
    public @Nullable Marker removeById(int id) {
        var marker = markersById.get(id);
        if (marker == null) return null;
        markersById.remove(id);

        var markerList = markersByName.get(marker.getName());
        markerList.remove(marker);
        if (markerList.isEmpty()) {
            markersByName.remove(marker.getName());
        }
        return marker;
    }

    @Override
    public @Nullable List<Marker> removeByName(String name) {
        var markerList = markersByName.get(name);
        if (markerList == null) return null;
        markersByName.remove(name);
        markerList.forEach(marker -> markersById.remove(marker.getId()));
        return Collections.unmodifiableList(markerList);
    }

    @Override
    public @NotNull List<Marker> drop() {
        var markers = markersById.values().stream().toList();
        markersById.clear();
        markersByName.clear();
        return markers;
    }

    @Override
    protected @NotNull IncrementalGenerator getIdGenerator() {
        return idGenerator;
    }

    @Override
    protected @NotNull Set<MarkerData> toMarkerDataSet(Location base) {
        return markersById.values().stream().map(marker -> marker.toMarkerData(base)).collect(Collectors.toSet());
    }
}
