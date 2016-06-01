package net.pixelstatic.pixeleditor.modules;

import net.pixelstatic.pixeleditor.PixelEditor;
import net.pixelstatic.utils.modules.Module;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

public class Input extends Module<PixelEditor> implements InputProcessor{

	@Override
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}
	}

	@Override
	public boolean keyDown(int keycode){
		return false;
	}

	@Override
	public boolean keyUp(int keycode){
		return false;
	}

	@Override
	public boolean keyTyped(char character){
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer){
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY){
		return false;
	}

	@Override
	public boolean scrolled(int amount){
		return false;
	}

}
