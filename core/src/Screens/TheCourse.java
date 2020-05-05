package Screens;

import Other.*;
import Physics.Vector2D;
import com.badlogic.gdx.math.Vector3;

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
    public List<GameObject> objects = new ArrayList<>();

    public TheCourse(String courseName) {
//        game = g;

        if (courseName.equals(""))
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
            int i = 0, j = 0;/*
            while (myReader.hasNextDouble()) {
                obstacle[i][j] = myReader.nextDouble();
                if (j == 1) {
                    j--;
                    i++;
                } else j++;
            }*/


            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();

        }

        if (this.objects.size() == 0) {
            this.objects.add(new Ball());
            this.objects.add(new Hole());
        }

        this.objects.get(0).position = new Vector3((float) start[0], (float) start[1], (float) this.get_height().evaluate(get_start_position()));
        this.objects.get(0).old_position = new Vector3((float) start[0], (float) start[1], (float) this.get_height().evaluate(get_start_position()));

        this.objects.get(1).position = new Vector3((float) goal[0], (float) goal[1], (float) this.get_height().evaluate(get_flag_position()));
    }


    public Function2d get_height() {
        return new HeightSolver(heightFun);
    }

    public double heightEval(double x, double y) {
        return get_height().evaluate(new Vector2D(x, y));
    }

    public Vector2D get_flag_position() {
        return new Vector2D(goal[0], goal[1]);
    }

    public Vector2D get_start_position() {
        return new Vector2D(start[0], start[1]);
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

    public double getMass() {
        return mass;
    }


    public Vector2D[] getObstacle() {
        Vector2D[] v1 = new Vector2D[obstacle.length];
        for (int i = 0; i < obstacle.length; i++) {
            v1[i] = new Vector2D(obstacle[i][0], obstacle[i][1]);
        }
        return v1;
    }


    public GameObject getBall() {
        return objects.get(0);
    }

    public List<GameObject> getObjects() {
        return objects;
    }

}


