/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.tag.serialization;

import org.bukkit.Location;
import pl.szelagi.tag.Tag;
import pl.szelagi.tag.TagResolve;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TagResolveStorage implements Serializable {
	private final List<TagStorage> tagStorages = new ArrayList<>();

	public void add(Tag tag) {
		var tagStorage = new TagStorage(tag.name(), tag
				.relativeLocation()
				.toRelativePoint(), tag.blockFace(), tag.args());
		tagStorages.add(tagStorage);
	}

	public TagResolve toTagResolve(Location base) {
		var tagResolve = new TagResolve();
		for (var s : tagStorages) {
			var signTag = new Tag(s.tag(), s
					.relativePoint()
					.toRelativeLocation(base), s.blockFace(), s.args());
			tagResolve.add(signTag);
		}
		return tagResolve;
	}
}
