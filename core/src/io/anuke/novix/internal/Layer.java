package io.anuke.novix.internal;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

import io.anuke.novix.Novix;
import io.anuke.novix.Vars;
import io.anuke.novix.filter.Filter;
import io.anuke.ucore.graphics.PixmapUtils;

public class Layer implements Disposable{
	final private static ObjectMap<Integer, ByteBuffer> buffers = new ObjectMap<Integer, ByteBuffer>();
	
	final public String name;
	public boolean visible = true;
	
	final private Pixmap pixmap;
	final private Texture texture;
	
	private Pixmap filterPixmap;
	private Texture filterTexture;
	
	private Color color; // pixmap color
	private Color temp = new Color();
	private float alpha = 1.0f;
	private PixelOperation operation;
	private boolean drawn;

	public Layer(Pixmap pixmap){
		this.pixmap = pixmap;
		name = Vars.control.projects().current().name;
		texture = new Texture(pixmap);
		operation = new PixelOperation(this);
		updateTexture();
	}

	public Layer(int width, int height){
		this(new Pixmap(width, height, Format.RGBA8888));
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public Pixmap getPixmap(){
		return pixmap;
	}

	public void drawPixel(int x, int y, boolean update){
		int preColor = getIntColor(x, y);

		pixmap.drawPixel(x, height() - 1 - y);

		operation.addPixel(this, x, y, preColor, getIntColor(x, y));
		
		if(update){
			//set blank color, since draw color does not equal output color
			PixmapUtils.drawPixel(texture, x, height() - 1 - y, getIntColor(x, y));
		}else{
			drawn = true;
		}
	}

	public void drawPixelBlendless(int x, int y, int color){
		int preColor = getIntColor(x, y);

		pixmap.drawPixel(x, height() - 1 - y, color);

		operation.addPixel(this, x, y, preColor, color);
	}

	public void erasePixel(int x, int y, boolean update){
		int preColor = getIntColor(x, y);

		temp.set(preColor);

		float newalpha = temp.a - alpha;

		if(newalpha <= 0 || MathUtils.isEqual(newalpha, 0)){
			newalpha = 0;
			temp.set(0, 0, 0, newalpha);
		}
		int newcolor = Color.rgba8888(temp.r, temp.g, temp.b, newalpha);

		pixmap.setBlending(Blending.None);

		pixmap.drawPixel(x, height() - 1 - y, newcolor);

		pixmap.setBlending(Blending.SourceOver);
		
		if(update)PixmapUtils.drawPixel(texture, x, height() - 1 - y, newcolor);
		
		operation.addPixel(this, x, y, preColor, newcolor);
		
		drawn = true;
	}

	public void erasePixelFullAlpha(int x, int y){
		int color = getIntColor(x, y);

		pixmap.setBlending(Blending.None);

		pixmap.drawPixel(x, height() - 1 - y, 0);

		pixmap.setBlending(Blending.SourceOver);

		operation.addPixel(this, x, y, color, 0);
	}

	public void drawRadius(int x, int y, int rad){
		texture.bind();
		for(int rx = -rad;rx <= rad;rx ++){
			for(int ry = -rad;ry <= rad;ry ++){
				if(Vector2.dst(rx, ry, 0, 0) < rad - 0.5f) 
					drawPixel(x + rx, y + ry, false);
			}
		}
		drawn = false;
		update(x-rad, y-rad, rad*2, rad*2);
	}

	public void eraseRadius(int x, int y, int rad){
		texture.bind();
		for(int rx = -rad;rx <= rad;rx ++){
			for(int ry = -rad;ry <= rad;ry ++){
				if(Vector2.dst(rx, ry, 0, 0) < rad - 0.5f) 
					erasePixel(x + rx, y + ry, false);
			}
		}
		drawn = false;
		update(x-rad, y-rad, rad*2, rad*2);
	}
	
	private void update(int x, int y, int w, int h){
		if(x < 0) x = 0;
		if(y < 0) y = 0;
		if(x >= width()) x = width()-w/2;
		if(y >= height()) y = height()-h/2;
		if(x + w >= width()) w = width()-x;
		if(y + h >= height()) h = height()-y;
		
		//Novix.log(x + ", " + y + ": " + w + "x" + h);
		
		int size = w*h;
		ByteBuffer buffer = null;
		
		if(!buffers.containsKey(size)){
			buffer = ByteBuffer.allocateDirect(size*4);
			buffers.put(size, buffer);
		}else{
			buffer = buffers.get(size);
		}
		
		buffer.clear();
		buffer.position(0);
		for(int cy = h-1; cy >= 0; cy --){
			for(int cx = 0; cx < w; cx ++){
			
				int color = pixmap.getPixel(x+cx, height()-1-y-cy);
				buffer.putInt(color);
			}
		}
		buffer.position(0);
		
		Gdx.gl.glTexSubImage2D(texture.glTarget, 0, x, height()-y-h, w, h, 6408, 5121,
				buffer);
	}

	public void drawPixel(int x, int y, Color color){
		setColor(color);
		drawPixel(x, y, true);
	}

	public void drawPixelActionless(int x, int y, int color){
		pixmap.setColor(color);
		pixmap.drawPixel(x, height() - 1 - y);
	}

	public void updatePixmapColor(){
		pixmap.setColor(this.color);
	}

	public Color getColor(int x, int y){
		return new Color(pixmap.getPixel(x, height() - 1 - y));
	}

	public int getIntColor(int x, int y){
		return pixmap.getPixel(x, height() - 1 - y);
	}

	public void setAlpha(float alpha){
		this.alpha = alpha;
		setColor(color);
	}

	public void setColor(Color color){
		setColor(color, false);
	}

	public void setColor(Color color, boolean ignoreAlpha){
		this.color = color;
		if( !ignoreAlpha) color.a = alpha;

		pixmap.setColor(color);
	}
	
	public void update(){
		if(drawn){
			drawn = false;
			updateTexture();
		}
	}

	public void updateTexture(){
		if(!Vars.control.saving()){
			texture.draw(pixmap, 0, 0);
			drawn = false;
		}else{
			Novix.log("skipping drawing...");
			drawn = true;
		}
	}

	public void updateAndPush(){
		texture.draw(pixmap, 0, 0);
		pushOperation();
	}

	public void pushOperation(){
		if(operation.isEmpty()) return;
		
		Vars.drawing.pushOperation(operation);
		operation = new PixelOperation(this);
	}

	public int width(){
		return pixmap.getWidth();
	}

	public int height(){
		return pixmap.getHeight();
	}
	
	/*
	public void drawPixmap(Pixmap pixmap){
		for(int x = 0;x < width();x ++){
			for(int y = 0;y < height();y ++){
				operation.addPixel(this, x, y, getIntColor(x, y), pixmap.getPixel(x, height() - 1 - y));
			}
		}
		
		pixmap.setBlending(Blending.None);
		this.pixmap.drawPixmap(pixmap, 0, 0);
		pixmap.setBlending(Blending.SourceOver);

		updateAndPush();
	}
	*/
	
	public void applyPixmap(Pixmap apply){
		pixmap.setBlending(Blending.None);
		pixmap.drawPixmap(apply, 0, 0);
		texture.draw(pixmap, 0, 0);
		pixmap.setBlending(Blending.SourceOver);
	}

	public float getAlpha(){
		return alpha;
	}
	
	public void applyPreviewFilter(Filter filter, Object...args){
		if(filterPixmap == null){
			filterPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Format.RGBA8888);
		}else if(filterPixmap.getWidth() != pixmap.getWidth() || filterPixmap.getHeight() != pixmap.getHeight()){
			filterPixmap.dispose();
			filterPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Format.RGBA8888);
		}
		
		filter.process(pixmap, filterPixmap, args);
		
		if(filterTexture == null){
			filterTexture = new Texture(filterPixmap);
		}else if(filterTexture.getWidth() != texture.getWidth() || filterTexture.getHeight() != texture.getHeight()){
			filterTexture.dispose();
			filterTexture = new Texture(filterPixmap);
		}else{
			filterTexture.draw(filterPixmap, 0, 0);
		}
	}
	
	public void applyFilter(){
		Vars.drawing.pushOperation(new FilterOperation(this, PixmapUtils.copy(pixmap), PixmapUtils.copy(filterPixmap)));
		pixmap.drawPixmap(filterPixmap, 0, 0);
		texture.draw(pixmap, 0, 0);
	}
	
	public Texture getPreviewTexture(){
		return filterTexture;
	}

	@Override
	public void dispose(){
		Novix.log("Disposing canvas: " + name);
		texture.dispose();
		pixmap.dispose();
		
		if(filterPixmap != null)
			filterPixmap.dispose();
		
		if(filterTexture != null)
			filterTexture.dispose();
	}

}
