## Technological prototype

This web site is really a technology prototype.  My expertise is focused on backend,
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

One important decision was not to use client based web technologies like React og Angular 
like everybody else nowadays, but to use server based html template generation.
The motivation for this is simplicity and to keep the main focus on backend.
It is simpler, quicker and I do not need to use tools like node and npm to produce the frontend.
I have professionally been working with Java and Kotlin projects that includes Angular or React.
It adds technical complexity and complicates the building process.  

Many projects really need the extra flexibility and control provided by frontend based html rendering.
A progressiv web app is an example where this is an absolute requirement. I have seen many projects using Angular or React just because everybody does that.
It can certainly be overkill for simple company internal applications.
I have previosly in projects used JSP or JSF for backend rendering.
I think Thymeleaf is a more modern, simpler and better approach, in particular combined with
Spring Boot and Spring MVC.

What I originally intended was to make a blog based system, since producing text on my
previous website was cumbersome. I have solved this by using markdown based text.
Blog functionality will be added later, I am not there yet. 
Text is simply prodused by editing a markdown file in Intellij.
The intent is to used MariaDB and web GUI to enter markdown text.
I have not done that yet. But I have made code that
stores text in a database and later output texts using hibernate.
This is tested by using H2 in unit tests.

### How to make a text based website without coding HTML

The goal is to make this as simple as possible where the user enters text in an input
window.  An option is to use markdown like this readme.md file, since
it is simpler than RTF and uses less space. Perhaps the system will support both
formats. Editors, both outside of this project or included in this project.  can be used.

The first attempt is to use markdown written in Intellij or other editor, store it as a file
together with project code and pick it up as a part of the Web GUI. No database is really needed.
The advantage and disadvantage of this approach is that my written text is included directly in the codebase,
and I have to redeploy to update. 

TODO how to use internal link. If possible.  
[The details of using markdown to produce html is outlined here](#About-usage-of-markdown)  
[The details of using markdown to produce html is outlined here](../About#about-usage-of-markdown)  

[The details of using markdown to produce html is outlined here](#About_usage_of_markdown)  
[The details of using markdown to produce html is outlined here](../About#about_usage_of_markdown)  
 
[The details of using markdown to produce html is outlined here](about.md#about-usage-of-markdown)  
[The details of using markdown to produce html is outlined here](about#about_usage_of_markdown)

[Link to home in this directory](home_en.md)  
[Link to home absolute link from root](/edrops/src/main/resources/static/markdown/home_en.md)
[Link to home absolute link from root](/src/main/resources/static/markdown/home_en.md)  
[Link to home absolute link2 from root](/static/markdown/home_en.md)  
[Link to home relative link from here](../markdown/home_en.md)

[Link to markdown](markdown.md)  


I use a relational database for structuring the blogs and metadata, and to enable to 
write and view the blogs online in various languages in the future. 
H2 is used for testing and for loading initial content from Kotlin into the database. 
I plan to use MariaDB in production. I plan to make this a multiuser system with proper secure login. 
I am not there yet.




