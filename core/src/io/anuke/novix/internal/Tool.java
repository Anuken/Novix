package io.anuke.novix.internal;

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
	},
	fill{
		{
			drawmove = false;
		}
	},
	pick{
		{
			symmetric = false;
			draw = false;
			push = false;
		}
	},
	zoom{
		
	},
	undo,
	redo;
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
