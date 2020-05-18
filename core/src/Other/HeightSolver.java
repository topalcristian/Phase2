package Other;

import Physics.Vector2D;

public class HeightSolver implements Function2d {

    double x, y;
    public String height;

    public HeightSolver(String heightFun) {
        this.height = heightFun;
    }

    @Override
    public double evaluate(Vector2D p) {
        x = p.get_x();
        y = p.get_y();
        return eval(height);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


    public Vector2D gradient(float x, float y) {
        return this.gradient(new Vector2D(x, y), 0.01);
    }

    public Vector2D gradient(Vector2D p, double delta) {
        double z = evaluate(p);
        return new Vector2D((z - evaluate(p.addX(delta))) * delta, (z - evaluate(p.addY(delta)) * delta));
    }


    public double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch == 'x' || ch == 'X') {
                    nextChar();
                    x = getX();
                } else if (ch == 'y' || ch == 'Y') { // numbers
                    nextChar();
                    x = getY();
                } else if (ch >= 'a' && ch <= 'w') { // functions
                    while (ch >= 'a' && ch <= 'w') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    switch (func) {
                        case "sqrt":
                            x = Math.sqrt(x);
                            break;
                        case "sin":
                            x = Math.sin(Math.toRadians(x));
                            break;
                        case "cos":
                            x = Math.cos(Math.toRadians(x));
                            break;
                        case "tan":
                            x = Math.tan(Math.toRadians(x));
                            break;
                        default:
                            throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    private static final double approximate_limit = Math.pow(10, -10);

    public double derivative_by_x(double x, double y) {
        Vector2D vectorX = new Vector2D(x + approximate_limit, y);
        Vector2D vector = new Vector2D(x, y);
        return ((this.evaluate(vectorX) - this.evaluate(vector)) / approximate_limit);
    }

    public double derivative_by_y(double x, double y) {
        Vector2D vectorY = new Vector2D(x, y + approximate_limit);
        Vector2D vector = new Vector2D(x, y);
        return ((this.evaluate(vectorY) - this.evaluate(vector)) / approximate_limit);
    }
}
