/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.tag;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.tag.exception.NoTagException;
import pl.szelagi.tag.serialization.TagResolveStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagResolve implements Serializable {
	private final Map<String, List<Tag>> map = new HashMap<>();

	public void add(@NotNull TagResolve resolve) {
		for (Map.Entry<String, List<Tag>> entry : resolve.map.entrySet()) {
			String key = entry.getKey();
			List<Tag> tags = entry.getValue();
			if (!map.containsKey(key)) {
				var list = new ArrayList<>(tags);
				map.put(key, list);
			} else {
				var list = map.get(key);
				list.addAll(tags);
			}
		}
	}

	public void add(Tag element) {
		List<Tag> sameTypeElements;

		if (!map.containsKey(element.name())) {
			sameTypeElements = new ArrayList<>();
			map.put(element.name(), sameTypeElements);
		} else {
			sameTypeElements = map.get(element.name());
		}

		sameTypeElements.add(element);
	}

	public @NotNull TagQuery query(String tagName) throws NoTagException {
		List<Tag> data = map.get(tagName);

		if (data == null || data.isEmpty())
			throw new NoTagException(tagName);

		return new TagQuery(tagName, data);
	}

	public boolean hasTagName(String tagName) {
		return map.containsKey(tagName);
	}

	public List<Location> toLocations() {
		var locations = new ArrayList<Location>();
		for (var list : map.values()) {
			for (var element : list) {
				locations.add(element.location());
			}
		}
		return locations;
	}

	public TagResolveStorage toSignTagDataStorage() {
		var storage = new TagResolveStorage();
		for (var array : map.values()) {
			if (array == null)
				continue;
			for (var signTag : array) {
				storage.add(signTag);
			}
		}
		return storage;
	}
}
