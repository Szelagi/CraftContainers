package pl.szelagi.tag;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class BlockFaceMapper {
    private static final Map<BlockFace, Float> FACE_TO_YAW = new HashMap<>();

    static {
        FACE_TO_YAW.put(BlockFace.SOUTH, 0f);
        FACE_TO_YAW.put(BlockFace.SOUTH_SOUTH_WEST, 22.5f);
        FACE_TO_YAW.put(BlockFace.SOUTH_WEST, 45f);
        FACE_TO_YAW.put(BlockFace.WEST_SOUTH_WEST, 67.5f);
        FACE_TO_YAW.put(BlockFace.WEST, 90f);
        FACE_TO_YAW.put(BlockFace.WEST_NORTH_WEST, 112.5f);
        FACE_TO_YAW.put(BlockFace.NORTH_WEST, 135f);
        FACE_TO_YAW.put(BlockFace.NORTH_NORTH_WEST, 157.5f);
        FACE_TO_YAW.put(BlockFace.NORTH, 180f);
        FACE_TO_YAW.put(BlockFace.NORTH_NORTH_EAST, -157.5f);
        FACE_TO_YAW.put(BlockFace.NORTH_EAST, -135f);
        FACE_TO_YAW.put(BlockFace.EAST_NORTH_EAST, -112.5f);
        FACE_TO_YAW.put(BlockFace.EAST, -90f);
        FACE_TO_YAW.put(BlockFace.EAST_SOUTH_EAST, -67.5f);
        FACE_TO_YAW.put(BlockFace.SOUTH_EAST, -45f);
        FACE_TO_YAW.put(BlockFace.SOUTH_SOUTH_EAST, -22.5f);
    }


    public static void updateYaw(Location location, BlockFace blockFace) {
        var yaw = FACE_TO_YAW.get(blockFace);
        if (yaw == null) yaw = 0f;
        location.setYaw(yaw);
    }
}
