package Other;

import Physics.Vector2d;

public interface Function2d {
    public double evaluate(Vector2d p);

    public Vector2d gradient(Vector2d p, double delta);

    public Vector2d gradient(float x, float y);
}
