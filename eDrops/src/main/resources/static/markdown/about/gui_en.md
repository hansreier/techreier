## User interface

### My GUI design principle

I want to keep the user interface clean, old-fashioned and simple with not clutter, but still efficient.
So fancy fonts, icons, animation effects and so on you will not find here.
The use of backend rendering and Thymeleaf forces me to keep it simple to be maintainable.
It is nothing wrong with e.g. React and fancy GUI libraries. I am not a focused front end developer and
decided not to use it. If you inspect my web pages, it is very clean HTML5.

### My GUI design and programming tools

This is very simple: A web browser with inspect mode, Intellij, Spring MVC, Thymeleaf, HTML and some CSS.

### The use of the ugly beast, JavaScript

Still this is unavoidable, but I have kept this at a minimum.

#### Collapsible menus

I realized that I had to do something with this, or else the dropdown menus would be too unorganized and use too much
vertical space.

Recently I have made collapsable menus based on menu items (blogs) organized by topics. To be efficient,
JavaScript was required. I wanted to keep the collapse state stored, so the user do not have to collapse and
expand all the time. After some consideration, session storage on the browser was used for this. I first attempted a
solution with topic selection at the top of the web page, beside the language selector, that would not require
JavaScript. The solution worked, but the user interface after testing itu was bad. This is not a very common
and intuitive way of solving the problem of too large drop down menus and unorganized content.
It confused me and would confuse the potential readers even more.  

A consequence of using session storage and not local storage, 
is that the menu collapsable state is kept within the same tab in the browser, but not when using a
new tab. The problem with local storage is that you actually need to clean the storage sometimes, else you can
end up in an unpredictable state. So if the state gets of sync (e,g. when adding or changing menu items),
it is just to open a new tab window.

I think that the current solution will be good enough for a very long time.

#### Showing and hiding passwords

Really very simple implemented, layout could have been improved.

#### Clicking on links 

Minimal JavaScript is used. I have avoided the use of URL query parameters and transferring state in internal links.
This keeps the code simple because I can use simple HTML links for everything (including in markdown)

### Displaying date and time information

Every blog and blog post have got a changed date and time. I could have made this simple and just viewed the timestamp in
Norwegian time Europe/Oslo or UTC, but I wanted to do this properly. Time information is stored in UTC without timezone information.
Timezone is not directly supported by MariaDB. The zone information is fetched from the client with JavaScript and
send to the server with a POST command. The user then will read the time in the local time zone.
If this is incorrect, it is the user's responsibility.

Both client session storage and server session storage are used for this.

One exception, and it is time that is not user (blog) related. This is valid for logging and for
displaying the build timestamp in Europe/Oslo time. The build timestamp can be seen at the bottom of the page:

````
version 250421152229 - Year 25 Month 04 Day 21 Hour 15  Minute 22 Second 29. 
````
Now you know what this cryptic number is. It is changed for every deploy to Docker.

#### Other usage of JavaScript

Simple actions that do not need to contact the server, e.g. changing value in dropdown or hiding a field.

Else, to be decided, I can add more, perhaps for graphs or a markdown editor.
The responsive design web page uses some JavaScript, 
just to demonstrate what information I can fetch for free from your device without you knowing it.
I dropped reading position, since it requires special care.

### Language independent web site

To achieve this both traditional language resource files and language dependent markdown is used.
My previous website used only resources files, but not advisable for a lot of unstructured text.
The labels and message system uses resource files. The menu system is constructed from blog metadata,
so resource files are not used there in general. Some of the menu items and topic anyhow will use hard coded
text fetched from resource files.

#### The use of URLs for easy access to contents in selected language.

I will keep the URLs simple and intuitive, so that a user can enter the URL and be sure to get the same content.
The content is based on the selected language, in practice this is Norwegian and English. Some content is not available
in both languages, because I am lazy. If I want everyone to read it, I have to use English.
The automatic translator tools in browsers is getting better and better, but I still prefer this dual language approach.
I have done some coding tricks to avoid ending up with 404 if the language is changed and no text available.

I have avoided using a lot of URL query parameters (lang can be used).

If I allow users to enter blogs I have to add another level. Not implemented yet.

````
www.techreier.com/userid/blog/blogpost
````
Userid is dropped in the current implementation.

### Read and write GUI

The current solution focuses mostly on a read only GUI with no login.  

To be able to enter blogs in a database centric system, I have to include admin GUI (included login) to be able to
administrate and enter blogs and blog posts. And this is not a simple task, actually about 30% of the system.
Markdown are stored on the file system in Docker or in the database. Some of the permanent text are stored on files,
the rest are stored in database. Images are stored directly on the VPS server, some are stored in the Docker container.

I have used Intellij as the markdown editor. This is not practical when storing blogs and images in a database.
I have already used  a Markdown parser (Flexmark), but I also need a Markdown editor. 
The simplest way of doing this is using an HTML text input field,
parsing the result on the server and using a separate window to present the result.
It is a question of styling, maybe editor buttons and special characters like emojis.
The simplest improvement is using a Tooltip for help.

A more advanced solution uses JavaScript, perhaps I will use some open source library.
This, including the scaling problem, is why I will keep this as a one user system for a long time.
Imagine commercial blog sites: I just cannot and will not compete with it. This system is really designed for
one or a few selected admin users and a small VPS.

### Thymeleaf, HTML5 and JavaScript in practice.

It probably is faster to develop simple content based web pages with Spring MVC, HTML5 and Thymeleaf
than using React, TypeScript and HTML5 and REST endpoints. At least for a backend developer...
I have tried React at work (employer policies), with a minimal Node server for authorization.
The most problematic part of React is a lot of configuration, error handling and state management.   

Thymeleaf involves some cryptic and difficult syntax, and the GUI programming is a bit low level. 
My advice is to take care when organizing code based on this stack. 
With proper organizing and layering the code base are more maintainable.
I have used inheritance for controllers, a basic template for all pages, and fragments for the specific page contents.
It is really very simple; for every round-trip, you have to obtain all code and data from the server, including the
entire menu system. I have considered Htmx, but have really not yet seen any advantage in partial load of the pages.

Some low level JavaScript is unavoidable, e.g. for multi level menus. TypeScript is no option here.
My experience is that the GUI parts are more work than I initially thought.
If the GUI is kept relatively simple, this tech stack is a valid choice.

CSS and styling is an important part of the system. I have used plain CSS and taken advantage of CSS inheritance.
With React it is common with individual CSS styling for GUI components. In a small project with just a few
developers plain simple global CSS is manageable. I use AI support for proper CSS. In some ways
this replaces the need of CSS libraries. It has also helped me with JavaScript.

To set up really advanced GUI, like pageable and sortable tables and animations can be a challenge. But do I need it?  

I have previously developed Windows GUI using Delphi RAD tool.
Whatever you do, this is 3 times faster than developing any Web GUI, regardless of tech stack used.
If you use React, you can double the developer time compared to using Thymeleaf.
But to use Delphi for web developement, I really would not recommend.  


