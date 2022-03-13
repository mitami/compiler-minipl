import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Expression expression() {
        return equality();
    }

    private Statement declaration() {
        try {
            if(match(TokenType.VAR)) return variableDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Statement statement() {
        if(match(TokenType.PRINT)) return printStatement();

        return expressionStatement();
    }

    private Statement printStatement() {
        Expression value = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after value.");
        return new Statement.PrintStatement(value);
    }

    private Statement expressionStatement() {
        Expression expression = expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
        return new Statement.ExpressionStatement(expression);
    }

    private Statement variableDeclaration() {
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");

        Expression initializer = null;
        // This actually omits the type definition for now and looks straight
        // for the ASSIGN operator to detect variable declarations.
        // TODO: add types
        if(match(TokenType.ASSIGN)) {
            initializer = expression();
        }

        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
        return new Statement.VariableStatement(name, initializer);
    }

    private Expression equality() {
        Expression expression = comparison();

        while(match(TokenType.EQUAL)) {
            Token operator = previous();
            Expression rightExpression = comparison();
            expression = new Expression.Binary(expression, operator, rightExpression);
        }

        return expression;
    }

    private Expression comparison() {
        Expression expression = term();

        while(match(TokenType.LESS, TokenType.GREATER)) {
            Token operator = previous();
            Expression rightExpression = term();
            expression = new Expression.Binary(expression, operator, rightExpression);
        }

        return expression;
    }

    private Expression term() {
        Expression expression = factor();

        while(match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expression rightExpression = factor();
            expression = new Expression.Binary(expression, operator, rightExpression);
        }

        return expression;
    }

    private Expression factor() {
        Expression expression = unary();

        while(match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expression rightExpression = unary();
            expression = new Expression.Binary(expression, operator, rightExpression);
        }

        return expression;
    }

    private Expression unary() {
        if(match(TokenType.NOT, TokenType.MINUS)) {
            Token operator = previous();
            Expression rightExpression = unary();
            return new Expression.Unary(operator, rightExpression);
        }

        return primary();
    }

    private Expression primary() {
        if(match(TokenType.FALSE)) return new Expression.Literal(false);
        if(match(TokenType.TRUE)) return new Expression.Literal(true);
        // if(match(TokenType.FALSE)) return new Expression.Literal(null);

        if(match(TokenType.NUMBER, TokenType.STRING_LIT)) return new Expression.Literal(previous().literal);

        if(match(TokenType.IDENTIFIER)) {
            return new Expression.Variable(previous());
        }

        if(match(TokenType.LEFT_PAREN)) {
            Expression expression = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expression.Grouping(expression);
        }

        throw error(peek(), "Expect expression.");
    }

    private Token consume(TokenType type, String message) {
        if(check(type)) return advance();

        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        CompilerMain.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        // If we don't find a token that means the start of a new expression, or
        // a semicolon, which means the end of the previous one, we continue 
        // advancing in the token stream.
        // We do this to reach a point where the previously encountered error will
        // no longer affect the source code.
        // In MiniPL actually the semicolon recognition is enough because they 
        // are mandatory in the syntax.
        while(!isAtEnd()) {
            if(previous().type == TokenType.SEMICOLON) return;

            switch(peek().type) {
                case VAR:
                case FOR: // This is not actually always the start of a new expression, but can be "end for"
                    if(previous().type != TokenType.END) return;
                case PRINT:
                case ASSERT:
                    return;
            }

            advance();
        }
    }

    private boolean match(TokenType... tokenTypes) {
        for(TokenType type : tokenTypes) {
            if(check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if(isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if(!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }


    // Take in an array of TOKENS, and create a tree structure
    public List<Statement> parseTokens() {
        List<Statement> statements = new ArrayList<>();
        while(!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;

        // try {
        //     return expression();
        // } catch (ParseError error) {
        //     return null;
        // }
    }
}
