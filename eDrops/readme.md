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

Language encoding:

Set to UTF-8 in the entire project
Default for property files in Intellij is ISO-8859-1, so must be changed  in 
settings, editor, fil encodings. If not Norwegian characters øæå ØÆÅ are 
dieplayed incorrectly. The problem seems to be there only in property files 
and not in .html files.

### Thymeleaf views, state and data fetching.

The parameter spring.jpa.open-in-view is set to false.
This prevents views to directly fetch data from database
Refer to below articles why this is recommended.

https://www.baeldung.com/spring-open-session-in-view
https://vladmihalcea.com/the-open-session-in-view-anti-pattern/

The disadvantage is that we must take care before rendering views and always fetch data eagerly using fetchgraphs.
Another way to fix this is never to include domain model objects directly in view, but use a separate view model.
This seems the best way to do it for input forms. But to separate the Sping MVC model from database model requires 
copying of data and a more complex application.

The advantage of lazily fetching data can be discussed since frequent roundtrips to server must be done due to 
the server based http rendering in Thymeleaf.  Every time the user requests or changes something needs a GET or POST to
the server.

This demo application is built on the principle to store as little state as possible in memory between roundtrips.
In a cloud based environment we have no control of server nodes, so this state can be wiped out at any time. Hibernate 
second level cache could be used to store state. But it is not possible in practice to avoid some state stored between
server requests. State could be preserved in different ways:
- In cookies / web storage on client (local/session storage, must use Javascript)
- In hidden fields on the client (only if just a very small amount of state)
- Using flash attributes for temporary storage in redirects (POST/REDIRECT/GET cycle)
- Server session object: State must be copied between server nodes (use tools like Redis) or sticky session.
- Hibernate second level cache (e.g. use Redis)
- In a database: Using e.g. Spring JDBC session.

Typical required state is user login information and conversational state. 

Temporary state between pages: Use flash attributes.

If you use a Thymeleaf page template (as in this application) there will be common controller logic belonging to the 
template. You need to handle redirect from template to the current web page. Errors detected in error handler
can redirect users to default error page or write an error message on the current page. What to do depends on the type
of error. Be aware to avoid recursive redirects due to unhandled errors.

What I would recommend to store state is to use Spring JDBC session, because it is independent of deploy platform.
To use cookies is another alternative.







