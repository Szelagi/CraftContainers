# Kolejność wykonywania zdarzeń lokalnie
W podejściu lokalnym analizujemy każdy komponent osobno, zwracając uwagę na kolejność wykonywania [zdarzeń wewnętrznych](/pl/learn/internal-events.md) w jego obrębie.


## Uruchomienie komponentu
1. `onComponentInit`
2. `onPlayerInit` - *wywoływane dla każdego gracza, który znajdował się na sesji.*
3. `onComponentRecovery`
4. `onPlayerRecovery` - *wywoływane dla każdego gracza, który znajdował się na sesji*


## Wyłączenie komponentu
1. `onPlayerDestory` - *wywoływane dla każdego gracza, który znajdował się na sesji.*
2. `onComponentDestroy`
3. Usunięcie danych `recovery`, które były utworzone w `onComponentRecovery` oraz `onPlayerRecovery`


## Dołączenie gracza
1. `onPlayerJoinRequest`
2. `onPlayerInit`
3. `onPlayerRecovery`


## Opuszczenie gracza
1. `onPlayerDestroy`
2. Usunięcie danych `recovery`, które były utworzone w `onPlayerRecovery` dla wychodzącego gracza