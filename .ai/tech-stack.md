# Tech Stack - Inventory Management System

## Backend - Spring Boot z Kotlin

- **Spring Boot 3.5.7** - Nowoczesny framework do budowania aplikacji enterprise z minimalną konfiguracją
  - Spring MVC dla obsługi HTTP i routingu
  - Spring Data JPA dla abstrakcji warstwy dostępu do danych
  - Spring Security dla uwierzytelniania i autoryzacji użytkowników
  - Wbudowane zarządzanie sesjami i CSRF protection
  
- **Kotlin 1.9.25** - Nowoczesny, statycznie typowany język dla JVM
  - Zwięzła składnia zmniejszająca ilość boilerplate code
  - Null safety na poziomie kompilatora
  - Data classes idealne do modelowania encji i DTO
  - Pełna interoperacyjność z ekosystemem Java
  
- **Java 21 (LTS)** - Najnowsza wersja długoterminowego wsparcia JVM
  - Nowoczesne API i optymalizacje wydajnościowe
  - Pattern matching i records
  - Virtual threads dla lepszej skalowalności

## Frontend - Server-Side Rendering

- **Thymeleaf** - Silnik szablonów po stronie serwera
  - Naturalne szablony HTML (valid HTML5)
  - Integracja z Spring Security dla autoryzacji w widokach
  - Fragment support dla komponentów wielokrotnego użytku
  - Brak potrzeby budowania osobnego frontend aplikacji
  
- **HTML5 + CSS3** - Standardowe technologie webowe
  - Semantyczny markup
  - Responsywny design
  - Minimalna ilość JavaScript (progressive enhancement)

## Database - PostgreSQL

- **PostgreSQL 15** - Zaawansowana relacyjna baza danych open source
  - ACID compliance dla spójności danych
  - Zaawansowane typy danych (JSON, Arrays, UUID)
  - Wsparcie dla CHECK constraints i foreign keys
  - Timestamptz dla poprawnej obsługi stref czasowych
  
- **Hibernate ORM** - Object-Relational Mapping przez Spring Data JPA
  - Automatyczne mapowanie encji Kotlin na tabele SQL
  - LAZY loading dla optymalizacji zapytań
  - Automatic schema generation (development) z możliwością migracji (production)
  
- **HikariCP** - Szybki i lekki connection pool
  - Domyślny connection pool w Spring Boot
  - Optymalizacja zarządzania połączeniami z bazą danych
  - Konfigurowalny rozmiar puli i timeouty

## Build Tool - Gradle

- **Gradle 8.14.3** - Nowoczesne narzędzie do budowania projektów
  - Gradle Kotlin DSL dla typowanej konfiguracji
  - Dependency management przez Maven Central
  - Spring Boot plugin dla pakowania do fat JAR
  - Kotlin plugins (JVM, Spring, JPA) dla wsparcia języka
  
- **Kotlin Compiler Plugins**:
  - `kotlin-spring` - Automatyczne otwieranie klas Spring
  - `kotlin-jpa` - Generowanie no-arg constructorów dla JPA entities
  - `kotlin-allopen` - Otwieranie klas z adnotacjami JPA

## Security - Spring Security

- **Spring Security 6** - Kompleksowy framework bezpieczeństwa
  - Form-based authentication z session management
  - BCrypt password hashing (nie plain text!)
  - CSRF protection dla wszystkich operacji POST/PUT/DELETE
  - Method-level security annotations
  - Integration z Thymeleaf dla autoryzacji w widokach
  
- **Session-based Authentication**:
  - HTTP sessions przechowywane po stronie serwera
  - Session cookies z HttpOnly i Secure flags
  - Logout invalidation

## Validation - Jakarta Bean Validation

- **Jakarta Validation API 3.1.1** - Standard walidacji Java
  - Adnotacje walidacyjne (@NotBlank, @Min, @Size, etc.)
  - Automatyczna walidacja w kontrolerach przez @Valid
  - Custom validation messages
  - Integration z Spring MVC dla error handling

## Testing - JUnit & Spring Test

- **JUnit 5** - Nowoczesny framework testowy dla Java/Kotlin
  - Parametryzowane testy
  - Nested test classes dla lepszej organizacji
  - Assertions API
  
- **Spring Boot Test** - Wsparcie testowania Spring Boot aplikacji
  - @WebMvcTest dla testowania kontrolerów
  - @DataJpaTest dla testowania repozytoriów
  - MockMvc dla testowania HTTP endpoints
  - @WithMockUser dla testowania z różnymi rolami
  
- **Spring Security Test** - Testowanie zabezpieczeń
  - MockMvc security integration
  - Mockowanie authenticated users
  
