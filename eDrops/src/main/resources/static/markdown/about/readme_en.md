## Functionality

This is a simple and limited Blog system. It is two types of blogs, both are markdown based.
- Hard coded text content stored in files in the project in the static/markdown folder with subfolders.
- Database blogs stored in MariaDB, the newest blog at the top.

Some of the hard coded blogs have added functionality, like calculation of energy.
A menu system supports navigation to the blogs, partly hard coded and partly dynamically generated from the database.  
The blog system uses a simple one column layout, to be able to be viewed on any device (including mobiles).

### How to enter blogs without coding HTML

The goal was to make this as simple as possible without a complex text editor. 

I choose to use markdown like this README.md file, since it is simpler than RTF and uses less disk space. 
Any text or markdown editor can be used, I have included my own simple markdown editor to produce the database
based blogs. The editor is based ont the HTML Textarea tag with some additions.

The first attempt was to use markdown written in Intellij or other editor, store it as a file  
together with project code and pick it up as a part of the Web GUI. 
My improved solution is to use a relational database for structuring the blogs and metadata, and to enable to
write, store and view the blogs in various languages.

Standard Markdown formatting, including tables, links and images are supported.
I mostly follow the GitHub Markdown dialect.

## Technology

### Storage of blogs, pictures and metadata

I use H2 for development and test and MariaDb for production. 
Everything is stored in the database, except pictures and permanent text stored on the file system in the VPS.
Note that the not dated blogs are stored in the GitHub project within the Docker container.
Most of the pictures are stored directly on the VPS, a very few is stored in the Docker container.
Refer to details [here](../about/databases_en.md) 

### Adding backup to the system

GitHub is backup of both code, metadata and permanent blogs. I have late 2025 added backups
on the VPS for the MariaDB database. The backup will be stored on Jottacloud, using the Jottacloud Cli
for Linux (Ubuntu). I have added daily backup of the database tables and contents, and use
Jottacloud's sync folder for images stored in the file system.

### Producing HTML GUI efficiently

Thymeleaf is used for server side rendering HTML pages. For simplicity
client side frameworks is not used to create Web GUI in this application.  
It is required to use Thymeleaf tags in the HTML. This can be challenging sometimes and some of this syntax
is cryptic and a little difficult to understand. Thymeleaf if much better to use than plain old JSP.

Spring MVC is used in the controllers. To use a Model View Controller concept for producing GUI is beneficial.
But I am not sure that I like the way Spring has implemented their Java API. Actually I liked JSF Java API
(now about deprecated) better. Remark that the Thymeleaf approach of adding some tags to regular HTML5,
is definitively better and cleaner than the proprietary JSF syntax for producing HTML.
For a better responsive user experience and PWAs of cause a frontend framework like React is better.

Using @EnableWebMvc is not adviced in a Spring Boot application, because configuration is completely changed and
will ignore my Spring Boot config. Just use the Spring-Boot-Starter-Web annotation instead. There are some issues
with Spring Boot 3 that is easier when using @EnableWebMvc, e.g. default error handling for Http 404 Not found.
The advice is still not to use it, and I am not in this application. If @EnableWebMvc is added, I have to do a
lot of configuration changes.

The method of generating HTML in the backend is definitely easier for a back
end developer like me. For small projects it is flexible enough. Actually I could recommend it in
projects for internal usage in big organizations. Changes in HTML and all code can be immediately viewed
in a browser using Intellij. I dislike the extra complexity by introducing node and npm.

### Dealing with state in a stateless system.

A web GUI is in principle stateless, but you still have to store som state for identifying the user and where
you are in a comversation with the server.

This demo application is built on the principle to store as little state as possible in memory between roundtrips.
In a cloud based environment we have no control of server nodes, so this state can be wiped out at any time. Hibernate
second level cache could be used to store state. But it is not possible in practice to avoid some state stored between
server requests. State could be preserved in different ways:

