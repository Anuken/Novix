package net.pixelstatic.pixeleditor.modules;

import net.pixelstatic.pixeleditor.PixelEditor;
import net.pixelstatic.pixeleditor.managers.GestureManager;
import net.pixelstatic.pixeleditor.scene2D.DrawingGrid;
import net.pixelstatic.pixeleditor.tools.Tool;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.modules.Module;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTextField;

public class Input extends Module<PixelEditor> implements InputProcessor{
	private Input input;

	@Override
	public void update(){
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}

		//PC only
		float speed = 10f;

		if(Gdx.input.isKeyPressed(Keys.W)) this.getModule(Main.class).drawgrid.offsety += speed;
		if(Gdx.input.isKeyPressed(Keys.A)) this.<Main>getModule(Main.class).drawgrid.offsetx -= speed;
		if(Gdx.input.isKeyPressed(Keys.S)) this.<Main>getModule(Main.class).drawgrid.offsety -= speed;
		if(Gdx.input.isKeyPressed(Keys.D)) this.<Main>getModule(Main.class).drawgrid.offsetx += speed;

		getModule(Main.class).drawgrid.updateSize();

		getModule(Main.class).drawgrid.updateBounds();
	}

	public void init(){
		input = this;
		Gdx.input.setCatchBackKey(true);
		GestureDetector gesture = new GestureDetector(20, 0.5f, 2, 0.15f, new GestureDetectorListener());

		InputMultiplexer plex = new InputMultiplexer();
		plex.addProcessor(new GestureDetector(20, 0.5f, 2, 0.15f, new GestureManager(Main.i)));
		plex.addProcessor(this);
		plex.addProcessor(this.getModule(Main.class).stage);
		plex.addProcessor(gesture);

		Gdx.input.setInputProcessor(plex);
	}

	@Override
	public boolean keyDown(int keycode){
		if(Main.i.stage.getScrollFocus() != null)
		if(keycode == Keys.BACK){
			Actor actor = MiscUtils.getTopParent(Main.i.stage.getScrollFocus());
			if(actor instanceof VisDialog){
				((VisDialog)actor).hide();
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode){
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
		//PC only
		float newzoom = getModule(Main.class).drawgrid.zoom - (amount / 10f * getModule(Main.class).drawgrid.zoom);
		getModule(Main.class).drawgrid.setZoom(newzoom);
		return false;
	}

	class GestureDetectorListener implements GestureListener{
		float initzoom = 1f;
		Vector2 lastinitialpinch = new Vector2();
		Vector2 lastpinch;
		Vector2 to = new Vector2();
		Vector2 toward = new Vector2();

		@Override
		public boolean touchDown(float x, float y, int pointer, int button){
			initzoom = input.<Main>getModule(Main.class).drawgrid.zoom;
			return false;
		}

		@Override
		public boolean tap(float x, float y, int count, int button){
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean longPress(float x, float y){
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean fling(float velocityX, float velocityY, int button){
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY){
			/*	if(input.<GUI>getModule(GUI.class).tool == Tool.snap){
					DrawingGrid grid = drawgrid();
					grid.setCursor(Gdx.input.getX() - grid.getX(), ((Gdx.graphics.getHeight() - Gdx.input.getY()) - grid.getY()));
				}else*/
			if(input.<Main>getModule(Main.class).tool == Tool.zoom){
				drawgrid().offsetx -= deltaX / drawgrid().zoom;
				drawgrid().offsety += deltaY / drawgrid().zoom;
				drawgrid().updateBounds();
			}
			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance){
			if(input.<Main>getModule(Main.class).tool != Tool.zoom) return false;
			float s = distance / initialDistance;
			float newzoom = initzoom * s;
			if(newzoom < drawgrid().maxZoom()) newzoom = drawgrid().maxZoom();
			drawgrid().setZoom(newzoom);
			//toward.interpolate(to, s / 40f, Interpolation.linear);

			//drawgrid().offsetx = toward.x;
			//drawgrid().offsety = toward.y;
			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2){
			if(input.<Main>getModule(Main.class).tool != Tool.zoom) return false;

			Vector2 afirst = initialPointer1.cpy().add(initialPointer2).scl(0.5f);
			Vector2 alast = pointer1.cpy().add(pointer2).scl(0.5f);

			if( !afirst.epsilonEquals(lastinitialpinch, 0.1f)){
				lastinitialpinch = afirst;
				lastpinch = afirst.cpy();
				toward.set(drawgrid().offsetx, drawgrid().offsety);
				to.x = (afirst.x - Gdx.graphics.getWidth() / 2) / drawgrid().zoom + drawgrid().offsetx;
				to.y = ((Gdx.graphics.getHeight() - afirst.y) - Gdx.graphics.getHeight() / 2) / drawgrid().zoom + drawgrid().offsety;
			}

			lastpinch.sub(alast);
			
			drawgrid().moveOffset(lastpinch.x / drawgrid().zoom, -lastpinch.y / drawgrid().zoom);
			

			lastpinch = alast;

			return false;
		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button){
			initzoom = input.<Main>getModule(Main.class).drawgrid.zoom;
			return false;
		}

	}

	public DrawingGrid drawgrid(){
		return input.<Main>getModule(Main.class).drawgrid;
	}
}
