import java.util.List;

public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {

    private Environment environment = new Environment();

    public void interpretExpression(List<Statement> statements) {
        try {
            for(Statement statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            CompilerMain.runtimeError(error);
        }
        // try {
        //     Object value = evaluate(expression);
        //     System.out.println(stringify(value));
        // } catch (RuntimeError error) {
        //     CompilerMain.runtimeError(error);
        // }
    }
    
    @Override
    public Object visitLiteralExpression(Expression.Literal expression) {
        return expression.value;
    }

    @Override
    public Object visitGroupingExpression(Expression.Grouping expression) {
        return evaluate(expression.expression);
    }

    @Override
    public Object visitUnaryExpression(Expression.Unary expression) {
        Object right = evaluate(expression.expression);

        switch(expression.operator.type) {
            case NOT:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expression.operator, right);
                return -(double)right;
        }

        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if(operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperand(Token operator, Object left, Object right) {
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    @Override
    public Object visitBinaryExpression(Expression.Binary expression) {
        Object left = evaluate(expression.leftExpression);
        Object right = evaluate(expression.rightExpression);

        // We check for each operation that the operands actually are numbers
        // and the user will be informed if there is an error without crashing
        // the interpreter.
        switch(expression.operator.type) {
            // Comparisons
            case GREATER:
                checkNumberOperand(expression.operator, left, right);
                return (double)left > (double)right;
            case LESS:
                checkNumberOperand(expression.operator, left, right);
                return (double)left < (double)right;
            case EQUAL:
                return isEqual(left, right);
            // Arithmetic operations
            case MINUS:
                checkNumberOperand(expression.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                checkNumberOperand(expression.operator, left, right);
                return (double)left + (double)right; // Assuming no concatenation of string with '+'
            case SLASH:
                checkNumberOperand(expression.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperand(expression.operator, left, right);
                return (double)left * (double)right;

        }

        return null;
    }



    private boolean isEqual(Object a, Object b) {
        if(a == null && b == null) return true;
        if(a == null) return false;

        return a.equals(b);
    }

    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }

    private void execute(Statement statement) {
        statement.accept(this);
    }

    private String stringify(Object object) {
        if(object == null) return "null";

        if(object instanceof Double) {
            String text = object.toString();
            if(text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    @Override
    public Void visitExpressionStatement(Statement.ExpressionStatement statement) {
        evaluate(statement.expression);
        return null;
    }

    @Override
    public Void visitPrintStatement(Statement.PrintStatement statement) {
        Object value = evaluate(statement.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVariableStatement(Statement.VariableStatement statement) {
        Object value = null;
        if(statement.initializer != null) {
            value = evaluate(statement.initializer);
        }

        environment.define(statement.name.lexeme, value);
        return null;
    }

    @Override
    public Object visitVariableExpression(Expression.Variable expression) {
        return environment.get(expression.name);
    }

    @Override
    public Object visitAssignExpression(Expression.Assign expression) {
        Object value = evaluate(expression.value);
        environment.assign(expression.name, value);
        return value;
    }
}
