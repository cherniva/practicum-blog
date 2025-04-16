# Blog Web Application

A web blog app built with Spring Boot (3.2.3), running on embedded Tomcat. Written in Java 21, and built with Gradle. Uses a MySQL database for production and H2 for testing.

Features two pages: a post feed (with previews, likes, comments, tags, tag filtering, and pagination) and a post page (with full content, tags, edit/delete options, like button, and comment section). Supports post/comment creation and editing. Covered by JUnit 5 unit and integration tests with Spring Boot's test support and context caching.

## 🚀 Technologies

- **Java 21**
- **Spring Boot 3.2.3**
- **Spring Web MVC**
- **Spring Data JDBC**
- **MySQL 8.0** - Production database
- **H2 Database** - In-memory database for testing
- **Thymeleaf 3.1.2**
- **JUnit 5**
- **Mockito**
- **Lombok**
- **Gradle 8.13**

## 📋 Prerequisites

- Java 21 or later
- MySQL 8.0 or later
- Gradle 8.13 or later

## 🛠️ Setup

1. Clone the repository
2. Configure your MySQL database connection in `src/main/resources/application.properties`
3. Build the application using Gradle:
   ```bash
   ./gradlew clean build
   ```
4. Run the application:
   ```bash
   ./gradlew bootRun
   ```
   Or use the generated JAR:
   ```bash
   java -jar build/libs/blog-1.0-SNAPSHOT.jar
   ```

## 🧪 Testing

Run the tests with:

```bash
./gradlew test
```

The test environment uses:
- H2 in-memory database
- Test-specific configuration in `src/test/resources/application-test.properties`
- Spring Boot's test support with context caching
- Automatic schema loading from `src/test/resources/schema.sql`

## 📦 Project Structure

```
src/
├── main/
│   ├── java/          # Java source code
│   ├── resources/     # Configuration files
│   └── webapp/        # Web resources
└── test/             # Test source code
```

## 🔧 Configuration

The application uses Spring Boot's configuration system. Key configuration files:
- `src/main/resources/application.properties` - Main configuration
- `src/test/resources/application-test.properties` - Test configuration
- `src/test/resources/schema.sql` - Database schema for tests

## 🔄 Migration History

- Migrated from Maven to Gradle for build automation
- Upgraded from Spring Framework 6.2.1 to Spring Boot 3.2.3
- Switched from external Tomcat to embedded Tomcat
- Enhanced test configuration with Spring Boot's test support