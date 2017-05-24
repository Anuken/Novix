package io.anuke.novix;

import static io.anuke.novix.Var.*;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTextField;

import io.anuke.novix.managers.GestureManager;
import io.anuke.novix.tools.Tool;
import io.anuke.ucore.modules.Module;
import io.anuke.utools.SceneUtils;
public class Input extends Module<Novix>{
	private Input input;
	private Vector2 vector = new Vector2();

	@Override
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}

		//PC only
		float speed = 10f;

		if(Gdx.input.isKeyPressed(Keys.W)) core.drawgrid.offsety += speed;
		if(Gdx.input.isKeyPressed(Keys.A)) core.drawgrid.offsetx -= speed;
		if(Gdx.input.isKeyPressed(Keys.S)) core.drawgrid.offsety -= speed;
		if(Gdx.input.isKeyPressed(Keys.D)) core.drawgrid.offsetx += speed;
		
		if(Gdx.app.getType() == ApplicationType.Desktop){
			Gdx.graphics.requestRendering();
		}

		getModule(Core.class).drawgrid.updateSize();

		getModule(Core.class).drawgrid.updateBounds();
	}

	public void init(){
		input = this;
		Gdx.input.setCatchBackKey(true);
		GestureDetector gesture = new GestureDetector(20, 0.5f, 2, 0.15f, new GestureDetectorListener());

		InputMultiplexer plex = new InputMultiplexer();
		plex.addProcessor(getModule(Tutorial.class));
		plex.addProcessor(new GestureDetector(20, 0.5f, 2, 0.15f, new GestureManager()));
		plex.addProcessor(this);
		plex.addProcessor(core.stage);
		plex.addProcessor(core.drawgrid.input);
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

	@Override
	public boolean scrolled(int amount){
		//PC only
		float newzoom = getModule(Core.class).drawgrid.zoom - (amount / 10f * getModule(Core.class).drawgrid.zoom);
		getModule(Core.class).drawgrid.setZoom(newzoom);
		return false;
	}

	class GestureDetectorListener extends GestureAdapter{
		float initzoom = 1f;
		Vector2 lastinitialpinch = new Vector2();
		Vector2 lastpinch;
		Vector2 to = new Vector2();
		Vector2 toward = new Vector2();
		
		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY){
			if(core.menuOpen()) return false;
			
			if(input.<Core>getModule(Core.class).tool() == Tool.zoom){
				core.drawgrid.offsetx -= deltaX / core.drawgrid.zoom;
				core.drawgrid.offsety += deltaY / core.drawgrid.zoom;
				core.drawgrid.updateSize();
				core.drawgrid.updateBounds();
			}
			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance){
			if(core.tool() != Tool.zoom || core.menuOpen()) return false;
			float s = distance / initialDistance;
			float newzoom = initzoom * s;
			if(newzoom < core.drawgrid.maxZoom()) newzoom = core.drawgrid.maxZoom();
			core.drawgrid.setZoom(newzoom);
			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2){
			if(core.tool() != Tool.zoom || core.menuOpen()) return false;

			Vector2 afirst = initialPointer1.cpy().add(initialPointer2).scl(0.5f);
			Vector2 alast = pointer1.cpy().add(pointer2).scl(0.5f);

			if( !afirst.epsilonEquals(lastinitialpinch, 0.1f)){
				lastinitialpinch = afirst;
				lastpinch = afirst.cpy();
				toward.set(core.drawgrid.offsetx, core.drawgrid.offsety);
				to.x = (afirst.x - Gdx.graphics.getWidth() / 2) / core.drawgrid.zoom + core.drawgrid.offsetx;
				to.y = ((Gdx.graphics.getHeight() - afirst.y) - Gdx.graphics.getHeight() / 2) / core.drawgrid.zoom + core.drawgrid.offsety;
			}

			lastpinch.sub(alast);

			core.drawgrid.moveOffset(lastpinch.x / core.drawgrid.zoom, -lastpinch.y / core.drawgrid.zoom);

			lastpinch = alast;

			return false;
		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button){
			initzoom = core.drawgrid.zoom;
			return false;
		}
	}
}
