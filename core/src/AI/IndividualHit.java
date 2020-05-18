package AI;

public class IndividualHit {
    private double velocity_vector_x;
    private double velocity_vector_y;
    private double fitness;

    public IndividualHit(double velocity_vector_x, double velocity_vector_y){
        this.velocity_vector_x = velocity_vector_x;
        this.velocity_vector_y = velocity_vector_y;
    }
    public void setFitness(double distanceFromGoal){
        fitness = distanceFromGoal;
    }
    public double getFitness(){
        return fitness;
    }
    public void set_x(double x){
        velocity_vector_x = x;
    }
    public void set_y(double y){
        velocity_vector_y = y;
    }
    public double get_x(){
        return velocity_vector_x;
    }
    public double get_y(){
        return velocity_vector_y;
    }
}
