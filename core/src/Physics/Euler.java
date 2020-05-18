package Physics;

import Other.GameObject;
import Screens.TheCourse;
import Screens.World;

public class Euler implements PhysicsEngine {

    private static final double approximate_limit = Math.pow(10, -10);
    private final Force gravitational_force = new Force(0, 0);
    private final Force friction_force = new Force(0, 0);
    private TheCourse course;
    private double mass;
    private double g;
    private double coefficient_of_friction;
    private double time_step;

    private Vector2D velocity_vector;
    private Vector2D acceleration_vector;

    @Override
    public void solve(GameObject obj, TheCourse c, double h) {
        if (World.started) {

            g = c.getGravity();
            mass = c.getMass();
            coefficient_of_friction = c.getFriction();
            time_step = h;
            velocity_vector = obj.velocity;
            acceleration_vector = obj.acceleration;
            course = c;

            Vector2D result_vector = euler();
            update_position(result_vector);
            update_velocity(result_vector);

            if (c.getObjects().get(0).velMagnitude() < 0.01)
                World.inMove = false;
        }

    }

    public double derivative_by_x(double x, double y) {
        return ((course.heightEval(x + approximate_limit, y) - course.heightEval(x, y)) / approximate_limit);
    }

    public double derivative_by_y(double x, double y) {
        return ((course.heightEval(x, y + approximate_limit) - course.heightEval(x, y)) / approximate_limit);
    }

