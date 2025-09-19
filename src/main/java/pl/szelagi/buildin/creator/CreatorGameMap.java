/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.creator;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.GameMode;
import org.bukkit.Material;
import pl.szelagi.Scheduler;
import pl.szelagi.SessionAPI;
import pl.szelagi.buildin.controller.environment.NoCreatureNaturalSpawn;
import pl.szelagi.buildin.controller.otherEquipment.OtherEquipment;
import pl.szelagi.buildin.controller.otherGameMode.OtherGameMode;
import pl.szelagi.component.container.Container;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.component.GameMap;
import pl.szelagi.file.FileManager;

@Deprecated
public class CreatorGameMap extends GameMap {
    private final String editName;
    private FileManager creatorFileManager;

    public CreatorGameMap(Container container, String editName) {
        super(container);
        this.editName = editName;
    }

    public FileManager creatorFileManager() {
        return creatorFileManager;
    }

    @Override
    protected void generate() {
        this.creatorFileManager = new FileManager(SessionAPI.BOARD_DIRNAME + '/' + editName);
        Scheduler.runAndWait(() -> {
            center().getBlock()
                    .setType(Material.BEDROCK);
        });
//        if (creatorFileManager.existSchematic(CONSTRUCTOR_FILE_NAME))
//            creatorFileManager.loadSchematic(CONSTRUCTOR_FILE_NAME, space(), center());
    }

    @Override
    protected void degenerate() {
        var space = space().minimalizeSync();
        var pointA = space.getMin();
        var pointB = space.getMax();
        BlockVector3 vecA = BlockVector3.at(pointA.getBlockX(), pointA.getBlockY(), pointA.getBlockZ());
        BlockVector3 vecB = BlockVector3.at(pointB.getBlockX(), pointB.getBlockY(), pointB.getBlockZ());

        CuboidRegion region = new CuboidRegion(vecA, vecB);

        try (EditSession editSession = WorldEdit
                .getInstance()
                .newEditSession(BukkitAdapter.adapt(pointA.getWorld()))) {
            assert BlockTypes.AIR != null;
            editSession.setBlocks((Region) region, BlockTypes.AIR.getDefaultState());

            Operations.complete(editSession.commit());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Scheduler.runAndWait(() -> {
            for (var entity : space().getMobsIn())
                entity.remove();
        });
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        new OtherEquipment(this, true).start();
        new OtherGameMode(this, GameMode.CREATIVE).start();
        new NoCreatureNaturalSpawn(this).start();
    }

}
