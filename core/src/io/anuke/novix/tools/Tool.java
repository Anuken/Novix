package io.anuke.novix.tools;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.IntSet;
import com.kotcrab.vis.ui.widget.VisImageButton;

import io.anuke.novix.modules.Core;
import io.anuke.novix.scene2D.DrawingGrid;
import io.anuke.ucore.graphics.Hue;
import io.anuke.utools.MiscUtils;

public enum Tool{
	pencil{
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){
			canvas.drawRadius(x, y, Core.i.drawgrid.brushSize);
		}
		
		public boolean scalable(){
			return true;
		}
	},
	eraser{
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){
			canvas.eraseRadius(x, y, Core.i.drawgrid.brushSize);
		}
		
		public void onColorChange(Color color, PixelCanvas canvas){
			canvas.setColor(Color.CLEAR.cpy(), true);
		}
		
		public boolean scalable(){
			return true;
		}
	},
	fill(true, false){
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){
			canvas.texture.bind();
			
			int dest = canvas.getIntColor(x, y);

			//if(colorEquals(color, dest)) return;
			int width = canvas.width();

			IntSet set = new IntSet();
			Stack<GridPoint2> points = new Stack<GridPoint2>();
			points.add(new GridPoint2(x, y));
			
			canvas.setColor(color.cpy());

			while( !points.isEmpty()){
				GridPoint2 pos = points.pop();
				set.add(MiscUtils.asInt(pos.x, pos.y, width));

				int pcolor = canvas.getIntColor(pos.x, pos.y);
				if(colorEquals(pcolor, dest)){

					canvas.drawPixel(pos.x, pos.y, false);

					if(pos.x > 0 && !set.contains(MiscUtils.asInt(pos.x - 1, pos.y, width))) points.add(new GridPoint2(pos).cpy().add( -1, 0));
					if(pos.y > 0 && !set.contains(MiscUtils.asInt(pos.x, pos.y - 1, width))) points.add(new GridPoint2(pos).cpy().add(0, -1));
					if(pos.x < canvas.width() - 1 && !set.contains(MiscUtils.asInt(pos.x + 1, pos.y, width))) points.add(new GridPoint2(pos).cpy().add(1, 0));
					if(pos.y < canvas.height() - 1 && !set.contains(MiscUtils.asInt(pos.x, pos.y + 1, width))) points.add(new GridPoint2(pos).cpy().add(0, 1));
				}
			}

			canvas.updateTexture();

		}

		boolean colorEquals(int a, int b){
			return a == b;
		}
	},
	pick(false){
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){
			Color selected = canvas.getColor(x, y);
			for(int i = 0; i < Core.i.getCurrentPalette().size(); i ++){
				if(Hue.approximate(selected, Core.i.getCurrentPalette().colors[i], 0.001f)){
					Core.i.colormenu.setSelectedColor(i);
					return;
				}
			}
			selected.a = 1f;
			Core.i.colormenu.setSelectedColor(selected);
			Core.i.colormenu.addRecentColor(selected);
		}
		
		public boolean symmetric(){
			return false;
		}
	},
	zoom(false, false){
		{
			cursor = "cursor-zoom";
		}
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){

		}

		public void update(DrawingGrid grid){
			grid.setCursor(Gdx.graphics.getWidth()/2 - grid.getX(), Gdx.graphics.getHeight()/2 - grid.getY());
		}

		public boolean moveCursor(){
			return false;
		}
		
		public boolean drawCursor(){
			return false;
		}
		
		public boolean symmetric(){
			return false;
		}
	},
	/*
	grid(false, false){
		public boolean selectable(){
			return false;
		}
		
		public void onSelected(){
			Core.i.drawgrid.canvas.actions.undo();
		}
	},*/
	undo(false, false){
		public void onSelected(){
			Core.i.drawgrid.actions.undo(Core.i.canvas());
		}

		public boolean selectable(){
			return false;
		}
	},
	redo(false, false){
		public void onSelected(){
			Core.i.drawgrid.actions.redo(Core.i.canvas());
		}

		public boolean selectable(){
			return false;
		}
	};
	public final boolean push; //whether the undo stack is pushed when the mouse is up
	public final boolean drawOnMove; //whether to draw when the mouse moves
	public String cursor = "cursor";
	public VisImageButton button;

	private Tool(){
		this(true);
	}

	private Tool(boolean pushOnUp){
		push = pushOnUp;
		this.drawOnMove = true;
	}

	private Tool(boolean pushOnUp, boolean drawOnMove){
		push = pushOnUp;
		this.drawOnMove = drawOnMove;
	}

	public boolean moveCursor(){
		return true;
	}
	
	public boolean drawCursor(){
		return true;
	}

	public boolean selectable(){
		return true;
	}
	
	public boolean scalable(){
		return false;
	}
	
	public boolean symmetric(){
		return true;
	}

	public void update(DrawingGrid grid){

	}

	public void onSelected(){

	}

	public void onColorChange(Color color, PixelCanvas canvas){
		canvas.setColor(color);
	}

	public String toString(){
		return name().substring(0, 1).toUpperCase() + name().substring(1);
	}

	public void clicked(Color color, PixelCanvas canvas, int x, int y){
	}
}
