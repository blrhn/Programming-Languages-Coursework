## Instruction (in Polish)
Podczas laboratorium należy zbudować aplikację działającą w środowisku rozproszonym, wykorzystującą do komunikacji gniazda TCP/IP obsługiwane za pomocą klas ServerSocket oraz Socket. Dokładniej - należy zaimlementować mały systemu, w którego skład wejdą podsystemy uruchamiane równolegle (na jednym lub na kilku różnych komputerach). Zakładamy, że system będzie pełnić rolę symulatora sieci rzecznej ze zbiornikami retencyjnymi. Napływ wody do danego zbiornika wynika z tego, ile wody niosą odcinki rzeczne wpadające do tego zbiornika. Dany ocinek rzeczny niesie tyle wody, ile wypływa ze zbiornika powyżej (jeśli taki jest) oraz ile wody przynoszą opady atmosferyczne występujące w jego zlewni. Dla uproszenia można założyć, że woda wypływająca z jakiegoś zbiornika pojawia się na końcu odcinka rzecznego z opóźnieniem oraz w takiej samej objętości, w jakiej wypłynęła z tego zbiornika (bez modelowania "fali", opóźnienie jest proporcjonalne do długości odcinka rzecznego), zaś wody z opadów atmosferycznych pojawiają się "natychmiast". Wielkości przepływów tych wód wyrażane są w m3/s, opóźnienie wyraża się w h. Każdy zbiornik retencyjny "i" ma zdefiniowaną maksymalną pojemność "V_i". Posiada też zasuwy, których otwarcie bądź domknięcie pozwala sterować wielkością zrzutu wody "d_i" (m3/s). Stopień zapełniania danego zbiornika można wyliczyć w interwałach czasu na podstawie tego, co się w nim znajdowało oraz różnicy między wielkością napływu i zrzutu wody. W przypadku przepełnienia zbiornika następuje zrównanie zrzutu wody z napływem. Aby to wszystko dało się zasymulować przyjęto założenia:

- Zbiornik (RetensionBasin) osobna aplikacja, która symuluje działanie zbiornika retencyjnego (w interfejsem graficznym),
  - parametryzowana objętością maksymalną, numerem własnego portu, host i numerem portu centrali, hostem i numerem portu odcinka rzecznego wejściowego dostarczającego mu wodę (takich odcinków może być więcej niż jeden),
   - otrzymuje informacje o napływie wody od wchodzącego odcinka rzecznego (odcinków rzecznych wchodzących może być kilka),
   - wysyła informacje o wypływie wody do wychodzącego odcinka rzecznego,
   - pozwala sterować zasuwami;
   - korzysta z interfejsu odcinka rzecznego wychodzącego, by przekazać informację o rzeczywistym zrzucie wody;
   - korzysta z interfejsu odcinka rzecznego przychodzącego, by przekazać informację o swoim porcie (takich odcinków może być więcej niż jeden), inaczej mówiąc zbiornik "dodaje się" do odcinków rzecznych wejściowych
   - korzysta z interfejsu centrali, by przekazać informację o swoim porcie
   - na zadanym porcie wystawia interfejs IRetensionBasin z metodami:
<pre>
int getWaterDischarge() - zwraca informację o zrzucie wody,
long getFillingPercentage() - zwraca informację o wypełnieniu zbiornika w procentach,
void setWaterDischarge(int waterDischarge) - ustawiania wielkości zrzutu wody,
void setWaterInflow(int waterInflow, int port) - ustawia wielkość napływu wody z odcinka rzecznego działającego na wskazanym porcie (numer portu pełni tu rolę identyfikatora),
void assignRiverSection(int port, string host) - ustawia host i port wychodzącego odcinka rzecznego;
</pre>

- Odcinek rzeczny (RiverSection) osobna aplikacja, która symuluje zachowanie odcinka rzecznego (z interfejsem graficznym),
  - parametryzowana wartością opóźnienia, numerem własnego portu, hostem i numerem portu środowiska, hostem i portem zbiornika na wejściu (by móc "dodać się" do tego zbiornika jako odcinek rzeczny wyjściowy)
  - otrzymuje informacje o rzeczywistym zrzucie wody ze zbiornika powyżej,
  - wysyła informacje o napływie wody do zbiornika na wyjściu;
  - korzysta z interfejsu zbiornika na wyjściu, by przekazać mu informację o aktualnym napływie wody;
  - korzysta z interfejsu środowiska, by przekazać informację o swoim porcie.
  - na zadanym porcie wystawia interfejs IRiverSection z metodami:
<pre>
void setRealDischarge(int realDischarge) - ustawia rzeczywistą wielkość zrzutu wody ze zbiornika znajdującego się na początku odcinka rzecznego,
void setRainfall(int rainfall) - ustawia wielkość opadów atmosferycznych,
void assignRetensionBasin(int port, string host) - ustawia host i port zbiornika retencyjnego na wyjściu;
</pre>

- Środowisko (Environment) osobna aplikacja, która odpowiada za generowanie opadów atmosferycznych na odcinkach rzecznych (z interfesem graficznym);
  - korzysta z interfejsów odcinków rzecznych, by przekazać im informacje o aktualnych opadach.
  - na zadanym porcie wystawia interfejs IEnvironment z metodami:
<pre>
  void assignRiverSection(int port, string host) - ustawia host i port odcinka rzecznego;
</pre>

- Centrala (ControlCenter) osobna aplikacja, która pozwala na monitorowanie stanu zapełnienia zbiorników oraz sterowanie ich zasuwami;
  - korzysta z interfesów zbiorników retencyjnych, by odczytać ich poziom zapełnienia oraz zrzut wody oraz by ustawić zrzut wody.  
  - na zadanym porcie wystawia interejs IControlerCenter z metodami:
<pre>
  void assignRetensionBasin(int port, string host) - ustawia host i port zbiornika retencyjnego (zbiorników może być kilka);
</pre>

Komunikacja między elementami systemu ma odbywać się z wykorzystaniem gniazd TCP/IP. Aby odpalić metodę danego interfejsu trzeba wysłać żądanie zakodowane tekstowo w postaci:
<pre>
digit excluding zero = "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" ;
digit                = "0" | digit excluding zero ;
positive integer = digit excluding zero, { digit } ;

host = positive integer, ".", positive integer, ".", positive integer, ".", positive integer ;
get method = "gwd" | "gfp" ;
set method = "swd:", positive integer | "swi:", positive integer "," positive integer | "srd:", positive integer | "srf:", positive integer ;
assign method =  "arb:" positive integer, ",", host | "ars:" positive integer, ",", host  ;

request = get method | set method | assign method;

response = positive integer;
</pre>
