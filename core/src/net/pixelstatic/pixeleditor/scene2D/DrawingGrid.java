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
	GridImage image;
	public boolean grid = true, cursormode = true;;
	private float cursorx, cursory;
	int tpointer;
	int touches = 0;
	boolean moving;
	public float zoom = 1f, offsetx = 0, offsety = 0;
	public boolean clip = true;
	public boolean vSymmetry = true, hSymmetry = false;

	public DrawingGrid(){
		image = new GridImage(1, 1);
		addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if( !GUI.gui.tool.moveCursor()) return false;
				touches ++;
				if(cursormode){
					if(moving){
						processToolTap(selected.x, selected.y);
						return true;
					}

					tpointer = pointer;
					moving = true;
				}else{
					cursorx = x;
					cursory = y;
					updateCursorSelection();
					processToolTap(selected.x, selected.y);
					return true;
				}
				return true;
			}

			public void touchUp(InputEvent event, float x, float y, int pointer, int button){
				if(cursormode){
					if(pointer == tpointer){
						moving = false;
					}else{
						if(GUI.gui.tool.push) canvas.pushActions();
					}
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
					processToolTap(selected.x, selected.y);
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

					if( !selected.equals(newx, newy) && (touches > 1 || Gdx.input.isKeyPressed(Keys.E)) && GUI.gui.tool.drawOnMove) 
						processToolTap(newx, newy);

					selected.set(newx, newy);
				}else{
					cursorx = x;
					cursory = y;
					int newx = (int)(cursorx / (canvasScale() * zoom)), newy = (int)(cursory / (canvasScale() * zoom));

					if( !selected.equals(newx, newy) && GUI.gui.tool.drawOnMove) processToolTap(newx, newy);
					selected.set(newx, newy);
				}
			}
		});
	}
	
	private void processToolTap(int x, int y){
		GUI.gui.tool.clicked(GUI.gui.colorbox.getColor(), canvas, x, y);
		
		if(vSymmetry){
			GUI.gui.tool.clicked(GUI.gui.colorbox.getColor(), canvas, canvas.width() - 1 - x, y);
		}
		
		if(hSymmetry){
			GUI.gui.tool.clicked(GUI.gui.colorbox.getColor(), canvas, x, canvas.height() - 1 - y);
			
			if(vSymmetry){
				GUI.gui.tool.clicked(GUI.gui.colorbox.getColor(), canvas, canvas.width() - 1 - x, canvas.height() - 1 - y);
			}
		}
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
		if(newzoom < maxAspectRatio()) newzoom = maxAspectRatio();

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

		this.canvas = canvas;

		updateSize();

		zoom = maxAspectRatio();

		cursorx = getWidth() / 2;
		cursory = getHeight() / 2;
		selected.set(cursorx / canvasScale(), cursory / canvasScale());
		offsetx = getWidth() / 2;
		offsety = getHeight() / 2;
		image.setImageSize(canvas.width(), canvas.height());
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

		batch.setColor(Color.WHITE);
		batch.draw(Textures.get("alpha"), getX(), getY(), canvas.width() * cscl, canvas.height() * cscl, 0, 0, canvas.width(), canvas.height());

		batch.draw(canvas.texture, getX(), getY(), getWidth(), getHeight());

		if(grid){
			image.setBounds(getX(), getY(), getWidth(), getHeight());
			image.draw(batch, parentAlpha);
		}

		//draw symmetry lines

		if(vSymmetry){
			batch.setColor(Color.CYAN);
			batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), (int)(getX() + getWidth() / 2f - 2f), getY(), 4, getHeight());
		}

		if(hSymmetry){
			batch.setColor(Color.PURPLE);
			batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), getX(), (int)(getY() + getHeight() / 2f - 2f), getWidth(), 4);
		}

		//draw center of grid
		batch.setColor(Color.CORAL);

		float size = 4;
		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), (int)(getX() + getWidth() / 2f - size / 2), (int)(getY() + getHeight() / 2f - size / 2), size, size);

		int xt = (int)(4 * (10f / canvas.width() * zoom)); //extra border thickness

		//draw selection
		if((cursormode || (touches > 0 && GUI.gui.tool.moveCursor())) && GUI.gui.tool.drawCursor()){
			batch.setColor(Color.CORAL);
			
			drawSelection(batch, selected.x, selected.y, cscl, xt);
			
		//	batch.setColor(Hue.blend(Color.CORAL, Color.WHITE, 0.5f));
			if(vSymmetry){
				drawSelection(batch, canvas.width() - 1 - selected.x, selected.y, cscl, xt);
			}
			
			if(hSymmetry){
				drawSelection(batch, selected.x, canvas.height() - 1 - selected.y, cscl, xt);
			}
			
			if(vSymmetry && hSymmetry){
				drawSelection(batch, canvas.width() - 1 - selected.x, canvas.height() - 1 - selected.y, cscl, xt);
			}
		}
		
		batch.setColor(Color.GRAY);

		//draw screen edges
		MiscUtils.drawBorder(batch, Gdx.graphics.getWidth() / 2 - min() / 2f, Gdx.graphics.getHeight() / 2 - min() / 2f, min(), min(), 2);

		batch.setColor(Color.CORAL);

		//draw pic edges
		MiscUtils.drawBorder(batch, (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight(), 2);

		//draw cursor
		if(cursormode || (touches > 0 && GUI.gui.tool.moveCursor())){
			batch.setColor(Color.PURPLE);
			batch.draw(Textures.get("cursor"), getX() + cursorx - 15, getY() + cursory - 15, 30, 30);
		}else{ //seriously, why is this necessary
			batch.draw(Textures.get("cursor"), -999, -999, 30, 30);
		}
		
		if(clip){
			clipEnd();
		}
		batch.flush();
		batch.setColor(Color.WHITE);
	}
	
	private void drawSelection(Batch batch, int x, int y, float cscl, float xt){
		batch.draw(Textures.get("grid_10"), getX() + x * cscl - xt, getY() + y * cscl - xt, cscl + xt * 2, cscl + xt * 2);
		batch.draw(Textures.get("grid_10"), getX() + x * cscl, getY() + y * cscl, cscl, cscl);

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
