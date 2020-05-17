public class main_class
{
    public static void main(String[] args)
    {
        Ball ball = new Ball( new Function3D());
        Vector2D start = new Vector2D(0,0);
        Vector2D goal = new Vector2D(1, 13);
        System.out.println(ball.getPosition().get_x());
        System.out.println(ball.getPosition().get_y());
        GolfBot bot = new GolfBot(start,goal,0.1,ball,20);
        IndividualHit solution = bot.run();
        System.out.println("solution fitness: " + solution.getFitness());
        ball.setPosition(0,0);
        ball.hit(solution.get_x(), solution.get_y(), 0.1);
        System.out.println(ball.getPosition().get_x());
        System.out.println(ball.getPosition().get_y());
        System.out.println(ball.getPosition().get_z());



        /*ball.setPosition(0,0);
        ball.hit(1,1, 0.1);

        //To get coordinates of position vector after hitting it, call:
           System.out.println(ball.getPosition().get_x());
           System.out.println(ball.getPosition().get_y());
           System.out.println(ball.getPosition().get_z());
         

        /* Same process for acceleration and velocity vector except that they don't have a z-coordinate  */


    }
}
