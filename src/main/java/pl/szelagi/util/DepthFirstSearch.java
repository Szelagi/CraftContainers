package pl.szelagi.util;

import pl.szelagi.component.baseComponent.BaseComponent;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

public class DepthFirstSearch implements Iterator<BaseComponent> {
    Stack<BaseComponent> stack = new Stack<>();

    public DepthFirstSearch(BaseComponent component, boolean includeRoot) {
        if (includeRoot) {
            stack.push(component);
        } else {
            var children = component.children();
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
    public BaseComponent next() {
        if (!hasNext()) throw new NoSuchElementException();

        var current = stack.pop();
        var child = current.children();
        for (int i = child.size() - 1; i >= 0; i--) {
            stack.push(child.get(i));
        }
        return current;
    }
}