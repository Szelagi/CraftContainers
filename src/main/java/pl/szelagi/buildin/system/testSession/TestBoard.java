/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system.testSession;

import pl.szelagi.component.board.Board;
import pl.szelagi.component.session.Session;
import pl.szelagi.spatial.ISpatial;
import pl.szelagi.spatial.Spatial;

public class TestBoard extends Board {
	public TestBoard(Session session) {
		super(session);
	}

	@Override
	public ISpatial defineSecureZone() {
		return new Spatial(center(), center());
	}
}
