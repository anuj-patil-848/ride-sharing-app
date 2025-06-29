# Ride Sharing Application

A Spring Boot application with GraphQL API for a ride-sharing service. This application handles creating rides, calculating fares, and supports ride sharing with other people who have similar routes.

## Features

- User management (create, update, activate/deactivate)
- Location management with geospatial queries
- Ride creation and management
- Fare calculation based on distance
- Driver assignment and ride status tracking
- Rating system for both riders and drivers
- Ride sharing capabilities for matching similar routes

## Technologies Used

- Java 17
- Spring Boot 3.2.5
- Spring GraphQL
- Spring Data JPA
- H2 Database (for development)
- Lombok

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application using Gradle:

```bash
./gradlew bootRun
```

The application will start on port 8080 by default.

### Accessing GraphQL API

Once the application is running, you can access the GraphiQL interface at:

```
http://localhost:8080/graphiql
```

This provides an interactive UI to explore and test the GraphQL API.

## API Overview

### Main Entities

- **User**: Represents users of the application (both riders and drivers)
- **Location**: Represents geographical locations for pickup and dropoff points
- **Ride**: Represents a ride in the system with all its details

### Key Queries

- Get user information
- Find locations within a radius
- View ride history
- Find potential ride shares

### Key Mutations

- Create and manage users
- Create and update locations
- Request rides
- Assign drivers
- Update ride status
- Rate rides

## Ride Sharing Logic

The application includes a sophisticated algorithm to match riders with similar routes:

1. When a new ride is requested, the system can search for other requested rides with:
   - Similar pickup locations (within a configurable radius)
   - Similar dropoff locations (within a configurable radius)
   - Similar pickup times (within a configurable time window)

2. If matching rides are found, they can be combined into a shared ride, potentially reducing costs for all riders.

## Future Enhancements

- Authentication and authorization
- Payment processing integration
- Real-time ride tracking
- Mobile app integration
- Advanced ride matching algorithms
- Driver availability management
- Surge pricing during high demand
