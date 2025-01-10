# Aplikacja webowa do zarządzania pracą stolarni

Pełna wersja aplikacji dostępna jest tutaj: [CarpentryShop](https://carpentry-shop-client.vercel.app).

## Opis

Aplikacja działa w oparciu o model klient-serwer

Strona serwerowa działa na frameworku Spring Boot Javy, co zapewnia ciągłość działania i reakcje na działania użytkownika
Komunikacja odbywa się poprzez Kontroler w którym zawarte są endpointy (adresy pod którymi udostępnione są poszczególne funkcjonalności/zasoby)
Dane są przechowywane w bazie danych (postgreSQL) na podstawie modeli

Strona klienta odpowiada za wygląd aplikacji jak i możliwość komunikacji z serwerem (pobieranie, dodawanie i modyfikowanie danych) i działa na frameworku React

Projekt zawiera nastepujące moduły :

### `Użytkowników`

Logowanie do kont użytkowników z weryfikacja loginu i hasła 

- W momencie podania poprawnych danych logowania generowany jest `JWT token`, który jest przypisany do konkretnego konta, a na jego podstawie można określić jego szczegóły (np. przypisana rola)
- W przeciwny wypadku zwracany jest błąd i informacja o nim

Możliwość tworzenia kont dla pracowników o przypisanej roli, która określa dostęp do konkretnych zasobów dostępnych w aplikacji

- Hasło jest szyfrowane
- W momencie podawania danych do konta muszą one być obecne (nie mogą być puste)
- Błędne przekazanie tych danych zwraca konkretny komunikat błędu

Aktualizowanie i usuwanie kont użytkowników

Klasa `SecurityFilterChain` umożliwia zarządzanie dostępem do zasobów poprzez ustawienie konkretnych reguł

- Podczas próby wykorzytania zasobów o ograniczonym dostępie wymagany jest odpowiedni token, który identyfikuje użytkownika
- Próba dostępu może zostać przyznana albo odrzucona

Przewidziane są trzy przypadki filtrowania zapytań wysyłanych do serwera: 

- Dany zasób może być udostępniony dla wszystkich
- Dany zasób może być udostępniony dla uwierzytelnionych użytkowników (takich co przekażą do zapytania `JWT token`)
- Dany zasób może być udostępniony dla zautoryzowanych użytkowników (takich co posiadają odpowiednie uprawnienia)

Logowanie jest dostępne dla każdego kto wyśle zapytanie, ponieważ nie wymaga tokena

Reszta zasobów wymaga przekazania w zapytaniu tokena, który wskazuje konto z rolą ADMIN

### `Projekty i Magazyn`

Obydwa moduły posiadaja w zasadzie takie same cechy, a jedyna rożnica wynika z ich przeznaczenia

Projekty służą jako ogół rzeczy oferowanych w stolarni. Mogą to być faktyczne projekty wyrobów stolarskich ale również ogólne rzeczy

Przykład -> projektem może być np. Krzesło drewniane do jadalni, ale może być też np. Usługa wytworzenia (w ogólnym tego słowa znaczeniu)

Konkretny schemat korzystania z tego wybiera sobie już użytkownik końcowy

Magazyn odpowiada już za bardziej rzeczywisty aspekt, a mianowicie faktyczne odwzorowanie rodzaju i ilości przedmiotów, które są posiadane. Kontrola nad tym jest połączona z pózniejszymi modułami

Inną różnicą tych modułów są również dane jakie posiadają. Projekty maja dane bardziej ogólne (nazwa, opis itp.), magazyn również posiada takie ale również bardziej szczegółowe (ilość, typ, kategoria)

Dostęp do zasobów możliwy poprzez przekazanie w zapytaniu token, który wskazuje na konto z rolami ADMIN lub MODERATOR

### `Zamówienia` 

Możliwość tworzenia zamówień, a pozycjami które można do nich dodać to projekty

Budowa zamówienia wygląda w sposób następujacy

Zamówienie -> lista pozycji -> projekty. W praktyce są to 3 osobne tabele które są w relacji ze sobą

Zamówienie zawiera powiązanie z instancja listy pozycji, która zawiera powiązanie z konkrentym projektem

Taki zabieg pozwala dodanie do zamówienia projektu, który może posiadać dodatkowe informacje (np. ilość)

Do stworzenia zamówienia wymagane jest przesłanie na odpowiedni endpoint (punkt w aplikacji po stronie serwera, który wykonuje jakąś operacje) listy pozycji

- Sprawdzane zostaje poprawność przesłanych danych (tj. czy dany projekt istnieje w bazie, czy parametry ceny w żądaniu zgadzają z tym z bazy itp.)
- Stworzone zostaje zamówienie, a nastepnie zapisane pozycje do których zostaje przypisane to konkretne zamówienie
- Zostaje wyliczona cena całkowita zamówienia, na podstawie cen i ilości pojedyńczych pozycji
- W przypadku każdego błędu zostaje zwrócona informacja o nim