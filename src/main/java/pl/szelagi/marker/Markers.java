/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.marker;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.transform.AffineTransform;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.transform.RotAxis;
import pl.szelagi.transform.Degree;
import pl.szelagi.transform.Rotation;
import pl.szelagi.util.IncrementalGenerator;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Markers extends AbstractMarkers<Markers> {
    @SuppressWarnings("unchecked")
    public static Markers read(@NotNull File file, @NotNull Location base) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            var idGenerator = (IncrementalGenerator) ois.readObject();
            var markerDataSet = (Set<MarkerData>) ois.readObject();
            var markerSet = markerDataSet.stream().map(markerData -> markerData.toMarker(base)).collect(Collectors.toSet());
            return new Markers(idGenerator, base, Collections.emptyList(), markerSet);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(@NotNull File file, @NotNull Location base) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            var markerDataList = toMarkerDataSet(base);
            oos.writeObject(getIdGenerator());
            oos.writeObject(markerDataList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Set<Marker> nativeMarkers;
    private final List<AffineTransform> transforms;

    public Markers(Location base) {
        super(new IncrementalGenerator(), base);

        nativeMarkers = new HashSet<>();
        transforms = new ArrayList<>();
    }

    protected Markers(IncrementalGenerator idGenerator, Location base, List<AffineTransform> transforms, Set<Marker> nativeMarkers) {
        super(idGenerator, base);

        this.nativeMarkers = nativeMarkers;
        this.transforms = transforms;
    }

    protected AffineTransform combined() {
        var combined = new AffineTransform();
        for (int i = transforms.size() - 1; i >= 0; i--) {
            combined = combined.combine(transforms.get(i));
        }
        return combined;
    }

    protected Marker nativeToTransformedMarker(Marker marker) {
        var adaptedBase = BukkitAdapter.adapt(getBase());
        var adaptedLocation = BukkitAdapter.adapt(marker.getLocation());

        var shiftedLocation = adaptedLocation.subtract(adaptedBase);
        var transformedLocation = combined().apply(shiftedLocation);
        var finalLocation = transformedLocation.add(adaptedBase);
        var location = new Location(getBase().getWorld(), finalLocation.x(), finalLocation.y(), finalLocation.z());

        return new Marker(marker.getId(), marker.getName(), location);
    }

    private Marker transformedToNativeMarker(Marker transformedMarker) {
        var adaptedBase = BukkitAdapter.adapt(getBase());
        var adaptedLocation = BukkitAdapter.adapt(transformedMarker.getLocation());

        var relative = adaptedLocation.subtract(adaptedBase);
        var reversed = combined().inverse().apply(relative);
        var finalLocation = reversed.add(adaptedBase);

        var location = new Location(getBase().getWorld(), finalLocation.x(), finalLocation.y(), finalLocation.z());
        return new Marker(transformedMarker.getId(), transformedMarker.getName(), location);
    }

    protected void addMarker(Marker marker) {
        var nativeMarker = transformedToNativeMarker(marker);
        nativeMarkers.add(nativeMarker);
    }

    @Override
    public @NotNull List<Marker> getMarkers() {
        return nativeMarkers.stream().map(this::nativeToTransformedMarker).collect(Collectors.toList());
    }

    @Override
    public @NotNull Marker create(String name, Location location) {
        var id = (int) getIdGenerator().next();
        var marker = new Marker(id, name, location);
        addMarker(marker);
        return marker;
    }

    @Override
    public @Nullable Marker getById(int id) {
        return nativeMarkers.stream()
                .filter(t -> t.getId() == id)
                .map(this::nativeToTransformedMarker)
                .findFirst().orElse(null);
    }

    @Override
    public @NotNull List<Marker> getByName(String name) {
        return nativeMarkers.stream()
                .filter(marker -> marker.getName().equals(name))
                .map(this::nativeToTransformedMarker)
                .toList();
    }

    @Override
    public @NotNull List<Marker> getNearbyMarkers(Location location, double radius) {
        return nativeMarkers.stream()
                .map(this::nativeToTransformedMarker)
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
        var marker = nativeMarkers.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);

        if (marker == null)
            return null;

        nativeMarkers.remove(marker);
        return nativeToTransformedMarker(marker);
    }

    @Override
    public @Nullable List<Marker> removeByName(String name) {
        var findMarkers = nativeMarkers.stream()
                .filter(marker -> marker.getName().equals(name))
                .toList();
        return findMarkers.stream().map(marker -> removeById(marker.getId())).toList();
    }

    @Override
    public @NotNull List<Marker> drop() {
        var markers = getMarkers();
        nativeMarkers.clear();
        return markers;
    }

    @Override
    protected @NotNull Set<MarkerData> toMarkerDataSet(Location base) {
        return nativeMarkers.stream()
                .map(marker -> marker.toMarkerData(base))
                .collect(Collectors.toCollection(HashSet::new));
    }


    private Markers createNew(@Nullable Location base, @Nullable AffineTransform transform, @Nullable Set<Marker> nativeMarkers) {
        if (base == null)
            base = getBase();
        var idGeneratorCopy = new IncrementalGenerator(getIdGenerator().peekNext());
        var transformsCopy = new ArrayList<>(transforms);
        var nativeMarkerCopy = nativeMarkers == null ? new HashSet<>(this.nativeMarkers) : new HashSet<>(nativeMarkers);
        if (transform != null)
            transformsCopy.add(transform);
        return new Markers(idGeneratorCopy, base, transformsCopy, nativeMarkerCopy);
    }

    @Override
    public Markers translateAbsolute(int dx, int dy, int dz) {
        var baseCopy = getBase().clone().add(dx, dy, dz);

        var newNativeMarkers = nativeMarkers.stream().map(marker -> {
            var locationCopy = marker.getLocation().clone().add(dx, dy, dz);
            return new Marker(marker.getId(), marker.getName(), locationCopy);
        }).collect(Collectors.toSet());

        return createNew(baseCopy, null, newNativeMarkers);
    }

    @Override
    public Markers rotate(Degree angle, Rotation direction, RotAxis axis) {
        var degree = angle.getDegree() * direction.getFactor();
        var transform = new AffineTransform();
        transform = switch (axis) {
            case PITCH_X -> transform.rotateX(degree);
            case YAW_Y -> transform.rotateY(degree);
            case ROLL_Z -> transform.rotateZ(degree);
        };
        return createNew(null, transform, null);
    }
}