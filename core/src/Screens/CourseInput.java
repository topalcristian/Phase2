package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
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


    private Game game;
    private Stage stage;

    public CourseInput(Game g) {

        game = g;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        final TextButton btnCreate = new TextButton("Click", skin);
        btnCreate.setPosition(300, 250);
        btnCreate.setSize(300, 60);
        btnCreate.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent e, float x, float y, int point, int button) {
                btnCreateClicked();
            }
        });


        gravityField = new TextField("", skin);
        gravityField.setPosition(300, 700);
        gravityField.setSize(300, 40);
        gravityField.setMessageText("gravity");
        stage.addActor(gravityField);

        massField = new TextField("", skin);
        massField.setPosition(300, 650);
        massField.setSize(300, 40);
        massField.setMessageText("mass");
        stage.addActor(massField);

        frictionField = new TextField("", skin);
        frictionField.setPosition(300, 600);
        frictionField.setSize(300, 40);
        frictionField.setMessageText("friction");
        stage.addActor(frictionField);

        iniSpeedField = new TextField("", skin);
        iniSpeedField.setPosition(300, 550);
        iniSpeedField.setSize(300, 40);
        iniSpeedField.setMessageText("initial speed");
        stage.addActor(iniSpeedField);

        winAreaField = new TextField("", skin);
        winAreaField.setPosition(300, 500);
        winAreaField.setSize(300, 40);
        winAreaField.setMessageText("win area");
        stage.addActor(winAreaField);

        startField = new TextField("", skin);
        startField.setPosition(300, 450);
        startField.setSize(300, 40);
        startField.setMessageText("start");
        stage.addActor(startField);

        goalField = new TextField("", skin);
        goalField.setPosition(300, 400);
        goalField.setSize(300, 40);
        goalField.setMessageText("goal");
        stage.addActor(goalField);

        heightField = new TextField("", skin);
        heightField.setPosition(300, 350);
        heightField.setSize(300, 40);
        heightField.setMessageText("formula");
        stage.addActor(heightField);
/*
        obstacleField = new TextField("",skin);
        obstacleField.setPosition(300,750);
        obstacleField.setSize(300, 40);
        obstacleField.setMessageText("obstacle x y x y x y");
        stage.addActor(obstacleField);
*/
        stage.addActor(btnCreate);
    }


    public void btnCreateClicked() {
        try {
            int i = 1;
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
