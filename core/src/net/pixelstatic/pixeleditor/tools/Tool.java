package net.pixelstatic.pixeleditor.tools;

import java.util.Stack;

import net.pixelstatic.pixeleditor.graphics.PixelCanvas;
import net.pixelstatic.pixeleditor.scene2D.DrawingGrid;
import net.pixelstatic.utils.Pos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntSet;
import com.kotcrab.vis.ui.widget.*;

public enum Tool{
	pencil {
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){
			canvas.setColor(color);
			canvas.drawPixel(x, y);
			canvas.updateTexture();
		}
		
		public void initTable(){
			table = new VisTable();
			
			final VisLabel label = new VisLabel("");
			table.bottom().left().add(label).align(Align.topLeft);
			table.row();
			final VisSlider slider = new VisSlider(1, 10, 1, false);
			table.bottom().left().add(slider).align(Align.topLeft);
			label.addAction(new Action(){
				@Override
				public boolean act(float delta){
					label.setText("Brush Size: " + (int)slider.getValue());
					return false;
				}
			});
		}
	},
	eraser {
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){
			Pixmap.setBlending(Blending.None);
			canvas.setColor(Color.CLEAR);
			canvas.drawPixel(x, y);
			
			Pixmap.setBlending(Blending.SourceOver);
			canvas.updateTexture();
		}
		
		public void initTable(){
			table = new VisTable();
			
			final VisLabel label = new VisLabel("");
			table.bottom().left().add(label).align(Align.topLeft);
			table.row();
			final VisSlider slider = new VisSlider(1, 10, 1, false);
			table.bottom().left().add(slider).align(Align.topLeft);
			label.addAction(new Action(){
				@Override
				public boolean act(float delta){
					label.setText("Brush Size: " + (int)slider.getValue());
					return false;
				}
			});
		}
	},
	fill (true, false){
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){
		    canvas.setColor(color);
		    
			Color dest = canvas.getColor(x, y);
			
			//if(colorEquals(color, dest)) return;
			int width = canvas.width();
			
			IntSet set = new IntSet();
			Stack<Pos> points = new Stack<Pos>();
		    points.add(new Pos(x, y));
		    
			while(!points.isEmpty()){
				Pos pos = points.pop();
				set.add(pos.asInt(width));
				
				Color pcolor = canvas.getColor(pos.x, pos.y);
				if(colorEquals(pcolor, dest)){
					
					canvas.setColor(color); 
					canvas.drawPixel(pos.x, pos.y);
					
					if(pos.x > 0 && !set.contains(Pos.asInt(pos.x - 1, pos.y, width)))
						points.add(pos.relative(-1, 0));
					if(pos.y > 0 && !set.contains(Pos.asInt(pos.x, pos.y - 1, width)))
						points.add(pos.relative(0, -1));
					if(pos.x < canvas.width() - 1 && !set.contains(Pos.asInt(pos.x + 1, pos.y, width)))
						points.add(pos.relative(1, 0));
					if(pos.y < canvas.height() - 1 && !set.contains(Pos.asInt(pos.x, pos.y + 1, width)))
						points.add(pos.relative(0, 1));
				}
			}
			
			canvas.updateTexture();
			
		}
		
		boolean colorEquals(Color a, Color b){
			return a.equals(b);
		}
		
		public void initTable(){
			table = new VisTable();
			
			final VisLabel label = new VisLabel("");
			table.bottom().left().add(label).align(Align.topLeft);
			table.row();
			final VisSlider slider = new VisSlider(1, 10, 1, false);
			table.bottom().left().add(slider);
			label.addAction(new Action(){
				@Override
				public boolean act(float delta){
					label.setText("Threshold: " + (int)slider.getValue());
					return false;
				}
			});
			
			table.row();
			table.add();
			table.row();
		}
	},
	pick(false){
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){
			color.set(canvas.getColor(x, y));
		}
	},
	snap {
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){
			
		}
		
		public void update(DrawingGrid grid){
			//if(Gdx.input.isTouched()){
			//	grid.setCursor(Gdx.input.getX() - grid.getX(),
			//	((Gdx.graphics.getHeight()-Gdx.input.getY()) - grid.getY()));
			//}
		}
		
		public boolean moveCursor(){
			return false;
		}
	},
	zoom (false, false){
		@Override
		public void clicked(Color color, PixelCanvas canvas, int x, int y){
			
		}
		
		public void update(DrawingGrid grid){
			
		}
		
		public boolean moveCursor(){
			return false;
		}
	};
	public final boolean push; //whether the undo stack is pushed when the mouse is up
	public final boolean drawOnMove; //whether to draw when the mouse moves
	public VisTable table;
	
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
	
	public void initTable(){
		table = new VisTable();
		
		for(int i = 0;i < 5;i ++){
			VisTextButton button = new VisTextButton("test");
			table.top().left().add(button).height(70).expandX().fillX();
		}
	}
	
	public boolean moveCursor(){
		return true;
	}
	
	public void update(DrawingGrid grid){
		
	}
	
	public String toString(){
		return name().substring(0, 1).toUpperCase() + name().substring(1);
	}
	
	public abstract void clicked(Color color, PixelCanvas canvas, int x, int y);
}
