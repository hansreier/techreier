## JPA and other database APIs

JPA with Hibernate implementation was originally selected because it was industry standard for Java, 
and I wanted to learn it to  become a better consultant. If it is the best tool for a stateless web app is questionable.
Object relational mapping (ORM) have advantages and limitations.
Hibernate has some disadvantages in Kotlin, and must use som effort to set it up correctly.

The challenge with Spring Data JPA and Hibernate is that you need to check that the SQL generated is correct
and effective. I have experienced misuse of Hibernate in many projects, with strange errors and inefficiency
in queries as a result. But I will advise against writing directly native SQL to the database.
Use a thin SQL like layer to be able abstract SQL dialects and CRUD functionality included.

You loose control of when and how a database call is handled using Hibernate. It is a question if it is better
use a thin layer on top of plain SQL that supports CRUD and simple abstraction of SQL dialects (H2, Oracle, PostgreSQL, MySQl ...)
Population of the required (view and or db) model objects must then be manually handled.
E.g. use Kotlin Exposed,JoOQ or Spring Data JDBC instead. I have used a lot of time
to verify that generated Hibernate SQL is correct and efficient. To configure Hibernate with Kotlin and Spring MVC
correctly required a lot of effort.

## About Kotlin and JPA

This is really not the best combination. Kotlin prefer immutable code by default, Hibernate is the opposite.
You have to use open classes to enable lazy loading and proxies.  Kotlin-allopen maven plugin can be used.
JPA requires a constructor without arguments, Kotlin likes the opposite. The Kotlin-noarg plugin must be used
to prevent problem. It is many ways described on the internet to adapt JPA to Kotlin. 
The missing null safety and use of Optional in id searches is not optimal either.

I prefer not to use data classes, due to problems with equals, hashcode and toString. 
My advice is to take care and test it  yourself with H2 integration tests when starting with Hibernate in Kotlin. 

## Short about other permanent storage APIs I have used

I have used Spring Data JDBC at work in Microservices with only one or two relational tables.  
One comment is that is too limited, and I advise only to be used it with a very simple domain model.
Key generation is too simple with too few options,  It is really not CRUD, but SUD (Save Update Delete).
Kotlin Exposed was not an option at work, due to a mix of Java and Kotlin code.
JoOQ could have been used, but I do not like to generate code for database access.  

You should evaluate if you need a relational database at all, e.g. for storing CVs it is not the best.
For images and videos perhaps cloud storage or quite simply the file system is the best.  

## How to use JPA effectively

What I really advice when using JPA, is to separate read and write logic. For reports and read GUI,
use projection (interfaces, proxy based) or DTOs. I rewrote the app to use this principle in 2026 due to several problems with
various JPA lazy loading exceptions and unwanted saves. My app is all about presenting blogs to the user.
The input forms are usually hidden from the regular reader.

Usually what happens in a web app is first that you read from the database. Then the GUI is presented to the user.
The create or update operation is separate from this after the user have pressed the save button. 
It does to some extent violate the DRY principle, but it is worth the extra coding effort because you get more stable code.

Hibernate have excellent CRUD and key generation support. One thing that is confusing is that it is a lot of ways
in Hibernate to actually trigger and control a database transaction. So it is a steep learning curve. What I strongly
advice against is using Criteria API (or the old HQL Api). Type safety is just not worth the extra effort.
The JPQL abstraction is a good one, but remember it is limited and not native SQL. 
Use @Query in the repositoty layer for control, or the looser tell Hibernate to do what you want in the method name
for simpler stuff, like  fun findPByBlogIdAndSegmentAndState(...). If you do not want lazy loading, use @EntityGraph.
Use native queries only if you absolutely have to use it.   

Turn on Logback logging if you get into trouble!  
It is better than using the log facility that comes with Hibernate.  

Queries and CRUD belongs to the repository layer in Spring Data JPA.
Controlling transactions with @transactional belongs to the service layer.
Be careful when using Hibernate controlled objects in other application layers, 
this is where the sneaky "lazy" exceptions usually occur.  

## Key takeaway

1. Be very careful with setup in Kotlin.
2. Separating concerns (Read vs. Write).
3. Explicit queries, do not use Criteria API. 
4. Proper layering in repository and database service layer. 
5. Use proper logging of SQL.
6. Hibernate is not the best for all use cases.  

## Some links to more detailed materials

[Jetbrains om Spring Data JPA](https://blog.jetbrains.com/idea/2026/03/using-spring-data-jpa-with-kotlin/)
[Thorben janssen tutorials](https://thorben-janssen.com/tutorials/)