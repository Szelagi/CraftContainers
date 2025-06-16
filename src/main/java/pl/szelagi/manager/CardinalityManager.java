/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager;

import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.session.Session;

import java.util.HashMap;
import java.util.Map;

public class CardinalityManager {
    private static final Map<Session, Map<Class<? extends BaseComponent>, Integer>> components = new HashMap<>();

    public static void baseComponentStart(@NotNull BaseComponent baseComponent) {
        // Utwórz dla każdej sesji instancję śledzącą ilość komponentów dla każdego typu komponentu
        if (baseComponent instanceof Session session) {
            if (components.containsKey(session)) throw new IllegalArgumentException("session already started");
            components.put(session, new HashMap<>());
        }

        // Pobierz licznik dla sesji przypisanej do komponentu
        var counter = components.get(baseComponent.session());
        if (counter == null) throw new IllegalArgumentException("session does not have counter");

        // Zwiększ liczność dla konkretnego komponentu
        var value = counter.get(baseComponent.getClass());
        if (value == null) value = 0;
        counter.put(baseComponent.getClass(), value + 1);
    }

    public static void baseComponentStop(@NotNull BaseComponent baseComponent) {
        // Usuń wszystko, jeżeli jest to sesja
        if (baseComponent instanceof Session) {
            components.remove((Session) baseComponent);
            return;
        }

        // Pobierz licznik dla sesji przypisanej do komponentu
        var counter = components.get(baseComponent.session());
        if (counter == null) throw new IllegalStateException("Session " + baseComponent.session() + " has not been started");

        // Pobierz liczność dla konkretnego typu komponentu
        var currentValue = counter.get(baseComponent.getClass());
        // Liczność musi istnieć, ponieważ podczas uruchomienia powinna być zarejestrowana
        if (currentValue == null) throw new IllegalStateException("Session " + baseComponent.session() + " has not been started");

        var nextValue = currentValue - 1;
        // usuń klucz lub zmniejsz
        if (nextValue == 0)
            counter.remove(baseComponent.getClass());
        else counter.put(baseComponent.getClass(), nextValue);
    }

    public static int cardinality(@NotNull Session session, @NotNull Class<? extends BaseComponent> componentClass) {
        var counter = components.get(session);
        if (counter == null) throw new IllegalStateException("Session " + session + " has not been registered");
        var cardinality = counter.get(componentClass);
        return cardinality == null ? 0 : cardinality;
    }


}
