# Proiect Energy System Etapa 2

## Despre

Programare Orientata pe Obiecte, Seriile CA, CD
2020-2021

<https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/proiect/etapa2>

Student: Zamfirescu Radu Ioan, 322CDa

## Rulare teste

Clasa Test#main
  * ruleaza solutia pe testele din checker/, comparand rezultatele cu cele de referinta
  * ruleaza checkstyle

Detalii despre teste: checker/README

Biblioteci necesare pentru implementare:
* Jackson Core 
* Jackson Databind 
* Jackson Annotations

Tutorial Jackson JSON: 
<https://ocw.cs.pub.ro/courses/poo-ca-cd/laboratoare/tutorial-json-jackson>

## Implementare

### Entitati
  
* entities#Consumer - pastreaza informatiile specifice unui consumator dupa
    enunt;
    
* entities#Distributor - obiectul care pastreaza informatiile specifice unui
    distribuitor ca in enunt;
    
* entities#Producer - clasa specifica unui producator dupa enunt;

* entites#Payer - interfata care pune in evidenta comportamentul unui obiect
    de a executa o plata;
    
* entities#Contract - retine datele unei intelegeri dintre un distribuitor si
    un consumator;
    
* entitites#EnergyType - enum cu toate felurile de energie ce pot fi furnizate
    de producatori;

* factory#Factory - clasa care creeaza obiecte pe baza datelor de intrare;
* factory#Result - clasa cu informatiile folosite pentru testare

* input - pachetul cu clasele pentru fisierele json din input

* strategies#ContractPriceStrategy - interfata pentru calcularea pretului unui
    contract oferit de un distribuitor;

* strategies#EnergyChoiceStrategy - interfata ce returneaza multimea de
    producatori potrivita unui distribuitor; este implementata in cele 3 moduri
    descrise in cerinta;

* utils#Utils - clasa cu metode statice de transformare a string-urilor in
    enum-uri;
    
* utils#Constants - constante utile;

* utils#BusinessFlow - clasa cu logica simularii;

* utils#Observable - interfata implementata de un obiect de al carui
    comportament depinde alta multime de obiecte;

* utils#Observer - interfata care pune in evidenta modificarea starii unui
    obiect atunci cand obiectul pe care il observa a suferit schimbari;

* Main - punctul de start pentru simulare;    
### Flow

In Main#main se creeaza un obiect al clasei BusinessFlow in care se 
initializeaza toate entitatile folsindu-se datele primite in input.
 
La inceputul fiecarei luni, se schimba costul de infrastructura pentru anumiti
distribuitori, apoi se adauga noii consumatori. Mai departe, fiecare consumator
se asigura sa aiba un contract apoi plateste pentru acesta distribuitorului sau.
In continuare, distribuitorii isi platesc si ei costurile.
Dupa aceste etape, se fac schimbarile proprii producatorilor, iar sistemul de
rulare (clasa BusinessFlow) notifica distribuitorii, luandu-i in ordine dupa id,
despre modificarile efectuate asupra producatorilor lor. In final, producatorii
isi salveaza cati distribuitori au avut fiecare in parte, cu exceptia rundei
initiale (0).
Am incercat pe cat posibil sa retin in fiecare clasa doar informatiile care mi
s-au parut ca sunt esentiale pentru ele, modificandu-le din exterior folosind
metodele proprii.

### Elemente de design OOP

Am folosit incapsulare in clasa Producer creand clasa interna MontlhyStat si in
pachetul cu strategii cand am facut interfata ProducersWithdrawStrategy.
Am folosit abstractizare pentru metoda de plata facuta de o entitate, aceasta
fiind realizata in mod diferit depinzand de tipul clasei.

### Design patterns

Am folosit 4 design-patterns:
* singleton + factory - la clasa Factory, facuta special pentru a crea obiecte;
* strategy - atat pentru modul in care un distributor isi alege un producator,
    cat si pentru modul in care isi calculeaza pretul unui contract;
* observer - a fost folosit pentru evidentierea relatiei dintre sistemul de
    rulare (obiectul observabil) si distribuitori (obiectele observatoare),
    acestia din urma fiind notificati constant de modificarile aduse asupra
    preturilor producatorilor;

### Dificultati intalnite, limitari, probleme

Pentru a rezolva ConcurrentModificationException, am facut un deep copy listei
prin care iteram atunci cand mi se arunca aceasta exceptie.

Initial, pattern-ul Observer l-am aplicat astfel incat obiectul observabil sa
fie un producator, insa dupa ce mi-am dat seama de modul in care sunt
notificati distribuitorii, daca as fi apelat pentru fiecare producator schimbat
(luandu-i dupa id) metoda notifyObservers() in care anunt fiecare distribuitor
propriu (tot dupa id) nu as mai fi respectat exact ordinea tuturor
distribuitorilor dupa id. De exemplu, daca producatorii 1 si 2 se schimba intr-o
luna si primul are distribuitorii 1, 3, 4, iar al doilea 1, 2, 4, ordinea
actualizarii distribuitorilor ar fi 1, 3, 4, 2 in loc de 1, 2, 3, 4. Din aceasta
cauza nu imi trecea testul complex_5;
