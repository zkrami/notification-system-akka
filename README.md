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

Sequence Diagram of `DELETE /api/identifiers/identifier`

```mermaid
sequenceDiagram
    Client->>HTTP: Delete Identifier 
    HTTP->>System: DeleteIdentifiers(identifier)
    System->>HTTP: SuccessReply
    HTTP->>Client: DELETED
 ```


Sequence Diagram of `POST /api/notifications`

```mermaid
sequenceDiagram
    Client->>HTTP: Post notifications 
    HTTP->>System: SendNotifications(message,Identifier[])
    System->>NotificationPublisher: Process(message,Identifier[])
    NotificationPublisher->> IdentifierActor: AddNotification(Notification)
    IdentifierActor->> NotificationPublisher :IdentifierWorkerAck
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




Sequence Diagram of `GET /api/notifications/identifier`

```mermaid
sequenceDiagram
    Client->>HTTP: Get identifier notifications 
    HTTP->>System: GetIdentifierNotifications(identifier)
    System->>IdentifierActor: GetNotifications()
    IdentifierActor->>HTTP: IdentifierNotification[]
    HTTP->>Client: IdentifierNotification[]
 ```


Sequence Diagram of `Get /api/stats/{id}`

```mermaid
sequenceDiagram
    Client->>HTTP: Get Notification Stats  
    HTTP->>System: QueryNotification(notificationId)
    System->>NotificationAggregator: Process(notificationId)
    NotificationAggregator->> IdentifierActor: QueryNotification(notificationId)
    IdentifierActor->> NotificationAggregator :IdentifierWorkerResponse(identifier, received, delivered)
    NotificationAggregator->>HTTP: QueryNotificationReply(NotificationStatistics)  
    HTTP->>Client: NotificationStatistics
 ```