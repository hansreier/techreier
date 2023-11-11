## About usage of markdown

Commonmark reference implementation was first used for markdown. What I 
found is that is was not very configurable, even if some extensions could be 
added (like Table and Image extension) in additon to just use the Commonmark 
standard.
To achive github flavoured markdown, like in github and in Intellij, I had to 
use and configure Flexmark. Flexmark is much more configurable. Note that it 
can be discussed how much deviation from the standard that is adviceable. As 
a developer, github flavoured markdown can be considered as standard.

In addition support for tables, links and image
attributes is added with commonmark extension, and images can be used as well.

One disadvantage with markdown is the limited set of formatting options for
individual Html tags. It is possible to do some of it
with eiher smart css or including html in the markdown.
As a more customizable alternative flexmark could be used instead
of the commonmark reference library (not tested).
My experience with these smart markdown tricks, is that it is difficult to
obtain
the desired layout effects without a lot of effort. The best is to accept the
limitations of markdown. One option is to add more markdown extensions or to use
a
more advances markdown library that extends the Commonmark standard.

The markdown is embedded in the Html. It is two ways to add markdown to this web
site:

- To include static markdown files
- To use embedded text editor and store the markdown in a database.

The markdown context is selected from a menu

- Hard coded menu for the static markdown files
- Storing metadata in a database and produce a menu from it.

### Alternatives to markdown

- Plain HTMl (too much work and includes coding)
- language resource file text (tried that, not ideal)
- To use RTF.

The RTF option is the best alternative, but needs an RTF editor and a more
complex parser.
The generated embedded HTML also is not as clean as if markdown is used.
Language resource files
is used in this project, but only for short text.

### Security

Markdown does not prevent HTML injection, since it is possible to add plan HTML
to markdown. To avoid security problems
a sanitizer is used to remove dangerous HTML. A problem with this is that it
also remove som useful
HTML tags that can be included in the markdown. The Owasp sanitizer is
configurable, so I have allowed some html tags
and attributes that was denied in the default setup. The right adjustment of
numbers used in the table example below,
is removed by the sanitizer, if I do not allow p tag and align attribute when
configuring the sanitizer.
The title attribute of img is also removed, if I do not tell the sanitizer to
keep it.

### Examples

Examples of links:

[Link to tech](tech.md)  
[Link to internal Example headline](#example-headline)  
[Link to tech how to headline](tech.md#how-to-make-a-text-based-website-without-coding-html)  
[Link to default blog](../blogs)  
[Link to energy blog](../blogs/energy)  
[Link to external chatgpt](https://openai.com/blog/chatgpt)

The tech links uses Flexmark to convert Markdown links to HTML links on the web page.
This enables the link to work correctly in Github, Intellij and web page.
It is possible to link to headlines with id's defined, as in HTML.
Flexmark has to be configured to generate ids for headlines,
and the Sanitizer must be configured not to remove them.
The links to database blog content, only works on the produced web page.
All link examples contain relative links, except the bottom link that is just a regular external link.

Example of embedded table in markdown.

| City   |              No of citizens | County    | Comment                                |
|--------|---------------------------:|-----------|----------------------------------------|
| Oslo   |                      709037 | Oslo      | Capital of Norway                      | 
| Bergen |                      288133 | Vestland  | Second largest city                    | 
| Moelv  |                        4459 | Innlandet | My home village, I love it and hate it |

Example of embeded image in markdown.

![PerSeter](../images/pas.jpg "Per Seter"){#image-id}

### Example headline 

The contents should be visible by clicking on internal page link above