    public Vector2D euler() {

        //Find sum of forces and velocity for K1 (K1 = f(Xn, Tn))

        double sum_of_forces_x_rk = this.gravitational_force.get_x() + this.friction_force.get_x();
        double sum_of_forces_y_rk = this.gravitational_force.get_y() + this.friction_force.get_y();

        Vector2D acceleration_rk = new Vector2D(sum_of_forces_x_rk * (1 / this.mass), sum_of_forces_y_rk * (1 / this.mass));

        double velocity_x_rk = this.velocity_vector.get_x() + time_step * this.acceleration_vector.get_x();
        double velocity_y_rk = this.velocity_vector.get_y() + time_step * this.acceleration_vector.get_y();

        Vector2D K1 = new Vector2D(velocity_x_rk, velocity_y_rk);

        //Find sum of forces and velocity for K2 (K2 = f(Xn+h/2*K1, Tn + h/2))

        double derivative_by_x_rk = derivative_by_x(course.getObjects().get(0).position.x + time_step / 2 * K1.get_x(), course.getObjects().get(0).position.y + time_step / 2 * K1.get_y());
        double derivative_by_y_rk = derivative_by_y(course.getObjects().get(0).position.x + time_step / 2 * K1.get_x(), course.getObjects().get(0).position.y + time_step / 2 * K1.get_y());

        double g_x1 = -1 * this.mass * this.g * derivative_by_x_rk;
        double g_y1 = -1 * this.mass * this.g * derivative_by_y_rk;

        Force gravitational_force_rk = new Force(g_x1, g_y1);

        double scalar_rk = -1 * this.coefficient_of_friction * this.g * this.mass * (1 / K1.get_magnitude());

        Force friction_force_rk = new Force(
                scalar_rk * K1.get_x(),
                scalar_rk * K1.get_y());

        sum_of_forces_x_rk = gravitational_force_rk.get_x() + friction_force_rk.get_x();
        sum_of_forces_y_rk = gravitational_force_rk.get_y() + friction_force_rk.get_y();

        acceleration_rk = new Vector2D(sum_of_forces_x_rk * (1 / this.mass), sum_of_forces_y_rk * (1 / this.mass));
        velocity_x_rk = this.velocity_vector.get_x() + time_step + (time_step / 2) * this.acceleration_vector.get_x();
        velocity_y_rk = this.velocity_vector.get_y() + time_step + (time_step / 2) * this.acceleration_vector.get_y();

        Vector2D K2 = new Vector2D(velocity_x_rk, velocity_y_rk);

        //Find sum of forces and velocity for K3 (K3 = f(Xn+h/2*K2, Tn + h/2))

        derivative_by_x_rk = derivative_by_x(course.getObjects().get(0).position.x + time_step / 2 * K2.get_x(), course.getObjects().get(0).position.y + time_step / 2 * K2.get_y());
        derivative_by_y_rk = derivative_by_y(course.getObjects().get(0).position.x + time_step / 2 * K2.get_x(), course.getObjects().get(0).position.y + time_step / 2 * K2.get_y());

        g_x1 = -1 * this.mass * this.g * derivative_by_x_rk;
        g_y1 = -1 * this.mass * this.g * derivative_by_y_rk;

        gravitational_force_rk = new Force(g_x1, g_y1);

        scalar_rk = -1 * this.coefficient_of_friction * this.g * this.mass * (1 / K2.get_magnitude());

        friction_force_rk = new Force(
                scalar_rk * K2.get_x(),
                scalar_rk * K2.get_y());

        sum_of_forces_x_rk = gravitational_force_rk.get_x() + friction_force_rk.get_x();
        sum_of_forces_y_rk = gravitational_force_rk.get_y() + friction_force_rk.get_y();

        acceleration_rk = new Vector2D(sum_of_forces_x_rk * (1 / this.mass), sum_of_forces_y_rk * (1 / this.mass));
        velocity_x_rk = this.velocity_vector.get_x() + time_step + (time_step / 2) * this.acceleration_vector.get_x();
        velocity_y_rk = this.velocity_vector.get_y() + time_step + (time_step / 2) * this.acceleration_vector.get_y();

        Vector2D K3 = new Vector2D(velocity_x_rk, velocity_y_rk);

        //Find sum of forces and velocity for K4 (K4 = f(Xn+h*k3, Tn + h))

        derivative_by_x_rk = derivative_by_x(course.getObjects().get(0).position.x + time_step * K3.get_x(), course.getObjects().get(0).position.y + time_step * K3.get_y());
        derivative_by_y_rk = derivative_by_y(course.getObjects().get(0).position.x + time_step * K3.get_x(), course.getObjects().get(0).position.y + time_step * K3.get_y());

        g_x1 = -1 * this.mass * this.g * derivative_by_x_rk;
        g_y1 = -1 * this.mass * this.g * derivative_by_y_rk;

        gravitational_force_rk = new Force(g_x1, g_y1);

        scalar_rk = -1 * this.coefficient_of_friction * this.g * this.mass * (1 / K3.get_magnitude());

        friction_force_rk = new Force(
                scalar_rk * K3.get_x(),
                scalar_rk * K3.get_y());

        sum_of_forces_x_rk = gravitational_force_rk.get_x() + friction_force_rk.get_x();
        sum_of_forces_y_rk = gravitational_force_rk.get_y() + friction_force_rk.get_y();

        acceleration_rk.set_x(sum_of_forces_x_rk * (1 / this.mass));
        acceleration_rk.set_y(sum_of_forces_y_rk * (1 / this.mass));
        velocity_x_rk = this.velocity_vector.get_x() + time_step + (time_step / 2) * this.acceleration_vector.get_x();
        velocity_y_rk = this.velocity_vector.get_y() + time_step + (time_step / 2) * this.acceleration_vector.get_y();

        Vector2D K4 = new Vector2D(velocity_x_rk, velocity_y_rk);

        Vector2D result_vector = new Vector2D(K1.get_x() + 2 * K2.get_x() + 2 * K3.get_x() + K4.get_x(), K1.get_y() + 2 * K2.get_y() + 2 * K3.get_y() + K4.get_y());

        return result_vector;
    }

    private void update_position(Vector2D result_vector) {

        double position_x = course.getObjects().get(0).position.x + (time_step / 6) * result_vector.get_x();
        double position_y = course.getObjects().get(0).position.y + (time_step / 6) * result_vector.get_y();
        double position_z = course.get_height().evaluate(new Vector2D(position_x, position_y));

        course.getObjects().get(0).old_position.x = (course.getObjects().get(0).position.x);
        course.getObjects().get(0).old_position.y = (course.getObjects().get(0).position.y);
        course.getObjects().get(0).old_position.z = (course.getObjects().get(0).position.z);

        course.getObjects().get(0).position.x = (float) (position_x);
        course.getObjects().get(0).position.y = (float) (position_y);
        course.getObjects().get(0).position.z = (float) (position_z);
    }

    private void update_velocity(Vector2D result_vector) {
        course.getObjects().get(0).velocity = result_vector;
    }
}/*

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

        // Velocity vector
        Vector3 f = this.force(obj, c, h);

        response[1] = new Vector3(
                (float) (obj.velocity.x + (h * f.x) / obj.mass),
                (float) (obj.velocity.y + (h * f.y) / obj.mass),
                (float) (obj.velocity.z + (h * f.z) / obj.mass)
        );
        response[2] = f;
        return response;
    }*/
