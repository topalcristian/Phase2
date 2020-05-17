public class Function3D
{

    private static final double approximate_limit = Math.pow(10,-10);

    public Function3D()
    {
    }

    public double apply(double x, double y)
    {
        return (
                50/(x*x+y*y+10)
                );
    }


    public double derivative_by_x(double x, double y)
    {
        return ((this.apply(x+approximate_limit, y)-this.apply(x,y))/approximate_limit);
    }

    public double derivative_by_y(double x, double y)
    {
        return ((this.apply(x, y+approximate_limit)-this.apply(x,y))/approximate_limit);
    }

}
