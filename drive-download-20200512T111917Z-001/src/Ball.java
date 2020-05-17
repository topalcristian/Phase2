
public class Ball {
    private final Vector3D position;
    private final Vector3D old_position;
    private final Vector2D velocity_vector;
    private final Vector2D acceleration_vector;
    private final Vector2D old_acceleration_vector;

    private double mass;
    private double g;
    private double coefficient_of_friction;
    private final Function3D function;

    private final Force gravitational_force;
    private final Force friction_force;

    private double time_step;

    public Ball( Function3D f)
    {
        this.mass = 0.04593;//in kilograms  //default
        this.g = 9.81;
        this.coefficient_of_friction = 0.31;
        this.function = f;
        this.gravitational_force = new Force(0,0);
        this.friction_force = new Force(0,0);

        this.position = new Vector3D(0, 0, function.apply(0, 0));
        this.old_position = new Vector3D(0, 0, function.apply(0, 0));
        this.velocity_vector = new Vector2D(0, 0);
        this.acceleration_vector = new Vector2D(0, 0);
        this.old_acceleration_vector = new Vector2D(0, 0);
    }


    public void hit(double velocity_vector_x, double velocity_vector_y, double time_step)
    {
        this.time_step = time_step;
        this.velocity_vector.set_x(velocity_vector_x);
        this.velocity_vector.set_y(velocity_vector_y);

        calculate_previous_position_once();

        while(this.velocity_vector.get_magnitude() >= 0.1){

            update_position();

        }
    }

    private void calculate_previous_position_once()
    {

        /**old position*/

        update_acceleration();

        double x_x = this.position.get_x() - this.velocity_vector.get_x()*time_step + 0.5*this.acceleration_vector.get_x()*(time_step*time_step);
        double x_y = this.position.get_y() - this.velocity_vector.get_y()*time_step + 0.5*this.acceleration_vector.get_y()*(time_step*time_step);
        double x_z = this.function.apply(x_x,x_y);

        this.old_position.set_x(x_x);
        this.old_position.set_y(x_y);
        this.old_position.set_z(x_z);

    }

    private void update_position()
    {
        /**position*/

        double p_x = 2*this.position.get_x() - this.old_position.get_x() + this.acceleration_vector.get_x()*time_step*time_step;
        double p_y = 2*this.position.get_y() - this.old_position.get_y() + this.acceleration_vector.get_y()*time_step*time_step;
        double p_z = this.function.apply(p_x,p_y);

        this.old_position.set_x(this.position.get_x());
        this.old_position.set_y(this.position.get_y());
        this.old_position.set_z(this.position.get_z());

        this.position.set_x(p_x);
        this.position.set_y(p_y);
        this.position.set_z(p_z);

        update_velocity();
        update_acceleration();
    }

    public void update_velocity()
    {
        /**velocity*/
        double v_x = this.velocity_vector.get_x() + time_step*this.acceleration_vector.get_x();
        double v_y = this.velocity_vector.get_y() + time_step*this.acceleration_vector.get_y();

        this.velocity_vector.set_x(v_x);
        this.velocity_vector.set_y(v_y);
    }

    public void update_acceleration()
    {
        /**gravitational force*/
        double derivative_by_x = this.function.derivative_by_x(this.position.get_x(), this.position.get_y());
        double derivative_by_y = this.function.derivative_by_y(this.position.get_x(), this.position.get_y());

        double g_x = -1*this.mass*this.g*derivative_by_x;
        double g_y = -1*this.mass*this.g*derivative_by_y;

        this.gravitational_force.set_x(g_x);
        this.gravitational_force.set_y(g_y);

        /**friction*/
        double scalar =( -1*this.coefficient_of_friction*this.g*this.mass ) / this.velocity_vector.get_magnitude();

        double f_x = scalar*this.velocity_vector.get_x();
        double f_y = scalar*this.velocity_vector.get_y();

        this.friction_force.set_x(f_x);
        this.friction_force.set_y(f_y);

        /**acceleration*/
        double sum_of_forces_x = this.gravitational_force.get_x()+this.friction_force.get_x();
        double sum_of_forces_y = this.gravitational_force.get_y()+this.friction_force.get_y();

        this.old_acceleration_vector.set_x(this.acceleration_vector.get_x());
        this.old_acceleration_vector.set_y(this.acceleration_vector.get_y());

        this.acceleration_vector.set_x(sum_of_forces_x/this.mass);
        this.acceleration_vector.set_y(sum_of_forces_y/this.mass);
    }


    public void setPosition(double x, double y)
    {
        this.position.set_x(x);
        this.position.set_y(y);
        this.position.set_z(function.apply(x,y));
    }
    public void set_gravitational_constant(double new_g) {this.g = new_g; }
    public void set_coefficient_of_friction(double new_f) {this.coefficient_of_friction = new_f; }
    public void set_mass(double new_m) {this.mass = new_m; }
    public Vector3D getPosition() {return this.position;}
    public Vector2D getVelocity() {return this.velocity_vector;}
    public Vector2D getAcceleration() {return this.acceleration_vector;}
}