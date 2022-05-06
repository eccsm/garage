# Spring Boot Garage Application

In this sample, 

- Spring Rest Controller to process HTTP requests 
- Spring Data JPA to work with In-Memory Database
- Interact with H2 Database
- Mockito for testing

## Run Spring Boot application
```
mvn spring-boot:run
```
## REST Controller Usage

#### Status

```http
  GET /api/status
```

#### Leave

```http
  DELETE /api/leave/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `Long` | **Mandatory**. Ticket ID |

#### Park

```http
  POST /api/park
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `plate` | `String` | **Mandatory**. Proper Turkish Plate. |
| `color` | `String` | **Mandatory**. Color. |
| `vehicle` | `Vehicle` | **Mandatory**. Vehicle type. |

- Vehicle can be three types such as CAR, JEEP and TRUCK respectively allocate 1, 2, and 4 slots.