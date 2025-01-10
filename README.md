# Aplikacja webowa do zarządzania pracą stolarni

Pełna wersja aplikacji dostępna jest tutaj: [CarpentryShop](https://carpentry-shop-client.vercel.app).

## Opis

Projekt zawiera nastepujące moduły :

### `Użytkowników`

Logowanie do kont użytkowników z weryfikacja loginu i hasła 

- W momencie podania poprawnych danych logowania generowany jest `JWT token`, który jest przypisany do konkretnego konta, a na jego podstawie można określić jego szczegóły(np. przypisana rola)
- W przeciwny wypadku zwracany jest błąd i inforamcja o nim

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
Reszta zasobów wymaga przekazania w zapytaniu tokena, a w niektórych tokena który wskazuje na konto z odpowiednią rolą
