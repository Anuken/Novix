package net.pixelstatic.pixeleditor.graphics;

import net.pixelstatic.pixeleditor.tools.ActionStack;
import net.pixelstatic.pixeleditor.tools.DrawAction;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.Disposable;

public class PixelCanvas implements Disposable{
	private Color color; // pixmap color
	final public Pixmap pixmap;
	final public Texture texture;
	private DrawAction action = new DrawAction();
	public ActionStack actions = new ActionStack(this);
	
	public PixelCanvas(Pixmap pixmap){
		this.pixmap = pixmap;
		texture = new Texture(pixmap);
		updateTexture();
	}
	
	public PixelCanvas(int width, int height){
		pixmap = new Pixmap(width, height, Format.RGBA8888);
		texture = new Texture(pixmap);
		updateTexture();
	}
	
	public void drawPixel(int x, int y){
		action.push(x, y, getColor(x,y), color.cpy());
		pixmap.drawPixel(x, height() -1 - y); 
	}
	
	public void drawPixel(int x, int y, Color color){
		setColor(color);
		drawPixel(x, y); 
	}
	
	public void drawPixelActionless(int x, int y, Color color){
		pixmap.setColor(color);
		pixmap.drawPixel(x, height() -1 - y); 
	}
	
	public Color getColor(int x, int y){
		return new Color(pixmap.getPixel(x, height() -1 - y));
	}
	
	public void setColor(Color color){
		this.color = color;
		pixmap.setColor(color);
	}
	
	public void updateTexture(){
		texture.draw(pixmap, 0, 0);
	}
	
	public void updateAndPush(){
		texture.draw(pixmap, 0, 0);
		pushActions();
	}
	
	public void pushActions(){
		if(action.positions.size == 0) return;
		actions.add(action);
		action = new DrawAction();
	}
	
	public int width(){
		return pixmap.getWidth();
	}
	
	public int height(){
		return pixmap.getHeight();
	}

	@Override
	public void dispose(){
		texture.dispose();
		pixmap.dispose();
	}

}
