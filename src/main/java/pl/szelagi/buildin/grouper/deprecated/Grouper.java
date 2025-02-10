///*
// * SessionAPI - A framework for game containerization on Minecraft servers
// * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
// * Licensed under the GNU General Public License v3.0.
// * For more details, visit <https://www.gnu.org/licenses/>.
// */
//
//package pl.szelagi.buildin.grouper;
//
//import org.bukkit.entity.Player;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//import pl.szelagi.buildin.grouper.deprecated.event.*;
//import pl.szelagi.component.ISessionComponent;
//import pl.szelagi.component.controller.Controller;
//import pl.szelagi.util.IncrementalGenerator;
//import pl.szelagi.util.event.Event;
//
//import java.util.Iterator;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public abstract class Grouper extends Controller implements IGrouper {
//	private final IncrementalGenerator generator = new IncrementalGenerator();
//	private final Event<PlayerAddEvent> playerAddEventEvent = new Event<>();
//	private final Event<PlayerRemoveEvent> playerRemoveEvent = new Event<>();
//	private final Event<PlayerSwitchEvent> playerSwitchEvent = new Event<>();
//	private final Event<GroupCreateEvent> groupCreateEvent = new Event<>();
//	private final Event<GroupDeleteEvent> groupDeleteEvent = new Event<>();
//
//	public Grouper(ISessionComponent component) {
//		super(component);
//	}
//
//	@Override
//	public @NotNull List<Player> getAllPlayers() {
//		return getGroups().stream()
//		                  .map(Group::getPlayers)
//		                  .flatMap(List::stream)
//		                  .collect(Collectors.toList());
//	}
//
//	@Override
//	public @NotNull List<Player> getAllInSessionPlayers() {
//		return getGroups().stream()
//		                  .map(Group::getInSessionPlayers)
//		                  .flatMap(List::stream)
//		                  .collect(Collectors.toList());
//	}
//
//	@Override
//	public @Nullable Group getUnfairGroup() {
//		if (isFair() || isEmpty())
//			return null;
//		return getGroups().stream().sorted()
//		                  .findFirst()
//		                  .orElseThrow();
//	}
//
//	@Override
//	public @Nullable Group getGroup(Player player) {
//		return getGroups().stream()
//		                  .filter(baseGroup -> baseGroup.hasPlayer(player))
//		                  .findFirst()
//		                  .orElse(null);
//	}
//
//	@Override
//	public boolean hasPlayer(Player player) {
//		return getGroups().stream()
//		                  .anyMatch(baseGroup -> baseGroup.hasPlayer(player));
//	}
//
//	@Override
//	public int getGroupCount() {
//		return getGroups().size();
//	}
//
//	@Override
//	public int getAllPlayerCount() {
//		return getGroups().stream()
//		                  .map(Group::getPlayerCount)
//		                  .reduce(0, Integer::sum);
//	}
//
//	@Override
//	public int getAllInSessionPlayerCount() {
//		return getGroups().stream()
//		                  .map(Group::getInSessionPlayerCount)
//		                  .reduce(0, Integer::sum);
//	}
//
//	@Override
//	public boolean isFair() {
//		if (isEmpty())
//			return true;
//		var firstSize = getGroups().get(0)
//		                           .getPlayerCount();
//		return getGroups().stream()
//		                  .allMatch(baseGroup -> baseGroup.getPlayerCount() == firstSize);
//	}
//
//	@Override
//	public boolean isEmpty() {
//		if (getGroups().isEmpty())
//			return true;
//		return getAllPlayerCount() == 0;
//	}
//
//	@Override
//	public long nextGroupId() {
//		return generator.next();
//	}
//
//	@NotNull
//	@Override
//	public Iterator<Group> iterator() {
//		return getGroups().iterator();
//	}
//
//	@Override
//	public @NotNull Event<PlayerSwitchEvent> getPlayerSwitchEvent() {
//		return playerSwitchEvent;
//	}
//
//	@Override
//	public @NotNull Event<PlayerAddEvent> getPlayerAddEvent() {
//		return playerAddEventEvent;
//	}
//
//	@Override
//	public @NotNull Event<PlayerRemoveEvent> getPlayerRemoveEvent() {
//		return playerRemoveEvent;
//	}
//
//	@Override
//	public @NotNull Event<GroupCreateEvent> getGroupCreateEvent() {
//		return groupCreateEvent;
//	}
//
//	@Override
//	public @NotNull Event<GroupDeleteEvent> getGroupDeleteEvent() {
//		return groupDeleteEvent;
//	}
//}
