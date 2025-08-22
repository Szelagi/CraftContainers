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
import pl.szelagi.util.IncrementalGenerator;

import java.io.*;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractMarkers implements IMarkers{
    protected abstract @NotNull IncrementalGenerator getIdGenerator();
    protected abstract @NotNull Set<MarkerData> toMarkerDataSet(Location base);

    public static void save(AbstractMarkers markers, File file, Location base) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            var markerDataList = markers.toMarkerDataSet(base);
            oos.writeObject(markers.getIdGenerator());
            oos.writeObject(markerDataList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static AbstractMarkers read(File file, Location base) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            var idGenerator = (IncrementalGenerator) ois.readObject();
            var markerDataSet = (Set<MarkerData>) ois.readObject();
            var markerSet = markerDataSet.stream().map(markerData -> markerData.toMarker(base)).collect(Collectors.toSet());
            return new Markers(idGenerator, markerSet);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
