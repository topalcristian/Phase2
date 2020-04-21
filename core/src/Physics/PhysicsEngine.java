package Physics;

import Other.GameObject;
import Screens.TheCourse;
import com.badlogic.gdx.math.Vector3;

public interface PhysicsEngine {

    Vector3[] solve(GameObject obj, TheCourse c, double h);
}
