# Aplikacja webowa do zarządzania pracą stolarni

Pełna wersja aplikacji dostępna jest tutaj: [CarpentryShop](https://carpentry-shop-client.vercel.app).

## Opis

Aplikacja działa w oparciu o model klient-serwer

Strona serwerowa działa na frameworku Spring Boot Javy, co zapewnia ciągłość działania i reakcje na działania użytkownika

Komunikacja odbywa się poprzez Kontroler w którym zawarte są endpointy (adresy pod którymi udostępnione są poszczególne funkcjonalności/zasoby)

Dane są przechowywane w bazie danych (postgreSQL) na podstawie modeli

Strona klienta oparta na frameworku React odpowiada za wygląd aplikacji jak i możliwość komunikacji z serwerem (pobieranie, dodawanie i modyfikowanie danych)

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

Możliwość tworzenia zamówień w których pozycjami są projekty

Budowa zamówienia wygląda w sposób następujacy

Zamówienie -> lista pozycji -> projekty. W praktyce są to 3 osobne tabele które są w relacji ze sobą

Zamówienie zawiera powiązanie z listą instancji pozycji, która zawiera powiązanie z konkrentym projektem

Taki zabieg pozwala dodanie do zamówienia projektu, który może posiadać dodatkowe informacje (np. ilość)

Do stworzenia zamówienia wymagane jest przesłanie na odpowiedni endpoint listy pozycji

Kolejność operacji tworzenia zamówienia:

1. Sprawdzona zostaje poprawność przesłanych danych (tj. czy dany projekt istnieje w bazie, czy parametry ceny w żądaniu zgadzają z tym z bazy itp.)
2. Zostaje wyliczona cena całkowita zamówienia, na podstawie cen i ilości pojedyńczych pozycji
3. Stworzona zostaje nowa instancja zamówienie, a nastepnie poszczególne pozycje z listy zotają zapisane do bazy danych do których zostaje przypisane to konkretne zamówienie

W przypadku jakiegokolwiek błędu zostaje zwrócona informacja o nim

Wyświetlanie listy pozycji po stronie klienta opiera się na pobieraniu danych z pamięci localStorage. Dane te są zapisywane w momencie wybierania poszczególnych projektów

Wymagane jest również przesłanie parametru płatności, które określa jej status (true/false) 

Ze strony klienta status jest określany na podstawie wybranej metody płatności

- Płatność bramką Stripe, po zaakceptowaniu płatności wybierany jest status true
- Płatność gotówką, wybierany jest status płątności true
- Płatność później, wybierany jest status false

Status ten określa czy zamówienie można zamknąć (ta funkcjonalność dostępna z poziomu kolejnego modułu)

Dostęp do tych zasobów możliwy po przekazaniu w zapytaniu tokena z obojętnie jaką rolą

### `Zlecenia` 

Po przyjęciu zamówienia jest one do realizacji.

Do każdej z pozycji w zamówieniu należy wybrać przedmioty z magazynu, które zostały wykorzystane do jego realizacji

Na odpowiedni endpoint należy wysłać liste "zasobów" wykorzystanych do konkretnej pozycji oraz jej numer id

Kolejność operacji dodawania "zasobów" do pozycji:

1. Sprawdzona zostaje poprawność przesłanych danych (tj. czy dany przedmiot istnieje w bazie, czy jest go odpowiednia ilość itp.)
2. Wyszukanie odpowiedniej pozycji
3. Poszczególne pozycje z listy "zasobów" zostają zapisane do bazy danych jednocześnie zmieniając ilość przedmiotu z magazynu
4. "Zasoby" zostają przypisane do konkretnej pozycji

Przy każdym dodawaniu sprawdzane jest czy wszystkie pozycje z zamówienia są uzupełnione, aby określić czy można zamknąć zamówienie

Nie można zamknąć zamówienie jeżeli nie jest opłacone oraz jeżeli wszystkie pozycje nie są uzupełnione

"Zasoby" można również usuwać. Takie działanie powoduje usunięcie ich instancji oraz przywracanie ilości przedmiotów w magazynie

Jeżeli do zamówienia została wbrana opcja płatności później z tego poziomu można dokonać ponownie płatności (do wyboru płatność gotówką lub bramką Stripe)

### `Statystyki`

Dane te wyświetlane są na panelu głównym Dashboard

Strona kliencka wysyła zapytanie do serwera o konkretne statystyki (np. suma dzisiejszej sprzedaży)

Dostęp do nich posiada każdy zalogowany użytkownik

### `Bramka Stripe` 

Sposób płatności online lub kartą bankową

Na stronie Stripe, po założeniu konta wygenerowane zostają klucze uwierzytelniające do obsługi płatności 

Funkcjonalność działa w trybie testowym co oznacza, że można ją obsłużyć bez wykonania faktycznej opłaty z rzeczywistych środków

Do pełni działania wystarczy przełączyć ją na tryb "live" w panelu Stripe

Generowanie płatności działa na podstawie przekazania klucza uwierzytelniającego, który służy do stworzenia płatności przypisanej do naszego konta Stripe (w tym przypadku konta stworzonego na potrzeby aplikacji)

Wraz z kluczem przekazujemy szczegółowe dane o pozycjach za które ma być pobrana opłata (nazwa, ilość, cena)

Po przetworzeniu danych przez Stripe generowany jest link do płatności

Z poziomu klienta wyglada to tak, że po wybraniu płatności kartą zostają przesłane dane, a następnie wykonane przekierowanie na adres do płatności w bramce Stripe (adres wygenerowany na podstawie kluczy)

Dane do płątności w trybie testowym :

Numer karty: 4242 4242 4242 4242

Data ważności: jakakolwiek byle w przyszłości

Kod CVC: jakiekolwik trzy liczby

### `Google Drive API`

Dysk Google wykorzystany do przechowywanie niektórych plików wykorzystywanych w aplikaci

Po założeniu konta w API Google Drive możliwe jest wygenerowanie pliku, który służy do połączenia z faktycznym kontem Dysku Google (zawiera informacje odnośnie konta, kluczy itp)

Przy każdej operacji którą chcemy wykonać za zasobach Dysku Google należy nawiązać z nim połączenia na podstawie wcześniej wspomnianego pliku

Przechowywane informacje to m.in
- Zdjęcia, które można opcjonalnie dodać do projektu
- Pliki zapasowe projektów i magazynu

Zdjęcia przechowywane są w imiennych plikach ponieważ nazwy muszą być unikalne podczas tworzenia projektów

Każda aktualizacja nazwy projektu powoduje zmiane folderów w którym przechowywane jest zdjęcie 

Pliki zapasowe działaja na zasadzie zapisanie informacji binarnie do pliku i zapisanie tego pliku na dysku

Dane można zapisać jak i odczytać w celu przywrócenia danych lub przy przynoszeniu na inną baze danych

Pliki w nazwie zawierają date utworzenia, dzięki czemu można wybierać z różnych dostępnych wersji

### `JavaMailSender`

Możliwość wysyłania wiadomości mail za pomocą klasy `JavaMailSender`

Z poziomu panelu Dashboard możliwe jest wysłanie wiadomości z tabela zarobków na konkretny przedział dat

Zakres dat wybieramy z kalendarza oraz podajemy adres odbiorcy, który otrzyma wiadomość

W wiadomości znajdować się będzie rozpiska zarobków z każdego dnia z danego przedziału wraz z sumą wszystkich dni

Aplikacja również wysyła automatycznie wiadomość raz w miesiącu za pomocą adnotacji `Scheduled` w metodzie z odpowiedniej klasy (zawiera tabele z podsmowaniem miesiąca poprzedzjącego)