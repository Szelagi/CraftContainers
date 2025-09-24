# Kreator Blueprint
Kreator to wbudowane narzędzie umożliwiające modyfikację schematów budowli oraz lokacji kluczowych w schemacie.
Został zaprojektowany tak, aby mógł z niego korzystać każdy, nawet bez umiejętności programowania.
Pozwala na tworzenie nowych szablonów budowli oraz edycję istniejących.

## Komendy

- `/blueprint`
  - `edit <nazwa>` - Opens the editor using an existing schematic or creates a new file.
  - `save` - Saves the currently edited schematic without exiting.
  - `exit` - Exits the editor.
  - `radius <number>` - Sets the maximum size of the schematic. (size = 2*radius-1)
- `/marker`
  - `addhere <nazwa>` - Creates a marker at the player’s current position.
  - `removeid <id> ` - Removes a specific marker by its ID.
  - `removename <name>` - Removes all markers with the specified name.
  - `givemarkerblock <name>` - Gives the player a block that creates a marker at the placement location.
  - `giveclearblock` - Gives a block that deletes markers at the placement location.
  - `list` - Displays all markers grouped by name.
  - `list <name>` - Display all markers in group.
  - `tp <id>` - Teleports the player to the specified marker.


## Zalecany format nazwy szablonu
Aby uniknąć konfliktów między nazwami szablonów i ułatwić identyfikację pluginu, z którego pochodzi dany szablon, stosuj format `<nazwa_pluginu>#<nazwa_szablonu>` pisany małymi literami.

### Przykład
- `/blueprint edit bedwars#arena1`
- `/blueprint edit duelspvp#arena1`


![uruchomienie kreatora](../../img/creator-start.png)
Jeśli szablon nie istnieje, zostanie utworzony nowy, domyślny szablon.


## Punkt origin schematu
Origin określa punkt odniesienia, względem którego schemat będzie wklejany do świata.

![punkt środka](../../img/blueprint-origin.png)

## Kluczowe lokacje
Kluczowe lokacje to specjalne punkty oznaczone w edytorze, do których można później odwoływać się w kodzie.

### Ustawienie lokacji
Za pomocą komendy `/markers`.
![kluczowe lokacje](../../img/markers.png)

### Pobieranie lokacji
- `@NotNull List<Marker> requireAnyByName(String name)`  
  Wymaga co najmniej jednego markera o podanej nazwie (inaczej wyjątek).

- `@NotNull Marker requireOneByName(String name)`  
  Wymaga dokładnie jednego markera o podanej nazwie (inaczej wyjątek).

- `@NotNull Location requireOneLocationByName(String name)`  
  Wymaga dokładnie jednego markera i zwraca jego lokalizację.

- `@NotNull List<Location> requireAnyLocationsByName(String name)`  
  Wymaga co najmniej jednego markera i zwraca ich lokalizacje.

<details>
  <summary>Pozostałe metody</summary>

- `@NotNull Location getBase()`  
  Zwraca bazową lokalizację według, której zostały wyliczone markery.

- `@NotNull List<Marker> getMarkers()`  
  Pobiera wszystkie istniejące markery.

- `@Nullable Marker getById(int id)`  
  Pobiera marker na podstawie jego identyfikatora lub `null`.

- `@NotNull List<Marker> getByName(String name)`  
  Pobiera wszystkie markery o podanej nazwie (może być pusta lista).

- `@NotNull List<Marker> getNearbyMarkers(Location location, double radius)`  
  Pobiera wszystkie markery w zadanym promieniu od lokalizacji.
</details>

## Przykłady
Aby zobaczyć, jak wykorzystać przygotowane schematy i markery w praktyce, zajrzyj do sekcji [Generowanie mapy](/pl/learn/gamemap-generating.md), gdzie znajdziesz przykłady statycznego i dynamicznego wczytywania map oraz pobierania kluczowych lokacji.