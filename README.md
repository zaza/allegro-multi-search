allegro-multi-search
===============

Prosty program wykorzystujący [Allegro Web API](http://allegro.pl/webapi/) do wyszukania sprzedawcy wystawiającego kilka poszukiwanych przedmiotów w tym samym czasie. Pozwala zaoszczędzić na kosztach wysyłki.

Sposób użycia:

1. Załóż konto na Allegro i poproś o [klucz WebAPI](http://allegro.pl/webapi/general.php#klucze).

2. W Eclipse [sklonuj](http://wiki.eclipse.org/EGit/User_Guide#Cloning_or_adding_Repositories) poniższe projekty do swojego workspace:
  * https://github.com/zaza/allegro-webapi
  * https://github.com/zaza/allegro-multi-search

3. Odszukaj klasę com.allegro.webapi.multisearch.Main i [uruchom ją jako aplikację Java](http://help.eclipse.org/indigo/index.jsp?topic=/org.eclipse.jdt.doc.user/tasks/tasks-java-local-configuration.htm) z następującymi argumentami:
  * {login}
  * {hasło}
  * {klucz WebAPI}
  * "{zapytanie}[,{zapytanie}]*"
