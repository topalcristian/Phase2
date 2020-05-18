package AI;

import Physics.Vector2D;
import Screens.TheCourse;
import Screens.World;

//private put; //In play class
//put = new PuttingSimulator(course,PS); //In the Play constructor
//
////At the end of the render method (Play.java) this:
//if(put != null)
//{
//      //////////////////////
//      ///// TRY ////////////
//      //////////////////////
//      put.take_shot();
//      or
//      put.take_random_shot();
//}
//

public class PuttingSimulator {

    private final TheCourse course;
    private final World ball;
    private int distanceFromTo;
    private double x_direction;
    private double y_direction;
    private Vector2D startPos;
    private Vector2D flagPos;

    public PuttingSimulator(TheCourse course,World ball)
    {
        this.course = course;
        this.ball = ball;

        startPos = course.get_start_position();
        flagPos = course.get_flag_position();

        distanceFromTo = (int)Math.ceil( Math.sqrt(Math.pow(flagPos.get_x()-startPos.get_x(),2) + Math.pow(flagPos.get_y()-startPos.get_y(),2)) );
        x_direction = -distanceFromTo;
        y_direction = -distanceFromTo;
        System.out.println("The distance is " +distanceFromTo);

        take_shot();
        //take_random_shot();
    }

    public void take_shot()
    {
        if(!ball.isInMove())
        {
            if(!ball.checkIfCompleted())
            {
                ball.setBallPosition(startPos);
                Vector2D shootVector2D = new Vector2D(x_direction,y_direction);

                ball.takeShot(shootVector2D);
                updateDirection();
            }
        }
    }

    public void take_random_shot()
    {
        if(!ball.isInMove())
        {
            int randomX = (int)(Math.random()*(distanceFromTo - (-distanceFromTo) +1)) + (-distanceFromTo);
            int randomY = (int)(Math.random()*(distanceFromTo - (-distanceFromTo) +1)) + (-distanceFromTo);

            ball.setBallPosition(startPos);
            Vector2D shootVector2D = new Vector2D(randomX,randomY);
            ball.takeShot(shootVector2D);
        }
    }

    public void updateDirection()
    {
        if(x_direction <= distanceFromTo)
        {
            x_direction += 1;
        }
        else if(y_direction <= distanceFromTo)
        {
            y_direction += 1;
            x_direction = -distanceFromTo;
        }
        else
        {
            System.out.println("Tried every possible shot.");
        }
    }
}