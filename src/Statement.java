abstract class Statement {
    interface Visitor<R> {
        R visitExpressionStatement(ExpressionStatement statement);
        R visitPrintStatement(PrintStatement statement);
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
}
