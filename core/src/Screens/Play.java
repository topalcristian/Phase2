package Screens;

import AI.GolfBot;
import AI.IndividualHit;
import AI.PuttingSimulator;
import AI.TestBall;
import Other.TrackingCameraController;
import Physics.*;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;


public class Play implements Screen {
    public static boolean timePassed = false;
    public static int borderSize = 50;
    public static String gamePhysics = "rk4";
    public static boolean Bot = false;
    public static TheCourse course;
    final float terrainStepSize = 1;
    final int terrainWidth = 20;
    final int terrainLength = 15;
    //Position attribute - (x, y, z)
    final int POSITION_COMPONENTS = 3;
    //Color attribute - (r, g, b, a), but using Packed
    final int COLOR_COMPONENTS = 1;
    //Total number of components for all attributes
    final int NUM_COMPONENTS = POSITION_COMPONENTS + COLOR_COMPONENTS;
    //The "size" (total number of floats) for a single triangle
    final int PRIMITIVE_SIZE = 3 * NUM_COMPONENTS;
    //The maximum number of triangles our mesh will hold
    //Size of the terrain / stepSize = the amount of squares, *2=the amount of triangles
    final int MAX_TRIS = (int) ((terrainLength * terrainWidth) / terrainStepSize) * 2;
    //The maximum number of vertices our mesh will hold
    final int MAX_VERTS = MAX_TRIS * 3;
    //The array which holds all the vertices
    float[] terrainVertices = new float[MAX_VERTS * NUM_COMPONENTS];
    int terrainVertexIndex = 0;
    public static final String VERT_SHADER =
            "attribute vec3 a_position;\n" +
                    "attribute vec4 a_color;\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "varying vec4 vColor;\n" +
                    "void main() {\n" +
                    "	vColor = a_color;\n" +
                    "	gl_Position =  u_projTrans * vec4(a_position.xyz, 1.0);\n" +
                    "}";

    public static final String FRAG_SHADER =
            "#ifdef GL_ES\n" +
                    "precision mediump float;\n" +
                    "#endif\n" +
                    "varying vec4 vColor;\n" +
                    "void main() {\n" +
                    "	gl_FragColor = vColor;\n" +
                    "}";

    protected static ShaderProgram createMeshShader() {
        ShaderProgram.pedantic = false;
        ShaderProgram shader = new ShaderProgram(VERT_SHADER, FRAG_SHADER);
        String log = shader.getLog();
        if (!shader.isCompiled())
            throw new GdxRuntimeException(log);
        if (log != null && log.length() != 0)
            System.out.println("Shader Log: " + log);
        return shader;
    }


    public PerspectiveCamera cam;
    public static ArrayList<ModelInstance> instances = new ArrayList<>();

    public SpriteBatch batch = new SpriteBatch();
    public ModelBatch modelBatch = new ModelBatch();
    public static World theSimulation;
    public Environment env;
    public ModelBuilder modelBuilder = new ModelBuilder();
    public static PhysicsEngine engine = new Verlet();
    int time1 = 0;
    public TrackingCameraController camController;
    public Model golfBall;
    public Model hole;
    public Model sky;
    //public CameraInputController trackingCameraController;
    public ModelInstance ourGolfBall, goal, skybox;
    Mesh terrain;
    ShaderProgram terrainShader;
    Stage UIStage;
    Skin skin;
    private Label turnCount;
    private Label loading;
    ////////////////////////// FOR THE BOT /////////////////////////////
    private TestBall testBall;
    private PuttingSimulator put;
    private GolfBot bot1;
    private IndividualHit hit;
    ////////////////////////////////////////////////////////////////////

    public static int Width3DScreen;
    public static int Height3DScreen;
    public static int Width2DScreen;
    public static int Height2DScreen;
    public OrthographicCamera cam2D;
    public Game game;
    Stage fullScreenStage;
    private FitViewport hudViewport;
    private FitViewport dialogViewPort;
    private InputMultiplexer inputMain;

