## Instruction (in Polish)
Załóżmy, że na jakiejś sali odbywa się festiwal gier planszowych. Organizatorzy tego festiwalu przygotowali specjalnie na tę okazję zestawy gier, jak również stoliki, na których można je rozłożyć. Kłopot w tym, że na początku nie wiadomo, w jakie gry będą chcieli zagrać odwiedzający festiwal gracze. Dlatego pojawił się problem, jak rozłożyć gry na stolikach oraz w jakim porządku usadzić przy tych stolikach graczy. Problem nie jest trywialny, gdyż:
gracze mogą mieć różne preferencje co do gier, w które chcieliby zagrać;
gry mogą mieć różne ograniczenia co do liczby grających w nie graczy;
stoliki mogą mieć różną liczbę dostępnych przy nich miejsc. <br />
Organizatorom zależy, by liczba grających, jak również ich satysfakcja wynikająca z udziału w grach były jak największe. Oczekiwanie to można wyrazić wzorem: <br />
    MAX ( W1 * Liczba grających + W2 * Suma satysfakcji wszystkich graczy + W3 * Kara) <br />
przy czym satysfakcję pojedynczego gracza można wyliczyć ze wzoru: <br />
    s = 1/i <br />
gdzie i - to pozycja gry, do której przypisano danego gracza, na liście preferencji tegoż gracza(niedopuszczalna jest sytuacja, w której graczowi przypisano grę spoza listy jego preferencji), zaś Kara to liczba wszystkich stolików minus suma wszystkich rozdysponowanych gier (przy czym kara pojawia się, gdy przy którymś ze stolików rozgrywa się więcej niż jedna gra). <br />

Informacje o dostępnych grach zapisano w pliku gry.txt o zawartości: <br />
\# id gry; liczba egzemplarzy; min graczy; max graczy <br />
1; 2; 2; 4 <br />
2; 3; 2; 2 <br />
... <br />
Informacje o dostępnych stolikach zapisano w pliku stoliki.txt o zawartości: <br />
\# id stolika, liczba miejsc <br />
1; 4 <br />
2; 6 <br />
... <br />
Informacje o preferencjach graczy zapisano w pliku preferencje.txt o zawartości: <br />
\# id gracza, lista identyfikatorów preferowanych gier  <br />
1; 1, 2 <br />
2; 3, 1, 2 <br />
... <br />
Wagi W1, W2 i W3 występujące w kryterium można wczytać z linii komend podczas uruchamiania programu, wczytać ze standardowego wejścia podczas działania programu, wczytać z dodatkowego pliku.
