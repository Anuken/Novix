package net.pixelstatic.pixeleditor.tools;

import net.pixelstatic.pixeleditor.graphics.PixelCanvas;
import net.pixelstatic.pixeleditor.modules.GUI;
import net.pixelstatic.utils.Pos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Keys;

public class DrawAction{
	public ObjectMap<Integer, ColorPair> positions = new ObjectMap<Integer, ColorPair>();

	public void push(int x, int y, Color from, Color to){
		int key = Pos.asInt(x, y, GUI.gui.drawgrid.canvas.width());
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

	public void apply(PixelCanvas canvas, boolean reapply){
		
		Keys<Integer> keys = positions.keys();
		
		for(Integer i : keys){
			ColorPair pos = positions.get(i);
			int x = i % GUI.gui.drawgrid.canvas.width();
			int y = i / GUI.gui.drawgrid.canvas.width();
			
			Pixmap.setBlending(Blending.None);
			canvas.drawPixelActionless(x, y, reapply ? pos.tocolor : pos.fromcolor);

			Pixmap.setBlending(Blending.SourceOver);
		}

		canvas.updateTexture();
	}

	class ColorPair{
		Color fromcolor;
		Color tocolor;

		public ColorPair(Color fromcolor, Color tocolor){
			this.fromcolor = fromcolor;
			this.tocolor = tocolor;
		}
	}
	
	
	public String toString(){
		return "DrawAction: " + positions.size + "x";
	}
}
