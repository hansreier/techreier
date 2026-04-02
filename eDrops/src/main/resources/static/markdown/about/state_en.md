## A stateless web application?

My experience, it is nearly impossible, but ...  

This blog application is built on the principle to store as little state as possible in memory between roundtrips.
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

I have only one container running of Hostinger. This means that users will not be automatically switched to another node
that easily can occur with several Kubernetes pods. Perhaps later I will store
state in the database using Spring JDBC session. But it is not absolutely required.

Cookies can be practical because kept when session expires, but I am not very fond of cookie abuse.
Note: Spring Security uses a cookie by default for user and login information and I use that.

### State and the URL path

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
I do not use path as id in the database directly.
SQL selects are used to find the id (unknown to the user)
I think it is better to use sequential numbers (synthetic key).
that more easily allows for renaming of these identifiers.

Currently, the username is skipped and I use only a default user to create blogs (me).
This system makes it very easy to save hyperlinks and return to the same place.
The path is always shared with the server, so no problem it is state.

A lang query parameter can be added to adjust language. This is according to conventions for web apps.
But it is not state. Spring stores internally language in the session as state.