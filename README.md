# Getting Started
Questo progetto è un demo che usa un livello di persistenza embedded
e tutti i dati vengono persi ogni volta che il server viene riavviato.
Per questo motivo ho inserito una fase di preload dove vengono inseriti un paese, una regione e una città.

Le tecnologie utilizzare sono le seguenti:
* Spring boot
* JPA e DB H2
* AOP (esempio di programmazione orientata agli aspetti), utile per gestire log (come in questo caso), ma anche per verificare il tempo si esecuzione delle chiamate o altro
* Validazione delle API rest
* Documentazione API rest con swagger
* Possibilità di utilizzare il link http://localhost:8080/swagger-ui/index.html per testare il funzionamento
* Lombok (comodo per ridurre la verbosità di java)
* Modelmapper per la conversione da entity a dto e viceversa
* Test automatici che testano tutti i metodi dei rest con spring-boot-starter-test


L'eseguibile viene creato con il seguente script:
* .\gradlew build

Per lanciare il servizio si può sceglie tra le seguenti alternative:
* java -jar test-0.0.1-SNAPSHOT.jar
* .\gradlew bootRun

### Riferimenti alla documentazione

* [API DOCS](http://localhost:8080/v3/api-docs/)
* [Swagger](http://localhost:8080/swagger-ui/index.html)

### Miglioramenti
I miglioramenti da fare per l'utilizzo in un ambiente di produzione possono essere:

#### Gestione persistenza e docker
* Cambiare il DB utilizzato an esempio con mysql o postgresql;
* gestire il livello di persistenza come servizio docker
* creare un immagine docker, per esempio con il seguente dockerfile:

FROM openjdk:11-jdk-alpine

VOLUME /tmp

ARG JAR_FILE

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]

* poi con docker-compose si crea il file yaml per gestire l'intero stack con un servizio per il db e uno per l'applicazione.

#### Responce con link navigabili invece del dettaglio degli oggetti collegati.
Un'altra miglioria utile per un eventuale front-end è sostituire i dettagli degli oggeti collegati con un url navigabile già pronto,
in modo che per vederne il dettaglio bisogna navigare tale link,
rendendo la navigazione più leggera e quindi più reattiva.
