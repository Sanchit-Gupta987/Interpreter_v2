import java.util.List;

public class SpartieParser {
    private static class ParseError extends RuntimeException {}
    private List<Token> tokens;
    private int current = 0;

    public SpartieParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Expression parse() {
        try {
            return expression();
        }
        catch (ParseError error) {
            return null;
        }
    }

    private Expression expression() {
        return equality();
    }

    private Expression equality() {
        Expression expression = comparison();

        while (match(TokenType.NOT_EQUAL, TokenType.EQUIVALENT)) {
            Token operator = previous();
            Expression right = comparison();
            expression = new Expression.BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private Expression comparison() {
        Expression expression = term();

        while (match(TokenType.GREATER_THAN, TokenType.GREATER_EQUAL, TokenType.LESS_THAN, TokenType.LESS_EQUAL)) {
            Token operator = previous();
            Expression right = term();
            expression = new Expression.BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private Expression term() {
        Expression expression = factor();

        while (match(TokenType.SUBTRACT, TokenType.ADD)) {
            Token operator = previous();
            Expression right = factor();
            expression = new Expression.BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private Expression factor() {
        Expression expression = unary();

        while (match(TokenType.DIVIDE, TokenType.MULTIPLY)) {
            Token operator = previous();
            Expression right = unary();
            expression = new Expression.BinaryExpression(expression, operator, right);
        }

        return expression;
    }

    private Expression unary() {
        if (match(TokenType.NOT, TokenType.SUBTRACT)) {
            Token operator = previous();
            Expression right = unary();
            return new Expression.UnaryExpression(operator, right);
        }

        return primary();
    }

    private Expression primary() {
        if (match(TokenType.FALSE)) return new Expression.LiteralExpression(false);
        if (match(TokenType.TRUE)) return new Expression.LiteralExpression(true);
        if (match(TokenType.NULL)) return new Expression.LiteralExpression(null);

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expression.LiteralExpression(previous().literal);
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expression expression = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expression.ParenthesesExpression(expression);
        }

        throw error(peek(), "Expected expression");
    }

    // Error reporting
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    static ParseError error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            System.err.println("Error occurred on line: " + token.line + " at end " + message);
        } else {
            System.err.println(token.line + " at '" + token.text + "'" + message);
        }
        return new ParseError();
    }

    // Utility functions
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
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
}
