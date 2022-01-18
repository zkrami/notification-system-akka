
Sequence Diagram of `POST /api/identifiers`

```mermaid
sequenceDiagram
    Client->>HTTP: Create Identifier 
    HTTP->>System: CreateIdentifier("identifier-string")
    System->>HTTP: CREATED
    HTTP->>Client: CREATED
 ```


Sequence Diagram of `GET /api/identifiers`

```mermaid
sequenceDiagram
    Client->>HTTP: Get Identifiers 
    HTTP->>System: GetIdentifiers()
    System->>HTTP: Identifier[]
    HTTP->>Client: Identifier[]
 ```

