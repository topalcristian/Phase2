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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static Screens.Play.theSimulation;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


public class Win implements Screen {


    private Stage stage;
    private Skin skin;
    private Table table;
    private Game game;


    public Win(Game game) {
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

    Texture texture;
    TextureRegion backgroundTexture;

    @Override
    public void show() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("menuSkin.json"), new TextureAtlas("atlas.pack"));

        table = new Table(skin);
        table.setFillParent(true);
        table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("youwin.jpg"))));
        // creating heading

        // creating buttons
        TextButton buttonPlay = new TextButton("PLAY AGAIN", skin);
        buttonPlay.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(sequence(moveTo(0, -stage.getHeight(), .5f), run(new Runnable() {

                    @Override
                    public void run() {
                        theSimulation.completed = false;
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

        table.add(buttonPlay).spaceBottom(200).row();
        table.add(buttonSettings).spaceBottom(15).row();
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
