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
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;


public class Play implements Screen {

    public static int borderSize = 75;
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


    public ModelBatch modelBatch = new ModelBatch();
    public static TheCourse course = new TheCourse();
    public Environment env;
    public ModelBuilder modelBuilder = new ModelBuilder();
    public static PhysicsEngine engine = new Verlet();
    public static World PS = new World(course, engine);
    public TrackingCameraController camController;
    public Model golfBall;
    public Model hole;
    //public CameraInputController trackingCameraController;
    public ModelInstance ourGolfBall, Goal;
    Mesh terrain;
    ShaderProgram terrainShader;


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
        game = g;
        //PhysicsEngine
        switch (CourseInput.gamePhysics) {
            case "rk4":
                engine = new RungeKutta();
            case "euler":
                engine = new Euler();
            case "verlet":
                engine = new Verlet();

        }


        // Ball
        golfBall = modelBuilder.createSphere(1, 1, 1, 25, 25, new Material(ColorAttribute.createDiffuse(Color.WHITE)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ourGolfBall = new ModelInstance(golfBall, course.getObjects().get(0).position);

        // Hole
        hole = modelBuilder.createSphere((float) course.get_hole_tolerance() + 2, 3, 1, 25, 25, new Material(new BlendingAttribute((float) 0.5)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        Goal = new ModelInstance(hole, course.getObjects().get(1).position);


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

        terrainShader = new ShaderProgram(VERT_SHADER, FRAG_SHADER);
        terrain = new Mesh(true, MAX_VERTS, 0, new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, "a_position"), new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));
        instances.add(ourGolfBall);
        instances.add(Goal);

        ///////////////////////// FOR THE BOT ///////////////////////////////////////

        //put = new PuttingSimulator(course,PS);

        bot1 = new GolfBot(course, PS, engine, 500, 0.5);
        //PS.takeShot(new Vector2D(1.1909131860485525, 6.872457137166267));

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
            terrainVertices[terrainVertexIndex++] = Color.GREEN.toFloatBits();
        }
        //top left vertex
        terrainVertices[terrainVertexIndex++] = x;
        terrainVertices[terrainVertexIndex++] = z + 1;
        if ((float) course.get_height().evaluate(new Vector2D(x, z + 1)) < 0) {
            terrainVertices[terrainVertexIndex++] = 0;
            terrainVertices[terrainVertexIndex++] = Color.BLUE.toFloatBits();
        } else {
            terrainVertices[terrainVertexIndex++] = (float) course.get_height().evaluate(new Vector2D(x, z + 1));
            terrainVertices[terrainVertexIndex++] = Color.GREEN.toFloatBits();
        }
        //top right vertex
        terrainVertices[terrainVertexIndex++] = x + 1;
        terrainVertices[terrainVertexIndex++] = z + 1;
        if ((float) course.get_height().evaluate(new Vector2D(x + 1, z + 1)) < 0) {
            terrainVertices[terrainVertexIndex++] = 0;
            terrainVertices[terrainVertexIndex++] = Color.BLUE.toFloatBits();
        } else {
            terrainVertices[terrainVertexIndex++] = (float) course.get_height().evaluate(new Vector2D(x + 1, z + 1));
            terrainVertices[terrainVertexIndex++] = Color.GREEN.toFloatBits();
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

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        //Create terrain
        createTerrain(0, 0);

        //this will render the remaining triangles
        flush();


        PS.step(delta);
        camController.setTrackedVector(course.getObjects().get(0).position);

        if (!TrackingCameraController.SHOOT)
            camController.update(delta);


        if (PS.completed)
            game.setScreen(new Win(game));

        // Show
        modelBatch.begin(cam);
        instances.get(0).transform.setTranslation(course.getObjects().get(0).position);
        modelBatch.render(instances, env);
        modelBatch.end();

/*
        if (put != null) {
            //////////////////////
            ///// TRY ////////////
            //////////////////////
            put.take_shot();
            //put.take_random_shot();
        }*/
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
    }
}
