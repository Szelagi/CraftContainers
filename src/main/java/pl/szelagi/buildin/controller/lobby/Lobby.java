/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.lobby;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentDestructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerConstructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerDestructor;
import pl.szelagi.component.baseComponent.internalEvent.playerRequest.PlayerJoinRequest;
import pl.szelagi.component.baseComponent.internalEvent.playerRequest.Reason;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.event.handler.HandlerEvent;
import pl.szelagi.manager.listener.Listeners;
import pl.szelagi.util.timespigot.Time;

public class Lobby extends Controller {
	private static final Sound BREAK_SOUND = Sound.sound(Key.key("block" + ".note_block.bit"), Sound.Source.AMBIENT, 1f, .5f);
	private boolean isLobby = true;
	private final Location lobby;
	private final int maxSlots;
	private final int minSlots;
	private final Time waitTime;
	private final HandlerEvent<Void> finalizeHandlerEvent = new HandlerEvent<>();
	private MessageTimer messageTimer;

	public Lobby(BaseComponent baseComponent, Time waitTime, Location lobby, int maxSlots, int minSlots) {
		super(baseComponent);
		this.lobby = lobby;
		this.maxSlots = maxSlots;
		this.minSlots = minSlots;
		this.waitTime = waitTime;
	}

	private void registerMessage(int timeBefore, char color) {
		String plural = timeBefore <= 1 ? "" : "s";
		var message = "§eThe game starts in §" + color + timeBefore + " §esecond" + plural + "!";
		messageTimer.registerMessageIfSecNotBusy(Time.seconds(timeBefore), message);
	}

	@Override
	public void onComponentInit(ComponentConstructor event) {
		super.onComponentInit(event);
		messageTimer = new MessageTimer(this, this.waitTime);

		// initialize messages
		registerMessage(waitTime.toSeconds(), 'e');
		registerMessage(30, '6');
		registerMessage(20, '6');
		registerMessage(10, '6');
		for (int i = 1; i <= 5; i++)
			registerMessage(i, 'c');

		messageTimer.setBreakCountMessage("§cWe don't have enough players! Start cancelled.");
		messageTimer.getFinalizeEvent()
				.bind(this::lobbyFinalize);
	}

	@Override
	public void onComponentDestroy(ComponentDestructor event) {
		super.onComponentDestroy(event);
	}

	@Override
	public void onPlayerInit(PlayerConstructor event) {
		super.onPlayerInit(event);
		var player = event.getPlayer();
		if (!isLobby)
			return;
		player.teleport(lobby);
		broadcast("§e" + player.getName() + " has joined " + playerStateMessage() + "!");
		if (!isCounting() && players().size() >= minSlots)
			startCountDown();
	}

	@Override
	public void onPlayerDestroy(PlayerDestructor event) {
		super.onPlayerDestroy(event);
		var player = event.getPlayer();
		if (!isLobby)
			return;
		for (var p : players())
			p.sendMessage("§e" + player.getName() + " has left " + playerStateMessage() + "!");

		if (isCounting() && players().size() < minSlots)
			stopCountDown();
	}

	@Override
	public void onPlayerJoinRequest(PlayerJoinRequest event) {
		super.onPlayerJoinRequest(event);
		if (maxSlots > players().size())
			return;
		event.setCanceled(new Reason(this, "lobby is full"));
	}

	private String playerStateMessage() {
		return "§e(§b" + players().size() + "§e/§b" + maxSlots + "§e)";
	}

	public boolean isLobby() {
		return isLobby;
	}

	public void startCountDown() {
		if (isCounting())
			return;
		messageTimer.start();
	}

	public void stopCountDown() {
		if (!isCounting())
			return;
		messageTimer.stop();
		broadcastSound(BREAK_SOUND);
	}

	public boolean isCounting() {
		return messageTimer.isCounting();
	}

	public HandlerEvent<Void> getFinalizeEvent() {
		return finalizeHandlerEvent;
	}

	private void broadcast(String message) {
		for (var player : players())
			player.sendMessage(message);
	}

	@Override
	public Listeners defineListeners() {
		return super.defineListeners().add(LobbyListener.class);
	}

	private void lobbyFinalize() {
		isLobby = false;
		finalizeHandlerEvent.call(null);
	}

	private void broadcastSound(Sound sound) {
		for (var player : players())
			Audience.audience(player)
			        .playSound(sound);
	}
}
