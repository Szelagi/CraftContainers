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
//import pl.szelagi.util.event.Event;
//
//import java.util.List;
//
//public interface IGrouper extends Iterable<Group> {
//	@NotNull List<Group> getGroups();
//
//	@NotNull List<Player> getAllPlayers();
//
//	@NotNull List<Player> getAllInSessionPlayers();
//
//	@Nullable Group getUnfairGroup();
//
//	@Nullable Group getGroup(Player player);
//
//	boolean hasPlayer(Player player);
//
//	boolean switchPlayer(Player player, Group group);
//
//	boolean addPlayer(Player player, Group group);
//
//	boolean removePlayer(Player player);
//
//	int getGroupCount();
//
//	int getAllPlayerCount();
//
//	int getAllInSessionPlayerCount();
//
//	boolean isFair();
//
//	boolean isComplete();
//
//	boolean isEmpty();
//
//	long nextGroupId();
//
//	@NotNull Event<PlayerSwitchEvent> getPlayerSwitchEvent();
//
//	@NotNull Event<PlayerAddEvent> getPlayerAddEvent();
//
//	@NotNull Event<PlayerRemoveEvent> getPlayerRemoveEvent();
//
//	@NotNull Event<GroupCreateEvent> getGroupCreateEvent();
//
//	@NotNull Event<GroupDeleteEvent> getGroupDeleteEvent();
//}
