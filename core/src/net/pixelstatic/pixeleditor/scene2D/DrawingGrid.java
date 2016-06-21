package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.pixeleditor.graphics.PixelCanvas;
import net.pixelstatic.pixeleditor.modules.GUI;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.Pos;
import net.pixelstatic.utils.graphics.Textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.VisUI;

public class DrawingGrid extends Actor{
	public PixelCanvas canvas;
	public Pos selected = new Pos();
	public boolean grid = true, cursormode = true;;
	private float cursorx, cursory;
	int tpointer;
	int touches = 0;
	boolean moving;
	public float zoom = 1f, offsetx = 0, offsety = 0;
	public boolean clip = true;

	public DrawingGrid(){
		addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if( !GUI.gui.tool.moveCursor()) return false;
				touches ++;
				if(cursormode){
					if(moving){
						GUI.gui.tool.clicked(GUI.gui.colorbox.getColor(), canvas, selected.x, selected.y);
						return true;
					}

					tpointer = pointer;
					moving = true;
				}else{
					cursorx = x;
					cursory = y;
					updateCursorSelection();
					GUI.gui.tool.clicked(GUI.gui.colorbox.getColor(), canvas, selected.x, selected.y);
					return true;
				}
				return true;
			}

			public void touchUp(InputEvent event, float x, float y, int pointer, int button){
				if(pointer == tpointer){
					moving = false;
				}else{
					if(GUI.gui.tool.push) canvas.pushActions();
				}
				touches --;
			}

			//pc debugging
			public boolean keyUp(InputEvent event, int keycode){
				if(keycode == Keys.E){
					if(GUI.gui.tool.push){
						canvas.pushActions();
					}
					return true;
				}
				return false;
			}

			//pc debugging
			public boolean keyDown(InputEvent event, int keycode){
				if(keycode == Keys.E){
					GUI.gui.tool.clicked(GUI.gui.colorbox.getColor(), canvas, selected.x, selected.y);
					return true;
				}
				return false;
			}

