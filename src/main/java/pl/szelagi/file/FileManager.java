/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.file;

import org.bukkit.Location;
import pl.szelagi.SessionAPI;
import pl.szelagi.spatial.ISpatial;
import pl.szelagi.tag.TagResolve;
import pl.szelagi.tag.exception.SignTagException;

import java.io.File;

public class FileManager {
    private static final String SCHEMATIC_EXTENSION = ".schem";
    private static final String TAG_EXTENSION = ".bin";

    private final File directory;

    public FileManager(String directoryName) {
        this.directory = new File(SessionAPI.SESSION_API_DIRECTORY, directoryName);
    }

    public File directory() {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    // FAWE SCHEMATIC
    private String schematicPath(String name) {
        return directory().getPath() + "/" + name + SCHEMATIC_EXTENSION;
    }

    public boolean existSchematic(String name) {
        return FAWESchematicLoader.exists(schematicPath(name));
    }

    public void loadSchematic(String name, ISpatial spatial, Location toLocation) throws SchematicException {
        FAWESchematicLoader.load(schematicPath(name), toLocation);
    }

    public void saveSchematic(String name, Location firstLocation, Location secondLocation, Location baseLocation) throws SchematicException {
        FAWESchematicLoader.save(schematicPath(name), firstLocation, secondLocation, baseLocation);
    }

    public void saveEmptySchematic(String name, Location firstLocation, Location secondLocation, Location baseLocation) throws SchematicException {
        FAWESchematicLoader.saveEmptySchematic(schematicPath(name), firstLocation, secondLocation, baseLocation);
    }

    public ISpatial loadSchematicToSpatial(String name, Location baseLocation) throws SchematicException {
        return FAWESchematicLoader.loadToSpatial(schematicPath(name), baseLocation);
    }

    // TAG
    private String tagPath(String name) {
        return directory().getPath() + "/" + name + TAG_EXTENSION;
    }

    public boolean existTag(String name) {
        return TagLoader.exists(tagPath(name));
    }

    public TagResolve loadTag(String name, Location baseLocation) throws SignTagException {
        return TagLoader.load(tagPath(name), baseLocation);
    }

    public void saveTag(String name, TagResolve tagResolve) throws SignTagException {
        TagLoader.save(tagPath(name), tagResolve);
    }
}