package net.pixelstatic.pixeleditor.graphics;

import net.pixelstatic.pixeleditor.tools.ActionStack;
import net.pixelstatic.pixeleditor.tools.DrawAction;
import net.pixelstatic.utils.graphics.PixmapUtils;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class PixelCanvas implements Disposable{
	private Color color; // pixmap color
	private Pixmap blank;
	final public Pixmap pixmap;
	final public Texture texture;
	private float alpha = 1.0f; 
	private DrawAction action = new DrawAction();
	public ActionStack actions = new ActionStack(this);
	
	public PixelCanvas(Pixmap pixmap){
		this.pixmap = pixmap;
		texture = new Texture(pixmap);
		blank = PixmapUtils.blankPixmap();
		updateTexture();
	}
	
	public PixelCanvas(int width, int height){
		pixmap = new Pixmap(width, height, Format.RGBA8888);
		texture = new Texture(pixmap);
		blank = PixmapUtils.blankPixmap();
		updateTexture();
	}
	
	public void drawPixel(int x, int y){
		action.push(x, y, getIntColor(x,y), Color.rgba8888(color));
		pixmap.drawPixel(x, height() -1 - y); 
		if(!MathUtils.isEqual(alpha, 1f)){
			Pixmap.setBlending(Blending.None);
			blank.drawPixel(0, 0, pixmap.getPixel(x, height() - 1 - y));
			Pixmap.setBlending(Blending.SourceOver);
		}
		texture.draw(blank, x, height() -1 - y);
	}
	
	public void drawRadius(int x, int y, int rad){
		for(int rx = -rad; rx <= rad; rx ++){
			for(int ry = -rad; ry <= rad; ry ++){
				if(Vector2.dst(rx, ry, 0, 0) < rad - 0.5f)
				drawPixel(x + rx,y + ry);
			}
		}
	}
	
	public void drawPixel(int x, int y, Color color){
		setColor(color);
		drawPixel(x, y); 
	}
	
	public void drawPixelActionless(int x, int y, int color){
		pixmap.setColor(color);
		pixmap.drawPixel(x, height() -1 - y); 
	}
	
	public Color getColor(int x, int y){
		return new Color(pixmap.getPixel(x, height() -1 - y));
	}
	
	public int getIntColor(int x, int y){
		return pixmap.getPixel(x, height() -1 - y);
	}
	
	public void setAlpha(float alpha){
		this.alpha = alpha;
		setColor(color);
	}
	
	public void setColor(Color color){
		this.color = color;
		color.a = alpha;
		blank.setColor(color);
		
		Pixmap.setBlending(Blending.None);
		blank.drawPixel(0, 0);
		Pixmap.setBlending(Blending.SourceOver);
		
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
	
	public PixelCanvas asResized(int newwidth, int newheight){
		return new PixelCanvas(PixmapUtils.resize(pixmap, newwidth, newheight));
	}
	
	public void drawPixmap(Pixmap pixmap){
		for(int x = 0; x < width(); x ++){
			for(int y = 0; y < height(); y ++){
				action.push(x, y, getIntColor(x,y), pixmap.getPixel(x, height() - 1 - y));
			}
		}
		
		Pixmap.setBlending(Blending.None);
		this.pixmap.drawPixmap(pixmap, 0, 0);
		Pixmap.setBlending(Blending.SourceOver);
		
		
		updateAndPush();
	}

	@Override
	public void dispose(){
		texture.dispose();
		pixmap.dispose();
		blank.dispose();
	}

}
