# NewsStream

NewsStream is an Android app that provides the latest global news, offering categorized articles, sentiment analysis, and summarized content for quick reading. Stay informed with real-time updates and essential news highlights.

## Features

- **User Authentication**: Signup, Login, and JWT-based authentication.
- **News Ingestion**: Fetches news from custom providers (e.g., NewsFeedScraper).
- **ML Inference**: Sentiment analysis and categorization of news articles using ns-models-fastapi-app microservice.
- **Scheduling**: Automated news fetching via scheduled tasks.
- **API Documentation**: Swagger UI integration.

## Tech Stack

- **Java 21**
- **Spring Boot 3**
- **Spring Security** (JWT)
- **PostgreSQL**
- **Maven**

## Setup

1.  **Clone the repository**
2.  **Configure Environment Variables**:
    Set the following environment variables:
    ```env
    AWS_DB_URL=jdbc:postgresql://localhost:5432/newsstream
    AWS_DB_USER=postgres
    AWS_DB_PASSWORD=password
    NEWSAPI_BASE_URL=https://newsapi.org/v2
    NEWSAPI_KEY=your_newsapi_key
    SCHEDULER_INTERNAL_TOKEN=your_internal_token
    JWT_SECRET=your_very_long_secret_key_at_least_32_chars
    ```
3.  **Run the Application**:
    ```bash
    ./mvnw spring-boot:run
    ```

## API Endpoints

### Authentication
- `POST /v1/signup`: Register a new user.
- `POST /v1/login`: Authenticate and receive tokens.
- `POST /v1/refresh`: Refresh access token.

### News
- `GET /v1/category-news/all`: Fetch unfiltered news.
- `GET /v1/category-news/{categoryName}`: Fetch news by category.
- `GET /v1/trending-topics`: Fetch trending topics.
- `GET /v1/categories`: Fetch all available categories.

### Scheduler
- `POST /v1/refresh-news`: Trigger manual news ingestion (Internal use).

## Swagger UI

Access the API documentation at:
`http://localhost:8080/swagger-ui.html`
