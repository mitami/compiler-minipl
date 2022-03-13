abstract class Statement {
    interface Visitor<R> {
        R visitExpressionStatement(ExpressionStatement statement);
        R visitPrintStatement(PrintStatement statement);
        R visitVariableStatement(VariableStatement statement);
    }

    abstract <R> R accept(Visitor<R> visitor);

    static class ExpressionStatement extends Statement {
        final Expression expression;

        ExpressionStatement(Expression expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Statement.Visitor<R> visitor) {
            return visitor.visitExpressionStatement(this);
        }
    }

    static class PrintStatement extends Statement {
        final Expression expression;

        PrintStatement(Expression expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Statement.Visitor<R> visitor) {
            return visitor.visitPrintStatement(this);
        }
    }

    static class VariableStatement extends Statement {
        final Token name;
        final Expression initializer;

        VariableStatement(Token name, Expression initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <R> R accept(Statement.Visitor<R> visitor) {
            return visitor.visitVariableStatement(this);
        }
    }
}
