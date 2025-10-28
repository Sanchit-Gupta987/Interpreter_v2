/**
 * Author : Sanchit Gupta(sxg1243) and Ritvik Sajja(rxs1280)
 */
public class SpartieInterpreter {
    Object run(Expression expression) {
        Object result = interpret(expression);
        return result;
    }

    Object interpret(Expression expression) {
        // Depending on the expression type from the parser, we attempt to interpret the expression
        return switch (expression) {
            case Expression.LiteralExpression literalExpression -> interpretLiteral(literalExpression);
            case Expression.ParenthesesExpression parenthesesExpression -> interpretParenthesis(parenthesesExpression);
            case Expression.UnaryExpression unaryExpression -> interpretUnary(unaryExpression);
            case Expression.BinaryExpression binaryExpression -> interpretBinary(binaryExpression);
            case null, default -> null;
        };
    }

    private Object interpretLiteral(Expression.LiteralExpression expression) {
        // This is fairly simple, just return the actual literal value. For example "some string" or 3.0
        return expression.literalValue;
    }

    private Object interpretParenthesis(Expression.ParenthesesExpression expression) {
        // Take what is inside the parenthesis and send it back to our interpreter
        return this.interpret(expression.expression);
    }

    private Object interpretUnary(Expression.UnaryExpression expression) {
        Object right = interpret(expression.right);

        switch (expression.operator.type) {
            case NOT:
                return !isTrue(right);
            case SUBTRACT:
                validateOperand(expression.operator, right);
                return -((double)right);
        }
        return null;
    }

    private Object interpretBinary(Expression.BinaryExpression expression) {
        Object left = interpret(expression.left);
        Object right = interpret(expression.right);

        if (expression.operator.type == TokenType.ADD) {
            if (left instanceof Double l && right instanceof Double r) {
                return l + r;
            }
            else if (left instanceof String l && right instanceof String r) {
                return l + r;
            }
            else if (left instanceof String l && right instanceof Double r) {
                return l + String.format("%.2f",(double) Math.round(r));
            }
            else if (left instanceof Double l && right instanceof String r) {
                return String.format("%.2f",(double) Math.round(l)) + r;
            }
            else {
                error("Invalid types for addition on line " + expression.operator.line + " : " + left + " + " + right);
            }
        }

        switch(expression.operator.type) {
            case EQUIVALENT:
                return isEquivalent(left, right);
            case NOT_EQUAL:
                return !isEquivalent(left, right);
        }

        // At this point, we can validate if our operands are doubles because they cannot be Strings for the other operation
        validateOperands(expression.operator, left, right);

        // TODO: Handle binary operator for operands. Keep in mind, at this point, we know they are doubles, but you
        // TODO: still need to cast them to doubles. Use the primitive type, e.g. (double)left
        // TODO: we do not support >, >=, <, or <= on Strings
        switch(expression.operator.type) {
            case SUBTRACT:
                return (double) left - (double) right;
            case MULTIPLY:
                return (double) left * (double) right;
            case DIVIDE:
                return (double) left / (double) right;
            case GREATER_THAN:
                return (double) left > (double) right;
            case GREATER_EQUAL:
                return (double) left >= (double) right;
            case LESS_THAN:
                return (double) left < (double) right;
            case LESS_EQUAL:
                return (double) left <= (double) right;
        }
        return null;
    }

    // Helper Methods

    private boolean isEquivalent(Object left, Object right) {
        if (left == null && right == null) return true;
        if (left != null) {
            return left.equals(right);
        }
        return false;
    }

    private boolean isTrue(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean b) return b;
        return true;
    }

    // Validate the type
    private void validateOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        error("Invalid type on line " + operator.line + " : " + operator.text + operand);
    }

    private void validateOperands(Token operator, Object operand1, Object operand2) {
        if (operand1 instanceof Double && operand2 instanceof Double) return;
        error("Invalid type on line " + operator.line + " : " + operand1 + operator.text + operand2);
    }


    private void error(String message) {
        System.err.println(message);
        System.exit(2);
    }
}
