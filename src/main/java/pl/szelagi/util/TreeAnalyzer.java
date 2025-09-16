package pl.szelagi.util;

import pl.szelagi.component.base.Component;

import java.util.*;

@Deprecated
public class TreeAnalyzer {
    private final Map<Integer, List<Component>> layers;

    public TreeAnalyzer(Component root) {
        this.layers = analyzeTree(root);
    }

    // sprawdzone: daje poprawne wyniki
    private Map<Integer, List<Component>> analyzeTree(Component root) {
        var layers = new HashMap<Integer, List<Component>>();
        Queue<Component> queue = new LinkedList<>();
        queue.add(root);
        int level = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Component> layer = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                Component current = queue.poll();
                layer.add(current);
                queue.addAll(current.children());
            }

            layers.put(level, layer);
            level++;
        }

        return layers;
    }

    public Map<Integer, List<Component>> layers() {
        return layers;
    }

    public int numberOfLayers() {
        return layers.size();
    }

    // sprawdzone: daje poprawne wyniki
    public Iterable<Component> iterateOldToYoung() {
        return () -> layers.values().stream().flatMap(List::stream).iterator();
    }

    // sprawdzone: daje poprawne wyniki
    public Iterable<Component> iterateOldToYoungNoRoot() {
        return () -> layers.values().stream().skip(1).flatMap(List::stream).iterator();
    }

    // sprawdzone: daje poprawne wyniki
    public Iterable<Component> iterableYoungToOld() {
        var rootStream = layers.values().stream();
        return () -> ReverseStream.reverse(rootStream.flatMap(List::stream)).iterator();
    }

    // sprawdzone: daje poprawne wyniki
    public Iterable<Component> iterableYoungToOldNoRoot() {
        var noRootStream = layers.values().stream().skip(1);
        return () -> ReverseStream.reverse(noRootStream.flatMap(List::stream)).iterator();
    }


}