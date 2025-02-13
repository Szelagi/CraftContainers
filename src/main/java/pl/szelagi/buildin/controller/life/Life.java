/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.life;

import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.ISessionComponent;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.component.session.cause.LoseCause;
import pl.szelagi.state.PlayerContainer;
import pl.szelagi.util.timespigot.Time;

import java.util.ArrayList;

public class Life extends Controller {
	private final PlayerContainer<LifePlayerState> playerStateContainer;
	private int lives;
	private final int respawnTicks;

	public Life(ISessionComponent sessionComponent, int lives, Time respawn) {
		super(sessionComponent);
		playerStateContainer = new PlayerContainer<>(LifePlayerState::new);
		this.lives = lives;
		this.respawnTicks = respawn.toTicks();
	}

	@Nullable
	@Override
	public Listener getListener() {
		return new LifeListener();
	}

	public int getAlivePlayerCount() {
		int count = 0;
		for (var p : getSession().getPlayers()) {
			var state = playerStateContainer.getOrCreate(p);
			if (state.isAlive())
				count++;
		}
		return count;
	}

	@Nullable
	public Player getFirstAlivePlayer() {
		for (var p : getSession().getPlayers()) {
			var state = playerStateContainer.getOrCreate(p);
			if (state.isAlive())
				return p;
		}
		return null;
	}

	public void killPlayer(Player player) {
		var state = playerStateContainer.getOrCreate(player);
		state.setAlive(false);

		lives--;
		boolean hasAfterLives = lives > 0;
		boolean isAfterSomeoneAlive = getAlivePlayerCount() > 0;
		boolean isSinglePlayerGame = getSession().getPlayerCount() == 1;

		int realLives = lives - 1;

		// fix me pls
		// refactor me ;(

		if (realLives == 0) {
			getSession().getPlayers()
			            .forEach(e -> {
				            e.sendMessage("§c" + player.getName() + " died! §7(no lives in the team)");
			            });
		} else {
			getSession().getPlayers()
			            .forEach(e -> {
				            e.sendMessage("§c" + player.getName() + " died! §7(" + realLives + " team lives)");
			            });
		}

		if (!hasAfterLives) {
			getSession().stop(new LoseCause("NO_LIVES")); // no lives
			return;
		}
		if (isSinglePlayerGame) {
			actionSingleHasLives(player);
		} else if (isAfterSomeoneAlive) {
			actionMultiHasLives(state);
		} else {
			getSession().stop(new LoseCause("NO_LIVES")); // multi no remain players
		}
	}

	public void respawnAll() {
		for (var player : getSession().getPlayers()) {
			var state = playerStateContainer.getOrCreate(player);
			if (!state.isAlive())
				respawnPlayer(player);
		}
	}

	private void actionSingleHasLives(Player p) {
		var effects = new ArrayList<PotionEffect>();
		effects.add(new PotionEffect(PotionEffectType.SPEED, 20 * 2, 1, false));
		effects.add(new PotionEffect(PotionEffectType.RESISTANCE, (int) (20 * 1.5f), 3, false));
		effects.add(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 6, 2, false));
		effects.add(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1, false));
		p.addPotionEffects(effects);
		p.playSound(p.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
		p.spawnParticle(Particle.TOTEM_OF_UNDYING, p.getLocation(), 50, 0.3f, 0.3f, 0.3f, 1f);
		p.playEffect(EntityEffect.TOTEM_RESURRECT);
		p.setHealth(8);
		// todo add potem animation and remain lives and You death
	}

	private void actionMultiHasLives(LifePlayerState state) {
		var p = state.getPlayer();

		state.setStopOneSpectateEvent(true);
		p.setGameMode(GameMode.SPECTATOR);
		p.setSpectatorTarget(this.getFirstAlivePlayer());

		var nowMillis = System.currentTimeMillis();
		var respawn = respawnTicks / 20 * 1000;

		var titleTimeRemain = getProcess().runControlledTaskTimer(() -> {
			var delta = System.currentTimeMillis() - nowMillis;
			var remainMillis = respawn - delta;
			if (remainMillis < 1000)
				return;
			var remainSeconds = remainMillis / 1000;
			p.sendMessage("Respawn time remain: " + remainSeconds + "s");
		}, Time.Seconds(1), Time.Seconds(1));

		state.setRespawnRemainTimePlayerTask(titleTimeRemain);

		var respawnTask = getProcess().runControlledTaskLater(() -> {
			respawnPlayer(p);
		}, Time.Ticks(respawnTicks));

		state.setRespawnPlayerTask(respawnTask);

		// teleport all
		for (var pp : getSession().getPlayers()) {
			if (pp.equals(p))
				continue;
			var ss = playerStateContainer.getOrCreate(pp);
			if (ss.isAlive())
				continue; // I added
			ss.setStopOneSpectateEvent(true);
			pp.setSpectatorTarget(getFirstAlivePlayer());
		}
		// todo poinformuj to tym teams o o życiach teamowych
	}

	public void respawnPlayer(Player player) {
		var s = playerStateContainer.getOrCreate(player);
		if (s.isAlive())
			return;

		var respawnTask = s.getRespawnPlayerTask();
		if (respawnTask != null)
			respawnTask.cancel();

		var remainTimeTask = s.getRespawnRemainTimePlayerTask();
		if (remainTimeTask != null)
			remainTimeTask.cancel();

		var firstAlivePlayer = getFirstAlivePlayer();
		if (firstAlivePlayer == null)
			return;

		player.teleport(firstAlivePlayer);
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(player.getHealthScale() / 2);
		s.setAlive(true);
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public PlayerContainer<LifePlayerState> getPlayerStateContainer() {
		return playerStateContainer;
	}

	public int getRespawnTicks() {
		return respawnTicks;
	}
}
