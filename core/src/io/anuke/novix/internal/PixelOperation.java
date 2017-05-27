package io.anuke.novix.internal;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.utils.ObjectMap;

import io.anuke.utools.MiscUtils;

public class PixelOperation extends DrawOperation{
	
	public PixelOperation(Layer layer) {
		super(layer);
	}

	private ObjectMap<Integer, ColorPair> positions = new ObjectMap<Integer, ColorPair>();
	
	public void addPixel(Layer layer, int x, int y, int from, int to){
		if(from == to) return; //ignore action that doesn't do anything
		int key = MiscUtils.asInt(x, y, layer.width());
		if(positions.containsKey(key)){
			ColorPair pos = positions.get(key);
			pos.tocolor = to;
		}else{
			positions.put(key, new ColorPair(from, to));
		}
	}
	
	public boolean isEmpty(){
		return positions.size == 0;
	}

	@Override
	public void apply(){
		set(false);
	}

	@Override
	public void reapply(){
		set(true);
	}
	
	private void set(boolean reapply){
		
		for(Integer i : positions.keys()){
			ColorPair pos = positions.get(i);
			int x = i % layer.width();
			int y = i / layer.width();
			
			Pixmap.setBlending(Blending.None);
			layer.drawPixelActionless(x, y, reapply ? pos.tocolor : pos.fromcolor);

			Pixmap.setBlending(Blending.SourceOver);
		}
		
		layer.updatePixmapColor();
		layer.updateTexture();
	}
	
	private class ColorPair{
		int fromcolor;
		int tocolor;

		public ColorPair(int fromcolor, int tocolor){
			this.fromcolor = fromcolor;
			this.tocolor = tocolor;
		}
	}

}
