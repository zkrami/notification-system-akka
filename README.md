Sequence Diagram of `POST /api/identifiers`

```mermaid
sequenceDiagram
    Client->>HTTP: Create Identifier 
    HTTP->>System: CreateIdentifier(Identifier("identifier-string"))
    System->>HTTP: SuccessReply
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

Sequence Diagram of `POST /api/notifications`

```mermaid
sequenceDiagram
    Client->>HTTP: Post notifications 
    HTTP->>System: SendNotifications(message,Identifier[])
    System->>NotificationPublisher: PublishNotification(message,Identifier[])
    NotificationPublisher->> IdentifierActor: AddNotification(Notification)
    NotificationPublisher->>HTTP: SuccessReply  
    HTTP->>Client: CREATED
 ```


Sequence Diagram of `GET /api/notifications`

```mermaid
sequenceDiagram
    Client->>HTTP: Get notifications 
    HTTP->>System: GetNotifications()
    System->>HTTP: Notification[]
    HTTP->>Client: Notification[]
 ```