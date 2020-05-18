package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CourseInput implements Screen {

    private TextField gravityField;
    private TextField massField;
    private TextField frictionField;
    private TextField iniSpeedField;
    private TextField winAreaField;
    private TextField startField;
    private TextField goalField;
    private TextField heightField;
    private TextField obstacleField;
    public static String gamePhysics = "rk4";

    private TextButton buttonPhysicsV;
    private TextButton buttonPhysicsH;
    private TextButton buttonPhysicsRK;
    private TextButton buttonPhysicsE;
    private SelectBox<String> selectBox;

    private Game game;
    private Stage stage;

    public CourseInput(Game g) {

        game = g;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        final TextButton btnCreate = new TextButton("Create", skin);
        btnCreate.setPosition(Gdx.graphics.getWidth() / 2 - 300, 250);
        btnCreate.setSize(250, 60);
        btnCreate.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button) {
                btnCreateClicked(TheCourse.getCourseAmount() + 1);
            }
        });

        final TextButton btnSave = new TextButton("Save", skin);
        btnSave.setPosition(Gdx.graphics.getWidth() / 2, 250);
        btnSave.setSize(250, 60);
        btnSave.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button) {
                btnCreateClicked(selectBox.getSelectedIndex());
            }
        });


        buttonPhysicsV = new TextButton("Verlet", skin, "toggle");
        buttonPhysicsV.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gamePhysics = "verlet";
            }
        });
/*
        buttonPhysicsH = new TextButton("Heun's3", skin,"toggle");
        buttonPhysicsH.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new Heuns3();
            }
        });
*/
        buttonPhysicsRK = new TextButton("RK4", skin, "toggle");
        buttonPhysicsRK.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gamePhysics = "rk4";
            }
        });

        buttonPhysicsE = new TextButton("Euler", skin, "toggle");
        buttonPhysicsE.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gamePhysics = "euler";
            }
        });

        ButtonGroup buttonGroupPhysics = new ButtonGroup(buttonPhysicsE, buttonPhysicsV, buttonPhysicsRK);
//next set the max and min amount to be checked
        buttonGroupPhysics.setMaxCheckCount(1);
        buttonGroupPhysics.setMinCheckCount(1);
        buttonGroupPhysics.setChecked("RK4");


        Table tablePhysics = new Table();
        tablePhysics.setWidth(stage.getWidth());
        //  tableDimensions.align(Align.center|Align.top);
        tablePhysics.setPosition(0, Gdx.graphics.getHeight() - 200);
        tablePhysics.row();
        tablePhysics.add(buttonPhysicsE).size(100, 50);
        tablePhysics.add(buttonPhysicsV).size(100, 50);
        tablePhysics.add(buttonPhysicsRK).size(100, 50);
        stage.addActor(tablePhysics);


        gravityField = new TextField("", skin);
        gravityField.setPosition(Gdx.graphics.getWidth() / 2 - 125, 700);
        gravityField.setSize(250, 40);
        gravityField.setMessageText("gravity");
        stage.addActor(gravityField);

        massField = new TextField("", skin);
        massField.setPosition(Gdx.graphics.getWidth() / 2 - 125, 650);
        massField.setSize(250, 40);
        massField.setMessageText("mass");
        stage.addActor(massField);

        frictionField = new TextField("", skin);
        frictionField.setPosition(Gdx.graphics.getWidth() / 2 - 125, 600);
        frictionField.setSize(250, 40);
        frictionField.setMessageText("friction");
        stage.addActor(frictionField);

        iniSpeedField = new TextField("", skin);
        iniSpeedField.setPosition(Gdx.graphics.getWidth() / 2 - 125, 550);
        iniSpeedField.setSize(250, 40);
        iniSpeedField.setMessageText("initial speed");
        stage.addActor(iniSpeedField);

        winAreaField = new TextField("", skin);
        winAreaField.setPosition(Gdx.graphics.getWidth() / 2 - 125, 500);
        winAreaField.setSize(250, 40);
        winAreaField.setMessageText("win area");
        stage.addActor(winAreaField);

        startField = new TextField("", skin);
        startField.setPosition(Gdx.graphics.getWidth() / 2 - 125, 450);
        startField.setSize(250, 40);
        startField.setMessageText("start");
        stage.addActor(startField);

        goalField = new TextField("", skin);
        goalField.setPosition(Gdx.graphics.getWidth() / 2 - 125, 400);
        goalField.setSize(250, 40);
        goalField.setMessageText("goal");
        stage.addActor(goalField);

        heightField = new TextField("", skin);
        heightField.setPosition(Gdx.graphics.getWidth() / 2 - 125, 350);
        heightField.setSize(250, 40);
        heightField.setMessageText("formula");
        stage.addActor(heightField);
