// This is a tool for printing the AST in a format that allows easy inspection 
// of the precedence of operations.
// It uses the Visitor pattern recursively to print out the whole tree and adds 
// parentheses around the expressions to indicate precedence.
public class AstPrinter implements Expression.Visitor<String> {
    private String parenthesize(String name, Expression... expressions) {
        StringBuilder builder = new StringBuilder();

        // Enclose groups of expressions to parentheses
        builder.append("(").append(name);
        for(Expression expression : expressions) {
            builder.append(" ");
            builder.append(expression.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }

    String print(Expression expression) {
        return expression.accept(this);
    }

    @Override
    public String visitUnaryExpression(Expression.Unary expression) {
        return this.parenthesize(expression.operator.lexeme, expression.expression);
    }

    @Override
    public String visitBinaryExpression(Expression.Binary expression) {
        return parenthesize(expression.operator.lexeme, expression.leftExpression, expression.rightExpression);
    }

    @Override
    public String visitLiteralExpression(Expression.Literal expression) {
        if(expression.value == null) return null;
        return expression.value.toString();
    }

    @Override
    public String visitGroupingExpression(Expression.Grouping expression) {
        return parenthesize("group", expression.expression);
    }}
