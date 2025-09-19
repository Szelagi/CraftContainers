package pl.szelagi.component;

import pl.szelagi.component.base.Component;
import pl.szelagi.util.Instances;

import java.util.*;

public class ComponentIndex {
    private final Map<Class<?>, Set<Component>> index = new HashMap<>();

    public void onComponentStart(Component component) {
        var clazz = component.getClass();
        var instances = Instances.getAllInstanceofTypes(clazz);
        for (var instance : instances) {
            index.computeIfAbsent(instance, key -> new HashSet<>()).add(component);
        }
    }

    public void onComponentStop(Component component) {
        var clazz = component.getClass();
        var instances = Instances.getAllInstanceofTypes(clazz);
        for (var instance : instances) {
            var components = index.get(instance);
            components.remove(component);
            if (components.isEmpty()) index.remove(instance);
        }
    }

    public <U> Set<U> get(Class<U> clazz) {
        var components = index.get(clazz);
        if (components == null) return new HashSet<>();
        return (Set<U>) Collections.unmodifiableSet(components);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ComponentIndex {\n");
        for (var entry : index.entrySet()) {
            sb.append("  ")
                    .append(entry.getKey().getClass().getName())
                    .append(":\n");
            for (var component : entry.getValue()) {
                sb.append("    - ")
                        .append(component.identifier())
                        .append("\n");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