/*
        obstacleField = new TextField("",skin);
        obstacleField.setPosition(300,750);
        obstacleField.setSize(300, 40);
        obstacleField.setMessageText("obstacle x y x y x y");
        stage.addActor(obstacleField);
*/


        /*
            Set up the drop-down menu (select box).
         */

        selectBox = new SelectBox<String>(skin);
        selectBox.setPosition(300, Gdx.graphics.getHeight() * 0.9f - 130);
        Vector2 selectBoxSize = new Vector2(200, 50);
        selectBox.setSize(selectBoxSize.x, selectBoxSize.y);

        String[] boxItems = new String[TheCourse.getCourseAmount()];
        for (int i = 0; i < TheCourse.getCourseAmount(); i++) {
            boxItems[i] = "Course " + i;
        }
        selectBox.setItems(boxItems);

        /*
            Listener that triggers action if different option is chosen in select box.
         */
        selectBox.addListener(new EventListener() {
                                  @Override
                                  public boolean handle(Event event) {
                                      if (event instanceof ChangeListener.ChangeEvent) {
                                          updateCourseInfo();
                                      }
                                      return true;
                                  }
                              }
        );
        stage.addActor(btnSave);
        stage.addActor(selectBox);
        stage.addActor(btnCreate);
    }


    public void btnCreateClicked(int i) {
        try {
            TheCourse.activeI = i;


            File tmpDir = new File("course" + i + ".txt");
            FileWriter myWriter = new FileWriter(tmpDir);

            if (!gravityField.getText().isEmpty())
                myWriter.write(gravityField.getText() + "\n");
            else
                myWriter.write("9.81" + "\n");
            if (!massField.getText().isEmpty())
                myWriter.write(massField.getText() + "\n");
            else
                myWriter.write("45.93" + "\n");
            if (!frictionField.getText().isEmpty())
                myWriter.write(frictionField.getText() + "\n");
            else
                myWriter.write("0.131" + "\n");
            if (!iniSpeedField.getText().isEmpty())
                myWriter.write(iniSpeedField.getText() + "\n");
            else
                myWriter.write("3" + "\n");
            if (!winAreaField.getText().isEmpty())
                myWriter.write(winAreaField.getText() + "\n");
            else
                myWriter.write("0.02" + "\n");
            if (!startField.getText().isEmpty())
                myWriter.write(startField.getText() + "\n");
            else
                myWriter.write("0.0 0.0" + "\n");
            if (!goalField.getText().isEmpty())
                myWriter.write(goalField.getText() + "\n");
            else
                myWriter.write("0.0 10.0" + "\n");
            if (!heightField.getText().isEmpty())
                myWriter.write(heightField.getText());
            else
                myWriter.write("-0.01*x + 0.003*x^2 + 0.04 * y" + "\n");
            myWriter.close();


            dispose();
            game.setScreen(new Play(game));


        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    public void updateCourseInfo() {
        TheCourse.activeI = selectBox.getSelectedIndex();
        TheCourse updater = new TheCourse();
        gravityField.setText(Double.toString(updater.getGravity()));
        massField.setText(Double.toString(updater.getMass()));
        frictionField.setText(Double.toString(updater.getFriction()));
        iniSpeedField.setText(Double.toString(updater.get_maximum_velocity()));
        winAreaField.setText(Double.toString(updater.get_hole_tolerance()));
        startField.setText((updater.get_start_position().x) + " " + updater.get_start_position().y);
        goalField.setText((updater.get_flag_position().x) + " " + updater.get_flag_position().y);
        heightField.setText(updater.getHeightFun());
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