			public void touchDragged(InputEvent event, float x, float y, int pointer){
				if(cursormode){
					if(pointer != tpointer || !GUI.gui.tool.moveCursor()) return;
					cursorx += Gdx.input.getDeltaX(pointer);
					cursory -= Gdx.input.getDeltaY(pointer);
					cursorx = MiscUtils.clamp(cursorx, 0, getWidth() - 1);
					cursory = MiscUtils.clamp(cursory, 0, getHeight() - 1);
					int newx = (int)(cursorx / (canvasScale() * zoom)), newy = (int)(cursory / (canvasScale() * zoom));

					if( !selected.equals(newx, newy) && (touches > 1 || Gdx.input.isKeyPressed(Keys.E)) && GUI.gui.tool.drawOnMove) GUI.gui.tool.clicked(GUI.gui.colorbox.getColor(), canvas, newx, newy);

					selected.set(newx, newy);
				}else{
					cursorx = x;
					cursory = y;
					int newx = (int)(cursorx / (canvasScale() * zoom)), newy = (int)(cursory / (canvasScale() * zoom));

					if( !selected.equals(newx, newy)) GUI.gui.tool.clicked(GUI.gui.colorbox.getColor(), canvas, newx, newy);
					selected.set(newx, newy);
				}
			}
		});
	}

	public void moveOffset(float x, float y){
		offsetx += x;
		offsety += y;
	}

	public void moveCursor(float x, float y){
		cursorx += x;
		cursory += y;
	}

	public void setZoom(float newzoom){
		cursorx *= (newzoom / zoom);
		cursory *= (newzoom / zoom);

		zoom = newzoom;

		updateSize();

		updateBounds();
	}

	public void setCursor(float x, float y){
		cursorx = x;
		cursory = y;
		cursorx = MiscUtils.clamp(cursorx, 0, getWidth() - 1);
		cursory = MiscUtils.clamp(cursory, 0, getHeight() - 1);
		int newx = (int)(cursorx / (canvasScale() * zoom)), newy = (int)(cursory / (canvasScale() * zoom));

		selected.set(newx, newy);
	}

	public void setCanvas(PixelCanvas canvas){
		if(this.canvas != null) this.canvas.dispose();

		zoom = 1f;

		this.canvas = canvas;

		updateSize();

		cursorx = getWidth() / 2;
		cursory = getHeight() / 2;
		selected.set(cursorx / canvasScale(), cursory / canvasScale());
		offsetx = getWidth() / 2;
		offsety = getHeight() / 2;
	}

	public void updateCursorSelection(){
		int newx = (int)(cursorx / (canvasScale() * zoom)), newy = (int)(cursory / (canvasScale() * zoom));
		selected.set(newx, newy);
	}

	public void draw(Batch batch, float parentAlpha){
		updateSize();
		updateBounds();
		updateCursor();

		if(clip){
			batch.flush();
			clipBegin(Gdx.graphics.getWidth() / 2 - min() / 2, Gdx.graphics.getHeight() / 2 - min() / 2, min(), min());
		}

		float cscl = canvasScale() * zoom;
		String gridtype = "grid_25";

		batch.setColor(Color.WHITE);
		batch.draw(Textures.get("alpha"), getX(), getY(), canvas.width() * cscl, canvas.height() * cscl, 0, 0, canvas.width(), canvas.height());

		batch.draw(canvas.texture, getX(), getY(), getWidth(), getHeight());

		if(grid){
			batch.draw(Textures.get(gridtype), getX(), getY(), canvas.width() * cscl, canvas.height() * cscl, 0, 0, canvas.width(), canvas.height());
		}

		int xt = (int)(4 * (10f / canvas.width() * zoom)); //extra border thickness

		//draw selection
		if(cursormode || (touches > 0 && GUI.gui.tool.moveCursor())){
			batch.setColor(Color.CORAL);
			batch.draw(Textures.get("grid_10"), getX() + selected.x * cscl - xt, getY() + selected.y * cscl - xt, cscl + xt * 2, cscl + xt * 2);
			batch.draw(Textures.get("grid_10"), getX() + selected.x * cscl, getY() + selected.y * cscl, cscl, cscl);
		}
		
		batch.setColor(Color.GRAY);
		//draw screen edges
		
		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), Gdx.graphics.getWidth() / 2 - min() / 2f, Gdx.graphics.getHeight() / 2 - min() / 2f, min(), 2);
		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), Gdx.graphics.getWidth() / 2 - min() / 2f, Gdx.graphics.getHeight() / 2 + min() / 2f, min(), -2);
		
		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), Gdx.graphics.getWidth() / 2 + min() / 2f, Gdx.graphics.getHeight() / 2 - min() / 2f, -2, min());
		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), Gdx.graphics.getWidth() / 2 - min() / 2f, Gdx.graphics.getHeight() / 2 - min() / 2f, 2, min());
	
		//batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), Gdx.graphics.getWidth()/2 + min()/2f, Gdx.graphics.getHeight() / 2 - min()/2f, 2, min());
		//batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), Gdx.graphics.getWidth()/2 - min()/2f, Gdx.graphics.getHeight() / 2 - min()/2f, min(), 2);
		
		//draw pic edges
		
		batch.setColor(Color.CORAL);
		
		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), getX(), getY(), getWidth(), 2);
		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), getX(), getY() + getHeight(), getWidth(), -2);
		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), getX(), getY(), 2, getHeight());
		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"),getX() + getWidth(), getY(), -2, getHeight());

		
		//draw cursor
		if(cursormode || (touches > 0 && GUI.gui.tool.moveCursor())){
			batch.setColor(Color.PURPLE);
			batch.draw(Textures.get("cursor"), getX() + cursorx - 15, getY() + cursory - 15, 30, 30);
		}
		if(clip){
			clipEnd();
		}

		batch.setColor(Color.WHITE);
	}

	public void updateCursor(){
		if(cursorx > Gdx.graphics.getWidth() - getX()) cursorx = Gdx.graphics.getWidth() - getX();
		if(cursory > Gdx.graphics.getHeight() / 2 + Gdx.graphics.getWidth() / 2 - getY()) cursory = Gdx.graphics.getHeight() / 2 + Gdx.graphics.getWidth() / 2 - getY();

		if(cursorx < -getX()) cursorx = -getX();
		if(cursory < Gdx.graphics.getHeight() / 2 - Gdx.graphics.getWidth() / 2 - getY()) cursory = Gdx.graphics.getHeight() / 2 - Gdx.graphics.getWidth() / 2 - getY();

		if(cursorx > getWidth() - 1) cursorx = getWidth() - 1;
		if(cursory > getHeight() - 1) cursory = getHeight() - 1;

		if(cursorx < 0) cursorx = 0;
		if(cursorx < 0) cursory = 0;

		updateCursorSelection();

	}

	public void updateBounds(){
		int toolheight = (Gdx.graphics.getHeight() - Gdx.graphics.getWidth()) / 2, colorheight = toolheight;

		if(aspectRatio() >= 1f){
			if(getX() + getWidth() < Gdx.graphics.getWidth()) offsetx = -(Gdx.graphics.getWidth() / 2 - getWidth()) / zoom;
			if(getX() > 0) offsetx = Gdx.graphics.getWidth() / 2 / zoom;

		}

		if(aspectRatio() <= 1f){
			if(getY() + getHeight() < Gdx.graphics.getHeight() - colorheight) offsety = -(Gdx.graphics.getHeight() / 2 - getHeight() - colorheight) / zoom;
			if(getY() > toolheight) offsety = (Gdx.graphics.getHeight() / 2 - toolheight) / zoom;

			//if(aspectRatio() < 1f){
			//	if(getX() + getWidth() > Gdx.graphics.getWidth()) offsetx = -(Gdx.graphics.getWidth()/2 - getWidth())/zoom;
			//	if(getX() < 0) offsetx = Gdx.graphics.getWidth()/2/zoom;
			//}
		}

	}

	public void updateSize(){
		setWidth(min() * zoom);
		setHeight(min() / canvas.width() * canvas.height() * zoom);

		setX(Gdx.graphics.getWidth() / 2 - offsetx * zoom);
		setY(Gdx.graphics.getHeight() / 2 - offsety * zoom);
	}

	public float maxAspectRatio(){
		return Math.min(getWidth() / getHeight(), getHeight() / getWidth());
	}

	public float aspectRatio(){
		return getWidth() / getHeight();
	}

	public float canvasScale(){
		return min() / canvas.width();
	}

	public float min(){
		return Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
}
