# Forms

Basically this is what the parsed program contains, they help in having a unique data type for the first machine construction in which an Ev continuation is created

### FormList
---
The container of a function, useful to identify when a composed instruction stats/stops

### FormLiteral
---
Strings, numbers, bools or any other value

### FormSymbol
---
Function calls and keywords 


As an example the LISP code

    (let [x 2] 
    (+ x 2))

Would be a

    FormList(
        [
        FormSymbol("let"),
            FormList([
                FormSymbol("x"),
                FormLiteral(2)
            ]),
        FormList([
            FormSymbol("+")
            FormSymbol("x")
            FormLiteral(2)
        ])
    ]) 


