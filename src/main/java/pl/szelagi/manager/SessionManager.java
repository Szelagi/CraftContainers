/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.session.Session;
import pl.szelagi.component.session.event.SessionStartEvent;
import pl.szelagi.component.session.event.SessionStopEvent;
import pl.szelagi.manager.compare.CompareSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SessionManager {
	public static final ArrayList<Session> SESSIONS = new ArrayList<>();
	private static final HashMap<Player, Session> PLAYER_SESSION_HASH_MAP = new HashMap<>();

	public static void addRelation(Player p, Session d) {
		PLAYER_SESSION_HASH_MAP.put(p, d);
	}

	public static void removeRelation(Player p) {
		PLAYER_SESSION_HASH_MAP.remove(p);
	}

	public static boolean isInSession(Player p) {
		return PLAYER_SESSION_HASH_MAP.containsKey(p);
	}

	@Nullable
	public static Session getSession(Player p) {
		return PLAYER_SESSION_HASH_MAP.get(p);
	}

	@Nullable
	public static Session getSession(Player p, CompareSession c) {
		var res = PLAYER_SESSION_HASH_MAP.get(p);
		if (res == null)
			return null;
		return c.compare(res) ? res : null;
	}

	@Nullable
	public static Session getSession(Player p, Class<?> classType) {
		return getSession(p, classType::isInstance);
	}

	@NotNull
	public static List<Session> getSessions() {
		return SESSIONS;
	}

	public static void initialize(JavaPlugin p) {
		class ManagerListener implements Listener {
			@EventHandler(ignoreCancelled = true)
			public void onSessionStartEvent(SessionStartEvent event) {
				SESSIONS.add(event.getSession());
			}

			@EventHandler(ignoreCancelled = true)
			public void onSessionStopEvent(SessionStopEvent event) {
				SESSIONS.remove(event.getSession());
			}
		}
		p.getServer().getPluginManager()
		 .registerEvents(new ManagerListener(), p);
	}
}
