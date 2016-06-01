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
	float tdx, tdy;
	int tpointer;
	int touches = 0;
	boolean moving;
	public float zoom = 2f;

	public DrawingGrid(){
		addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
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
						System.out.println("pushing action");
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
				if(pointer != tpointer) return;
				tdx += Gdx.input.getDeltaX(pointer);
				tdy -= Gdx.input.getDeltaY(pointer);
				tdx = MiscUtils.clamp(tdx, getX(), getX() + getWidth() - 1);
				tdy = MiscUtils.clamp(tdy, getY(), getY() + getHeight() - 1);
				int newx = (int)(tdx / (canvasScale()*zoom)), newy = (int)(tdy / (canvasScale()*zoom));

				if( !selected.equals(newx, newy) && (touches > 1 || Gdx.input.isKeyPressed(Keys.E)) && GUI.gui.tool.drawOnMove) GUI.gui.tool.clicked(GUI.gui.selected.getColor(), canvas, newx, newy);

				selected.set(newx, newy);

			}
		});
	}

	public void setCanvas(PixelCanvas canvas){
		if(this.canvas != null) this.canvas.dispose();

		this.canvas = canvas;

		updateSize();
		tdx = getWidth() / 2;
		tdy = getHeight() / 2;
		selected.set(tdx / canvasScale(), tdy / canvasScale());
	}

	public void draw(Batch batch, float parentAlpha){
		updateSize();
		setX(-100);
		setY(-100);
		
		
		
		float cscl = canvasScale() * zoom;
		String gridtype = "grid_25";

		batch.setColor(Color.WHITE);
		batch.draw(Textures.get("alpha"), getX() , getY() , canvas.width() * cscl, canvas.height() * cscl, 0, 0, canvas.width(), canvas.height());

		batch.draw(canvas.texture, getX() , getY() , getWidth()*zoom, getHeight()*zoom);

		if(grid){
			batch.draw(Textures.get(gridtype), getX() , getY() , canvas.width() * cscl, canvas.height() * cscl, 0, 0, canvas.width(), canvas.height());
		}

		int xt = (int)(4 * (10f / canvas.width())); //extra border thickness

		batch.setColor(Color.CORAL);

		batch.draw(Textures.get("grid_10"), getX() + selected.x * cscl - xt , getY() + selected.y * cscl - xt , cscl + xt * 2, cscl + xt * 2);
		batch.draw(Textures.get("grid_10"), getX() + selected.x * cscl , getY() + selected.y * cscl , cscl, cscl);

		batch.setColor(Color.PURPLE);

		batch.draw(Textures.get("cursor"), getX() + tdx - 15 , getY() + tdy - 15 , 30, 30);

		batch.setColor(Color.WHITE);
	}

	public void updateSize(){
		setWidth(min());
		setHeight(min() / canvas.width() * canvas.height());
	}

	float canvasScale(){
		return min() / canvas.width();
	}

	public float min(){
		return Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
}
