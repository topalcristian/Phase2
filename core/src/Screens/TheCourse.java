package Screens;

import Other.Ball;
import Other.Function2d;
import Other.HeightSolver;
import Physics.Vector2d;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TheCourse {

    private double gravity;
    private double mass;
    private double friction;
    private double iniSpeed;
    private double winArea;
    private double[] start = new double[2];
    private double[] goal = new double[2];
    private String heightFun;
    private double[][] obstacle;
    public List<Ball> balls = new ArrayList<Ball>();

    public TheCourse(String courseName) {
//        game = g;

        if (courseName == "")
            courseName = "course1.txt";
        try {
            File myObj = new File(courseName);
            Scanner myReader = new Scanner(myObj);
            gravity = Double.parseDouble(myReader.nextLine());
            mass = Double.parseDouble(myReader.nextLine());
            friction = Double.parseDouble(myReader.nextLine());
            iniSpeed = Double.parseDouble(myReader.nextLine());
            winArea = Double.parseDouble(myReader.nextLine());
            start[0] = myReader.nextDouble();
            start[1] = myReader.nextDouble();
            goal[0] = myReader.nextDouble();
            goal[1] = myReader.nextDouble();
            myReader.nextLine();
            heightFun = myReader.nextLine();
            int i = 0, j = 0;
            while (myReader.hasNextDouble()) {
                obstacle[i][j] = myReader.nextDouble();
                if (j == 1) {
                    j--;
                    i++;
                } else j++;
            }


            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();

        }

        if (this.balls.size() == 0) {
            this.balls.add(new Ball());
        }
    }


    public Function2d get_height() {
        Function2d H = new HeightSolver(heightFun);
        return H;
    }

    public Vector2d get_flag_position() {
        Vector2d v1 = new Vector2d(goal[0], goal[1]);
        return v1;
    }

    public Vector2d get_start_position() {
        Vector2d v1 = new Vector2d(start[0], start[1]);
        return v1;
    }

    public double get_friction_coefficient() {
        return friction;
    }

    public double get_maximum_velocity() {
        return iniSpeed;
    }

    public double get_hole_tolerance() {
        return winArea;
    }

    public double getFriction() {
        return friction;
    }

    public double getGravity() {
        return gravity;
    }

    public double getIniSpeed() {
        return iniSpeed;
    }

    public double getMass() {
        return mass;
    }


    public Vector2d[] getObstacle() {
        Vector2d[] v1 = new Vector2d[obstacle.length];
        for (int i = 0; i < obstacle.length; i++) {
            v1[i] = new Vector2d(obstacle[i][0], obstacle[i][1]);
        }
        return v1;
    }

    public boolean checkIfCompleted(Ball b) {

        // If ball within tolerance of finish flag
        // and ball is not moving return true
        if (this.get_flag_position().dst(new Vector2d(b.position.x, b.position.y)) <= get_hole_tolerance() && b.velocity.len() == 0)
            return true;
        return false;
    }

    public List<Ball> getBalls() {
        return balls;
    }

}


