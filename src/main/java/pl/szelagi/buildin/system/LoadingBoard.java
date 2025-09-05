/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.buildin.controller.otherGameMode.OtherGameMode;
import pl.szelagi.buildin.controller.otherSpeed.OtherSpeed;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.player.PlayerConstructor;
import pl.szelagi.event.internal.player.PlayerDestructor;
import pl.szelagi.component.Board;
import pl.szelagi.component.session.Session;
import pl.szelagi.manager.SessionManager;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;
import pl.szelagi.recovery.internalEvent.PlayerRecovery;
import pl.szelagi.spatial.ISpatial;
import pl.szelagi.spatial.Spatial;
import pl.szelagi.util.timespigot.Time;

import java.time.Duration;

public class LoadingBoard extends Board {
    public LoadingBoard(Session session) {
        super(session);
    }

    @Override
    protected void generate() {
    }

    @Override
    protected void degenerate() {
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        new OtherGameMode(this, GameMode.SPECTATOR).start();
        new OtherSpeed(this, 0, 0).start();
        runTaskTimer(
                () -> players().forEach(this::showMessage),
                Time.seconds(0),
                Time.seconds(5)
        );
    }

    private void showMessage(@NotNull Player player) {
        var title = Component.text("§6§lSessionAPI");
        var subtitle = Component.text("§eThe map is being generated...");

        var fullTitle = Title.title(
                title,
                subtitle,
                Title.Times.times(
                        Duration.ofMillis(0),
                        Duration.ofSeconds(6),
                        Duration.ofMillis(0)
                )
        );

        player.showTitle(fullTitle);
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        super.onPlayerInit(event);
        var player = event.player();
        showMessage(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, PotionEffect.INFINITE_DURATION, 0));
    }

    @Override
    public void onPlayerDestroy(PlayerDestructor event) {
        super.onPlayerDestroy(event);
        var player = event.player();
        player.clearTitle();
        player.removePotionEffect(PotionEffectType.BLINDNESS);
    }

    @Override
    public void onPlayerRecovery(PlayerRecovery event) {
        super.onPlayerRecovery(event);
        event.register(this, (player -> {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }));
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    private static class MyListener implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onPlayerTeleport(PlayerTeleportEvent event) {
            var cause = event.getCause();
            var player = event.getPlayer();
            if (cause != PlayerTeleportEvent.TeleportCause.SPECTATE) return;
            var session = SessionManager.session(player);
            ListenerManager.first(session, getClass(), LoadingBoard.class, loadingBoard -> {
                event.setCancelled(true);
            });
        }

    }

}
