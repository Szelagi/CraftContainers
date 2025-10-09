/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.marker;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class MarkersData implements Serializable {
    @SerializedName("file_version")
    private final int fileVersion = 1;
    @SerializedName("next_id")
    private final int nextId;
    @SerializedName("markers")
    private final Set<MarkerData> markerDataSet;

    public MarkersData(int nextId, Set<MarkerData> markerDataSet) {
        this.nextId = nextId;
        this.markerDataSet = new HashSet<>(markerDataSet);
    }

    public int getFileVersion() {
        return fileVersion;
    }

    public int getNextId() {
        return nextId;
    }

    public Set<MarkerData> getMarkerDataSet() {
        return markerDataSet;
    }
}
