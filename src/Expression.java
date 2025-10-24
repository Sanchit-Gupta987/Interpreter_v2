abstract class Expression {
    static class BinaryExpression extends Expression {
        public Expression left;
        public Token operator;
        public Expression right;

        public BinaryExpression(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
    }

    static class UnaryExpression extends Expression {
        public Token operator;
        public Expression right;

        public UnaryExpression(Token operator, Expression right) {
            this.operator = operator;
            this.right = right;
        }
    }

    static class LiteralExpression extends Expression {
        public Object literalValue;

        public LiteralExpression(Object literalValue) {
            this.literalValue = literalValue;
        }
    }

    static class ParenthesesExpression extends Expression {
        public Expression expression;

        public ParenthesesExpression(Expression expression) {
            this.expression = expression;
        }
    }
}