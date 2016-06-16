package net.pixelstatic.pixeleditor.tools;

import net.pixelstatic.pixeleditor.graphics.PixelCanvas;

import com.badlogic.gdx.utils.Array;

public class ActionStack{
	private Array<DrawAction> stack = new Array<DrawAction>();
	private PixelCanvas canvas;
	private int index = 0;
	
	public ActionStack(PixelCanvas canvas){
		this.canvas = canvas;
		DrawAction action = new DrawAction();
		stack.add(action);
	}
	
	public void clear(){
		stack.clear();
		index = 0;
	}
	
	public void add(DrawAction action){
		stack.truncate(stack.size + index);
		stack.add(action);
	}

	public void undo(){
		//System.out.println("undo: index = " + index + ", size: " + stack.size);
		if(stack.size - 1 + index < 1){
			return;
		}

		//System.out.println("applying " + stack.get(stack.size - 1 + index));
		stack.get(stack.size - 1 + index).apply(canvas, false);
		index --;
		
	}

	public void redo(){
		//System.out.println("redo: index = " + index + ", size: " + stack.size);
		
		if(index > -1 || stack.size - 1 + index < 0){
			return;
		}
		
		//System.out.println("applying " + stack.get(stack.size - 1 + index));
		index ++;
		stack.get(stack.size - 1 + index).apply(canvas, true);
	}
	
	
}
