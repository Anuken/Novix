package io.anuke.novix.internal;

import java.util.Stack;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.IntSet;

import io.anuke.novix.internal.NovixEvent.ColorPick;
import io.anuke.ucore.core.Events;
import io.anuke.ucore.core.Settings;

public enum Tool{
	pencil{
		{
			scalable = true;
		}
		
		public void clicked(Layer layer, int x, int y){
			layer.drawRadius(x, y, Settings.getInt("brushsize"));
		}
	}, 
	eraser{
		{
			scalable = true;
		}
		
		public void clicked(Layer layer, int x, int y){
			layer.eraseRadius(x, y,  Settings.getInt("brushsize"));
		}
	},
	fill{
		{
			drawmove = false;
		}
		
		public void clicked(Layer canvas, int x, int y){
			canvas.getTexture().bind();
			
			int dest = canvas.getIntColor(x, y);

			//if(colorEquals(color, dest)) return;
			int width = canvas.width();

			IntSet set = new IntSet();
			Stack<GridPoint2> points = new Stack<GridPoint2>();
			points.add(new GridPoint2(x, y));

			while( !points.isEmpty()){
				GridPoint2 pos = points.pop();
				set.add(asInt(pos.x, pos.y, width));

				int pcolor = canvas.getIntColor(pos.x, pos.y);
				if(colorEquals(pcolor, dest)){

					canvas.drawPixel(pos.x, pos.y, false);

					if(pos.x > 0 && !set.contains(asInt(pos.x - 1, pos.y, width))) points.add(new GridPoint2(pos).cpy().add( -1, 0));
					if(pos.y > 0 && !set.contains(asInt(pos.x, pos.y - 1, width))) points.add(new GridPoint2(pos).cpy().add(0, -1));
					if(pos.x < canvas.width() - 1 && !set.contains(asInt(pos.x + 1, pos.y, width))) points.add(new GridPoint2(pos).cpy().add(1, 0));
					if(pos.y < canvas.height() - 1 && !set.contains(asInt(pos.x, pos.y + 1, width))) points.add(new GridPoint2(pos).cpy().add(0, 1));
				}
			}

			canvas.updateTexture();
		}
		
		int asInt(int x, int y, int width){
			return x+y*width;
		}
		
		boolean colorEquals(int a, int b){
			return a == b;
		}
	},
	pick{
		{
			symmetric = false;
			draw = false;
			push = false;
		}
		
		public void clicked(Layer layer, int x, int y){
			Events.fire(ColorPick.class, layer.getColor(x, y));
			//TODO
			/*
			Color selected = layer.getColor(x, y);
			
			for(int i = 0; i < core.getCurrentPalette().size(); i ++){
				if(Hue.approximate(selected, core.getCurrentPalette().colors[i], 0.001f)){
					core.setSelectedColor(i);
					return;
				}
			}
			selected.a = 1f;
			core.setSelectedColor(selected);
			core.addRecentColor(selected);
			*/
		}
	},
	zoom{
		{
			symmetric = draw = drawmove = push = scalable 
					= drawCursor = false;
		}
	};
	protected boolean symmetric = true, draw = true, move = true, drawmove = true, push = true, scalable = false, drawCursor = true;
	protected String cursor = "cursor";
	
	public void clicked(Layer layer, int x, int y){}
	
	public boolean symmetric(){return symmetric;}
	public boolean move(){return move;}
	public boolean draw(){return draw;}
	public boolean drawMove(){return drawmove;}
	public boolean push(){return push;}
	public boolean scalable(){return scalable;}
	public boolean drawCursor(){return drawCursor;}
	public String cursor(){return cursor;}
}
