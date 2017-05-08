package io.anuke.novix.scene2D;


import static io.anuke.ucore.UCore.s;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;

import io.anuke.novix.Novix;
import io.anuke.novix.modules.Core;
import io.anuke.novix.tools.*;
import io.anuke.ucore.graphics.PixmapUtils;
import io.anuke.ucore.graphics.ShapeUtils;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.util.Mathf;
import io.anuke.utools.MiscUtils;
import io.anuke.utools.MiscUtils.GridChecker;

public class DrawingGrid extends Actor{
	private Core core;
	private float cursorx, cursory;
	private int tpointer;
	private int touches = 0;
	private boolean moving;
	private GridPoint2 selected = new GridPoint2();
	private GridImage gridimage;
	private AlphaImage alphaimage;
	private final boolean clip = true;
	private Vector2[][] brushPolygons = new Vector2[10][];
	private Color tempcolor = new Color();

	public PixelCanvas canvas;
	public ActionStack actions = new ActionStack();
	public float zoom = 1f, offsetx = 0, offsety = 0, baseCursorSpeed = 1.03f;
	public boolean vSymmetry = false, hSymmetry = false;
	public int brushSize;
	public GridInput input = new GridInput();
	public class GridInput extends InputAdapter{
		@Override
		public boolean touchDown(int x, int y, int pointer, int button){
			if( !Core.i.tool().moveCursor() || !Core.i.colorMenuCollapsed() || !Core.i.toolMenuCollapsed() || checkRange(y)) return false;
			touches ++;
			if(cursormode()){
				if(moving){
					processToolTap(selected.x, selected.y);
					return true;
				}

				tpointer = pointer;
				moving = true;
			}else{
				cursorx = x - getX();
				cursory = Gdx.graphics.getHeight() - y - getY();
				updateCursorSelection();
				processToolTap(selected.x, selected.y);
				return true;
			}
			return true;
		}
		
		@Override
		public boolean touchUp(int x, int y, int pointer, int button){
			if(touches == 0) return false;
			
			if(cursormode()){
				if(pointer == tpointer){
					moving = false;
				}else{
					if(Core.i.tool().push) canvas.pushActions();
				}
			}else{
				if(Core.i.tool().push) canvas.pushActions();
			}
			
			touches --;
			
			return true;
		}

		//pc debugging
		@Override
		public boolean keyUp(int keycode){
			if(keycode == Keys.E){
				if(Core.i.tool().push){
					canvas.pushActions();
				}
				return true;
			}
			return false;
		}

		//pc debugging
		@Override
		public boolean keyDown(int keycode){
			if(keycode == Keys.E){
				processToolTap(selected.x, selected.y);
				return true;
			}
			return false;
		}
		
		@Override
		public boolean touchDragged(int x, int y, int pointer){
			if(pointer != 0 && Gdx.app.getType() != ApplicationType.Desktop /*|| checkRange(y)*/ || touches == 0 || !Core.i.tool().moveCursor()) return false; //not the second pointer
			float cursorSpeed = baseCursorSpeed * core.prefs.getFloat("cursorspeed", 1f);
			
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
				if(cursormode()){
					int newx = (int)((cursorx + currentx) / (canvasScale() * zoom)), newy = (int)((cursory + currenty) / (canvasScale() * zoom));

					if( !(selected.x == newx && selected.y == newy) && (touches > 1 || Gdx.input.isKeyPressed(Keys.E)) && Core.i.tool().drawOnMove) processToolTap(newx, newy);

					selected.set(newx, newy);
				}else{
					int newx = (int)((cursorx + currentx) / (canvasScale() * zoom)), newy = (int)((cursory + currenty) / (canvasScale() * zoom));

					if( !(selected.x == newx && selected.y == newy) && Core.i.tool().drawOnMove) processToolTap(newx, newy);
					selected.set(newx, newy);
				}
			}

			if(cursormode()){
				if(pointer != tpointer || !Core.i.tool().moveCursor()) return false;

				cursorx += deltax;
				cursory += deltay;

				cursorx = Mathf.clamp(cursorx, 0, getWidth() - 1);
				cursory = Mathf.clamp(cursory, 0, getHeight() - 1);

			}else{
				cursorx = x - getX();
				cursory = (Gdx.graphics.getHeight() - y - getY());
			}
			return true;
		}
		
