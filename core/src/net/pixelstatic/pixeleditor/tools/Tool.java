package net.pixelstatic.pixeleditor.tools;

import java.util.Stack;

import net.pixelstatic.pixeleditor.graphics.PixelCanvas;
import net.pixelstatic.pixeleditor.modules.Main;
import net.pixelstatic.pixeleditor.scene2D.DrawingGrid;
import net.pixelstatic.utils.Pos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.IntSet;

public enum Tool{
	pencil{
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){
			canvas.drawRadius(x, y, Main.gui.getBrushSize());
		}
	},
	eraser{
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){
			canvas.eraseRadius(x, y, Main.gui.getBrushSize());
		}
		
		public void onColorChange(Color color, PixelCanvas canvas){
			canvas.setColor(Color.CLEAR.cpy(), true);
		}
	},
	fill(true, false){
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){
			
			int dest = canvas.getIntColor(x, y);

			//if(colorEquals(color, dest)) return;
			int width = canvas.width();

			IntSet set = new IntSet();
			Stack<Pos> points = new Stack<Pos>();
			points.add(new Pos(x, y));
			
			canvas.setColor(color.cpy());

			while( !points.isEmpty()){
				Pos pos = points.pop();
				set.add(pos.asInt(width));

				int pcolor = canvas.getIntColor(pos.x, pos.y);
				if(colorEquals(pcolor, dest)){

					canvas.drawPixel(pos.x, pos.y);

					if(pos.x > 0 && !set.contains(Pos.asInt(pos.x - 1, pos.y, width))) points.add(pos.relative( -1, 0));
					if(pos.y > 0 && !set.contains(Pos.asInt(pos.x, pos.y - 1, width))) points.add(pos.relative(0, -1));
					if(pos.x < canvas.width() - 1 && !set.contains(Pos.asInt(pos.x + 1, pos.y, width))) points.add(pos.relative(1, 0));
					if(pos.y < canvas.height() - 1 && !set.contains(Pos.asInt(pos.x, pos.y + 1, width))) points.add(pos.relative(0, 1));
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
			selected.a = 1f;
			Main.gui.setSelectedColor(selected);
		}
		
		public boolean symmetric(){
			return false;
		}
	},
	zoom(false, false){
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){

		}

		public void update(DrawingGrid grid){

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

	undo(false, false){
		public void onSelected(){
			Main.gui.drawgrid.canvas.actions.undo();
		}

		public boolean selectable(){
			return false;
		}
	},
	redo(false, false){
		public void onSelected(){
			Main.gui.drawgrid.canvas.actions.redo();
		}

		public boolean selectable(){
			return false;
		}
	};
	public final boolean push; //whether the undo stack is pushed when the mouse is up
	public final boolean drawOnMove; //whether to draw when the mouse moves

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
