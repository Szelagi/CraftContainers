# Modularne podejście do tworzenia projektów

Wszystkie projekty tworzymy w oparciu o komponenty.
Każdy komponent powinien odpowiadać za jedną konkretną funkcjonalność i być zaprojektowany w sposób umożliwiający jego ponowne użycie w innych projektach.
W ten sposób otrzymujemy modułowe rozwiązanie naszego problemu, które można łatwo integrować z innymi elementami.
Następnie takie komponenty możemy łączyć, tworząc bardziej złożone struktury i kompletne projekty.

**Wyróżniamy trzy podstawowe komponenty: `Session`, `Board` i `Controller`.**


- **Session**: główny komponent, od którego zaczynamy projekt. Pełni rolę korzenia w strukturze projektu i zarządza graczami. To do niego dołączamy pozostałe komponenty.


- **Board**: odpowiada za załadowanie planszy gry oraz przechowuje określone lokacje na mapie, do których można łatwo odwołać się w kodzie.


- **Controller**: służy do implementacji modularnych rozwiązań. Umożliwia kontrolowaną obsługę zdarzeń Bukkit, a także zarządzanie zadaniami i wątkami przypisanymi do danego komponentu.