- In cookies / web storage on client (local/session storage, must use JavaScript)
- In hidden fields on the client (only if just a very small amount of state and noncritical attribute)
- Using flash attributes for temporary storage in redirects (POST/REDIRECT/GET cycle)
- Server session object: State must be copied between server nodes (use tools like Redis) or sticky session.
- In the Url. But must not be overused. 
- Hibernate second level cache (e.g. use Redis)
- In a database: Using e.g. Spring JDBC session.

Typical required state is user login information and conversational state.
Spring Boot uses secure cookie for session information and authentication.

Temporary state between pages: Use flash attributes.

If you use a Thymeleaf page template (as in this application) there will be common controller logic belonging to the
template. You need to handle redirect from template to the current web page. Errors detected in error handler
can redirect users to default error page or write an error message on the current page. What to do depends on the type
of error. Be aware to avoid recursive redirects due to unhandled errors.

What I would recommend to store state is to use session state, either using key value store in HttpSession or
more advanced Spring session beans. But a session will at some point expire,
either by user logging out or by built in timer. The result can be that the user
suddenly is redirected to home page. To avoid this to a certain extent, I can increase the 30-min Spring default timeout, 
but not too much since sessions should not live forever. Spring JDBC session mechanism that automatically stores state in 
the database can be used, because it is independent of deploy platform and easier than Redis.

As a start I have used some hidden fields, but it has to be included in almost every form post, so a bit cumbersome.
Too many hidden fields will usually mess up the code.
One advantage with hidden fields, is that it does not matter if the session expires. 
The biggest disadvantage is that a hidden field can be modified with inspect mode in any browser.
This also applies to any type of web storage, except secure cookies.
So do not used hidden fields, web local/session storage for critical attributes that e.g. modifies the database.
But I will use session storage for blog searching and grouping attributes, 
and for storing timezone information originating from client. 

Loosing this information will not force a return to the homepage anyhow.  

Recently I have made collapsable menus based on menu items (blogs) organized by topics. To be efficient, 
JavaScript was required. I wanted to keep the collapse state stored, so the user do not have to collapse and
expand all the time. After some consideration, session storage on the browser was used for this. 
In addition, I store timezone information in browser session storage.

I have only one container running av Hostinger. This means that users will not be automatically switched to another node
that easily can occur with several Kubernetes pods. Perhaps later I will store
state in the database using Spring JDBC session. But it is not absolutely required.  

Cookies can be practical because kept when session expires, but I am not very fond of cookie abuse. 
Note: Spring Security uses a cookie by default for user and login information and I use that.  

Storing state in the URL path. It seems like a strange idea... What it means is to store some state in path variables,
not query parameters since query parameters usually only used for GET. I have used the path to identify which
blog and blog post the user is looking at. E.g. 
````
https://techreier/blog/reier/cat/feeding
https://techreier/edit/blog/cat/feeding/published
https://techreier/edit/blog/cat/feeding/draft
https://techreier/blog/cat/feeding  (username is skipped for default admin user)
````
- blog refers to blogs tab in main menu.
- reier refer to the unique username that created the blog.
- cat refers to the blog name about cats
- feeding refers to the blog post with timestamp
- edit refers  to edit mode for users with blog editing privileges
- published means that everyone can read this blog
- draft, means that the blog is a draft not visible to everyone.
- weather refers to a blog post with unique identifier weather. 

For simplicity a blog post is defined with fixed states (idea, published, draft, backup, ...).
This limits the number of instances of the same blogpost, and is done on purpose.
Two drafts can not exist, so it is a kind of state. This is a code limitation, not database.  

Note: the identifiers must conform with allowed naming rules used for a url segment.
The path should uniquely define a database instance, it is easy fetch the instance using the url.
If the path does not define a unique database instance, a "duplicate" error message is thrown.
The database id I have used is a simple Kotlin Long number (artificial key, the simplest),
I do not path as id in the database directly.
SQL selects are used to find the id (unknown to the user)
I think it is better to use sequential numbers (synthetic key).
that more easily allows for renaming of these identifiers.  

