/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.Controller;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.event.EventDispatcher;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.component.ComponentDestructor;
import pl.szelagi.manager.listener.Listeners;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MobDeathWatcher extends Controller {
    private final EventDispatcher<Void> finishDispatcher;
    private final Set<LivingEntity> entities;

    public MobDeathWatcher(@NotNull BaseComponent parent, Collection<LivingEntity> entities) {
        super(parent);
        this.finishDispatcher = new EventDispatcher<>();
        this.entities = new HashSet<>(entities);
    }

    public EventDispatcher<Void> getFinishDispatcher() {
        return finishDispatcher;
    }

    private void tryRemoveEntity(LivingEntity entity) {
        entities.remove(entity);
    }

    private boolean check() {
        if (entities.isEmpty()) {
            finishDispatcher.dispatch(null);
            stop();
            return true;
        }
        return false;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
    }

    @Override
    public void onComponentDestroy(ComponentDestructor event) {
        super.onComponentDestroy(event);
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners();
    }

    private static class MyListener implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onEntityDeath(EntityDeathEvent event) {

        }

        @EventHandler(ignoreCancelled = true)
        public void onEntitiesUnload(EntitiesUnloadEvent event) {
        }

        @EventHandler(ignoreCancelled = true)
        public void onEntityExplode(EntityExplodeEvent event) {
        }


    }
}
