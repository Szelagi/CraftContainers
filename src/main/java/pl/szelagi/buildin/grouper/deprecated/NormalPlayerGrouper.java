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
//import pl.szelagi.buildin.grouper.deprecated.event.PlayerAddEvent;
//import pl.szelagi.buildin.grouper.deprecated.event.PlayerRemoveEvent;
//import pl.szelagi.buildin.grouper.deprecated.event.PlayerSwitchEvent;
//import pl.szelagi.component.ISessionComponent;
//import pl.szelagi.event.player.initialize.InvokeType;
//import pl.szelagi.event.player.initialize.PlayerConstructorEvent;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class NormalPlayerGrouper extends Grouper {
//	private final ArrayList<Group> groups = new ArrayList<>();
//	private final int exceptedGroups;
//
//	public NormalPlayerGrouper(ISessionComponent component, int exceptedGroups) {
//		super(component);
//		this.exceptedGroups = exceptedGroups;
//	}
//
//	@Override
//	public void playerConstructor(PlayerConstructorEvent event) {
//		super.playerConstructor(event);
//		if (event.getInvokeType() == InvokeType.CHANGE)
//			return;
//
//		if (getGroupCount() < exceptedGroups) {
//			addPlayerToNewGroup(event.getPlayer());
//		} else {
//			addPlayerToSmallestGroup(event.getPlayer());
//		}
//	}
//
//	private void addPlayerToNewGroup(Player player) {
//		var group = new Group(this);
//		groups.add(group);
//		addPlayer(player, group);
//	}
//
//	private void addPlayerToSmallestGroup(Player player) {
//		var group = groups.stream().sorted()
//		                  .findFirst()
//		                  .orElseThrow();
//		addPlayer(player, group);
//	}
//
//	@Override
//	public @NotNull List<Group> getGroups() {
//		return groups;
//	}
//
//	@Override
//	public boolean switchPlayer(Player player, Group group) {
//		var playerGroup = getGroup(player);
//		if (!groups.contains(group))
//			return false;
//		if (playerGroup == null)
//			return false;
//		playerGroup.remove(player);
//		group.add(player);
//		getPlayerSwitchEvent().call(new PlayerSwitchEvent(player, this, playerGroup, group));
//		return true;
//	}
//
//	@Override
//	public boolean addPlayer(Player player, Group group) {
//		if (hasPlayer(player))
//			return false;
//		if (!groups.contains(group))
//			return false;
//		group.add(player);
//		getPlayerAddEvent().call(new PlayerAddEvent(player, this, group));
//		return true;
//	}
//
//	@Override
//	public boolean removePlayer(Player player) {
//		var playerGroup = getGroup(player);
//		if (playerGroup == null)
//			return false;
//		playerGroup.add(player);
//		getPlayerRemoveEvent().call(new PlayerRemoveEvent(player, this, playerGroup));
//		return true;
//	}
//
//	@Override
//	public boolean isComplete() {
//		return getGroupCount() >= exceptedGroups;
//	}
//}
//
