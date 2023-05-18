## About usage of markdown

Commonmark is used for markdown.  In addision support for tables and links
is added with commonmark extension, and images can be used as well.  

One disadvantage with markdown is the limited set of formatting options for 
individual Html tags. It is possible to do some of it 
with eiher smart css or including html in the markdown.
My experience with these smart markdown tricks, is that it is difficult to obtain
the desired layout effects without a lot of effort. The best is to accept the 
limitations of markdown. One option is to add more markdown extensions or to use a
more  advances markdown library that extends the Commonmark standard.

The markdown is embedded in the Html. It is two ways to add markdown to this web site:
- To include static markdown files
- To use embedded text editor and store the markdown in a database.  

The markdown context is selected from a menu
- Hard coded menu for the static markdown files
- Storing metadata in a database and produce a menu from it.
  
### Alternatives to markdown:  

- Plain HTMl (too much work and includes coding)
- language resource file text (tried that, not ideal)
- To use RTF.

The RTF option is the best alternative, but needs an RTF editor and a more complex parser.
The generated embedded HTML also is not as clean as if markdown is used. Language resource files
is used in this project, but only for short text.

### Security

Markdown does not prevent HTML injection, since it is possible to add plan HTML to markdown. To avoid security problems
a sanitizer is used to remove dangerous HTML. A problem with this is that it also remove som useful 
HTML tags that can be included in the markdown. The Owasp sanitizer is configurable, so I have allowed some html tags 
and attributes that was denied in the default setup. The right adjustment of numbers used in the table example below, 
is removed by the sanitizer, if I do not allow p tag and align attribute when configuring the sanitizer.
The title attribute of img is also removed, if I do not tell the sanitizer to keep it.

### Examples

Example of embeded image in markdown.

![PerSeter](../images/pas.jpg "Per Seter")

Example of embedded table in markdown.

| City   | No of citizens               | County    | Commment                               |
|--------|------------------------------|-----------|----------------------------------------|
| Oslo   | <p align="right">709037 </p> | Oslo      | Capital of Norway                      | 
| Bergen | <p align="right">288133 </p> | Vestland  | Second largest city                    | 
| Moelv  | <p align="right">4459</p>    | Innlandet | My home village, I love it and hate it | 

I had to use the paragraph tag and html to right adjust numbers in the cells. This demostrates the problem 
that markdown lacks ability to individually style by css class or id.
```
<p align="right">4459</p>
```
Example of link: https://openai.com/blog/chatgpt
