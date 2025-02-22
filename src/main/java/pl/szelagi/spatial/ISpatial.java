/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.spatial;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface ISpatial extends Cloneable {

    static ISpatial clone(ISpatial spatial) {
        return new Spatial(spatial.getFirstPoint().clone(), spatial.getSecondPoint().clone());
    }

    static ISpatial from(Block block) {
        return new Spatial(block.getLocation().clone(), block.getLocation().clone());
    }

    @NotNull Location getFirstPoint();

    @NotNull Location getSecondPoint();

    @NotNull
    default List<Block> getBlocksIn() {
        return BlockMethods.getBlocksIn(getFirstPoint(), getSecondPoint());
    }

    default void eachBlocks(Consumer<Block> predicate) {
        BlockMethods.eachBlocks(getFirstPoint(), getSecondPoint(), predicate);
    }

    default boolean isLocationIn(Location location) {
        var l1 = this.getFirstPoint();
        var l2 = this.getSecondPoint();
        boolean isXZ = isLocationInXZ(location);
        boolean isZ = MathMethods.isBetween(location.getBlockZ(), l1.getBlockZ(), l2.getBlockZ());
        return isXZ && isZ;
    }

    default boolean isLocationInXZ(Location location) {
        var l1 = this.getFirstPoint();
        var l2 = this.getSecondPoint();
        if (!isSameWorld(l1, l2))
            return false;
        if (!isSameWorld(location, l1))
            return false;
        boolean isX = MathMethods.isBetween(location.getBlockX(), l1.getBlockX(), l2.getBlockX());
        boolean isZ = MathMethods.isBetween(location.getBlockZ(), l1.getBlockZ(), l2.getBlockZ());
        return isX && isZ;
    }

    default Location getCenter() {
        var x = getAxiAverage(Location::getBlockX);
        var y = getAxiAverage(Location::getBlockY);
        var z = getAxiAverage(Location::getBlockZ);
        var location = new Location(getFirstPoint().getWorld(), x, y, z);
        return location.add(0.5, 0, 0.5);
    }

    default Location getCenterBlockLocation() {
        return getCenter().toBlockLocation();
    }

    default int getAxiAverage(AxiGetter<Integer> getter) {
        var a = getter.get(getFirstPoint());
        var b = getter.get(getSecondPoint());
        return (a + b) / 2;
    }

    default Location getAbove(Location location) {
        return location.clone().add(0, 1, 0);
    }

    private int distanceBlock(int a, int b) {
        return Math.abs(a - b) + 1;
    }

    private double getRadius(AxiGetter<Integer> axiGetter) {
        return Math.abs(axiGetter.get(getFirstPoint()) - axiGetter.get(getSecondPoint())) / 2d + 1;
    }

    // Size operations
    default int sizeX() {
        return distanceBlock(getFirstPoint().getBlockX(), getSecondPoint().getBlockX());
    }

    default int sizeY() {
        return distanceBlock(getFirstPoint().getBlockY(), getSecondPoint().getBlockY());
    }

    default int sizeZ() {
        return distanceBlock(getFirstPoint().getBlockZ(), getSecondPoint().getBlockZ());
    }

    default int size() {
        return sizeX() * sizeY() * sizeZ();
    }

    // Radius operations
    default ImmutableRadius3D<Double> getRadiusInscribed() {
        var radiusX = getRadius(Location::getBlockX);
        var radiusY = getRadius(Location::getBlockY);
        var radiusZ = getRadius(Location::getBlockZ);
        return new ImmutableRadius3D<>(radiusX, radiusY, radiusZ);
    }

    default ImmutableRadius3D<Double> getRadiusCircumscribed() {
        final var sqrt2 = Math.sqrt(2);
        var inscribed = getRadiusInscribed();
        return new ImmutableRadius3D<>(inscribed.x() * sqrt2, inscribed.y() * sqrt2, inscribed.z() * sqrt2);
    }

    // Partition operations
    default Set<ISpatial> partition(int length) {
        return SpatialPartition.partition(this, length);
    }

    // Minimalize operations
    @Deprecated
    default ISpatial toOptimized() {
        return new SpatialOptimizer(getFirstPoint(), getSecondPoint()).optimize();
    }

    default void minimalizeAsync(Consumer<ISpatial> callback) {
        SpatialMinimalize.async(this, callback);
    }

    default ISpatial minimalizeSync() {
        return SpatialMinimalize.sync(this);
    }

    // Entity operations
    default @NotNull Collection<Entity> getEntitiesIn() {
        var radius = getRadiusCircumscribed();
        return getCenterBlockLocation()
                .getNearbyEntities(radius.x(), radius.y(), radius.z())
                .stream()
                .filter(entity -> isLocationIn(entity.getLocation()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    default @NotNull Collection<Entity> getMobsIn() {
        var radius = getRadiusCircumscribed();
        return getCenterBlockLocation().getNearbyEntitiesByType(Entity.class, radius.x(), radius.y(), radius.z(), entity -> !(entity instanceof Player));
    }

    default @NotNull Collection<Player> getPlayersIn() {
        var radius = getRadiusCircumscribed();
        return getCenterBlockLocation().getNearbyPlayers(radius.x(), radius.y(), radius.z());
    }

    private static boolean isSameWorld(Location location1, Location location2) {
        return location1.getWorld().getName()
                .equals(location2
                        .getWorld()
                        .getName());
    }

    interface AxiGetter<T> {
        T get(Location location);
    }

    interface AxiSetter<T> {
        void set(Location location, T value);
    }

}
