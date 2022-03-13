abstract class Expression {
    // We take advantage of the Visitor pattern here to enable diverse 
    // functionality for the Expression subclasses from outside the class
    interface Visitor<R> {
        R visitUnaryExpression(Unary expression);
        R visitBinaryExpression(Binary expression);
        R visitLiteralExpression(Literal expression);
        R visitGroupingExpression(Grouping expression);
    }

    abstract <R> R accept(Visitor<R> visitor);

    static class Unary extends Expression {
        final Token operator;
        final Expression expression;

        Unary(Token operator, Expression expression) {
            this.operator = operator;
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpression(this);
        }
    }

    static class Binary extends Expression {
        final Token operator;
        final Expression leftExpression;
        final Expression rightExpression;

        Binary(Expression leftExpression, Token operator, Expression rightExpression) {
            this.operator = operator;
            this.leftExpression = leftExpression;
            this.rightExpression = rightExpression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpression(this);
        }
    }

    static class Literal extends Expression {
        final Object value;

        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpression(this);
        }
    }

    static class Grouping extends Expression {
        final Expression expression;

        Grouping(Expression expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpression(this);
        }
    }
}
