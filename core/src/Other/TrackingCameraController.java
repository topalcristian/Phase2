package Other;

import Physics.Vector2D;
import Screens.Play;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import static Screens.Play.theSimulation;


public class TrackingCameraController implements InputProcessor {
    private final static BoundingBox bounds = new BoundingBox();
    static public boolean SHOOT = false;
    float cameraDistance = 3f;
    float maxCameraDistance = 10f;
    float minCameraDistance = 0.1f;
    Vector3 dirShot;
    Vector2 delta;
    double maxVelo = theSimulation.course.get_maximum_velocity();
    double ratioX = 1000 / maxVelo;
    double ratioY = 600 / maxVelo;
    ModelBuilder arrowBuilder = new ModelBuilder();
    Model _model;
    ModelInstance _instance;
    private Camera cam;
    private Vector3 position = new Vector3();
    private Vector3 trackedPosition;
    private Vector3 targetPosition;
    private Vector3 tmpV1 = new Vector3();
    private Vector2 lastTouch;
    private int mouseLastX = 0;
    private int mouseLastY = 0;


    public TrackingCameraController(Camera cam) {

        this.cam = cam;
        trackedPosition = new Vector3();
        targetPosition = cam.position.cpy();
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || theSimulation.isInMove()) {
            return true;
        }
        dirShot = getObject(screenX, screenY);
        if (dirShot == null) return false;
        SHOOT = true;
        mouseLastX = screenX;
        mouseLastY = screenY;
        lastTouch = new Vector2(mouseLastX, mouseLastY);

        return false;
    }

    public Vector3 getObject(int screenX, int screenY) {
        Ray ray = cam.getPickRay(screenX, screenY);
        Vector3 result = null;
        float distance = -1;
        final ModelInstance instance = Play.instances.get(0);
        instance.transform.getTranslation(position);
        instance.calculateBoundingBox(bounds);
        Vector3 center = new Vector3();
        Vector3 dimensions = new Vector3();
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        position.add(center);
        float dist2 = ray.origin.dst2(position);
        if (distance >= 0f && dist2 > distance) return ray.origin;
        if (Intersector.intersectRaySphere(ray, position, dimensions.len() + 2 / 2f, null)) {
            result = position;
        }
        return result;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (SHOOT && Play.instances.size() == 3 && (!theSimulation.isInMove())) {
            Play.instances.remove(2);


            theSimulation.takeShot(new Vector2D(-delta.x / ratioX, delta.y / ratioY));
            System.out.println(-delta.x / ratioX);
            System.out.println(delta.y / ratioY);
        }

        SHOOT = false;

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) || (theSimulation.isInMove())) {
            tmpV1.set(cam.direction).crs(cam.up).y = 0f;

            //cam.rotateAround(trackedPosition, Vector3.X, mouseLastY - screenY);
            cam.rotateAround(trackedPosition, Vector3.Z, mouseLastX - screenX);

            mouseLastX = screenX;
            mouseLastY = screenY;
            return true;
        }
        if (!SHOOT) return false;
        if (Play.instances.size() == 3)
            Play.instances.remove(2);
        Vector2 newTouch = new Vector2(screenX, screenY);
        delta = newTouch.cpy().sub(lastTouch);
        _model = arrowBuilder.createArrow(dirShot.x + (delta.x / 50), dirShot.y - (delta.y / 50), dirShot.z, dirShot.x, dirShot.y, dirShot.z, 0.1f, 0.3f, 10, GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        _instance = new ModelInstance(_model);
        Play.instances.add(_instance);
        return false;
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        cameraDistance = Math.max(minCameraDistance, Math.min(cameraDistance + amount, maxCameraDistance));
        return true;
    }

    public void update(float deltaTime) {
        if (trackedPosition != null) {
            targetPosition.set(trackedPosition).mulAdd(cam.direction, -10 * cameraDistance);

            //Insead of jumping straight to the new position, interpolate into target, so we get that nice swooshy camera feel
            //cam.position.lerp(targetPosition, cameraLerp);
            cam.position.set(targetPosition);
            cam.update();
        }
    }

    /**
     * getTrackedEntity
     *
     * @return returns the entity that is currently tracked by the camera
     */
    public Vector3 getTrackedEntity() {
        return trackedPosition;
    }

    /**
     * setTrackedVector
     *
     * @param trackedPosition the entity that the camera will be tracking
     */
    public void setTrackedVector(Vector3 trackedPosition) {
        this.trackedPosition = trackedPosition;
    }
}
