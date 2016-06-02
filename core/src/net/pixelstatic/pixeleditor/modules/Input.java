package net.pixelstatic.pixeleditor.modules;

import net.pixelstatic.pixeleditor.PixelEditor;
import net.pixelstatic.utils.modules.Module;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;

public class Input extends Module<PixelEditor> implements InputProcessor{

	@Override
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}
		
		float speed = 10f;
		
		if(Gdx.input.isKeyPressed(Keys.W))
			this.<GUI>getModule(GUI.class).drawgrid.offsety += speed;
		if(Gdx.input.isKeyPressed(Keys.A))
			this.<GUI>getModule(GUI.class).drawgrid.offsetx -= speed;
		if(Gdx.input.isKeyPressed(Keys.S))
			this.<GUI>getModule(GUI.class).drawgrid.offsety -= speed;
		if(Gdx.input.isKeyPressed(Keys.D))
			this.<GUI>getModule(GUI.class).drawgrid.offsetx += speed;
	}
	
	public void init(){

		InputMultiplexer plex = new InputMultiplexer();
		plex.addProcessor(this);
		plex.addProcessor(this.<GUI>getModule(GUI.class).stage);

		Gdx.input.setInputProcessor(plex);
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
		float newzoom = this.<GUI>getModule(GUI.class).drawgrid.zoom - amount / 10f;
		if(newzoom >= 0)
		this.<GUI>getModule(GUI.class).drawgrid.setZoom(newzoom);
		this.<GUI>getModule(GUI.class).drawgrid.updateSize();
		return false;
	}

}
