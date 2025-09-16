/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager;

import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.container.Container;

import java.util.HashMap;
import java.util.Map;

public class CardinalityManager {
    private static final Map<Container, Map<Class<? extends Component>, Integer>> components = new HashMap<>();

    public static void baseComponentStart(@NotNull Component component) {
        // Utwórz dla każdej sesji instancję śledzącą ilość komponentów dla każdego typu komponentu
        if (component instanceof Container container) {
            if (components.containsKey(container)) throw new IllegalArgumentException("session already started");
            components.put(container, new HashMap<>());
        }

        // Pobierz licznik dla sesji przypisanej do komponentu
        var counter = components.get(component.container());
        if (counter == null) throw new IllegalArgumentException("session does not have counter");

        // Zwiększ liczność dla konkretnego komponentu
        var value = counter.get(component.getClass());
        if (value == null) value = 0;
        counter.put(component.getClass(), value + 1);
    }

    public static void baseComponentStop(@NotNull Component component) {
        // Usuń wszystko, jeżeli jest to sesja
        if (component instanceof Container) {
            components.remove((Container) component);
            return;
        }

        // Pobierz licznik dla sesji przypisanej do komponentu
        var counter = components.get(component.container());
        if (counter == null) throw new IllegalStateException("Session " + component.container() + " has not been started");

        // Pobierz liczność dla konkretnego typu komponentu
        var currentValue = counter.get(component.getClass());
        // Liczność musi istnieć, ponieważ podczas uruchomienia powinna być zarejestrowana
        if (currentValue == null) throw new IllegalStateException("Session " + component.container() + " has not been started");

        var nextValue = currentValue - 1;
        // usuń klucz lub zmniejsz
        if (nextValue == 0)
            counter.remove(component.getClass());
        else counter.put(component.getClass(), nextValue);
    }

    public static int cardinality(@NotNull Container container, @NotNull Class<? extends Component> componentClass) {
        var counter = components.get(container);
        if (counter == null) throw new IllegalStateException("Session " + container + " has not been registered");
        var cardinality = counter.get(componentClass);
        return cardinality == null ? 0 : cardinality;
    }


}