- **PostgreSQL Test Container** - Baza danych dla testów
  - Izolowane testy z prawdziwą bazą PostgreSQL
  - Automatyczne tworzenie i niszczenie kontenerów
  - Konfiguracja przez GitHub Actions services

## CI/CD - GitHub Actions

- **GitHub Actions** - Platforma CI/CD zintegrowana z GitHub
  - Workflow triggers na push i pull requests
  - Matrix builds dla różnych wersji
  - Artifact management dla JAR files
  - Secrets management dla danych wrażliwych
  
- **Pipeline stages**:
  1. **Test** - Uruchomienie testów jednostkowych i integracyjnych z PostgreSQL service
  2. **Build** - Kompilacja i pakowanie do executable JAR (bootJar)
  3. **Deploy** - Upload JAR na serwer przez SSH i restart aplikacji
  4. **Health Check** - Weryfikacja dostępności aplikacji po wdrożeniu

## Deployment - SSH na VPS

- **Linux VPS** - Virtual Private Server z systemem Linux
  - Bezpośrednie wdrożenie JAR file na serwerze
  - Java 21 runtime environment
  
- **SSH Deployment**:
  - SCP dla uploadu JAR file
  - SSH commands dla restart aplikacji
  - Environment variables z ~/.profile
  - Background process z nohup i disown
  
- **Nginx** - Reverse proxy i web server
  - SSL/TLS termination
  - Static content serving
  - Load balancing (dla przyszłej skali)
  - Proxy pass do aplikacji Spring Boot (port 8321)

## DNS & Security - Cloudflare

- **Cloudflare** - CDN i security proxy
  - DNS management
  - DDoS protection
  - SSL/TLS encryption (HTTPS)
  - WAF (Web Application Firewall)
  - Caching statycznych zasobów
  - Rate limiting
  - Analytics i monitoring

## Monitoring & Health Checks

- **Custom Health Endpoint** - `/health` endpoint bez Spring Actuator
  - Prosty JSON response: `{"status": "OK"}`
  - Używany przez CI/CD do weryfikacji wdrożenia
  - Monitoring dostępności aplikacji
  
- **Application Logs** - Standardowe logowanie Spring Boot
  - SLF4J + Logback jako implementacja
  - Logi zapisywane do pliku na serwerze
  - Możliwość integracji z ELK stack w przyszłości

## Development Tools

- **IntelliJ IDEA** - Rekomendowane IDE dla Kotlin i Spring Boot
  - Kotlin support out-of-the-box
  - Spring Boot integration
  - Database tools
  - Git integration
  
- **Gradle Wrapper** - Wersjonowany wrapper Gradle
  - Jednolita wersja Gradle w zespole
  - Brak potrzeby globalnej instalacji Gradle
  - Scripts: `./gradlew` (Linux/Mac) i `gradlew.bat` (Windows)

## Version Control - Git & GitHub

- **Git** - Rozproszony system kontroli wersji
  - Branching strategy (main branch)
  - Pull requests dla code review
  - .gitignore dla plików build i IDE
  
- **GitHub** - Hosting repository i CI/CD
  - Code hosting
  - Pull request reviews
  - GitHub Actions dla CI/CD
  - Secrets management
  - Release management

## Additional Libraries & Dependencies

- **Jackson Kotlin Module** - JSON serialization/deserialization
  - Wsparcie dla Kotlin data classes
  - Nullable types handling
  
- **PostgreSQL JDBC Driver** - Sterownik dla PostgreSQL
  - Runtime dependency dla połączenia z bazą danych

## Future Enhancements (Tech Stack)

- **Flyway/Liquibase** - Zarządzanie migracjami bazy danych dla produkcji
- **Spring Boot Actuator** - Rozszerzony monitoring i health checks
- **Redis** - Cache'owanie dla poprawy wydajności
- **Docker** - Konteneryzacja aplikacji dla łatwiejszego wdrożenia
- **Kubernetes** - Orkiestracja kontenerów dla skalowalności
- **Prometheus + Grafana** - Metryki i dashboardy
- **ELK Stack** - Centralizowane logowanie (Elasticsearch, Logstash, Kibana)
- **REST API** - JSON API obok HTML views dla mobile/SPA clients
- **WebSockets** - Real-time notifications o niskich stanach

---

## Tech Stack Summary

**Primary Technologies:**
- Backend: Spring Boot 3.5 + Kotlin 1.9
- Frontend: Thymeleaf (Server-Side Rendering)
- Database: PostgreSQL 15
- Build: Gradle 8.14
- Security: Spring Security 6
- Testing: JUnit 5 + Spring Test
- CI/CD: GitHub Actions
- Deployment: SSH na Linux VPS
- Proxy: Nginx + Cloudflare
