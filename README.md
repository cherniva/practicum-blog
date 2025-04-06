# Blog Web Application

A web blog app built with Spring Framework (6.2.1), running on Tomcat (no Spring Boot). Written in Java 21, and built with Gradle. Uses a MySQL database. 

Features two pages: a post feed (with previews, likes, comments, tags, tag filtering, and pagination) and a post page (with full content, tags, edit/delete options, like button, and comment section). Supports post/comment creation and editing. Covered by JUnit 5 unit and integration tests with TestContext Framework and context caching.

## 🚀 Technologies

- **Java 21**
- **Spring Framework 6.2.1**
- **Spring Web MVC**
- **Spring Data JDBC**
- **MySQL 8.0** - Primary database
- **H2 Database** - In-memory database for testing
- **Thymeleaf 3.1.2**
- **JUnit 5**
- **Mockito**
- **Lombok**
- **Maven**

## 📋 Prerequisites

- Java 21 or later
- MySQL 8.0 or later
- Maven 3.8 or later

## 🛠️ Setup

1. Clone the repository
2. Configure your MySQL database connection in `src/main/resources/application.properties`
3. Build the application using Maven:
   ```bash
   mvn clean package
   ```
4. Copy `target/blog.war` into webapps folder inside the Tomcat directory

## 🧪 Testing

Run the tests with:

```bash
mvn test
```

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

The application uses Spring's configuration system. Key configuration files are located in `src/main/resources/`.