## Technological prototype

This website is really a technology prototype.  My expertise is focused on backend,
but I cannot maintain this website without some devops and frontend knowlegde. 

I could have used an existing platform for publishing web content like WordPress,
but it is much more engaging and educational to use my own technology stack.
I do not really care about making a perfect website according to WCAG rules,
but I try to follow it briefly. Since this is an evolving prototype, 
you can expect some parts of this website to be unfinished at any time.

Previously I had a website published on a VPS with Tomcat directly installed.
I got dependent on what version of Tomcat and Java that the vendor supported.
This is the motivation for using Docker containers and using a more modern
technology stack. 

One important decision was not to use client based web technologies like React and Angular 
like everybody else nowadays, but to use server based html template generation.
The motivation for this is simplicity and to keep the main focus on backend.
It is simpler, quicker and I do not need to use tools like node and npm to produce the frontend.
I have professionally been working with Java and Kotlin projects that includes Angular or React.
It adds technical complexity and complicates the building process.  

Many projects really need the extra flexibility and control provided by frontend based HTML rendering.
A progressive web app is an example where this is an absolute requirement. 
I have seen many projects using Angular or React just because everybody does that.
It can certainly be overkill for simple company internal applications.
I have previously in projects used JSP or JSF for backend rendering.
I think Thymeleaf is a more modern, simpler and better approach, in particular combined with
Spring Boot and Spring MVC.

What I originally intended was to make a blog based system, since producing text on my
previous website was cumbersome. I have solved this by using markdown based text.
Text is simply produced by editing a markdown file in Intellij.
Later I implemented a database based solution with MariaDB and web GUI to enter text as markdown.

### How to make a text based website without coding HTML

The original goal was to make this as simple as possible where the user enters text in an input
window.  An option is to use markdown like this readme.md file, since
it is simpler than RTF and uses less space. Perhaps the system will support both
formats in the future. Editors, both outside of this project or included in this project can be used.

The first attempt was to use markdown written in Intellij or other editor, store it as a file
together with project code and pick it up as a part of the Web GUI. No database is really needed.
The advantage and disadvantage of this approach is that my written text is included directly in the codebase,
and I have to redeploy to update. 

The second attempt was to use a relational database H2 for structuring the blogs and metadata.
I use server based markdown parsing. I started with CommonMark,
the Java reference implementation, found it too limited and switched to Flexmark. What I have seen now
is that I perhaps need to switch back to CommonMark again, no more Flexmark releases seen to be produced.
Some issues with link replacement and GitHub flavor need to be solved in another way to manage this.

The third attempt was to enable to write and view the blogs online in various languages.
This required the use of Spring Security and a blogOwner concept with a simple login screen. 
The production database was changed from H2 to MariaDB. I have used some effort on 
expanding the simple button row below the HTML textarea editor; with preview and quick Markdown help.
I have selected not to use additional buttons or keyboard shortcuts to insert markdown codes into the textarea.
This is the present status of the project. I have looked at other Markdown editors doing this, and my experience is
that it is the best just to skip it. My editor and markdown parsing is server based instead of using JavaScript.
This have some limitations, but it works surprisingly well. The response time does not feel slow.  

Future plan is to make the system a true multiuser project with 4 user permission levels:
- Admin user
- BlogOwner
- Logged in read user
- Anonymous read user

I am not there yet. The question is if I ever will. The website could be used for a very limited set of
blog owners, e.g. by a sports club or something. If I let any user into the system, this implies I have created
a free blog platform without advertising or a payment mechanisms. And do I really want this. Probably not.  
My simple way of using Jottacloud for synchronizing media content to a Docker volume, can not be used either. 
It could soon outgrow the current Hostinger plan. Regarding the future use of other formats than markdown,
I have come to realize that is the best not to expand to other formats. A commercial blog system probably
requires frontend markdown rendering too.  

A more realistic and fun future plan is to expand the blogs with functionality, calculations and graphical curves, 
to support my own blogs.


