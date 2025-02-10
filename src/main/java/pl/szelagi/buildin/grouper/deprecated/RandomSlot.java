///*
// * SessionAPI - A framework for game containerization on Minecraft servers
// * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
// * Licensed under the GNU General Public License v3.0.
// * For more details, visit <https://www.gnu.org/licenses/>.
// */
//
//package pl.szelagi.buildin.grouper;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//
//public class RandomSlot implements Iterator<Integer>, Iterable<Integer> {
//	private final ArrayList<Integer> free = new ArrayList<>();
//
//	public RandomSlot(int slotCount) {
//		for (int i = 0; i < slotCount; i++)
//			free.add(i);
//	}
//
//	public RandomSlot(int min, int maxExcluding) {
//		for (int i = min; i < maxExcluding; i++)
//			free.add(i);
//	}
//
//	public boolean hasNext() {
//		return !free.isEmpty();
//	}
//
//	public Integer next() {
//		if (!hasNext())
//			throw new RuntimeException("all slots are allocated");
//		int index = (int) (Math.random() * free.size());
//		var slot = free.get(index);
//		free.remove(index);
//		return slot;
//	}
//
//	@NotNull
//	@Override
//	public Iterator<Integer> iterator() {
//		return this;
//	}
//}
