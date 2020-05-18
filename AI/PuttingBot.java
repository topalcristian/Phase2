package AI;

import Physics.Vector2D;
import Screens.TheCourse;

public interface PuttingBot {
    public Vector2D shot_velocity(TheCourse course, Vector2D ball_position);
}
