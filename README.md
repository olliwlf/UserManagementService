
# User Management Service

Der UserManagementService ist ein RESTful Webservice, der es Nutzern ermöglicht, sich zu registrieren und ihre Accounts zu verwalten. Die JakartaEE-Anwendung läuft auf einem Wildfly Application Server (v32.0.1) und speichert die Daten in der WildFly In-Memory-Datenbank (H2). Die Kommunikation zwischen Client und Server erfolgt im JSON-Datenformat.

## Tech-Stack
- Programmiersprache: Java
- Frameworks und Bibliotheken: 
    - Jakarta EE
    - Project Lombok
    - Mockito + Mockito-JUnit-Jupiter
    - JUnit-Jupiter
    - Rest-Assured
- Application Server: Wildfly (v32.0.1)
- Datenbank: In-Memory-Database (H2) of Wildfly (v32.0.1)

## Detaillierte Beschreibung
### Datenmodell: Entität "User"
- Attribute: id, firstname, lastname, email, birthday, password
- Eindeutige Id (fortlaufend nummeriert)
- Validierung:
- Pflichtfelder (NotNull): firstname, lastname, email, password 
    - Nicht leer (NotEmpty): firstname, lastname: 
    - Email-Format: email
    - Länge zwischen 6 und 20 Zeichen: password

### Data-Access-Layer: „persistence.xml“
- Code-First-Ansatz: Datenbank-Tabelle für User-Entität wird im Code bzw. in der Konfiguration (persistence.xml) definiert
- Tabelle “users” und ihre Spalten wird per JPA erzeugt
    - id: bigint generated by default as identity [primary key]
    - birthday: date
    - password: varchar(20) not null
    - email: varchar(255) not null
    - firstname: varchar(255) not null
    - lastname: varchar(255) not null

### Service-Layer: „UserService“
- Service zum Zugriff auf die in-memory Datenbank (H2) des WildFly Application Servers (via EntityManager)
- Implementierung mit CRUD-Operationen (Create, Read, Update, Delete) für die Entität „User“

### Presentation-Layer: „UserResource“
- RESTful Web-Service mit folgenden Endpunkten:
    - GET /api/users: Liste aller Benutzer abrufen
    - GET /api/users/{userId}: Einzelnen Benutzer anhand der ID abrufen
    - POST /api/users: Neuen Benutzer hinzufügen
    - PUT /api/users/{userId}: Existierenden Benutzer aktualisieren
    - DELETE /api/users/{userId}: Existierenden Benutzer löschen
- Verwendung des JSON-Datenformats für die Kommunikation zwischen Client und Server

## Test-Konzept
- Zur Überprüfung der Funktionalität können Unit- und Integrationstests verwendet werden:
    - Unit-Tests für die Endpunkte des RESTful Web-Service via „UserResource“ und für die Service-Klasse „UserService“ 
    - Systemtests für die Endpunkte wurden mithilfe von Postman durchgeführt:
        - Benutzer erstellen: POST http://localhost:8080/UMS-1.0/api/users
        - Liste aller Benutzer abrufen: GET http://localhost:8080/UMS-1.0/api/users
        - Einen Benutzer anhand der ID abfragen: GET http://localhost:8080/UMS-1.0/api/users/1 
        - Existierenden Benutzer aktualisieren: PUT http://localhost:8080/UMS-1.0/api/users/1
        - Existierenden Benutzer löschen: DELETE http://localhost:8080/UMS-1.0/api/users/1 
    - Zudem wurde die Validierung der Dateneingabe via POST und PUT (Pflichtfelder, Email-Format, Passwortlänge) und die Datenabfrage für ungültige Angaben (Nutzer mit der ID … existiert nicht in der Datenbank) via GET und DELETE überprüft.

### Beispiel JSON:
{
    "firstname": "Timo",
    "lastname": "Truthahn",
    "email": "timo.truthahn@example.com",
    "birthday": "2000-01-01",
    "password": "testPassword123"
}