		public boolean checkRange(int y){
			return !(y > Gdx.graphics.getHeight()/2 - min()/2 && y < Gdx.graphics.getHeight()/2 + min()/2);
		}
	};

	public DrawingGrid(final Core core){
		this.core = core;
		gridimage = new GridImage(1, 1);
		alphaimage = new AlphaImage(1, 1);
		generatePolygons();
	}

	private void processToolTap(int x, int y){
		Core.i.tool().clicked(Core.i.selectedColor(), canvas, x, y);

		if(Core.i.tool().symmetric()){
			if(vSymmetry){
				Core.i.tool().clicked(Core.i.selectedColor(), canvas, canvas.width() - 1 - x, y);
			}

			if(hSymmetry){
				Core.i.tool().clicked(Core.i.selectedColor(), canvas, x, canvas.height() - 1 - y);

				if(vSymmetry){
					Core.i.tool().clicked(Core.i.selectedColor(), canvas, canvas.width() - 1 - x, canvas.height() - 1 - y);
				}
			}
		}
	}

	private void generatePolygons(){
		for(int i = 1;i < 11;i ++){
			final int index = i;

			GridChecker checker = new GridChecker(){
				@Override
				public int getWidth(){
					return index * 2;
				}

				@Override
				public int getHeight(){
					return index * 2;
				}

				@Override
				public boolean exists(int x, int y){
					return Vector2.dst(x - index, y - index, 0, 0) < index - 0.5f;
				}
			};

			brushPolygons[i - 1] = MiscUtils.getOutline(checker);
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
		float max = Math.max(canvas.width(), canvas.height())/5;
		if(newzoom < maxZoom()) newzoom = maxZoom();
		if(newzoom > max) newzoom = max;

		cursorx *= (newzoom / zoom);
		cursory *= (newzoom / zoom);

		zoom = newzoom;

		updateSize();

		updateBounds();
	}

	public void setCursor(float x, float y){
		cursorx = x;
		cursory = y;
		cursorx = Mathf.clamp(cursorx, 0, getWidth() - 1);
		cursory = Mathf.clamp(cursory, 0, getHeight() - 1);
		int newx = (int)(cursorx / (canvasScale() * zoom)), newy = (int)(cursory / (canvasScale() * zoom));

		selected.set(newx, newy);
	}
	
	public void clearActionStack(){
		actions.dispose();
		actions = new ActionStack();
	}

	public void setCanvas(PixelCanvas canvas, boolean saveOp){
		Novix.log("Drawgrid: setting new canvas.");
		
		
		if(this.canvas != null){
			Novix.log("this.Pixmap disposed at start?" + PixmapUtils.isDisposed(this.canvas.pixmap));
			if(saveOp){
				Novix.log("Drawgrid: performing switch operation: " + this.canvas.name);
				DrawAction action = new DrawAction();
				action.fromCanvas = this.canvas;
				action.toCanvas = canvas;
				actions.add(action);
			}else{
				Novix.log("Drawgrid: disposing old canvas: " + this.canvas.name);
				this.canvas.dispose();
			}
		}

		resetCanvas(canvas);
		Novix.log("Pixmap disposed at end?" + PixmapUtils.isDisposed(canvas.pixmap));
	}
	
	/**Used for undo operations only.*/
	public void actionSetCanvas(PixelCanvas canvas){
		Novix.log("Drawgrid: undoing operation.");
		resetCanvas(canvas);
	}
	
	/**Called internally. This simply sets the current canvas and performs no other operations.*/
	private void resetCanvas(PixelCanvas canvas){
		this.canvas = canvas;

		Novix.log("Drawgrid: new canvas \"" + canvas.name + "\" set.");

		updateSize();
		updateBounds();

		zoom = maxZoom();
		
		cursorx = getWidth() / 2;
		cursory = getHeight() / 2;
		selected.set((int)(cursorx / canvasScale()), (int)(cursory / canvasScale()));
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

		int asize = 20;
		int u = (int)(((getWidth()/s) / asize) / canvas.width()) * canvas.width();
		
		if(u == 0){
			u = (int)(getWidth() / asize);
			if(u == 0) u = 1;
			
			u = u > canvas.width() ? canvas.width() : canvas.width()/(canvas.width() / u);
		}
		
		batch.draw(Textures.get("alpha"), getX(), getY(), getWidth(), getHeight(), u, u / ((float)canvas.width() / canvas.height()), 0, 0);

		alphaimage.setImageSize(canvas.width(), canvas.height());
		alphaimage.setBounds(getX(), getY(), getWidth(), getHeight());
		//alphaimage.draw(batch, parentAlpha);

		batch.draw(canvas.texture, getX(), getY(), getWidth(), getHeight());

		if(core.prefs.getBoolean("grid", true)){
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
		if((cursormode() || (touches > 0 && Core.i.tool().moveCursor())) && Core.i.tool().drawCursor()){
			tempcolor.set(canvas.getIntColor(selected.x, selected.y));
			//tempcolor.r = 1f - tempcolor.r;
			//tempcolor.g = 1f - tempcolor.g;
			//tempcolor.b = 1f - tempcolor.b;
			float sum = tempcolor.r + tempcolor.g + tempcolor.b;
			int a = 18;
			if(sum >= 1.5f && tempcolor.a >= 0.01f && !(core.tool().scalable() && core.prefs.getInteger("brushsize") > 1)){
				tempcolor.set((14 + a) / 255f, (15 + a) / 255f, (36 + a) / 255f, 1);
			}else{
				tempcolor.set(Color.CORAL);
			}
			tempcolor.a = 1f;

			batch.setColor(tempcolor);

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
		MiscUtils.drawBorder(batch, (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight(), (int)(2*s), aspectRatio() < 1 ? 1 : 0, aspectRatio() > 1 ? 1 : 0);

		//draw cursor
		if(cursormode() || (touches > 0 && Core.i.tool().moveCursor()) || !Core.i.tool().moveCursor()){
			batch.setColor(Color.PURPLE);
			float csize = 32 * core.prefs.getFloat("cursorsize", 1f) * s;
			
			batch.draw(Textures.get(Core.i.tool().cursor), getX() + cursorx - csize / 2, getY() + cursory - csize / 2, csize, csize);
			
			batch.setColor(Color.CORAL);
			
			if(Core.i.tool() != Tool.pencil && Core.i.tool() != Tool.zoom) 	
				batch.draw(VisUI.getSkin().getRegion("icon-" + Core.i.tool().name()), getX() + cursorx, getY() + cursory, csize, csize);
		} //seriously, why is this necessary
		batch.draw(Textures.get("alpha"), -999, -999, 30, 30);

		if(clip){
			clipEnd();
		}
		batch.flush();
		batch.setColor(Color.WHITE);
	}

	private void drawSelection(Batch batch, int x, int y, float cscl, float xt){
		ShapeUtils.thickness = (int)(4*s);
		ShapeUtils.polygon(batch, !Core.i.tool().scalable() ? brushPolygons[0] : brushPolygons[brushSize - 1], (int)(getX() + x * cscl), (int)(getY() + y * cscl), cscl);
	}

	public void updateCursor(){
		if(cursorx > Gdx.graphics.getWidth() - getX()) cursorx = Gdx.graphics.getWidth() - getX();
		if(cursory > Gdx.graphics.getHeight() / 2 + Gdx.graphics.getWidth() / 2 - getY()) cursory = Gdx.graphics.getHeight() / 2 + Gdx.graphics.getWidth() / 2 - getY();

		if(cursorx < -getX()) cursorx = -getX();
		if(cursory < Gdx.graphics.getHeight() / 2 - Gdx.graphics.getWidth() / 2 - getY()) cursory = Gdx.graphics.getHeight() / 2 - Gdx.graphics.getWidth() / 2 - getY();
		
		cursorx = Mathf.clamp(cursorx, 0, getWidth() - 1);
		cursory = Mathf.clamp(cursory, 0, getHeight() - 1);
		
		updateCursorSelection();

	}

	public void updateBounds(){
		int toolheight = (Gdx.graphics.getHeight() - Gdx.graphics.getWidth()) / 2, colorheight = toolheight;

		if(aspectRatio() >= 1f){
			if(getX() + getWidth() < Gdx.graphics.getWidth()) offsetx = -(Gdx.graphics.getWidth() / 2 - getWidth()) / zoom;
			if(getX() > 0) offsetx = Gdx.graphics.getWidth() / 2 / zoom;
			
			if(getHeight() > Gdx.graphics.getWidth()){
				if(getY() + getHeight() < Gdx.graphics.getHeight() - colorheight) offsety = -(Gdx.graphics.getHeight() / 2 - getHeight() - colorheight) / zoom;
				if(getY() > toolheight) offsety = (Gdx.graphics.getHeight() / 2 - toolheight) / zoom;
			}else{
				offsety = getHeight()/2/zoom;
			}
		}

		if(aspectRatio() <= 1f){
			if(getY() + getHeight() < Gdx.graphics.getHeight() - colorheight) offsety = -(Gdx.graphics.getHeight() / 2 - getHeight() - colorheight) / zoom;
			if(getY() > toolheight) offsety = (Gdx.graphics.getHeight() / 2 - toolheight) / zoom;
			
			if(getWidth() > Gdx.graphics.getWidth()){
				if(getX() + getWidth() < Gdx.graphics.getWidth()) offsetx = -(Gdx.graphics.getWidth() / 2 - getWidth()) / zoom;
				if(getX() > 0) offsetx = Gdx.graphics.getWidth() / 2 / zoom;
			}else{
				offsetx = getWidth()/2/zoom;
			}
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

	boolean cursormode(){
		return core.prefs.getBoolean("cursormode");
	}
}
