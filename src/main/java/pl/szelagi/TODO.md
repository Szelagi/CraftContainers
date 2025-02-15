- naprawić recovery, ponieważ w trakcie przerwania serwera, może być w trakcie zapisu

- optymalizacja MainProcess, RemoteProcess
  - trudne w zrozumieniu kodu
  - wymagana lepsza złożoność obliczeniowa

- systemy plików mapy i schematy są trudne w zrozumieniu kodu
  - refaktoryzacja kodu
  - osobna metoda do systemu plików mapy

- session .stop()
  - poprawić StopCause, ponieważ jest niezrozumiały
  - zrobić wyjściowy listener dla Session jako StopEvent, aby można było odczytać wynik końcowy

- zdarzenie nie mogę używać refleksji