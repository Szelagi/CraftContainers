/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.tree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ReverseDepthFirstSearch<T extends IHierarchical<T>> implements Iterator<T> {
    private final Deque<T> stack = new ArrayDeque<>();
    private final T root;
    private final boolean includeRoot;

    public ReverseDepthFirstSearch(T root, boolean includeRoot) {
        this.root = root;
        this.includeRoot = includeRoot;
        traverseToYoungest(root);
    }

    private void traverseToYoungest(T hierarchical) {
        while (hierarchical != null) {
            stack.push(hierarchical);
            var children = hierarchical.children();
            hierarchical = children.isEmpty() ? null : children.getLast();
        }
    }

    @Override
    public boolean hasNext() {
        return includeRoot ? !stack.isEmpty() : (stack.size() > 1 || (stack.size() == 1 && !root.equals(stack.peek())));
    }

    @Override
    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        var current = stack.pop();

        if (root.equals(current) && includeRoot) {
            return current;
        }

        var siblings = current.parent().children();
        int index = siblings.indexOf(current);
        if (index > 0) {
            traverseToYoungest(siblings.get(index - 1));
        }

        return current;
    }
}