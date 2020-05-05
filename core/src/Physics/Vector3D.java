package Physics;

import com.badlogic.gdx.math.Vector3;

public class Vector3D {
    double x;
    double y;
    double z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double get_magnitude() {
        return (Math.sqrt(this.get_x() * this.get_x() + this.get_y() * this.get_y() + this.get_z() * this.get_z()));
    }

    public boolean isEqual(Vector3D v) {
        double THRESHOLD = 0.0000000000000001;
        return Math.abs(this.x - v.x) < THRESHOLD && Math.abs(this.y - v.y) < THRESHOLD && Math.abs(this.z - v.z) < THRESHOLD;
    }

    public void add(final Vector3 vector) {
        add(vector.x, vector.y, vector.z);
    }


    public void add(float x, float y, float z) {
        set_x(this.x + x);
        set_y(this.y + y);
        set_z(this.z + z);
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

    public double get_z() {
        return this.z;
    }

    public void set_z(double z) {
        this.z = z;
    }


}
