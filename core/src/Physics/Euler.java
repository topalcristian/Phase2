package Physics;

import Other.GameObject;
import Screens.TheCourse;
import com.badlogic.gdx.math.Vector3;

public class Euler implements PhysicsEngine {

    public Vector3 force(GameObject obj, TheCourse c, double h) {

        // Friction
        Vector3 friction = new Vector3(
                obj.velocity.len() == 0 ? 0 : (float) (-c.get_friction_coefficient() * obj.mass * c.getGravity() * obj.velocity.x / (double) obj.velocity.len()),
                obj.velocity.len() == 0 ? 0 : (float) (-c.get_friction_coefficient() * obj.mass * c.getGravity() * obj.velocity.y / (double) obj.velocity.len()),
                0
        );

        // Gravity
        Vector3 gravity = new Vector3(
                (float) (-obj.mass * c.getGravity() * c.get_height().gradient(obj.position.x, obj.position.y).x),
                (float) (-obj.mass * c.getGravity() * c.get_height().gradient(obj.position.x, obj.position.y).y),
                0
        );

        System.out.println(c.get_height().gradient(obj.position.x, obj.position.y).x + "," + c.get_height().gradient(obj.position.x, obj.position.y).y);

        return friction.add(gravity);
    }

    public Vector3[] solve(GameObject obj, TheCourse c, double h) {
        Vector3[] response = new Vector3[3];

        // Position vector
        response[0] = new Vector3(
                (float) (obj.position.x + h * obj.velocity.x),
                (float) (obj.position.y + h * obj.velocity.y),
                (float) (obj.position.z + h * obj.velocity.z)
        );
        System.out.println("Position: " + response[0]);
        // Velocity vector
        Vector3 f = this.force(obj, c, h);

        response[1] = new Vector3(
                (float) (obj.velocity.x + (h * f.x) / obj.mass),
                (float) (obj.velocity.y + (h * f.y) / obj.mass),
                (float) (obj.velocity.z + (h * f.z) / obj.mass)
        );
        response[2] = f;
        return response;
    }
}