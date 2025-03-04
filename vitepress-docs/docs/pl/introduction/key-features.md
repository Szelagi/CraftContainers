# Korzyści i rozwiązania

Framework został zaprojektowany, aby rozwiązywać najczęstsze problemy związane z tworzeniem odizolowanych środowisk w grze Minecraft.
Dzięki niemu można łatwo zarządzać sesjami gry, optymalizować zasoby serwera oraz zwiększać modularność projektu.

## Kluczowe zalety

- **Modularna struktura** – ułatwia zarządzanie złożonymi projektami i zwiększa przejrzystość kodu.


- **Szybkie tworzenie projektów** – gotowe rozwiązania społeczności, wbudowane moduły i możliwość tworzenia własnych komponentów wielokrotnego użytku.


- **Filtrowanie zdarzeń Bukkit** – framework rozdziela ogólne zdarzenia serwera od tych, które zachodzą w odizolowanych środowiskach.


- **Zarządzanie zadaniami i wątkami** – monitoruje uruchamiane zadania i wątki, automatycznie je wyłączając, gdy nie są już potrzebne.


- **Wiele instancji tej samej gry** – brak potrzeby ręcznego wydzielania terenu, konfigurowania i budowania oddzielnych map dla każdej instancji.


- **Efektywne zarządzanie listenerami Bukkit** – ładowane są tylko te listenery, które są aktualnie wymagane, a niepotrzebne są zwalniane.


- **Wbudowany kreator map** – umożliwia modyfikowanie mapy oraz definiowanie kluczowych lokacji, do których można szybko odwołać się w kodzie.


- **Asynchroniczne ładowanie map** – minimalizuje wpływ na wydajność serwera, eliminując spadki TPS.


- **Zaawansowana ochrona stanu** – w przypadku crashu serwera framework dba o spójność danych graczy i serwera, np. automatycznie przywraca ekwipunek sprzed wejścia do sesji.


- **Definiowanie przenikliwości stanu** – pozwala ustawić, czy sesja wpływa na globalny stan gracza, np. zmiany w ekwipunku mogą być zachowane po jej zakończeniu.