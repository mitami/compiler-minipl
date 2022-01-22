import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lexer {
    private String source;
    private List<Token> tokens = new ArrayList<>();

    private int startPosition = 0;
    private int currentPosition = 0;
    private int lineNumber = 1;

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
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '+': addToken(TokenType.PLUS); break;
            case '-': addToken(TokenType.MINUS); break;
            case '*': addToken(TokenType.STAR); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '.': addToken(TokenType.DOT); break;
        }
    }

    private char getNextCharacter() {
        return source.charAt(this.currentPosition++);
    }

    private void addToken(TokenType tokenType) {
        addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object literal) {
        String text = this.source.substring(this.startPosition, this.currentPosition);
        this.tokens.add(new Token(tokenType, text, literal, this.lineNumber));
    }
}