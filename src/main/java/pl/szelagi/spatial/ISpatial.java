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
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface ISpatial extends Cloneable {

    static ISpatial clone(ISpatial spatial) {
        return new Spatial(spatial.getMin().clone(), spatial.getMax().clone());
    }

    static ISpatial from(Block block) {
        return new Spatial(block.getLocation().clone(), block.getLocation().clone());
    }

    @NotNull Location getMin();

    @NotNull Location getMax();

    @NotNull
    default List<Block> getBlocksIn() {
        return BlockMethods.getBlocksIn(getMin(), getMax());
    }

    default void eachBlocks(Consumer<Block> predicate) {
        BlockMethods.eachBlocks(getMin(), getMax(), predicate);
    }

    default boolean isLocationIn(Location location) {
        var l1 = this.getMin();
        var l2 = this.getMax();
        boolean isXZ = isLocationInXZ(location);
        boolean isZ = isBetween(location.getBlockZ(), l1.getBlockZ(), l2.getBlockZ());
        return isXZ && isZ;
    }

    default boolean isLocationInXZ(Location location) {
        var l1 = this.getMin();
        var l2 = this.getMax();
        if (!isSameWorld(l1, l2))
            return false;
        if (!isSameWorld(location, l1))
            return false;
        boolean isX = isBetween(location.getBlockX(), l1.getBlockX(), l2.getBlockX());
        boolean isZ = isBetween(location.getBlockZ(), l1.getBlockZ(), l2.getBlockZ());
        return isX && isZ;
    }

    default Location getCenter() {
        var x = getAxiAverage(Location::getBlockX);
        var y = getAxiAverage(Location::getBlockY);
        var z = getAxiAverage(Location::getBlockZ);
        var location = new Location(getMin().getWorld(), x, y, z);
        return location.add(0.5, 0, 0.5);
    }

    default Location getCenterBlockLocation() {
        return getCenter().toBlockLocation();
    }

    default int getAxiAverage(AxiGetter<Integer> getter) {
        var a = getter.get(getMin());
        var b = getter.get(getMax());
        return (a + b) / 2;
    }

    default Location getAbove(Location location) {
        return location.clone().add(0, 1, 0);
    }

    private int distanceBlock(int a, int b) {
        return Math.abs(a - b) + 1;
    }

    private double getRadius(AxiGetter<Integer> axiGetter) {
        return Math.abs(axiGetter.get(getMin()) - axiGetter.get(getMax())) / 2d + 1;
    }

    // Size operations
    default int sizeX() {
        return distanceBlock(getMin().getBlockX(), getMax().getBlockX());
    }

    default int sizeY() {
        return distanceBlock(getMin().getBlockY(), getMax().getBlockY());
    }

    default int sizeZ() {
        return distanceBlock(getMin().getBlockZ(), getMax().getBlockZ());
    }

    default int minSize() {
        return Math.min(sizeX(), Math.min(sizeY(), sizeZ()));
    }

    default int maxSize() {
        return Math.max(sizeX(), Math.max(sizeY(), sizeZ()));
    }

    default int minSizeXZ() {
        return Math.min(sizeX(), sizeZ());
    }

    default int maxSizeXZ() {
        return Math.max(sizeX(), sizeZ());
    }

    default int volume() {
        return sizeX() * sizeY() * sizeZ();
    }

    // Radius operations
    default Vector3d getRadiusInscribed() {
        var radiusX = getRadius(Location::getBlockX);
        var radiusY = getRadius(Location::getBlockY);
        var radiusZ = getRadius(Location::getBlockZ);
        return new Vector3d(radiusX, radiusY, radiusZ);
    }

    default Vector3d getRadiusCircumscribed() {
        final var sqrt2 = Math.sqrt(2);
        var inscribed = getRadiusInscribed();
        return new Vector3d(inscribed.x() * sqrt2, inscribed.y() * sqrt2, inscribed.z() * sqrt2);
    }

    // Partition operations
    default Set<ISpatial> partition(int length) {
        return SpatialPartition.partition(this, length);
    }

    // Minimalize operations
    @Deprecated
    default ISpatial toOptimized() {
        return new SpatialOptimizer(getMin(), getMax()).optimize();
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

    static boolean isBetween(double p, double a, double b) {
        double min = Math.min(a, b);
        double max = Math.max(a, b);
        return p >= min && p <= max;
    }

}
