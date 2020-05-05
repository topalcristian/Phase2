package Physics;

import Other.GameObject;
import Screens.TheCourse;
import Screens.World;

public class Verlet implements PhysicsEngine {/*
    private final Vector3D position;
    private final Vector3D old_position;
    private final Vector2D velocity_vector;
    private final Vector2D acceleration_vector;
    private final Vector2D old_acceleration_vector;
*/


    private static final double approximate_limit = Math.pow(10, -10);
    private final Force gravitational_force = new Force(0, 0);
    private final Force friction_force = new Force(0, 0);
    TheCourse course;
    private double mass;
    private double g;
    private double coefficient_of_friction;
    private double time_step;

    /*
        public void hit(double velocity_vector_x, double velocity_vector_y, double time_step)
        {
            this.time_step = time_step;

            this.velocity_vector.x= (float)(velocity_vector_x);
            this.velocity_vector.y= (float)(velocity_vector_y);

            calculate_previous_position_once();

            double timer = 0;

            while(course.getObjects().get(0).velocity.mnitude() >= 0.01 && timer <= 1000){

                update_position();
                timer++;

            }
        }
    */
    private void calculate_previous_position_once() {

        update_acceleration();

        double x_x = course.getObjects().get(0).position.x - course.getObjects().get(0).velocity.x * time_step + 0.5 * course.getObjects().get(0).acceleration.x * (time_step * time_step);
        double x_y = course.getObjects().get(0).position.y - course.getObjects().get(0).velocity.y * time_step + 0.5 * course.getObjects().get(0).acceleration.y * (time_step * time_step);
        double x_z = course.get_height().evaluate(new Vector2D(x_x, x_y));

        course.getObjects().get(0).old_position.x = (float) x_x;
        course.getObjects().get(0).old_position.y = (float) x_y;
        course.getObjects().get(0).old_position.z = (float) (x_z);

    }

    private void update_position() {

        double p_x = 2 * course.getObjects().get(0).position.x - course.getObjects().get(0).old_position.x + course.getObjects().get(0).acceleration.x * time_step * time_step;
        double p_y = 2 * course.getObjects().get(0).position.y - course.getObjects().get(0).old_position.y + course.getObjects().get(0).acceleration.y * time_step * time_step;
        double p_z = course.heightEval(p_x, p_y);

        course.getObjects().get(0).old_position.x = (course.getObjects().get(0).position.x);
        course.getObjects().get(0).old_position.y = (course.getObjects().get(0).position.y);
        course.getObjects().get(0).old_position.z = (course.getObjects().get(0).position.z);

        course.getObjects().get(0).position.x = (float) (p_x);
        course.getObjects().get(0).position.y = (float) (p_y);
        course.getObjects().get(0).position.z = (float) (p_z);

        update_velocity();
        update_acceleration();
    }

    public void update_velocity() {
        double v_x = course.getObjects().get(0).velocity.x + time_step * course.getObjects().get(0).acceleration.x;
        double v_y = course.getObjects().get(0).velocity.y + time_step * course.getObjects().get(0).acceleration.y;

        course.getObjects().get(0).velocity.x = (float) (v_x);
        course.getObjects().get(0).velocity.y = (float) (v_y);
    }

    public void update_acceleration() {
        /*gravitational force*/
        double derivative_by_x = this.derivative_by_x(course.getObjects().get(0).position.x, course.getObjects().get(0).position.y);
        double derivative_by_y = this.derivative_by_y(course.getObjects().get(0).position.x, course.getObjects().get(0).position.y);

        double g_x = -1 * this.mass * this.g * derivative_by_x;
        double g_y = -1 * this.mass * this.g * derivative_by_y;

        this.gravitational_force.x = (float) (g_x);
        this.gravitational_force.y = (float) (g_y);

        /*friction*/
        double scalar = (-1 * this.coefficient_of_friction * this.g * this.mass) / course.getObjects().get(0).velMagnitude();

        double f_x = scalar * course.getObjects().get(0).velocity.x;
        double f_y = scalar * course.getObjects().get(0).velocity.y;

        this.friction_force.x = (float) (f_x);
        this.friction_force.y = (float) (f_y);

        /*acceleration*/
        double sum_of_forces_x = this.gravitational_force.x + this.friction_force.x;
        double sum_of_forces_y = this.gravitational_force.y + this.friction_force.y;

        course.getObjects().get(0).old_acceleration_vector.x = (float) (course.getObjects().get(0).acceleration.x);
        course.getObjects().get(0).old_acceleration_vector.y = (float) (course.getObjects().get(0).acceleration.y);

        course.getObjects().get(0).acceleration.x = (float) (sum_of_forces_x / this.mass);
        course.getObjects().get(0).acceleration.y = (float) (sum_of_forces_y / this.mass);
    }


    public void setPosition(double x, double y) {
        course.getObjects().get(0).position.x = (float) (x);
        course.getObjects().get(0).position.y = (float) (y);
        course.getObjects().get(0).position.z = (float) (course.heightEval(x, y));
    }

    public void set_gravitational_constant(double new_g) {
        this.g = new_g;
    }

    public void set_coefficient_of_friction(double new_f) {
        this.coefficient_of_friction = new_f;
    }

    public void set_mass(double new_m) {
        this.mass = new_m;
    }

    @Override
    public void solve(GameObject obj, TheCourse c, double h) {
        if (World.started) {
            time_step = h;
            g = c.getGravity();
            mass = c.getMass();
            coefficient_of_friction = c.getFriction();
            course = c;
            if (World.justStarted)
                calculate_previous_position_once();
            update_position();
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
}

