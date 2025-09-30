# Kontrolery

## OtherEquipment
Zapewnia graczowi oddzielny ekwipunek na czas działania komponentu.  
Po wyłączeniu przywracany jest poprzedni stan.  

### Tryby działania:
- **czysty ekwipunek** – gracz zaczyna z pustym ekwipunkiem,  
- **kopia ekwipunku** – zachowuje aktualny stan i pracuje na kopii.  

### Wpływa na:
- ekwipunek,  
- zdrowie,  
- głód,  
- saturację,  
- doświadczenie (exp),  
- efekty mikstur.  

::: danger Uwaga!
Należy zablokować wszystkie formy przenoszenia itemów między kontenreami (np. auction house, enderchest, wymiany, zdalne plecaki), aby zapobiec wprowadzaniu, wyprowadzaniu i duplikacji przedmiotów.
:::

### Przykład użycia
```java
import pl.szelagi.buildin.controller.otherEquipment.OtherEquipment;
// ...

// Nowy pusty ekwipunek
new OtherEquipment(parent).start();

// Kopia aktualnego ekwipunku
new OtherEquipment(parent, true).start();
```

## OtherGameMode
Zapewnia graczowi oddzielny gamemode na czas działania komponentu.
Po wyłączeniu przywracany jest poprzedni stan.

### Przykład użycia
```java
import pl.szelagi.buildin.controller.otherGameMode.OtherGameMode;
// ...

// Każdy gracz na w kontenrze, będzie w trybie kreatywnym
new OtherGameMode(parent, GameMode.CREATIVE).start();

