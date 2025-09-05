/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.tag;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.sign.Side;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.SessionAPI;
import pl.szelagi.relative.RelativeLocation;
import pl.szelagi.spatial.ISpatial;
import pl.szelagi.tag.exception.SignTagException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

@Deprecated
public class TagAnalyzer {
    private static final int PART_SIZE = 70;
    private final static ArrayList<Material> SIGN_MATERIALS = new ArrayList<>(Arrays.asList(Material.OAK_SIGN, Material.OAK_WALL_SIGN));
    private final static String PREFIX = "@";

    public static void async(@NotNull ISpatial spatial, @NotNull Consumer<TagResolve> callback) {
        var parts = spatial.partition(PART_SIZE);
        var iterator = parts.iterator();
        var globalResolve = new TagResolve();
        var instance = SessionAPI.getInstance();

        var nextElement = new Runnable() {
            @Override
            public void run() {
                var resolved = process(spatial, iterator.next());
                globalResolve.add(resolved);
                if (iterator.hasNext()) {
                    Bukkit.getScheduler().runTaskLater(instance, this, 1L);
                } else {
                    callback.accept(globalResolve);
                }
            }
        };

        Bukkit.getScheduler().runTask(instance, nextElement);
    }

    private static TagResolve process(@NotNull ISpatial baseSpatial, @NotNull ISpatial processSpatial) {
        var signTagData = new TagResolve();
        processSpatial.eachBlocks(block -> {
            var material = block.getType();
            if (!SIGN_MATERIALS.contains(material))
                return;
            var sign = (Sign) block.getState();
            var signSide = sign.getSide(Side.FRONT);
            var mainLine = signSide.getLine(0);
            if (!mainLine.startsWith(PREFIX))
                return;
            var tagName = mainLine.replace(PREFIX, "");
            var args = Arrays
                    .stream(signSide.getLines())
                    .skip(1).toList();
            var relativeLocation = new RelativeLocation(block.getLocation(), baseSpatial.getCenter());
            var blockData = block.getBlockData();
            BlockFace blockFace;

            if (material == Material.OAK_SIGN && blockData instanceof Rotatable rotatable) {
                // SIGN
                blockFace = rotatable.getRotation();
            } else if (material == Material.OAK_WALL_SIGN && blockData instanceof Directional directional) {
                // WALL SIGN
                blockFace = directional.getFacing();
            } else {
                throw new SignTagException("Sign is not Rotatable or Directional");
            }
            signTagData.add(new Tag(tagName, relativeLocation, blockFace, args));
        });
        return signTagData;
    }
}
