package Physics;

public class Vector2D {
/*
    public double x;
    public double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    public double get_x() {
        return this.x;
    }

    public double get_y() {
        return this.y;
    }


*/


    public double x;
    public double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double get_magnitude() {
        return (Math.sqrt(this.get_x() * this.get_x() + this.get_y() * this.get_y()));
    }

    public double get_x() {
        return this.x;
    }

    public void set_x(double x) {
        this.x = x;
    }

    public double get_y() {
        return this.y;
    }

    public void set_y(double y) {
        this.y = y;
    }

    public void add(Vector2D a) {
        this.x += a.get_x();
        this.y += a.get_y();
    }

    public Vector2D addX(double val) {
        this.x += val;
        return this;
    }

    public Vector2D addY(double val) {
        this.y += val;
        return this;
    }

    public Vector2D copy() {
        return new Vector2D(get_x(), get_y());
    }

    public double magnitude() {
        return Math.sqrt(Math.pow(this.get_x(), 2) + Math.pow(this.get_y(), 2));
    }

    public double len() {
        return this.magnitude();
    }

    public float dst(Vector2D v) {
        final float x_d = (float) (v.x - x);
        final float y_d = (float) (v.y - y);
        return (float) Math.sqrt(x_d * x_d + y_d * y_d);
    }
}