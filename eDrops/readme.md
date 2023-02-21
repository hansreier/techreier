## Functionality
This is a simple and limited Blog system.
## Technology
Hibernate is used as ORM due to its widespread use, but has some disadvantages in Kotlin.
https://kotlinexpertise.com/hibernate-with-kotlin-spring-boot/
https://www.jpa-buddy.com/blog/best-practices-and-common-pitfalls/
https://spring.io/guides/tutorials/spring-boot-kotlin/
https://kotlination.com/kotlin-spring-jpa-postgresql-spring-boot-example/
https://medium.com/swlh/defining-jpa-hibernate-entities-in-kotlin-1ff8ee470805

Thymeleaf is used for server side rendering HTML pages. For simplicity 
client side frameworks is not used to create Web GUI in this application.

## Development
Make sure that Facets are set correctly in project settings in Intellij
- JPA
- Kotlin
- Spring - and set up application context
- Web
Or else thymeleaf syntax checking will not work properly

For development include Spring boot devtools and set
thymeleaf.cache: false           
thymeleaf.enabled: true
in config.

This enables quick rebuild in Intellij with minimal rebuilding.
User the Build Project button in Intellij. The browser preview in Intellij 
does not show Thymeleaf tags, so not very useful. So you have to start the 
server.

Add the Chrome LiveReload plugin.

Note: Still have to press the Intellij Build button, but no browser refresh is required.

https://www.baeldung.com/dates-in-thymeleaf

https://www.baeldung.com/spring-boot-internationalization



