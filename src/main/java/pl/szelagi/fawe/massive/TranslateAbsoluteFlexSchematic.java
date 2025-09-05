/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.fawe.massive;

import pl.szelagi.fawe.TransformOperation;

public class TranslateAbsoluteFlexSchematic extends TransformOperation.TranslateAbsolute<FlexSchematic> {
    public TranslateAbsoluteFlexSchematic(int dx, int dy, int dz) {
        super(dx, dy, dz);
    }

    @Override
    public FlexSchematic build(FlexSchematic schematic) {
        var prevOriginCopy = schematic.getOrigin().clone();
        var nextOrigin = prevOriginCopy.add(dx(), dy(), dz());
        return new FlexSchematic(schematic.getFile(), schematic.getSpace(), nextOrigin, schematic.getTransformations(), schematic.loader(), schematic.cleaner());
    }
}