    Play(Game g) {
        course = new TheCourse();
        theSimulation = new World(course, engine);
        game = g;
        //PhysicsEngine
        switch (gamePhysics) {
            case "rk4":
                engine = new RungeKutta();
            case "euler":
                engine = new Euler();
            case "verlet":
                engine = new Verlet();

        }
        instances.clear();

        // Ball
        golfBall = modelBuilder.createSphere(1, 1, 1, 25, 25, new Material(ColorAttribute.createDiffuse(Color.WHITE)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ourGolfBall = new ModelInstance(golfBall, course.getObjects().get(0).position);

        // Hole
        hole = modelBuilder.createSphere((float) course.get_hole_tolerance() + 2, 3, 1, 25, 25, new Material(new BlendingAttribute((float) 0.8), new ColorAttribute(1, Color.BLACK)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        goal = new ModelInstance(hole, course.getObjects().get(1).position);

        //Skybox
        sky = modelBuilder.createSphere(1000, 1000, 1000, 25, 25, new Material(new BlendingAttribute((float) 0.8), new ColorAttribute(1, Color.BLUE)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        skybox = new ModelInstance(sky, course.getObjects().get(0).position);


        // Environment
        env = new Environment();
        env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.7f, 1f));
        env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0, -0.8f, 0));

        // Cam

        Width2DScreen = 300;
        Width3DScreen = Gdx.graphics.getWidth() - Width2DScreen;
        Height2DScreen = Height3DScreen = Gdx.graphics.getHeight();
        cam2D = new OrthographicCamera();
        hudViewport = new FitViewport(Width2DScreen, Height2DScreen, cam2D);
        cam2D.update();
        dialogViewPort = new FitViewport(Width3DScreen, Height3DScreen, cam2D);
        fullScreenStage = new Stage(dialogViewPort);

        cam = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        cam.position.set(course.getObjects().get(0).position.x, course.getObjects().get(0).position.y - 10, course.getObjects().get(0).position.z + 10);
        cam.lookAt(course.getObjects().get(0).position);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        camController = new TrackingCameraController(cam);
        camController.setTrackedVector(course.getObjects().get(0).position);
        Gdx.input.setInputProcessor(camController);

        //UI Stage
        UIStage = new Stage(dialogViewPort, batch);
        UIStage.getViewport().setScreenBounds(Width3DScreen, 0, Width2DScreen - 1, Height2DScreen - 1);
        UIStage.getViewport().apply();
        UIStage.setDebugAll(false);
        skin = new Skin(Gdx.files.internal("menuSkin.json"), new TextureAtlas("atlas.pack"));

        //Labels
        turnCount = new Label("Turns: " + theSimulation.shots, skin);
        turnCount.setSize(30, 20);
        turnCount.setPosition(10, Height2DScreen - 200);
        turnCount.setColor(Color.BROWN);
        UIStage.addActor(turnCount);

        loading = new Label("", skin);
        loading.setSize(30, 20);
        loading.setPosition(10, Height2DScreen - 95);
        loading.setColor(Color.BROWN);
        UIStage.addActor(loading);

        //Shader
        terrainShader = new ShaderProgram(VERT_SHADER, FRAG_SHADER);
        terrain = new Mesh(true, MAX_VERTS, 0, new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, "a_position"), new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));
        instances.add(ourGolfBall);
        instances.add(goal);
        instances.add(skybox);

        ///////////////////////// FOR THE BOT ///////////////////////////////////////

        //put = new PuttingSimulator(course,PS);
        if (Bot)
            bot1 = new GolfBot(course, theSimulation, engine, 500, 0.5);

        //theSimulation.takeShot(new Vector2D(-5.157253569297628/1.66,5.149101993602694/1.66));


    }


    void drawGroundQuad(float x, float z) {
        //we don't want to hit any index out of bounds exception...
        //so we need to flush the batch if we can't store any more verts
        //4=amounts of indexes used per vertex; 3=amount of vertices per triangle; 2=amount of triangles
        if (terrainVertexIndex == terrainVertices.length - (4 * 3 * 2))
            flush();

        //First triangle (bottom left, bottom right, top left)
        //bottom left vertex
        terrainVertices[terrainVertexIndex++] = x;
        terrainVertices[terrainVertexIndex++] = z;
        if ((float) course.get_height().evaluate(new Vector2D(x, z)) < 0) {
            terrainVertices[terrainVertexIndex++] = 0;
            terrainVertices[terrainVertexIndex++] = Color.BLUE.toFloatBits();
        } else {
            terrainVertices[terrainVertexIndex++] = (float) course.get_height().evaluate(new Vector2D(x, z));
            terrainVertices[terrainVertexIndex++] = Color.GREEN.toFloatBits();
        }
        //bottom right vertex
        terrainVertices[terrainVertexIndex++] = x + 1;
        terrainVertices[terrainVertexIndex++] = z;
        if ((float) course.get_height().evaluate(new Vector2D(x + 1, z)) < 0) {
            terrainVertices[terrainVertexIndex++] = 0;
            terrainVertices[terrainVertexIndex++] = Color.BLUE.toFloatBits();
        } else {
            terrainVertices[terrainVertexIndex++] = (float) course.get_height().evaluate(new Vector2D(x + 1, z));
            terrainVertices[terrainVertexIndex++] = Color.GREEN.toFloatBits();
        }
        //Top left vertex
        terrainVertices[terrainVertexIndex++] = x;
        terrainVertices[terrainVertexIndex++] = z + 1;
        if ((float) course.get_height().evaluate(new Vector2D(x, z + 1)) < 0) {
            terrainVertices[terrainVertexIndex++] = 0;
            terrainVertices[terrainVertexIndex++] = Color.BLUE.toFloatBits();
        } else {
            terrainVertices[terrainVertexIndex++] = (float) course.get_height().evaluate(new Vector2D(x, z + 1));
            terrainVertices[terrainVertexIndex++] = Color.GREEN.toFloatBits();
        }
        //Second triangle (bottom right, top left, top right)
        //bottom right
        terrainVertices[terrainVertexIndex++] = x + 1;
        terrainVertices[terrainVertexIndex++] = z;
        if ((float) course.get_height().evaluate(new Vector2D(x + 1, z)) < 0) {
            terrainVertices[terrainVertexIndex++] = 0;
            terrainVertices[terrainVertexIndex++] = Color.BLUE.toFloatBits();
        } else {
            terrainVertices[terrainVertexIndex++] = (float) course.get_height().evaluate(new Vector2D(x + 1, z));
            terrainVertices[terrainVertexIndex++] = Color.FOREST.toFloatBits();
        }
        //top left vertex
        terrainVertices[terrainVertexIndex++] = x;
        terrainVertices[terrainVertexIndex++] = z + 1;
        if ((float) course.get_height().evaluate(new Vector2D(x, z + 1)) < 0) {
            terrainVertices[terrainVertexIndex++] = 0;
            terrainVertices[terrainVertexIndex++] = Color.BLUE.toFloatBits();
        } else {
            terrainVertices[terrainVertexIndex++] = (float) course.get_height().evaluate(new Vector2D(x, z + 1));
            terrainVertices[terrainVertexIndex++] = Color.FOREST.toFloatBits();
        }
        //top right vertex
        terrainVertices[terrainVertexIndex++] = x + 1;
        terrainVertices[terrainVertexIndex++] = z + 1;
        if ((float) course.get_height().evaluate(new Vector2D(x + 1, z + 1)) < 0) {
            terrainVertices[terrainVertexIndex++] = 0;
            terrainVertices[terrainVertexIndex++] = Color.BLUE.toFloatBits();
        } else {
            terrainVertices[terrainVertexIndex++] = (float) course.get_height().evaluate(new Vector2D(x + 1, z + 1));
            terrainVertices[terrainVertexIndex++] = Color.FOREST.toFloatBits();
        }
    }


