package Screens;

import Physics.PhysicsEngine;
import Physics.Vector2D;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

import java.io.*;


public class World {
    public static boolean inMove = false;
    public TheCourse course;
    public PhysicsEngine engine;
    public static boolean started = false;
    public static boolean justStarted = false;
    Game game;
    int shots = 0;

    public World(TheCourse course, PhysicsEngine engine) {
        this.course = course;
        this.engine = engine;
    }

    public void step(double h) {
        this.engine.solve(this.course.objects.get(0), this.course, h);
        justStarted = false;
        if (!isInMove()) {
            if (checkIfCompleted())
                ((Game) Gdx.app.getApplicationListener()).setScreen(new Win(game));
            //System.out.println("lol");
            started = false;
        }
    }

    public void takeShot(Vector2D v) {
        inMove = true;
        this.course.getObjects().get(0).velocity.add(v);
        justStarted = true;
        started = true;
    }


    public boolean checkIfCompleted() {

        // If ball within tolerance of finish flag
        // and ball is not moving return true
        return this.course.get_flag_position().dst(new Vector2D(this.course.getBall().position.x, this.course.getBall().position.y)) <= this.course.get_hole_tolerance();
    }

    public Vector3 getBallPosition() {
        return this.course.getObjects().get(0).position;
    }

    public void setBallPosition(Vector2D p) {
        this.course.getObjects().get(0).position.x = (float) p.x;
        this.course.getObjects().get(0).position.y = (float) p.y;
        this.course.getObjects().get(0).position.z = (float) this.course.get_height().evaluate(p);
    }

    public boolean isInWater() {
        if (this.course.getObjects().get(0).position.z < 0) {
            setBallPosition(course.get_start_position());
            return true;
        }
        return false;
    }

    public boolean isOutOfBorder() {
        if (this.course.getObjects().get(0).position.x > Play.borderSize || this.course.getObjects().get(0).position.x < -Play.borderSize || this.course.getObjects().get(0).position.y > Play.borderSize || this.course.getObjects().get(0).position.y < -Play.borderSize) {
            setBallPosition(course.get_start_position());
            return true;
        }
        return false;
    }

    public boolean isInMove() {
        if (isInWater() || isOutOfBorder()) {
            inMove = false;
            this.course.getObjects().get(0).velocity = new Vector2D(0, 0);
        }
        return inMove;
    }

    public boolean saveCourse(String filePath) {
        try {
            FileOutputStream f = new FileOutputStream(filePath);
            ObjectOutputStream outputStream = new ObjectOutputStream(f);
            outputStream.writeObject(course);
            outputStream.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean loadCourse(String filePath) {
        try {
            FileInputStream f = new FileInputStream(filePath);
            ObjectInputStream inputStream = new ObjectInputStream(f);
            course = (TheCourse) (inputStream.readObject());
            inputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
        return true;
    }
}
