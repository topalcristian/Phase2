package Physics;

import Other.GameObject;
import Screens.TheCourse;

public interface PhysicsEngine {

    void solve(GameObject obj, TheCourse c, double h);
}
