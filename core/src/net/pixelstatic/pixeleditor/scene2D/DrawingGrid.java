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

public class DrawingGrid extends Actor{
	public PixelCanvas canvas;
	public Pos selected = new Pos();
	public boolean grid = true;
	float cursorx, cursory;
	int tpointer;
	int touches = 0;
	boolean moving;
	public float zoom = 1f, offsetx = 0, offsety = 0;

	public DrawingGrid(){
		addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if(!GUI.gui.tool.moveCursor()) return false;
				touches ++;
				if(moving){
					GUI.gui.tool.clicked(GUI.gui.selected.getColor(), canvas, selected.x, selected.y);
					return true;
				}

				tpointer = pointer;
				moving = true;
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
					GUI.gui.tool.clicked(GUI.gui.selected.getColor(), canvas, selected.x, selected.y);
					return true;
				}
				return false;
			}

			public void touchDragged(InputEvent event, float x, float y, int pointer){
				if(pointer != tpointer || !GUI.gui.tool.moveCursor()) return;
				cursorx += Gdx.input.getDeltaX(pointer);
				cursory -= Gdx.input.getDeltaY(pointer);
				cursorx = MiscUtils.clamp(cursorx, 0, getWidth() - 1);
				cursory = MiscUtils.clamp(cursory, 0, getHeight() - 1);
				int newx = (int)(cursorx / (canvasScale() * zoom)), newy = (int)(cursory / (canvasScale() * zoom));

				if( !selected.equals(newx, newy) && (touches > 1 || Gdx.input.isKeyPressed(Keys.E)) && GUI.gui.tool.drawOnMove) GUI.gui.tool.clicked(GUI.gui.selected.getColor(), canvas, newx, newy);

				selected.set(newx, newy);

			}
		});
	}

	public void setZoom(float newzoom){
		cursorx *= (newzoom / zoom);
		cursory *= (newzoom / zoom);

		zoom = newzoom;

		updateSize();

		updateBounds();
	}


	public void setCanvas(PixelCanvas canvas){
		if(this.canvas != null) this.canvas.dispose();

		this.canvas = canvas;

		updateSize();
		
		cursorx = getWidth() / 2;
		cursory = getHeight() / 2;
		selected.set(cursorx / canvasScale(), cursory / canvasScale());
		offsetx = getWidth() / 2;
		offsety = getHeight() / 2;
	}

	public void draw(Batch batch, float parentAlpha){
		updateSize();
		updateBounds();

		float cscl = canvasScale() * zoom;
		String gridtype = "grid_25";

		batch.setColor(Color.WHITE);
		batch.draw(Textures.get("alpha"), getX(), getY(), canvas.width() * cscl, canvas.height() * cscl, 0, 0, canvas.width(), canvas.height());

		batch.draw(canvas.texture, getX(), getY(), getWidth(), getHeight());

		if(grid){
			batch.draw(Textures.get(gridtype), getX(), getY(), canvas.width() * cscl, canvas.height() * cscl, 0, 0, canvas.width(), canvas.height());
		}

		int xt = (int)(4 * (10f / canvas.width() * zoom)); //extra border thickness

		batch.setColor(Color.CORAL);

		batch.draw(Textures.get("grid_10"), getX() + selected.x * cscl - xt, getY() + selected.y * cscl - xt, cscl + xt * 2, cscl + xt * 2);
		batch.draw(Textures.get("grid_10"), getX() + selected.x * cscl, getY() + selected.y * cscl, cscl, cscl);

		batch.setColor(Color.PURPLE);

		batch.draw(Textures.get("cursor"), getX() + cursorx - 15, getY() + cursory - 15, 30, 30);

		batch.setColor(Color.WHITE);
	}
	
	
	public void updateBounds(){
		int toolheight = (Gdx.graphics.getHeight() - Gdx.graphics.getWidth())/2, colorheight = toolheight;
			
		if(getX() + getWidth() < Gdx.graphics.getWidth()) offsetx = -(Gdx.graphics.getWidth()/2 - getWidth())/zoom;
		if(getY() + getHeight() < Gdx.graphics.getHeight() - colorheight) offsety = -(Gdx.graphics.getHeight()/2 - getHeight() - colorheight)/zoom;
		
		if(getX() > 0) offsetx = Gdx.graphics.getWidth()/2/zoom;
		if(getY() > toolheight) offsety = (Gdx.graphics.getHeight()/2-toolheight)/zoom;
	}

	public void updateSize(){
		setWidth(min() * zoom);
		setHeight(min() / canvas.width() * canvas.height() * zoom);

		setX(Gdx.graphics.getWidth() / 2 - offsetx * zoom);
		setY(Gdx.graphics.getHeight() / 2 - offsety * zoom);
	}

	float canvasScale(){
		return min() / canvas.width();
	}

	public float min(){
		return Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
}
