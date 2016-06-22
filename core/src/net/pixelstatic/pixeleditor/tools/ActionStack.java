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
		index = 0;
		stack.add(action);
		
		//print();
	}

	public void undo(){
		//System.out.println("undo: index = " + index + ", size: " + stack.size);
		if(stack.size - 1 + index < 1){
			return;
		}

		//System.out.println("applying " + stack.get(stack.size - 1 + index));
		stack.get(stack.size - 1 + index).apply(canvas, false);
		index --;
		
		//print();
	}

	public void redo(){
		//System.out.println("redo: index = " + index + ", size: " + stack.size);
		
		if(index > -1 || stack.size - 1 + index < 0){
			return;
		}
		
		//System.out.println("applying " + stack.get(stack.size - 1 + index));
		index ++;
		stack.get(stack.size - 1 + index).apply(canvas, true);
		
		//print();
	}
	
	void print(){
		System.out.println("\n\n\n\n\n\n\n\n\n");
		
		System.out.println("index: " + index);
		int i = 0;
		for(DrawAction action : stack){

			boolean sel = stack.size + index - 1 == i;
			System.out.println("<"+i+"> " + (sel ? "[" : "") + action.positions.size + "S" + (sel ? "]" : "" ));
			
			i++;
		}
	}
}
