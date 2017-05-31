package io.anuke.novix;

import static io.anuke.novix.Vars.*;

import java.util.Arrays;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import io.anuke.novix.element.AlphaImage;
import io.anuke.novix.element.GridImage;
import io.anuke.novix.internal.*;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.core.Settings;
import io.anuke.ucore.modules.Module;
import io.anuke.ucore.scene.Element;
import io.anuke.ucore.scene.ui.layout.Unit;
import io.anuke.ucore.util.Mathf;


public class Drawing extends Module{
	private final boolean clip = true;
	
	private GridPoint2 selected = new GridPoint2();
	private float cursorx, cursory;
	private Vector2[][] brushPolygons = new Vector2[10][];
	private int tpointer;
	private int touches = 0;
	private boolean moving;
	
	private Color tempcolor = new Color();
	private float baseCursorSpeed = 1.03f;
	
	private OperationStack operations = new OperationStack();
	private Layer[] layers;
	private Layer layer;
	private DrawingGrid grid;
	
	public Drawing(){
		generatePolygons();
		
		grid = new DrawingGrid();
	}
	
	@Override
	public void init(){
		DrawContext.scene.add(grid);
	}
	
	@Override
	public void update(){
		
		if(control.tool() == Tool.zoom)
			grid.setCursor(Gdx.graphics.getWidth()/2 - grid.getX(), Gdx.graphics.getHeight()/2 - grid.getY());
	}
	
	/*
	public static void exportProject(FileHandle file){
		try{
			if( !file.extension().equalsIgnoreCase("png")) file = file.parent().child(file.nameWithoutExtension() + ".png");
			PixmapIO.writePNG(file, pixmap);
			showInfo(stage, "Image exported to " + file + ".");
		}catch(Exception e){
			e.printStackTrace();
			showError(stage, e);
		}
	}
	*/
	public void loadLayers(Layer[] layers){
		operations.dispose();
		
		operations = new OperationStack();
		
		Novix.log("Loading layers... " + Arrays.toString(layers));
		
		this.layers = layers;
		this.layer = layers[0];
		
		grid.resetView();
	}
	
	public int width(){
		return layer.width();
	}
	
	public int height(){
		return layer.height();
	}
	
	public GestureDetector getGestureDetector(){
		GestureDetector gesture = new GestureDetector(20, 0.5f, 2, 0.15f, new GestureDetectorListener());
		return gesture;
	}
	
	public boolean isImageLarge(){
		return layer.width() * layer.height() > largeImageSize;
	}
	
	public void undo(){
		//TODO
		operations.undo();
	}
	
	public void redo(){
		//TODO
		operations.redo();
	}
	
	public void pushOperation(DrawOperation action){
		//TODO
		operations.add(action);
	}
	
	public Layer getLayer(){
		return layer;
	}
	
	public Layer getLayer(int index){
		return layers[index];
	}
	
	public void updateGrid(){
		grid.updateBounds();
		grid.updateSize();
	}
	
	public void moveGrid(float x, float y){
		grid.moveOffset(x, y);
	}
	
	public boolean hasMouse(float y){
		return !grid.checkRange((int)y);
	}
	
	public void resetZoom(){
		grid.zoom = 1f;
	}
	
	@Override
	public boolean scrolled(int amount){
		//PC debugging
		float newzoom = grid.zoom - (amount / 10f * grid.zoom);
		grid.setZoom(newzoom);
		return false;
	}
	
	@Override
	public boolean touchDown(int x, int y, int pointer, int button){
		if( !control.tool().move() || ui.menuOpen() || grid.checkRange(y)) return false;
		
		touches ++;
		if(cursorMode()){
			if(moving){
				processToolTap(selected.x, selected.y);
				return true;
			}

			tpointer = pointer;
			moving = true;
		}else{
			cursorx = x - grid.getX();
			cursory = Gdx.graphics.getHeight() - y - grid.getY();
			grid.updateCursorSelection();
			processToolTap(selected.x, selected.y);
			return true;
		}
		return true;
	}
	
