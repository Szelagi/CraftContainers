package pl.szelagi.util;

import pl.szelagi.component.baseComponent.BaseComponent;

import java.util.*;

public class ReverseDepthFirstSearch implements Iterator<BaseComponent> {
    private final Deque<BaseComponent> stack = new ArrayDeque<>();
    private final BaseComponent root;
    private final boolean includeRoot;

    public ReverseDepthFirstSearch(BaseComponent root, boolean includeRoot) {
        this.root = root;
        this.includeRoot = includeRoot;
        traverseToYoungest(root);
    }

    private void traverseToYoungest(BaseComponent component) {
        while (component != null) {
            stack.push(component);
            var children = component.children();
            component = children.isEmpty() ? null : children.getLast();
        }
    }

    @Override
    public boolean hasNext() {
        return includeRoot ? !stack.isEmpty() : (stack.size() > 1 || (stack.size() == 1 && !root.equals(stack.peek())));
    }

    @Override
    public BaseComponent next() {
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