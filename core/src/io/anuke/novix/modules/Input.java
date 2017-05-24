package io.anuke.novix.modules;

import static io.anuke.novix.Var.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTextField;

import io.anuke.novix.Novix;
import io.anuke.novix.managers.GestureManager;
import io.anuke.ucore.modules.Module;
import io.anuke.utools.SceneUtils;

public class Input extends Module<Novix>{
	private Input input;
	private Vector2 vector = new Vector2();

	@Override
	public void update(){
		//PC debugging only
		
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}
		
		float speed = 10f;
		
		float dx=0, dy=0;
		
		if(Gdx.input.isKeyPressed(Keys.W)) dy += speed;
		if(Gdx.input.isKeyPressed(Keys.A)) dx -= speed;
		if(Gdx.input.isKeyPressed(Keys.S)) dy -= speed;
		if(Gdx.input.isKeyPressed(Keys.D)) dx += speed;
		
		if(Math.abs(dx) > 0 || Math.abs(dy) > 0){
			Gdx.graphics.requestRendering();
		}

		drawing.updateGrid();
	}

	public void init(){
		input = this;
		Gdx.input.setCatchBackKey(true);
		GestureDetector gesture = drawing.getGestureDetector();

		InputMultiplexer plex = new InputMultiplexer();
		plex.addProcessor(getModule(Tutorial.class));
		plex.addProcessor(new GestureDetector(20, 0.5f, 2, 0.15f, new GestureManager()));
		plex.addProcessor(this);
		plex.addProcessor(core.stage);
		plex.addProcessor(drawing);
		plex.addProcessor(gesture);

		Gdx.input.setInputProcessor(plex);
	}

	@Override
	public boolean keyDown(int keycode){
		if(keycode == Keys.BACK){
			VisDialog dialog = core.getCurrentDialog();
			if(dialog != null) dialog.hide();
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character){
		if((int)character == 10){ //char is "enter" key
			if(FocusManager.getFocusedWidget() != null && FocusManager.getFocusedWidget() instanceof VisTextField){
				Gdx.input.setOnscreenKeyboardVisible(false);
			}
		}
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		
		if(stage.getScrollFocus() != null){
			Actor parent = SceneUtils.getTopParent(stage.getScrollFocus());
			
			if( !(parent instanceof VisDialog)) return false;
			
			VisDialog dialog = (VisDialog)parent;
			
			if(!SceneUtils.mouseOnActor(dialog, vector)){
				dialog.hide();
			}
		}
		return false;
	}

}
