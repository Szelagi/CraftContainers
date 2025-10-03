/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.marker;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.buildin.controller.MarkerHologram;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.base.ComponentStatus;
import pl.szelagi.component.Controller;
import pl.szelagi.transform.RotAxis;
import pl.szelagi.transform.Degree;
import pl.szelagi.transform.Rotation;
import pl.szelagi.util.IncrementalGenerator;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class DisplayableMarkers extends Controller implements IMarkers<DisplayableMarkers> {
    private final Markers markers;
    private final Map<Integer, MarkerHologram> holograms = new HashMap<>();

    private void refreshHolograms() {
        for (var hologram : holograms.values()) {
            if (hologram.status() == ComponentStatus.RUNNING) hologram.stop();
        }
        holograms.clear();
        for (var marker : markers.getMarkers()) {
            createHologram(marker);
        }
    }

    private void createHologram(@NotNull Marker marker) {
        if (holograms.containsKey(marker.getId()))
            throw new IllegalStateException("Marker with id " + marker.getId() + " already exists");
        var markerHologram = new MarkerHologram(this, marker);
        markerHologram.start();
        holograms.put(marker.getId(), markerHologram);
    }

    private void removeHologram(@NotNull Marker marker) {
        var markerHologram = holograms.get(marker.getId());
        if (markerHologram == null)
            throw new IllegalStateException("Marker with id " + marker.getId() + " does not exist");

        markerHologram.stop();
        holograms.remove(marker.getId());
    }

    protected DisplayableMarkers(@NotNull Component parent, Markers markers) {
        super(parent);
        this.markers = markers;
        refreshHolograms();
    }

    @Override
    public void save(@NotNull File file, @NotNull Location base) {
        markers.save(file, base);
    }

    public static DisplayableMarkers from(@NotNull Component parent, File file, Location base) {
        var markers = Markers.read(file, base);
        return new DisplayableMarkers(parent, markers);
    }

    public static DisplayableMarkers empty(@NotNull Component parent, Location base) {
        return new DisplayableMarkers(parent, new Markers(base));
    }

    @NotNull
    public IncrementalGenerator getIdGenerator() {
        return markers.getIdGenerator();
    }

    @NotNull
    public Set<MarkerData> toMarkerDataSet(Location base) {
        return markers.toMarkerDataSet(base);
    }

    @Override
    public @NotNull List<Marker> getMarkers() {
        return markers.getMarkers();
    }

    @Override
    public @NotNull Marker create(String name, Location location) {
        var marker = markers.create(name, location);
        createHologram(marker);
        return marker;
    }

    @Override
    public @NotNull Marker create(String name, Location location, IMetadata metadata) {
        var marker = markers.create(name, location, metadata);
        createHologram(marker);
        return marker;
    }

    @Override
    public @Nullable Marker getById(int id) {
        return markers.getById(id);
    }

    @Override
    public @NotNull List<Marker> getByName(String name) {
        return markers.getByName(name);
    }

    @Override
    public @NotNull List<Marker> getNearbyMarkers(Location location, double radius) {
        return markers.getNearbyMarkers(location, radius);
    }

    @Override
    public @NotNull List<Marker> removeNearbyMarkers(Location location, double radius) {
        var removedMarkers = markers.removeNearbyMarkers(location, radius);
        for (var removedMarker : removedMarkers ) {
            removeHologram(removedMarker);
        }
        return removedMarkers;
    }

    @Override
    public @Nullable Marker removeById(int id) {
        var removedMarker = markers.removeById(id);
        if (removedMarker == null) return null;
        removeHologram(removedMarker);
        return removedMarker;
    }

    @Override
    public @Nullable List<Marker> removeByName(String name) {
        var removedMarkers = markers.removeByName(name);
        if (removedMarkers == null) return null;
        for (var removedMarker : removedMarkers ) {
            removeHologram(removedMarker);
        }
        return removedMarkers;
    }

    @Override
    public @NotNull List<Marker> drop() {
        var droppedMarkers = markers.drop();
        for (var droppedMarker : droppedMarkers) {
            removeHologram(droppedMarker);
        }
        return droppedMarkers;
    }

    @Override
    public @NotNull Location getBase() {
        return markers.getBase();
    }

    @Override
    public DisplayableMarkers translateAbsolute(int dx, int dy, int dz) {
        var translatedMarkers = markers.translateAbsolute(dx, dy, dz);
        return new DisplayableMarkers(parent(), translatedMarkers);
    }

    @Override
    public DisplayableMarkers rotate(Degree angle, Rotation direction, RotAxis axis) {
        var rotatedMarkers = markers.rotate(angle, direction, axis);
        return new DisplayableMarkers(parent(), rotatedMarkers);
    }

    @Override
    public @NotNull Marker updateMetadata(int id, BiConsumer<Marker, IMutableMetadata> modifier) {
        var prevMarker = markers.getById(id);
        var newMarker = markers.updateMetadata(id, modifier);
        assert prevMarker != null;
        removeHologram(prevMarker);
        createHologram(newMarker);
        return newMarker;
    }
}
