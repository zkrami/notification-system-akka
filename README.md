## Notification System with Akka HTTP

An application for sending notifications to users of the eduroam network. Each user is represented by an actor. Where
the state of the user actor (`IdentifierActor`) holds the notifications that have been sent to this user, and whether they have been
delivered to the user. The first entry point of the system is an actor called `System`. A query that requires
information from multiple actors is delegated to another actor. A client implementation is provided with this project to
show the functionalities of the system.

## Actors Architecture

The `System` actor is the supervisor of all the actors in the system. It manages the creation and the deletion
of `IdentifierActor`, and it creates a query actor when needed (e.g: `NotificationAggregator`).
A query actor is created to aggregate results from multiple actors. A query actor stops itself after responding to the query.

```mermaid
flowchart LR;
  System---IdentifierActor1[IdentifierActor1]
  System---IdentifierActor2[IdentifierActor2]
  System---QueryAcotr[QueryActor]
 ```

## Sequence Diagrams of the API

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


# Perspectives

- Error handling (For the moment a query actor who is waiting for responses from other actors is always supposed to
  receive this response, ex: `NotificationPublisher`)
- A query actor should ignore the actors that have been deleted  