///*
// * SessionAPI - A framework for game containerization on Minecraft servers
// * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
// * Licensed under the GNU General Public License v3.0.
// * For more details, visit <https://www.gnu.org/licenses/>.
// */
//
//package pl.szelagi.buildin.grouper.storage;
//
//import org.jetbrains.annotations.NotNull;
//import pl.szelagi.buildin.grouper.Group;
//import pl.szelagi.buildin.grouper.Grouper;
//import pl.szelagi.state.Container;
//
//import java.io.Serializable;
//import java.util.function.Function;
//
//public class GroupContainer<T extends GroupState> extends Container<Group, T> implements Serializable {
//	private final Grouper grouper;
//
//	public GroupContainer(@NotNull Grouper grouper, @NotNull Function<Group, T> creator) {
//		super(creator);
//		this.grouper = grouper;
//	}
//
//	public Grouper getGrouper() {
//		return grouper;
//	}
//
//	public void forceCreateStates() {
//		grouper.getGroups()
//		       .forEach(group -> create(group, getCreator()));
//	}
//}
