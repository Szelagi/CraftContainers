package pl.szelagi.allocator;

import org.bukkit.World;
import pl.szelagi.util.IncrementalGenerator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LazyWorld {
    public final Set<RegionAllocate> activeAllocations = new HashSet<>();
    public final IncrementalGenerator idGenerator = new IncrementalGenerator();
    public final World world;

    public LazyWorld(World world) {
        this.world = world;
    }

}