Currently, the username is skipped and I use only a default user to create blogs (me).
This system makes it very easy to save hyperlinks and return to the same place.
The path is always shared with the server, so no problem it is state.

A lang query parameter can be added to adjust language. This is according to conventions for web apps.
But it is not state. Spring stores internally language in the session as state.

## Security

I have used Spring security for login and other security issues. It seems to be the simplest option in a
Spring MVC Boot based system.

### Preventing security threats

- Remember to update library versions in pom regularly. This is easy for a small demo app like this.
- Watch for SQL injection. Prevented by using Hibernate / Spring JPA and not native SQL
- Watch for HTML injection. Required when using Markdown or RTF editors, because HTML is generated and included.

I really have to do this when using the utext tag to include html parsed markdown. 
instead of text renders the included HTML as HTML and not text.

```
<div class="container" th:utext="${blogPost.summary}"></div><br><br>
```

What I do to avoid Cross Site Scripting (XSS), is to check the html before injecting it into the web page.
It is possible to remove plain HTML in markup and RTF, but a better approach is to scan and remove the potential
dangerous parts after the HTML is generated. I have used the Owasp html Java Sanitizer library to do this.

Spring security also includes some methods of preventing it. I will try
that later.

## Docker

Generate .jar to deploy on Docker container
Can generate .war to deploy on Tomcat (change pom)

To deploy to docker container:  
mvn spring-boot:build-image -DskipTests

