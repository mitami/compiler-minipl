enum TokenType {
    LEFT_PAREN, RIGHT_PAREN,
    COMMA, DOT, SEMICOLON, COLON, ASSIGN, SPREAD,
    
    // Arithmetic
    MINUS, PLUS, STAR, SLASH,

    // Logical operators
    EQUAL, GREATER, LESS, OR, AND, NOT,

    // Literal values (strings, integers and booleans)
    STRING_LIT, NUMBER, FALSE, TRUE,

    // The 'names' of variables
    IDENTIFIER,

    // Reserved keywords
    VAR, FOR, END, IN, DO, READ, PRINT, ASSERT,
    // Types
    INT, STRING, BOOL,

    EOF
}
