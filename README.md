allegro-multi-search
===============

Prosty program wykorzystujący [Allegro Web API](http://allegro.pl/webapi/) do wyszukania sprzedawcy wystawiającego kilka poszukiwanych przedmiotów w tym samym czasie. Pozwala zaoszczędzić na kosztach wysyłki.

Sposób użycia:

1. Załóż konto na Allegro i poproś o [klucz WebAPI](http://allegro.pl/webapi/general.php#klucze).

2. Skompiluj projekt przy użyciu Maven: `mvn clean install -DskipTests`

3. Odszukaj klasę `com.allegro.webapi.multisearch.Main` i [uruchom ją jako aplikację Java](http://help.eclipse.org/indigo/index.jsp?topic=/org.eclipse.jdt.doc.user/tasks/tasks-java-local-configuration.htm) z następującymi argumentami:
  * {login}
  * {hasło}
  * {klucz WebAPI}
  * "{zapytanie}[,{zapytanie}]*"
