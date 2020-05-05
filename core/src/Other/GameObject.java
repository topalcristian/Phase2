package Other;

import Physics.Vector2D;
import com.badlogic.gdx.math.Vector3;

public class GameObject {
    public double friction = 0;        // coefficient of friction
    public double bounciness = 0;      // coefficient of restitution
    public Vector2D velocity = new Vector2D(0, 0);
    public Vector3 position = new Vector3(0, 0, 0);
    public Vector2D acceleration = new Vector2D(0, 0);
    public Vector3 old_position = new Vector3(0, 0, 0);
    public Vector2D old_acceleration_vector = new Vector2D(0, 0);
    public double mass = 0.1;

    public double velMagnitude() {
        return (Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y));
    }
}