    void createTerrain(float xOffset, float yOffset) {
        //Go over chunks of terrain and create as many chunks as needed to create the terrain
        for (int x = -borderSize; x < borderSize; x++) {
            for (int y = -borderSize; y < borderSize; y++) {
                float xCoordinate = x + xOffset;
                float zCoordinate = y + yOffset;
                drawGroundQuad(xCoordinate, zCoordinate);
            }
        }
    }

    void flush() {
        //if we've already flushed
        if (terrainVertexIndex == 0)
            return;

        //sends our vertex data to the mesh
        terrain.setVertices(terrainVertices);

        //number of vertices we need to render
        int vertexCount = (terrainVertexIndex / NUM_COMPONENTS);

        //start the shader before setting any uniforms
        terrainShader.begin();
        terrainShader.setUniformMatrix("u_projTrans", cam.combined);
        terrain.render(terrainShader, GL20.GL_TRIANGLES, 0, vertexCount);
        terrainShader.end();
        //TODO

        //reset index to zero
        terrainVertexIndex = 0;
    }


    @Override
    public void render(float delta) {


        time1++;
        if (time1 > 100)
            timePassed = true;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        //Create terrain
        createTerrain(0, 0);

        //this will render the remaining triangles
        flush();

        updateText();
        if (timePassed)
            theSimulation.step(delta);
        camController.setTrackedVector(course.getObjects().get(0).position);

        if (!TrackingCameraController.SHOOT)
            camController.update(delta);


        if (theSimulation.completed) {
            game.setScreen(new Win(game));

        }
        // Show
        cam2D.update();

        modelBatch.begin(cam);
        instances.get(0).transform.setTranslation(course.getObjects().get(0).position);

        modelBatch.render(instances, env);
        modelBatch.end();
        UIStage.draw();
        UIStage.act();
/*
        if (put != null) {
            //////////////////////
            ///// TRY ////////////
            //////////////////////
            put.take_shot();
            //put.take_random_shot();
        }*/
    }

    private void updateText() {
        if (theSimulation.isInMove())
            loading.setText("In Shot\nvelx:" + course.objects.get(0).velocity.get_x() + "\nvely:" + course.objects.get(0).velocity.get_y() + "\naccx:" + course.objects.get(0).acceleration.get_x() + "\naccy:" + course.objects.get(0).acceleration.get_y());
        else
            loading.setText("Press the ball and direct the arrow\nvelx:" + course.objects.get(0).velocity.get_x() + "\nvely:" + course.objects.get(0).velocity.get_y() + "\naccx:" + course.objects.get(0).acceleration.get_x() + "\naccy:" + course.objects.get(0).acceleration.get_y());
        turnCount.setText("Turns: " + theSimulation.shots);
    }


    @Override
    public void show() {

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
        modelBatch.dispose();
        instances = null;

    }
}
