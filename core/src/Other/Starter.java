package Other;

import Screens.MainMenu;
import com.badlogic.gdx.Game;


public class Starter extends Game {


	@Override
	public void create() {
		this.setScreen(new MainMenu(this));
	}

	@Override
	public void render() {
		super.render();
	}
}
