## User interface

### My GUI design principle

I want to keep the user interface clean, old-fashioned and simple with not clutter, but still efficient.
So fancy fonts, icons, animation effects and so on you will not find here.
The use of backend rendering and Thymeleaf forces me to keep it simple to be maintainable.
It is nothing wrong with e.g. React and fancy GUI libraries. I am just not a focused front end developer and
decided not to use it. If you inspect my web pages, it is very clean HTML5.

### My GUI design and programming tools

This is very simple: A web browser with inspect mode, Intellij, Spring MVC, Thymeleaf, HTML and some CSS.

### The use of the ugly beast, Javascript

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

### Clicking on links 

Minimal Javascript is used. I have avoided the use of URL query parameters and transferring state in internal links.
This keeps the code simple because I can use simple HTML links for everything (including in markdown).

#### Other usage of Javascript

To be decided, I guess I have to add more, perhaps a markdown editor. 
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
administrate and enter blogs and blog posts. And this is not a simple task, actually about 50% of the system.
Currently, markdown blogs including images are stored on the file system. I am gradually changing this to store it in 
the database. Some of the blogs I still will want to store in the file system, so I will not remove this functionality.

I have used Intellij as the markdown editor. This is not practical when storing blogs and images in a database.
I have already used  a Markdown parser (CommonMark based), but I also need a Markdown editor. 
The simplest way of doing this is just using an HTML text input field,
parsing the result on the server and using a separate window to present the result.
It is a question of styling, editor buttons and special characters like emojis.

The more advanced solution uses Javascript, perhaps I just will use some open source library.
And this (including the scaling problem) is why I will keep this as a one user system for a long time.
Imagine commercial blog sites: I just cannot and will not compete with it. This system is really designed for
one or a few selected admin users.

### Thymeleaf, HTML5 and JavaScript in practice.

Yes, it probably is faster to develop simple web pages with Spring MVC, HTML5 and Thymeleaf
than using React, TypeScript and HTML5 and REST endpoints. At least for a backend developer...
The most problematic parts are some cryptic and difficult syntax. 
This applies to all parts of this simple tech stack, it is a bit low level. My advice is to take care
when organizing code based on this stack. With proper organizing and layering the code base are more maintainable.
I have used inheritance for controllers, a basic template for all pages, and fragments for the specific page contents.
It is really very simple; for every round-trip, you have to obtain all code and data from the server, including the
entire menu system. I have considered Htmx, but have really not yet seen any advantage in partial load of the pages.

With just a small part of it being JavaScript, TypeScript is really no option.
My experience is that the GUI parts are more work than I initially thought.
But is the GUI is kept relatively simple, this tech stack is a valid choice.

To set up really advanced GUI, like pageable and sortable tables can be a challenge. But do I need it?  

I have previously developed Windows GUI using Delphi RAD tool.
Whatever you do, this is 3 times faster than developing any Web GUI, regardless of tech stack used.
But to get this out on the internet, I really would not recommend.  


