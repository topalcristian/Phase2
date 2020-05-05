package Other;

import Physics.Vector2D;

public interface Function2d {
    double evaluate(Vector2D p);

    Vector2D gradient(Vector2D p, double delta);

    Vector2D gradient(float x, float y);

}
