import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Lexer {
    private String source;
    private List<Token> tokens = new ArrayList<>();

    private int startPosition = 0;
    private int currentPosition = 0;
    private int lineNumber = 1;

    private static HashMap<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("print", TokenType.PRINT);
        keywords.put("var", TokenType.VAR);
        keywords.put("for", TokenType.FOR);
        keywords.put("end", TokenType.END);
        keywords.put("in", TokenType.IN);
        keywords.put("do", TokenType.DO);
        keywords.put("read", TokenType.READ);
        keywords.put("int", TokenType.INT);
        keywords.put("string", TokenType.STRING);
        keywords.put("bool", TokenType.BOOL);
        keywords.put("assert", TokenType.ASSERT);
    }

    public Lexer(String source) {
        this.source = source;
    }
    // Read characters and form TOKENS out of them
    public List<Token> scanFileForTokens() {
        while(!isAtEnd()) {
            this.startPosition = this.currentPosition;
            scanToken();
        }

        this.tokens.add(new Token(TokenType.EOF, "", null, this.lineNumber));
        return this.tokens;
    }

    private boolean isAtEnd() {
        return this.currentPosition >= this.source.length();
    }

    private void scanToken() {
        char currentCharacter = getNextCharacter();
        switch (currentCharacter) {
            case ' ':
            case '\r':
            case '\t': break;
            case '\n': this.lineNumber++; break;
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '+': addToken(TokenType.PLUS); break;
            case '-': addToken(TokenType.MINUS); break;
            case '*': addToken(TokenType.STAR); break;
            case '/': addToken(TokenType.SLASH); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '.':
                addToken(isNextCharacter('.') ? TokenType.SPREAD : TokenType.DOT);
                break;
            // We need to know whether we have just ':' or ':='
            case ':': 
                addToken(isNextCharacter('=') ? TokenType.ASSIGN : TokenType.COLON);
                break;
            case '"':
                handleStringLiteral();
                break;
            default:
                if(this.isLowerCaseAlpha(currentCharacter)) {
                    this.handleReservedKeyword();
                    return;
                }
                if(this.isNumber(currentCharacter)) {
                    this.handleNumber();
                    return;
                }
                CompilerMain.error(lineNumber, "Unexpected token: " + currentCharacter);
        }
    }

    private void handleNumber() {
        while(this.isNumber(this.peekNextCharacter()) && peekNextCharacter() != '\0' && !isAtEnd()) {
            getNextCharacter();
        }
        addToken(TokenType.NUMBER);
    }

    private void handleReservedKeyword() {
        // Read characters until a non-alpha or EOF is met.
        while(this.isAlphabet(this.peekNextCharacter()) && peekNextCharacter() != '\0' && !isAtEnd()) {
            getNextCharacter();
        }
        // Fetch from keyword hashmap with the result
        if(keywords.get(this.source.substring(this.startPosition, this.currentPosition)) != null) {
            addToken(keywords.get(this.source.substring(this.startPosition, this.currentPosition)));
            return;
        }
        // NULL => not a keyword, error. Needs to be able to detect identifiers too!
        CompilerMain.error(
            this.lineNumber, "Unknown keyword: " +
            this.source.substring(this.startPosition, this.currentPosition)
        );
        // Token returned -> addToken
    }

    private void handleStringLiteral() {
        // It's always a string, but we must make sure it has closing quote too, else it's an error.
        boolean foundClosingQuote = false;
        while(peekNextCharacter() != '\0' && !isAtEnd()) {
            if(peekNextCharacter() == '"')  {
                foundClosingQuote = true;
                getNextCharacter();
                addToken(TokenType.STRING_LIT);
                break;
            }
            if(peekNextCharacter() == '\n') this.lineNumber++;
            getNextCharacter();
        }
        // If we found a closing quote, break out of switch, otherwise error
        if(foundClosingQuote) return;
        CompilerMain.error(this.lineNumber, "Unterminated string");
    }

    private boolean isNumber(char character) {
        return character >= '0' && character <= '9';
    }

    private boolean isAlphabet(char character) {
        return (character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z');
    }

    private boolean isLowerCaseAlpha(char character) {
        return character >= 'a' && character <= 'z';
    }

    private char getNextCharacter() {
        return source.charAt(this.currentPosition++);
    }

    private char peekNextCharacter() {
        if(isAtEnd()) return '\0';
        return source.charAt(this.currentPosition);
    }

    private void addToken(TokenType tokenType) {
        addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object literal) {
        String text = this.source.substring(this.startPosition, this.currentPosition);
        this.tokens.add(new Token(tokenType, text, literal, this.lineNumber));
    }

    private boolean isNextCharacter(char character) {
        if(isAtEnd()) return false;
        if(this.source.charAt(this.currentPosition) != character) return false;

        this.currentPosition++;
        return true;
    }
}
