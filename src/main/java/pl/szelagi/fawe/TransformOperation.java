/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.fawe;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.session.ClipboardHolder;
import pl.szelagi.transform.Axis;
import pl.szelagi.transform.RotAxis;
import pl.szelagi.transform.Degree;
import pl.szelagi.transform.Rotation;

abstract public class TransformOperation<T extends ISchematic<T>> {
    public abstract T build(T schematic);
    public abstract void transform(Clipboard clipboard, ClipboardHolder holder);

    public static abstract class TranslateAbsolute<T extends ISchematic<T>> extends TransformOperation<T> {
        private final int dx, dy, dz;

        public TranslateAbsolute(int dx, int dy, int dz) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }

        protected int dx() {
            return dx;
        }

        protected int dy() {
            return dy;
        }

        protected int dz() {
            return dz;
        }

        @Override
        public void transform(Clipboard clipboard, ClipboardHolder holder) {}
    }

    public static class TranslateRelative<T extends ISchematic<T>> extends TransformOperation<T> {
        private final int dx, dy, dz;

        public TranslateRelative(int dx, int dy, int dz) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }

        @Override
        public T build(T schematic) {
            return schematic.clone();
        }

        @Override
        public void transform(Clipboard clipboard, ClipboardHolder holder) {
            FaweOperations.translateRelative(clipboard, holder, dx, dy, dz);
        }
    }

    public static class Rotate<T extends ISchematic<T>> extends TransformOperation<T> {
        public final Degree angle;
        public final Rotation direction;
        public final RotAxis axis;

        public Rotate(Degree angle, Rotation direction, RotAxis axis) {
            this.angle = angle;
            this.direction = direction;
            this.axis = axis;
        }

        @Override
        public T build(T schematic) {
            return schematic.clone();
        }

        @Override
        public void transform(Clipboard clipboard, ClipboardHolder holder) {
           FaweOperations.rotate(clipboard, holder, angle, direction, axis);
        }
    }

    public static class Flip<T extends ISchematic<T>> extends TransformOperation<T> {
        public final Axis axis;

        public Flip(Axis axis) {
            this.axis = axis;
        }

        @Override
        public T build(T schematic) {
            return schematic.clone();
        }

        @Override
        public void transform(Clipboard clipboard, ClipboardHolder holder) {
            FaweOperations.flip(clipboard, holder, axis);
        }
    }
}
