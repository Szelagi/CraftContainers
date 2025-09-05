/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.file;

import org.bukkit.Location;
import pl.szelagi.tag.TagResolve;
import pl.szelagi.tag.exception.SignTagException;
import pl.szelagi.tag.serialization.TagResolveStorage;

import java.io.*;

@Deprecated
public class TagLoader {
    public static boolean exists(String filePath) {
        var file = new File(filePath);
        return file.exists() && file.isFile() && file.canRead();
    }

    public static TagResolve load(String filePath, Location baseLocation) throws SignTagException {
        var file = new File(filePath);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            TagResolveStorage tagResolveStorage = (TagResolveStorage) objectInputStream.readObject();
            objectInputStream.close();
            return tagResolveStorage.toTagResolve(baseLocation);
        } catch (FileNotFoundException e) {
            throw new SignTagException(e.getMessage());
        } catch (IOException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void save(String filePath, TagResolve tagResolve) throws SignTagException {
        var file = new File(filePath);
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(tagResolve.toSignTagDataStorage());
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            throw new SignTagException(e.getMessage());
        }
    }

}
