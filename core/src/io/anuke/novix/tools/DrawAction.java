package io.anuke.novix.tools;

import static io.anuke.novix.Var.drawing;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Keys;

import io.anuke.utools.MiscUtils;

public class DrawAction{
	public ObjectMap<Integer, ColorPair> positions = new ObjectMap<Integer, ColorPair>();
	public Layer fromCanvas; //used only for undoing image operations that change the canvas
	public Layer toCanvas;
	

	public void push(int x, int y, int from, int to){
		if(from == to) return; //ignore action that doesn't do anything
		int key = MiscUtils.asInt(x, y, drawing.width());
		if(positions.containsKey(key)){
			ColorPair pos = positions.get(key);
			pos.tocolor = to;
		}else{
			positions.put(key, new ColorPair(from, to));
		}
	}

	public void clear(){
		positions.clear();
	}

	public void apply(Layer canvas, boolean reapply){
		if(fromCanvas != null){
			//TODO
			//io.anuke.novix.i.drawgrid.actionSetCanvas(reapply ? toCanvas : fromCanvas);
			return;
		}
		Keys<Integer> keys = positions.keys();
		
		for(Integer i : keys){
			ColorPair pos = positions.get(i);
			int x = i % drawing.width();
			int y = i / drawing.width();
			
			Pixmap.setBlending(Blending.None);
			canvas.drawPixelActionless(x, y, reapply ? pos.tocolor : pos.fromcolor);

			Pixmap.setBlending(Blending.SourceOver);
		}
		canvas.updatePixmapColor();
		canvas.updateTexture();
	}

	class ColorPair{
		int fromcolor;
		int tocolor;

		public ColorPair(int fromcolor, int tocolor){
			this.fromcolor = fromcolor;
			this.tocolor = tocolor;
		}
	}
	
	public String toString(){
		return "DrawAction: " + positions.size + "x";
	}
	
	long pair(int a, int b){
		return (((long)a) << 32) | (b & 0xffffffffL);
	}
	
	int from(long l){
		return (int)(l >> 32);
	}
	
	int to(long l){
		return (int)l;
	}
}
