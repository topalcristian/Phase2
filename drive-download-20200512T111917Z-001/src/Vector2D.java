public class Vector2D
{
    private double x;
    private double y;

    public Vector2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double get_magnitude()
    {
        return ( Math.sqrt( this.get_x()*this.get_x()+this.get_y()*this.get_y() ) );
    }

    public double get_x()
    {
        return this.x;
    }

    public double get_y()
    {
        return this.y;
    }

    public void set_x(double x)
    {
        this.x = x;
    }

    public void set_y(double y)
    {
        this.y = y;
    }

}