[Dockerize Spring Boot app](https://www.baeldung.com/dockerizing-spring-boot-application) using the
[jib maven plugin](https://medium.com/@sybrenbolandit/jib-maven-plugin-89c447473d76).

Remember to remove the scope "provided" in spring-boot-starter-tomcat.

Port must be set manually to 8080 to start the application. I have temporarily disabled Spring security, can add it
again and test.

- mvn jib:dockerBuild to deploy to local docker (not DockerHub)
- mvn jib:build to deploy to dockerhub

The jib-maven plugin is faster to use than the spring-boot-maven-plugin and more flexible. Recommended. Both method uses
no dockerfile.

- mvn spring-boot:build-image (maven-compile-plugin)

#### Authentication and security

Credential helper:

Wincred is downloaded to store credentials when logging into dockerhub.
Check in Windows Credential Manager or Legitimasjonsbehandling.
You have to change Windows path variable to point at it.
It could be placed together with Docker installation or elsewhere.
Use command line to login to Docker on Windows to set password for the
first time. Refer to text below.

Personal access token:

Can be used as an alternate to a credential helper. Not tested.

https://docs.docker.com/engine/reference/commandline/login/  
https://github.com/docker/docker-credential-helpers/releases

MFA Authentication can be set up on Dockerhub. 

To log in to Docker with username and password:

docker login registry-1.docker.io  
First successful login stores password in Windows Credentials Manager.  
Check in Windows Credential Manager or "Legitimasjonsbehandling", that password is stored.  
Check in file .docker\config.json. I first set it up manually to "credStore": "wincread",
but contents is changed in process. This seems to be the best way to store
credentials in Wincread.

I tried to do it manually by opening Credential Manager in Windows on a
company owned PC, but did not work.
It is an entry there called Persistence. This should be set to Local computer.
This is impossible using Credential Manager directly.

```
{
    "auths": {
        "https://index.docker.io/v1/": {},
        "registry-1.docker.io": {}
    },
    "credsStore": "desktop"
}
```

#### Docker volumes

This is needed for storing media data like images locally or on the VPS.
I have used a volume called mediafiles for this. In practice this is a
bind mount (I call it blind mount) directly to my harddisk. An ordinary mount will mean that
Docker manages my files somewhere. This is not wanted behaviour.

C:\media is the local source on my PC. The destination is /mediafiles.

| Where |          Local Source | Destination | Comment                   |
|-------|----------------------:|-------------|---------------------------|
| Local |              C:\media | /mediafiles | PCs local hard disk       | 
| VPS   | /jottasync/mediafiles | /mediafiles | Reversed order Cyberpanel | 

Do not name VPS catalog just media, it is already used on many Docker preinstalled images.
Note that the url path is e.g. this: https://techreier.com/media/cherries.jpg
or in subdirectories of /media.  

#### Docker database connections

The environment variable DB_PASSWORD is used and need to be entered when
starting the docker Container. The MariaDB database on my VPS is managed by Cyberpanel,
and stored directly on the VPS file system and not in a Docker container.
Using MariaDB Docker images are usually stupid, involves deletion risk and is not required.
I have tested it locally, but even locally I have installed MariaDB in the normal way,
not on a Docker Image.  

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

Add the Chrome LiveReload plugin (latest: Discontinued)

Note: Still have to press the Intellij Build button, but no browser refresh is required.

https://www.baeldung.com/dates-in-thymeleaf  
https://www.baeldung.com/spring-boot-internationalization

### Language encoding

Set to UTF-8 in the entire project.

Default for property files in Intellij is ISO-8859-1, so must be changed in
settings, editor, fil encodings. If not Norwegian characters øæå ØÆÅ are
displayed incorrectly. The problem seems to be there only in property files
and not in .html files. Note that code repos like Bitbucket og GitHub expects UTF-8
when changing code directly in the repos. For old Java 8 this is a problem since
it was the last version where property files is based on the ISO format.

h2 console: If present available at "root url"/h2-console

### Tomcat settings

Embedded Tomcat settings have to be controlled in yaml

app.server.tomcat....

E.g. used to allow for some special characters in URL that bypassed Spring Boot error handling

Refer to
[Spring boot tomcat settings](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties.server.server.undertow.options.server)

### Thymeleaf views and data fetching

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
the server based http rendering in Thymeleaf. Every time the user requests or changes something needs a GET or POST to
the server.

You loose control of when and how a database call is handled using Hibernate. It is a question if it is better
use a thin layer on top of plain SQL that supports CRUD and simple abstraction of SQL dialects (H2, Oracle, MySQl..)
Population of the required (view and or db) model objects must then be manually handled.
E.g. use Kotlin Exposed or Spring Data JDBC instead. I have used a lot of time
to verify that generated Hibernate SQL is correct and efficient. To configure Hibernate with Kotlin and Spring MVC
correctly required a lot of work.

### Storing text

I simply store it with VARCHAR for limited summary fields and TEXT for the content field in MariaDB.

My documents will not exceed 1Gb limit, forget it.

I selected MariaDB, PostgreSql is too large and not well suited for small web applications.

Text editors that can be used:
- Html textarea
- RTF based: Can include pictures.
- Sommernote - Tutorial for Thymeleaf.. Try this  
- TineMCE (not free for advanced features), also directly editing online using cloud version.  
- Froala  
- CKEditor 5?
- EasyMDE, markdown based
- Editor.js (JSON objekter)

But I decided to use markdown and the simple Html textarea.
This readme file is markdown simply stored as a file on the file system.
What I did was simply to add a view button, to view the result with backend rendering below the text box.
In addition, I added a markdown help button to reveal som markdown tricks.

### About images

Always use this way to include images to be able to view in intellij, locally and docker.
Not sure actually why .. works

![PerSeter](../images/pas.jpg "Per Seter")

| haha  | hoha   | hwhw      | de       | dd |
|-------|--------|-----------|----------|----|
| oah   | alan   | podagra   | fanitull | ss | 
| mygod | huff   | r         | reier    | ss |
| olav  | andres | pettersen | hipl     | ds |


## web stuff

Files that need to be included, else WARN message can be seen in logs.

favicon.ico file is created e.g. by 
https://www.favicon.cc/

robots.txt file for web crawlers is created

Caching of css and JavaScript is tricky, mobile phones is worst, since cache very seldom is cleared.
- local development. Clear cache for any change.
- production. Clear cache for users only if new version of the deployed code.
- Spring is set to do this each hour as a backup.

This is done by versioning the .css and JavaScript files when required.
Refer to the bottom line in app with display of current version.
