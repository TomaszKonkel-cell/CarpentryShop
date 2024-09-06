# Aplikacja webowa do zarządzania pracą stolarni

Pełna wersja aplikacji dostępna jest tutaj: [CarpentryShop](https://carpentry-shop-client.vercel.app).

## Opis

Projekt zawiera nastepujące moduły :

### `Uwierzytelniania i Autoryzowania`

Służy zabezpieczeniu poszczególnych części aplikacji przed nieporządanym dostępem. Za pomocą `SecurityFilterChain`, 
ustalamy kto do czego ma dostęp. Do ustalenia poprawnej "tożsamości" wykorzystywany jest `JWT token`, który przekazuje nam informacje o użytkowniku

Przewidziane są trzy przypadki filtrowania zapytań wysyłanych do serwera: 

    - Dany zasób może być udostępniony dla wszystkich
    - Dany zasób może być udostępniony dla uwierzytelnionych użytkowników (takich co przekażą do zapytania `JWT token`)
    - Dany zasób może być udostępniony dla zautoryzowanych użytkowników (takich co posiadają odpowiednie uprawnienia)



### `Użytkowników`

Pełne zarządzanie użytkownikami. Szyfrowanie hasła w momencie utworzenia użytkownika. Dodawanie ról które definiują dostęp do zasobów (Admin, Moderator, User)

Każdy zasób zawiera uwzględnienie specjalnych wyjątków odpowiadające ustalonej logice biznesowej np. unikalne nazwy użytkowników, wymagania dotyczące haseł
Zawierają także zabezpiecznia w przypadku podania złych typów wartości czy też pustych
Udostępnione zasoby w kontrolerze REST:

    - Logowanie (Dla wszystkich), przesłanie loginu i hasła oraz zweryfikowanie ich poprawności, poprawna weryfikacja zwraca szczegóły zalogowanego użytkownika a błędna szczegółową informacjew
    - Tworzenie użytkowników (Dla Admina), przesłanie danych, zaszyfrowanie hasła oraz zapisanie go w bazie
    - Lista użytkowników (Dla Admina), zwrócenia listy wszystkich dostępnych użytkowników
    - Szczegóły użytkowników (Dla Admina), przesłanie parametru ID po którym zwracany jest konkretny użytkownik
    - Zmiana nazwy (Dla Admina), przesłanie nowej nazwy oraz ID, po czym aktualizowany jest konkretny po ID użytkownik 
    - Zmiana hasła (Dla Admina), przesłanie starego hasła, nowego hasła oraz ID, po sprawdzeniu poprawności starego hasła, nowe jest szyfrowane i zapisywanee dla konkretnego użytkownika
    - Usuwanie ról (Dla Admina), przesłanie ID oraz nazwy roli, ustalane są nowe role i zapisywane dla użytkownika
    - Usuwanie użytkownika, przesłanie ID, znajdowanie użytkownika i usuwanie go

### `npm run build`

Builds the app for production to the `build` folder.\
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.\
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can't go back!**

If you aren't satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point you're on your own.

You don't have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn't feel obligated to use this feature. However we understand that this tool wouldn't be useful if you couldn't customize it when you are ready for it.

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).

### Code Splitting

This section has moved here: [https://facebook.github.io/create-react-app/docs/code-splitting](https://facebook.github.io/create-react-app/docs/code-splitting)

### Analyzing the Bundle Size

This section has moved here: [https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size](https://facebook.github.io/create-react-app/docs/analyzing-the-bundle-size)

### Making a Progressive Web App

This section has moved here: [https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app](https://facebook.github.io/create-react-app/docs/making-a-progressive-web-app)

### Advanced Configuration

This section has moved here: [https://facebook.github.io/create-react-app/docs/advanced-configuration](https://facebook.github.io/create-react-app/docs/advanced-configuration)

### Deployment

This section has moved here: [https://facebook.github.io/create-react-app/docs/deployment](https://facebook.github.io/create-react-app/docs/deployment)

### `npm run build` fails to minify

This section has moved here: [https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify](https://facebook.github.io/create-react-app/docs/troubleshooting#npm-run-build-fails-to-minify)
