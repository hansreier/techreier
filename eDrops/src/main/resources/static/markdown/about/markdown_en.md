## About usage of Markdown

Commonmark reference implementation was first used for Markdown. What I 
found is that it was not very configurable, even if some extensions could be 
added (like Table and Image extension) in addition to just use the Commonmark 
standard. I startet to use Flexmark instead. But I have later found that this
library is not actively maintained anymore, and I have started to go back to CommonMark again.

GitHub flavored Markdown is the most used Markdown dialect, also used in Intellij.
I have implemented support for most of it. 
Support for tables, link in images is absoulutely required in a blog system.

One disadvantage with Markdown is the limited set of formatting options for
individual HTML tags. It is possible to do some of it
with either smart CSS or including HTML in the Markdown.
My experience with these smart Markdown tricks, is that it is difficult to
obtain the desired layout effects without a lot of effort. The best is to accept the
limitations of Markdown. One option is to add more Markdown extensions.

The Markdown is embedded in the HTML. It is two ways to add Markdown to this website:

- To include static Markdown files
- To use embedded text editor and store the Markdown in a database.

The Markdown content is selected from a menu:  

- Hard coded menu for the static Markdown files
- Storing metadata in a database and produce a menu from it.

### Alternatives to Markdown

- Plain HTML (too much work and includes coding).
- language resource file text (tried that, not ideal).
- To use RTF.
- XML / XHTML (non-standard legacy solutions)

The RTF option is the best alternative, but needs an RTF editor and a more complex parser.
The generated embedded HTML also is not as clean as if Markdown is used.
Language resource files is used in this project, but only for short text.

### Security

Markdown does not prevent HTML injection, since it is possible to add plan HTML
to Markdown. To avoid security problems  a sanitizer is used to remove dangerous HTML. 
A problem with this is that it removes some useful HTML tags that can be included in the Markdown. 
The Owasp sanitizer is configurable, so I have allowed some HTML tags
and attributes that was denied in the default setup. 
The right adjustment of numbers used in the table example below,
is removed by the sanitizer, if I do not allow the <p> tag and align attribute when
configuring the sanitizer.
The title attribute of image is also removed, if I do not tell the sanitizer to keep it.

### Markdown links

Examples of links:  
[Link to home](../home_en.md)  
[Link to about electrical power](../elpower_en.md)  
[Link to tech](tech_en.md)  
[Link to energy view](../energy)  
[Relative link to blog sideways](/blog/sideways)  
[Absolute link blog web page sideways](https://techreier.com/blog/sideways)  
[Link to external web page ChatGPT](https://openai.com/blog/chatgpt)  

The code uses the Markdown parser to convert Markdown links to HTML links on the web page.  
This enables the link to work correctly in GitHub, Intellij and web page.
My blog system can use all these link types, but the absolute URL is preferred.

### Anchor links

It is possible to link to headlines with id's defined, as in HTML.  
[Link to internal Bottom headline](#bottom-headline)  INTERNAL ANCHOR NOT IMPLEMENTED  
[Link to tech how to headline](tech_en#how-to-make-a-text-based-website-without-coding-html)      EXTERNAL ANCHOR NOT IMPLEMENTED  
If you try this is will not work in my blog. It works in Intellij.
I got this to work with Flexmark, it is possible with Commonmark, but not tested.
My intent is to skip this because it is not much used,
and it is not very useful either with blog pages usually been less than 3 pages.
It is generally hard to implement because it can collide with internal id’s and need to be prefixed.
The parser need to be configured to generate ids for headlines,
and the Sanitizer must be configured not to remove them.  
If the headline you link to is changed, it will not work either.
This often introduces a lot of broken links in documentation systems. 


The links to blog content stored in a database, only works on the produced web page.
All link examples contain relative links, except the bottom link that is just a regular external link.

Example of embedded table in Markdown.

| City   |              No of citizens | County    | Comment                                |
|--------|---------------------------:|-----------|----------------------------------------|
| Oslo   |                      709037 | Oslo      | Capital of Norway                      | 
| Bergen |                      288133 | Vestland  | Second largest city                    | 
| Moelv  |                        4459 | Innlandet | My home village, I love it and hate it |

### Media files in Markdown

Example of embedded image in Markdown, stored in the Docker container.

![My mascot PerSeter](../../images/pas.jpg "Per Seter")

Example of embedded images in Markdown, stored on volume (bind mount) outside the Docker container

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


