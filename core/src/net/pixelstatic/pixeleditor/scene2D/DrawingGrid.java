package net.pixelstatic.pixeleditor.scene2D;

import static net.pixelstatic.pixeleditor.modules.Core.s;
import net.pixelstatic.gdxutils.graphics.Textures;
import net.pixelstatic.pixeleditor.modules.Core;
import net.pixelstatic.pixeleditor.tools.PixelCanvas;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.Pos;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;

public class DrawingGrid extends Actor{
	private float cursorx, cursory;
	private int tpointer;
	private int touches = 0;
	private boolean moving;
	private Pos selected = new Pos();
	private GridImage gridimage;
	private AlphaImage alphaimage;
	private final boolean clip = true;

	public PixelCanvas canvas;
	public boolean grid = true, cursormode = true;
	public float zoom = 1f, offsetx = 0, offsety = 0, cursorSpeed = 1.03f;
	public boolean vSymmetry = false, hSymmetry = false;
	public int brushSize;

	public DrawingGrid(){
		gridimage = new GridImage(1, 1);
		alphaimage = new AlphaImage(1, 1);
		addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if( !Core.i.tool.moveCursor()) return false;
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
						if(Core.i.tool.push) canvas.pushActions();
					}
				}else{
					if(Core.i.tool.push) canvas.pushActions();
				}
				touches --;
			}

			//pc debugging
			public boolean keyUp(InputEvent event, int keycode){
				if(keycode == Keys.E){
					if(Core.i.tool.push){
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
				if(pointer != 0 && Gdx.app.getType() != ApplicationType.Desktop) return; //not the second pointer

				float deltax = Gdx.input.getDeltaX(pointer) * cursorSpeed;
				float deltay = -Gdx.input.getDeltaY(pointer) * cursorSpeed;

				float movex = deltax;
				float movey = deltay;

				int move = (Math.round(Math.max(Math.abs(movex), Math.abs(movey))));

				if(Math.abs(movex) > Math.abs(movey)){
					movey /= Math.abs(movex);
					movex /= Math.abs(movex);
				}else{
					movex /= Math.abs(movey);
					movey /= Math.abs(movey);
				}

				float currentx = 0, currenty = 0;

				for(int i = 0;i < move;i ++){
					currentx += movex;
					currenty += movey;
					//	System.out.println("drawing: "+ vector.cpy().add(cursorx, cursory));
					if(cursormode){
						int newx = (int)((cursorx + currentx) / (canvasScale() * zoom)), newy = (int)((cursory + currenty) / (canvasScale() * zoom));

						if( !selected.equals(newx, newy) && (touches > 1 || Gdx.input.isKeyPressed(Keys.E)) && Core.i.tool.drawOnMove) processToolTap(newx, newy);

						selected.set(newx, newy);
					}else{
						int newx = (int)((cursorx + currentx) / (canvasScale() * zoom)), newy = (int)((cursory + currenty) / (canvasScale() * zoom));

						if( !selected.equals(newx, newy) && Core.i.tool.drawOnMove) processToolTap(newx, newy);
						selected.set(newx, newy);
					}
				}

				if(cursormode){
					if(pointer != tpointer || !Core.i.tool.moveCursor()) return;

					cursorx += deltax;
					cursory += deltay;

					cursorx = MiscUtils.clamp(cursorx, 0, getWidth() - 1);
					cursory = MiscUtils.clamp(cursory, 0, getHeight() - 1);

				}else{
					cursorx = x;
					cursory = y;
				}
			}
		});
	}

	private void processToolTap(int x, int y){
		Core.i.tool.clicked(Core.i.selectedColor(), canvas, x, y);

		if(Core.i.tool.symmetric()){
			if(vSymmetry){
				Core.i.tool.clicked(Core.i.selectedColor(), canvas, canvas.width() - 1 - x, y);
			}

			if(hSymmetry){
				Core.i.tool.clicked(Core.i.selectedColor(), canvas, x, canvas.height() - 1 - y);

				if(vSymmetry){
					Core.i.tool.clicked(Core.i.selectedColor(), canvas, canvas.width() - 1 - x, canvas.height() - 1 - y);
				}
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
		if(newzoom < maxZoom()) newzoom = maxZoom();

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
		Gdx.app.log("pedebugging", "Drawgrid: setting new canvas.");
		if(this.canvas != null){
			Gdx.app.log("pedebugging", "Drawgrid: disposing old canvas: " + this.canvas.name);
			this.canvas.dispose();
		}

		this.canvas = canvas;

		Gdx.app.log("pedebugging", "Drawgrid: new canvas \"" + canvas.name + "\" set.");

		updateSize();

		zoom = maxZoom();

		if(canvas.width() > 100 || canvas.height() > 100) grid = false;

		cursorx = getWidth() / 2;
		cursory = getHeight() / 2;
		selected.set(cursorx / canvasScale(), cursory / canvasScale());
		offsetx = getWidth() / 2;
		offsety = getHeight() / 2;
		gridimage.setImageSize(canvas.width(), canvas.height());
		alphaimage.setImageSize(canvas.width(), canvas.height());

		Core.i.projectmanager.saveProject();
	}

	public void updateCursorSelection(){
		int newx = (int)(cursorx / (canvasScale() * zoom)), newy = (int)(cursory / (canvasScale() * zoom));
		selected.set(newx, newy);
	}

	public void draw(Batch batch, float parentAlpha){
		canvas.update();
		setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Align.center);
		setZIndex(0);

		updateSize();
		updateBounds();
		updateCursor();

		if(clip){
			batch.flush();
			clipBegin(Gdx.graphics.getWidth() / 2 - min() / 2, Gdx.graphics.getHeight() / 2 - min() / 2, min(), min());
		}

		float cscl = canvasScale() * zoom;

		batch.setColor(Color.WHITE);

		//alphaimage.zoom = (int)((int)(zoom))+1;
		//alphaimage.setBounds(getX(), getY(), getWidth(), getHeight());

		//	float modx = ((getX()) % (getWidth() / canvas.texture.getWidth()));
		//float mody = ((getY()) % (getHeight() / canvas.texture.getHeight()));

		//int izoom = (int)zoom;
		alphaimage.setImageSize(canvas.width(), canvas.height());
		//System.out.println(alphaimage.zoom);
		alphaimage.setBounds(getX(), getY(), getWidth(), getHeight());
		alphaimage.draw(batch, parentAlpha);
		//	batch.draw(Textures.get("alpha"), getX(), getY(), canvas.width() * cscl, canvas.height() * cscl, 0, 0, canvas.width(), canvas.height());

		batch.draw(canvas.texture, getX(), getY(), getWidth(), getHeight());

		if(grid){
			gridimage.setBounds(getX(), getY(), getWidth(), getHeight());
			gridimage.draw(batch, parentAlpha);
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
		//batch.setColor(Color.CORAL);

		//float size = 4;
		//batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), (int)(getX() + getWidth() / 2f - size / 2), (int)(getY() + getHeight() / 2f - size / 2), size, size);

		int xt = (int)(4 * (10f / canvas.width() * zoom)); //extra border thickness

		//draw selection
		if((cursormode || (touches > 0 && Core.i.tool.moveCursor())) && Core.i.tool.drawCursor()){
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
		MiscUtils.drawBorder(batch, (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight(), 2, aspectRatio() < 1 ? 1 : 0, aspectRatio() > 1 ? 1 : 0);

		//draw cursor
		if(cursormode || (touches > 0 && Core.i.tool.moveCursor())){
			batch.setColor(Color.PURPLE);
			float csize = 30 * Core.i.prefs.getFloat("cursorsize")*s;
			batch.draw(VisUI.getSkin().getRegion("cursor-normal"), getX() + cursorx - csize / 2, getY() + cursory - csize / 2, csize, csize);
		} //seriously, why is this necessary
		batch.draw(Textures.get("alpha"), -999, -999, 30, 30);

		if(clip){
			clipEnd();
		}
		batch.flush();
		batch.setColor(Color.WHITE);
	}

	private void drawSelection(Batch batch, int x, int y, float cscl, float xt){
		MiscUtils.drawBorder(batch, (int)(getX() + x * cscl), (int)(getY() + y * cscl), cscl, cscl, 4, 2);
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

	public float maxZoom(){
		return Math.min(getWidth() / getHeight(), 1f);
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
