public class Vector3D
{
    private double x;
    private double y;
    private double z;

    public Vector3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double get_magnitude()
    {
        return (Math.sqrt(this.get_x()*this.get_x()+this.get_y()*this.get_y()+this.get_z()*this.get_z()));
    }

    public boolean isEqual(Vector3D v)
    {
        double THRESHOLD = 0.0000000000000001;
        if(Math.abs(this.x - v.x) < THRESHOLD && Math.abs(this.y - v.y) < THRESHOLD && Math.abs(this.z - v.z) < THRESHOLD)
            return true;
        return false;
    }

    public double get_x()
    {
        return this.x;
    }

    public double get_y()
    {
        return this.y;
    }

    public double get_z()
    {
        return this.z;
    }

    public void set_x(double x)
    {
        this.x = x;
    }

    public void set_y(double y)
    {
        this.y = y;
    }

    public void set_z(double z)
    {
        this.z = z;
    }


}
