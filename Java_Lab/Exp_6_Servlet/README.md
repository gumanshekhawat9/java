# Java Servlets: Login, Session and Filter (Jakarta Servlet, Tomcat 11)

A Maven web application covering the Servlet API: GET and POST handling, sessions, listeners and filters, combined into a small student login system.

## Aim
To study server-side programming with Java Servlets and implement a web application covering HTTP, request/response objects, session handling, event listeners and filters.

## Concepts Used
- `HttpServlet`, `doGet()` and `doPost()`, URL mapping with `@WebServlet`
- `HttpServletRequest` and `HttpServletResponse`
- Session handling with `HttpSession` (login and logout)
- Filters: authentication (`/home`) and request logging (`/*`) with `FilterChain`
- Listeners: `ServletContextListener`, `HttpSessionListener`
- Escaping user input to prevent XSS

## Project Structure
```
Exp_6_Servlet/
├── pom.xml
└── src/main/
    ├── java/com/servlet/    (servlets, filters, listeners)
    └── webapp/              (index.html, student.html)
```

## Request Flow
```
Browser -> Tomcat -> Filter -> Servlet -> Response -> Filter -> Browser
```
Login flow:
```
index.html -> POST /login -> validate -> create session -> /home -> AuthFilter
   session valid?  YES -> HomeServlet     NO -> redirect to login
```

## Endpoints
| URL | Servlet | Purpose |
|-----|---------|---------|
| `/hello?name=...` | `HelloServlet` | GET example |
| `/student` | `StudentServlet` | POST form (name, email, course) |
| `/login` | `LoginServlet` | Validates credentials, creates a session |
| `/home` | `HomeServlet` | Protected page (guarded by `AuthFilter`) |
| `/logout` | `LogoutServlet` | Invalidates the session |

## GET vs POST
| | GET | POST |
|---|---|---|
| Servlet method | `doGet()` | `doPost()` |
| Typical use | Read data | Submit data for processing |
| Parameters | Visible in the URL | Sent in the request body |

## Requirements
- JDK 17 or higher
- Apache Maven
- Apache Tomcat 11

## Build and Deploy
```
mvn package
```
Copy `target/servlet.war` into Tomcat's `webapps` folder, start Tomcat with `bin\startup.bat`, and open http://localhost:8080/servlet/

## Demo Login
Username `admin`, password `1234`. These credentials are for the lab demo only.

## Author
Your Name, B.Tech CSE, GIT Jaipur