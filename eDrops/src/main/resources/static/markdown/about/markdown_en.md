## About usage of markdown

Commonmark reference implementation was first used for markdown. What I 
found is that it was not very configurable, even if some extensions could be 
added (like Table and Image extension) in addition to just use the Commonmark 
standard.
To achieve Github flavoured markdown, like in Github and in Intellij, I had to 
use and configure Flexmark. Flexmark is much more configurable. Note that it 
can be discussed how much deviation from the standard that is advisable. As 
a developer, Github flavoured markdown can be considered as standard.

In addition, support for tables, links and image
attributes is added with commonmark extension, and images can be used as well.

One disadvantage with markdown is the limited set of formatting options for
individual HTML tags. It is possible to do some of it
with either smart css or including HTML in the markdown.
As a more customizable alternative flexmark could be used instead
of the commonmark reference library (not tested).
My experience with these smart markdown tricks, is that it is difficult to
obtain the desired layout effects without a lot of effort. The best is to accept the
limitations of markdown. One option is to add more markdown extensions or to use a
more advances markdown library that extends the Commonmark standard.

The markdown is embedded in the HTML. It is two ways to add markdown to this website:

- To include static markdown files
- To use embedded text editor and store the markdown in a database.

The markdown context is selected from a menu

- Hard coded menu for the static markdown files
- Storing metadata in a database and produce a menu from it.

### Alternatives to markdown

- Plain HTMl (too much work and includes coding)
- language resource file text (tried that, not ideal)
- To use RTF.

The RTF option is the best alternative, but needs an RTF editor and a more complex parser.
The generated embedded HTML also is not as clean as if markdown is used.
Language resource files is used in this project, but only for short text.

### Security

Markdown does not prevent HTML injection, since it is possible to add plan HTML
to markdown. To avoid security problems  a sanitizer is used to remove dangerous HTML. 
A problem with this is that it removes some useful HTML tags that can be included in the markdown. 
The Owasp sanitizer is configurable, so I have allowed some HTML tags
and attributes that was denied in the default setup. 
The right adjustment of numbers used in the table example below,
is removed by the sanitizer, if I do not allow the <p> tag and align attribute when
configuring the sanitizer.
The title attribute of image is also removed, if I do not tell the sanitizer to keep it.

**NOTE I will try JSoup instead, because it is more actively maintained.**
A new security risk is detected with the latest Owasp sanitizer.

### Examples

Examples of links:  
[Link to home](../home_en.md)  
[Link to about electrical power](../elpower_en.md)  
[Link to tech](tech_en)  
[Link to internal Bottom headline](#bottom-headline)  
[Link to tech how to headline](tech_en#how-to-make-a-text-based-website-without-coding-html)  
[Link to default blog](../blogs)  
[Link to environment blog](../blogs/env)  
[Link to energy blog](../blogs/energy). Test of failure, no English energy blog.  With Norwegian set as language: OK.  
[Link to external chatgpt](https://openai.com/blog/chatgpt)

The code uses Flexmark to convert Markdown links to HTML links on the web page.
This enables the link to work correctly in GitHub, Intellij and web page.
It is possible to link to headlines with id's defined, as in HTML.
Flexmark has to be configured to generate ids for headlines,
and the Sanitizer must be configured not to remove them.
The links to blog content stored in a database, only works on the produced web page.
All link examples contain relative links, except the bottom link that is just a regular external link.

Example of embedded table in markdown.

| City   |              No of citizens | County    | Comment                                |
|--------|---------------------------:|-----------|----------------------------------------|
| Oslo   |                      709037 | Oslo      | Capital of Norway                      | 
| Bergen |                      288133 | Vestland  | Second largest city                    | 
| Moelv  |                        4459 | Innlandet | My home village, I love it and hate it |

### Media files in markdown

Example of embedded image in markdown, stored in the Docker container.

![My mascot PerSeter](../../images/pas.jpg "Per Seter")

Example of embedded images in markdown, stored on volume outside the Docker container

![Cherries in my garden](cherries.jpg "Cherries in my garden")

There is no difference in syntax for the two methods.
I use a Flexmark Visitor pattern to be able to detect where the images are stored.
I have not yet made any GUI for uploading images, 
so an upload or syncronization tool is required to the VPS (e.g. Jottacloud).  

### Example headline 

The contents should be visible by clicking on internal page link above

### Bottom headline

Nothing interesting here at the bottom of page. Just som bullshit I am writing.
I do not care if you actually read this.

[Link to internal Examples headline](#examples)  


