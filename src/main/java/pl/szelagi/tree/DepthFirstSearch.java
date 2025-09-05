/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.tree;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class DepthFirstSearch<T extends IHierarchical<T>> implements Iterator<T> {
    Stack<T> stack = new Stack<>();

    public DepthFirstSearch(T hierarchical, boolean includeRoot) {
        if (includeRoot) {
            stack.push(hierarchical);
        } else {
            var children = hierarchical.children();
            for (int i = children.size() - 1; i >= 0; i--) {
                stack.push(children.get(i));
            }
        }
    }

    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }

    @Override
    public T next() {
        if (!hasNext()) throw new NoSuchElementException();

        var current = stack.pop();
        var child = current.children();
        for (int i = child.size() - 1; i >= 0; i--) {
            stack.push(child.get(i));
        }
        return current;
    }
}