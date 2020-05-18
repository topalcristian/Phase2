package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class MainMenu implements Screen {
    private TextButton buttonPhysicsV;
    private TextButton buttonPhysicsH;
    private TextButton buttonPhysicsRK;
    private TextButton buttonPhysicsE;
    private Stage stage;
    private Skin skin;
    private Table table;
    private Game game;
    private TextButton botButtton;

    public MainMenu(Game game) {
        this.game = game;
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {

        table.invalidateHierarchy();
    }


    @Override
    public void show() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));//skin3 =new Skin(Gdx.files.internal("menuSkin.json"), new TextureAtlas("atlas.pack"));

        table = new Table(skin);
        table.setFillParent(true);
        table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("background.jpg"))));

        // creating heading
        Skin skin3 = new Skin(Gdx.files.internal("menuSkin.json"), new TextureAtlas("atlas.pack"));
        Label heading = new Label("Crazy Golf", skin3);
        heading.setFontScale(2);


        //Bot button
        Skin skin2 = new Skin(Gdx.files.internal("uiskin.json"));
        botButtton = new TextButton("Bot", skin2, "toggle");
        botButtton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Play.Bot = true;
            }
        });
        botButtton.setSize(100, 50);
        botButtton.setPosition(50, 50);


        buttonPhysicsV = new TextButton("Verlet", skin2, "toggle");
        buttonPhysicsV.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Play.gamePhysics = "verlet";
            }
        });

        buttonPhysicsRK = new TextButton("RK4", skin2, "toggle");
        buttonPhysicsRK.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Play.gamePhysics = "rk4";
            }
        });

        buttonPhysicsE = new TextButton("Euler", skin2, "toggle");
        buttonPhysicsE.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Play.gamePhysics = "euler";
            }
        });

        ButtonGroup buttonGroupPhysics = new ButtonGroup(buttonPhysicsE, buttonPhysicsV, buttonPhysicsRK);
        //next set the max and min amount to be checked
        buttonGroupPhysics.setMaxCheckCount(1);
        buttonGroupPhysics.setMinCheckCount(1);
        buttonGroupPhysics.setChecked("RK4");


        Table tablePhysics = new Table();
        tablePhysics.setWidth(stage.getWidth());

        tablePhysics.add(buttonPhysicsE).size(100, 50);
        tablePhysics.add(buttonPhysicsV).size(100, 50);
        tablePhysics.add(buttonPhysicsRK).size(100, 50);


        // creating buttons
        TextButton buttonPlay = new TextButton("PLAY", skin);
        buttonPlay.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(sequence(moveTo(0, -stage.getHeight(), .5f), run(new Runnable() {

                    @Override
                    public void run() {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new Play(game));
                    }
                })));
            }
        });
        buttonPlay.pad(15);

        TextButton buttonSettings = new TextButton("SETTINGS", skin);
        buttonSettings.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(sequence(moveTo(0, -stage.getHeight(), .5f), run(new Runnable() {

                    @Override
                    public void run() {
                        ((Game) Gdx.app.getApplicationListener()).setScreen(new CourseInput(game));
                    }
                })));
            }
        });
        buttonSettings.pad(15);

        TextButton buttonExit = new TextButton("EXIT", skin);
        buttonExit.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        buttonExit.pad(15);

        // putting stuff together
        table.add(heading).spaceBottom(50).row();
        table.add(buttonPlay).spaceBottom(15).row();
        table.add(buttonSettings).spaceBottom(15).row();
        table.add(botButtton).spaceBottom(15).row();
        table.add(tablePhysics).spaceBottom(15).row();
        table.add(buttonExit);

        stage.addActor(table);


    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

}
