package Physics;

import com.badlogic.gdx.math.Vector2;

public class Vector2d {

    public double x;
    public double y;

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    public double get_x() {
        return this.x;
    }

    public double get_y() {
        return this.y;
    }

    public Vector2d add(Vector2d a) {
        return new Vector2d(this.get_x() + a.get_x(), this.get_y() + a.get_y());
    }

    public Vector2d addX(double val) {
        this.x += val;
        return this;
    }

    public Vector2d addY(double val) {
        this.y += val;
        return this;
    }

    public Vector2d copy() {
        return new Vector2d(get_x(), get_y());
    }

    public double magnitude() {
        return Math.sqrt(Math.pow(this.get_x(), 2) + Math.pow(this.get_y(), 2));
    }

    public double len() {
        return this.magnitude();
    }

    public float dst(Vector2d v) {
        final float x_d = (float) (v.x - x);
        final float y_d = (float) (v.y - y);
        return (float) Math.sqrt(x_d * x_d + y_d * y_d);
    }

}