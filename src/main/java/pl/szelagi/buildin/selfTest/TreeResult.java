/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.selfTest;

import java.util.ArrayList;
import java.util.List;

public class TreeResult {
    public final List<String> constructorMessage = new ArrayList<>();
    public final List<String> destructorMessage = new ArrayList<>();
    public final List<String> playerConstructorMessage = new ArrayList<>();
    public final List<String> playerDestructorMessage = new ArrayList<>();
}