	@Override
	public boolean touchUp(int x, int y, int pointer, int button){
		if(touches == 0) return false;
		
		if(cursorMode()){
			if(pointer == tpointer){
				moving = false;
			}else{
				if(control.tool().push()) layer.pushOperation();
			}
		}else{
			if(control.tool().push()) layer.pushOperation();
		}
		
		touches --;
		
		return true;
	}

	//pc debugging
	@Override
	public boolean keyUp(int keycode){
		if(keycode == Keys.E){
			if(control.tool().draw()){
				layer.pushOperation();
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
		if(pointer != 0 && Gdx.app.getType() != ApplicationType.Desktop /*|| checkRange(y)*/ || touches == 0 || !control.tool().move()) return false; //not the second pointer
		float cursorSpeed = baseCursorSpeed * Settings.getFloat("cursorspeed");
		
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

		for(int i = 0; i < move; i ++){
			currentx += movex;
			currenty += movey;
			
			int newx = (int)((cursorx + currentx) / (grid.layerScale() * grid.zoom)), 
					newy = (int)((cursory + currenty) / (grid.layerScale() * grid.zoom));

			if(cursorMode()){
				if( !(selected.x == newx && selected.y == newy) && (touches > 1 || Gdx.input.isKeyPressed(Keys.E)) 
						&& control.tool().drawMove()) 
					processToolTap(newx, newy);

			}else{
				if( !(selected.x == newx && selected.y == newy) 
						&& control.tool().drawMove()) 
					processToolTap(newx, newy);
			}
			
			selected.set(newx, newy);
		}

		if(cursorMode()){
			if(pointer != tpointer || !control.tool().move()) return false;

			cursorx += deltax;
			cursory += deltay;

			cursorx = Mathf.clamp(cursorx, 0, grid.getWidth() - 1);
			cursory = Mathf.clamp(cursory, 0, grid.getHeight() - 1);

		}else{
			cursorx = x - grid.getX();
			cursory = (Gdx.graphics.getHeight() - y - grid.getY());
		}
		return true;
	}
	
	private void processToolTap(int x, int y){
		control.tool().clicked(layer, x, y);

		if(control.tool().symmetric()){
			if(vSymmetry()){
				control.tool().clicked(layer, layer.width() - 1 - x, y);
			}

			if(hSymmetry()){
				control.tool().clicked(layer, x, layer.height() - 1 - y);

				if(vSymmetry()){
					control.tool().clicked(layer, layer.width() - 1 - x, layer.height() - 1 - y);
				}
			}
		}
	}
	
	private void generatePolygons(){
		for(int i = 1; i < 11; i ++){
			/*
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
			*/
			//brushPolygons[i - 1] = MiscUtils.getOutline(checker);
		}
	}
	
	private int brushSize(){
		return Settings.getInt("brushsize");
	}
	
	private boolean hSymmetry(){
		return Settings.getBool("hsymmetry");
	}
	
	private boolean vSymmetry(){
		return Settings.getBool("vsymmetry");
	}
	
	private boolean cursorMode(){
		return Settings.getBool("cursormode");
	}
	
	public static enum Operation{
		pixels
	}
	
	public class GestureDetectorListener extends GestureAdapter{
		float initzoom = 1f;
		Vector2 lastinitialpinch = new Vector2();
		Vector2 lastpinch;
		Vector2 to = new Vector2();
		Vector2 toward = new Vector2();
		
		@Override
		public boolean pan(float x, float y, float deltaX, float deltaY){
			if(ui.menuOpen()) return false;
			
			if(control.tool() == Tool.zoom){
				grid.offsetx -= deltaX / grid.zoom;
				grid.offsety += deltaY / grid.zoom;
				grid.updateSize();
				grid.updateBounds();
			}
			return false;
		}

		@Override
		public boolean zoom(float initialDistance, float distance){
			if(control.tool() != Tool.zoom || ui.menuOpen()) return false;
			float s = distance / initialDistance;
			float newzoom = initzoom * s;
			if(newzoom < grid.maxZoom()) newzoom = grid.maxZoom();
			grid.setZoom(newzoom);
			return false;
		}

		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2){
			if(control.tool() != Tool.zoom || ui.menuOpen()) return false;

			Vector2 afirst = initialPointer1.cpy().add(initialPointer2).scl(0.5f);
			Vector2 alast = pointer1.cpy().add(pointer2).scl(0.5f);

			if( !afirst.epsilonEquals(lastinitialpinch, 0.1f)){
				lastinitialpinch = afirst;
				lastpinch = afirst.cpy();
				toward.set(grid.offsetx, grid.offsety);
				to.x = (afirst.x - Gdx.graphics.getWidth() / 2) / grid.zoom + grid.offsetx;
				to.y = ((Gdx.graphics.getHeight() - afirst.y) - Gdx.graphics.getHeight() / 2) / grid.zoom + grid.offsety;
			}

			lastpinch.sub(alast);

			grid.moveOffset(lastpinch.x / grid.zoom, -lastpinch.y / grid.zoom);

			lastpinch = alast;

			return false;
		}

		@Override
		public boolean panStop(float x, float y, int pointer, int button){
			initzoom = grid.zoom;
			return false;
		}
	}
	
	private class DrawingGrid extends Element{
		private float zoom = 1f, offsetx = 0, offsety = 0;
		private GridImage gridimage;
		private AlphaImage alphaimage;

		public DrawingGrid(){
			gridimage = new GridImage(1, 1);
			alphaimage = new AlphaImage(1, 1);
			generatePolygons();
		}
		
		public boolean checkRange(int y){
			return !(y > Gdx.graphics.getHeight()/2 - min()/2 && y < Gdx.graphics.getHeight()/2 + min()/2);
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
			float max = Math.max(layer.width(), layer.height())/5;
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
			int newx = (int)(cursorx / (layerScale() * zoom)), newy = (int)(cursory / (layerScale() * zoom));

			selected.set(newx, newy);
		}
		
		/**Called internally. This simply resets the view.*/
		private void resetView(){

			updateSize();
			updateBounds();

			zoom = maxZoom();
			
			cursorx = getWidth() / 2;
			cursory = getHeight() / 2;
			selected.set((int)(cursorx / layerScale()), (int)(cursory / layerScale()));
			offsetx = getWidth() / 2;
			offsety = getHeight() / 2;
			gridimage.setImageSize(layer.width(), layer.height());
			alphaimage.setImageSize(layer.width(), layer.height());
		}

		public void updateCursorSelection(){
			int newx = (int)(cursorx / (layerScale() * zoom)), newy = (int)(cursory / (layerScale() * zoom));
			selected.set(newx, newy);
		}

		public void draw(Batch batch, float parentAlpha){
			layer.update();
			setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Align.center);
			setZIndex(0);

			updateSize();
			updateBounds();
			updateCursor();
			
			
			float s = Unit.dp.inPixels(1f);
			
			if(clip){
				batch.flush();
				clipBegin(Gdx.graphics.getWidth() / 2 - min() / 2, Gdx.graphics.getHeight() / 2 - min() / 2, min(), min());
			}

			float cscl = layerScale() * zoom;

			batch.setColor(Color.WHITE);

			int asize = 20;
			int u = (int)(((getWidth()/s) / asize) / layer.width()) * layer.width();
			
			if(u == 0){
				u = (int)(getWidth() / asize);
				if(u == 0) u = 1;
				
				u = u > layer.width() ? layer.width() : layer.width()/(layer.width() / u);
			}
			
			//batch.draw(Textures.get("alpha"), getX(), getY(), getWidth(), getHeight(), u, u / ((float)layer.width() / layer.height()), 0, 0);

			alphaimage.setImageSize(layer.width(), layer.height());
			alphaimage.setBounds(getX(), getY(), getWidth(), getHeight());
			
			for(int i = 0; i < layers.length; i ++){
				if(layers[i].visible)
					batch.draw(layers[i].getTexture(), getX(), getY(), getWidth(), getHeight());
			}

			if(Settings.getBool("grid")){
				gridimage.setBounds(getX(), getY(), getWidth(), getHeight());
				gridimage.draw(batch, parentAlpha);
			}

			//draw symmetry lines
			if(vSymmetry()){
				batch.setColor(Color.CYAN);
				batch.draw(Draw.region("white"), (int)(getX() + getWidth() / 2f - 2f), getY(), 4, getHeight());
			}

			if(hSymmetry()){
				batch.setColor(Color.PURPLE);
				batch.draw(Draw.region("white"), getX(), (int)(getY() + getHeight() / 2f - 2f), getWidth(), 4);
			}

			//draw center of grid
			int xt = (int)(4 * (10f / layer.width() * zoom)); //extra border thickness

			//draw selection
			if((cursorMode() || (touches > 0 && control.tool().move())) && control.tool().drawCursor()){
				tempcolor.set(layer.getIntColor(selected.x, selected.y));
				float sum = tempcolor.r + tempcolor.g + tempcolor.b;
				int a = 18;
				if(sum >= 1.5f && tempcolor.a >= 0.01f && !(control.tool().scalable() && brushSize() > 1)){
					tempcolor.set((14 + a) / 255f, (15 + a) / 255f, (36 + a) / 255f, 1);
				}else{
					tempcolor.set(Color.CORAL);
				}
				tempcolor.a = 1f;

				batch.setColor(tempcolor);

				drawSelection(batch, selected.x, selected.y, cscl, xt);

				//	batch.setColor(Hue.blend(Color.CORAL, Color.WHITE, 0.5f));
				if(vSymmetry()){
					drawSelection(batch, layer.width() - 1 - selected.x, selected.y, cscl, xt);
				}

				if(hSymmetry()){
					drawSelection(batch, selected.x, layer.height() - 1 - selected.y, cscl, xt);
				}

				if(vSymmetry() && hSymmetry()){
					drawSelection(batch, layer.width() - 1 - selected.x, layer.height() - 1 - selected.y, cscl, xt);
				}
			}

			batch.setColor(Color.GRAY);
			
			Draw.thick(2*s);

			//draw screen edges
			Draw.linerect(Gdx.graphics.getWidth() / 2 - min() / 2f, Gdx.graphics.getHeight() / 2 - min() / 2f, min(), min(), 2);

			batch.setColor(Color.CORAL);
			
			//draw pic edges
			Draw.linerect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight(), aspectRatio() < 1 ? 1 : 0, aspectRatio() > 1 ? 1 : 0);

			//draw cursor
			if(cursorMode() || (touches > 0 && control.tool().move()) || !control.tool().move()){
				Draw.color(Color.PURPLE);
				float csize = 32 * Settings.getFloat("cursorsize") * s;
				
				//TODO
				//batch.draw(Textures.get(control.tool().cursor()), getX() + cursorx - csize / 2, getY() + cursory - csize / 2, csize, csize);
				
				Draw.color(Color.CORAL);
				
				if(control.tool() != Tool.pencil && control.tool() != Tool.zoom) 	
					Draw.crect("icon-" + control.tool().name(), getX() + cursorx, getY() + cursory, csize, csize);
			} //seriously, why is this necessary
			
			//TODO
			//batch.draw(Textures.get("alpha"), -999, -999, 30, 30);

			if(clip){
				clipEnd();
			}
			batch.flush();
			batch.setColor(Color.WHITE);
		}

		private void drawSelection(Batch batch, int x, int y, float cscl, float xt){
			Draw.thick(Unit.dp.inPixels(4));
			Draw.polygon(!control.tool().scalable() ? brushPolygons[0] : brushPolygons[brushSize() - 1], (int)(getX() + x * cscl), (int)(getY() + y * cscl), cscl);
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
			setHeight(min() / layer.width() * layer.height() * zoom);

			setX(Gdx.graphics.getWidth() / 2 - offsetx * zoom);
			setY(Gdx.graphics.getHeight() / 2 - offsety * zoom);
		}

		public float maxZoom(){
			return Math.min(getWidth() / getHeight(), 1f);
		}

		public float aspectRatio(){
			return getWidth() / getHeight();
		}

		public float layerScale(){
			return min() / layer.width();
		}

		public float min(){
			return Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}
}
