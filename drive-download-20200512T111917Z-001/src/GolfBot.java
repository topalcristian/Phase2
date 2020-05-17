import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GolfBot {
    private Vector2D start;
    private Vector2D goal;
    private double winArea;
    private Ball testBall;
    private IndividualHit[] hitPopulation;
    private double maxVelocity;
    private double time_step;
    private int hitCounter;
    private double mutationRate = .5;
    private int populationSize = 500;

    public GolfBot(Vector2D start, Vector2D goal, double winArea, Ball testBall,double maxVelocity){
        this.start = start;
        this.goal = goal;
        this.winArea = winArea;
        this.testBall = testBall;
        this.maxVelocity = maxVelocity;
        this.time_step = .1;
        hitCounter = 0;
    }
    public IndividualHit run(){
        makeFirstPopulation(populationSize);
        printPopulation();
        for(int i=0; i<hitPopulation.length; i++){
            updateFitness(hitPopulation[i]);
        }
        long startTime = System.currentTimeMillis();
        while(!checkForWin()){
            //calculate fitness for the entire population

            System.out.println("best distance: " + getBestIndividual(hitPopulation).getFitness());
            IndividualHit[] nextGen = new IndividualHit[populationSize];
            IndividualHit dad = null;
            IndividualHit mom = null;
            for(int i = 0; i<populationSize; i++) {
              /*select two different parents from the population through rank selection.
                int j=0;
                while(j<1){
                    dad = rankSelection();
                    mom = rankSelection();
                    if(!dad.equals(mom)){
                        j++;
                    }
                }*/
                //select two parents through elitist selection
                HeapSort.sort(hitPopulation);
                dad = hitPopulation[0];
                mom = hitPopulation[1];

                //crossover between the two parents
                nextGen[i] = crossOver(dad, mom);
                //the two velocity vectors have a chance to change at the mutationRate
                if (Math.random() < .5) {
                    mutationSlightChange(nextGen[i]);
                } else { mutationRandomNumber(nextGen[i]);
                }
            }
            //update the current hitPopulation to the next generation:
            hitPopulation = nextGen;

            for(int j=0; j<hitPopulation.length; j++){
                updateFitness(hitPopulation[j]);
            }
            if(System.currentTimeMillis()-startTime>=6000000){ break;}

        }
        return getBestIndividual(hitPopulation);
    }

    //simulate a hit and return the position of the ball post hit.
    public Vector3D simulate(double velocity_vector_x, double velocity_vector_y) {
        testBall.setPosition(start.get_x(),start.get_y());
        testBall.hit(velocity_vector_x, velocity_vector_y, time_step);
        Vector3D position = testBall.getPosition();
        /*System.out.println(testBall.getPosition().get_x());
        System.out.println(testBall.getPosition().get_y());
        System.out.println(testBall.getPosition().get_z());*/
        return position;
    }

    //create the first population of random hits and check their fitness, size determines how many individuals per population
    public void makeFirstPopulation(int size){
        hitPopulation = new IndividualHit[size];
        for(int i=0; i<size; i++){
            IndividualHit hit = new IndividualHit(ThreadLocalRandom.current().nextDouble((maxVelocity*-1), maxVelocity),ThreadLocalRandom.current().nextDouble((maxVelocity*-1), maxVelocity));
            hitPopulation[i]=hit;
            updateFitness(hit);
        }
    }
    public IndividualHit getBestIndividual(IndividualHit[] selectFrom){
        IndividualHit best = selectFrom[0];
        for(int i = 0; i<selectFrom.length; i++){
            if(selectFrom[i].getFitness()<best.getFitness()){
                best = selectFrom[i];
            }
        }
        return best;
    }

    public void setHitPopulation(IndividualHit[] hitPopulation){this.hitPopulation = hitPopulation;}
    public IndividualHit[] getHitPopulation(){
        return hitPopulation;
    }

    public void setTimeStep(double time_step){
    this.time_step = time_step;
    }
    //a way to update the fitness of an individual
    public void updateFitness(IndividualHit hit){
        Vector3D hitResult = simulate(hit.get_x(),hit.get_y());
        double xDistanceFromGoal = goal.get_x()-hitResult.get_x();
        double yDistanceFromGoal = goal.get_y()-hitResult.get_y();
        double absoluteDistanceFromGoal =  Math.sqrt(Math.pow(xDistanceFromGoal,2)+Math.pow(yDistanceFromGoal,2));
        hit.setFitness(absoluteDistanceFromGoal);
    }

    public void printPopulation(){
        for(int i=0;i<hitPopulation.length;i++){
            System.out.println("x = " + hitPopulation[i].get_x() + " y = " + hitPopulation[i].get_y());
        }
    }
    public IndividualHit rankSelection(){
        HeapSort.sort(hitPopulation);
        int totalFitness = ((hitPopulation.length)*hitPopulation.length-1)/2; //calculate sum of the total ranks. N + N-1 + N-2 + N-3. N = the number of individual in pop
        for(int i = 0; i<hitPopulation.length; i++){ //Give each individual a fitness dependant on how they rank in the pop
            hitPopulation[i].setFitness((hitPopulation.length)-i);
        }
        return rouletteSelection(totalFitness); //return a roulette selection for the now sorted population
    }
    public IndividualHit rouletteSelection(int totalFitness){
        Random r = new Random();
        int rouletteNumber = r.nextInt(totalFitness);
        int fitnessSum = 0;
        for(int i = 0; i<hitPopulation.length; i++){
            fitnessSum += hitPopulation[i].getFitness();
            if(rouletteNumber<fitnessSum){
                return hitPopulation[i];
            }
        }
        System.out.println("rouletteSelection failed");
        return hitPopulation[0];
    }

    //crossover happens between the individuals as they reproduce x and y values inbetween the two of them
    public IndividualHit crossOver(IndividualHit dad, IndividualHit mom){
        double bigX, smallX, bigY, smallY;
        if(dad.get_x()>mom.get_x()){
            bigX = dad.get_x();
            smallX = mom.get_x();
        }
        else{
            bigX = mom.get_x();
            smallX = dad.get_x();
        }
        if(dad.get_y()>mom.get_y()){
            bigY = dad.get_y();
            smallY = mom.get_y();
        }
        else{
            bigY = mom.get_y();
            smallY = dad.get_y();
        }
        IndividualHit child;
        if(smallX!=bigX&&smallY!=bigY) {
            child = new IndividualHit(ThreadLocalRandom.current().nextDouble(smallX, bigX), ThreadLocalRandom.current().nextDouble(smallY, bigY));
        }
        else if(smallX!=bigX){
            child = new IndividualHit(ThreadLocalRandom.current().nextDouble(smallX, bigX), smallY);
        }
        else if(smallY!=bigY){
            child = new IndividualHit(smallX, ThreadLocalRandom.current().nextDouble(smallY, bigY));
        }
        else{
            child = new IndividualHit(smallX, smallY);
        }
     return child;
    }

    //at the rate of the mutation, the x and y value has a chance of being a random value.
    public void mutationRandomNumber(IndividualHit hit){
    if(Math.random()<mutationRate){
        hit.set_x(ThreadLocalRandom.current().nextDouble((maxVelocity*-1), maxVelocity));
    }
    if(Math.random()<mutationRate){
        hit.set_y(ThreadLocalRandom.current().nextDouble((maxVelocity*-1), maxVelocity));
    }
    }
    public void mutationSlightChange(IndividualHit hit){
        if (Math.random() < mutationRate) {
            hit.set_x(hit.get_x()+ThreadLocalRandom.current().nextDouble(-1,1));
        }
        if (Math.random() < mutationRate) {
            hit.set_y(hit.get_y()+ThreadLocalRandom.current().nextDouble(-1,1));
        }
    }
    public boolean checkForWin(){
        if(getBestIndividual(hitPopulation).getFitness() <= winArea) {
            return true;
        }
        return false;
    }
}
