package net.pixelstatic.pixeleditor.tools;

import com.badlogic.gdx.utils.Array;

public class ActionStack{
	private Array<DrawAction> stack = new Array<DrawAction>();
	private PixelCanvas canvas;
	private int index = 0;
	
	public ActionStack(PixelCanvas canvas){
		this.canvas = canvas;
		DrawAction action = new DrawAction();
		stack.add(action);
		update();
	}
	
	public void clear(){
		stack.clear();
		index = 0;
		update();
	}
	
	public void add(DrawAction action){
		stack.truncate(stack.size + index);
		index = 0;
		stack.add(action);
		
		update();
		//print();
	}
	
	public boolean canUndo(){
		return !(stack.size - 1 + index < 1);
	}
	
	public boolean canRedo(){
		return !(index > -1 || stack.size - 1 + index < 0);
	}

	public void undo(){
		if(!canUndo()) return;

		stack.get(stack.size - 1 + index).apply(canvas, false);
		index --;
		
		update();
		//print();
	}

	public void redo(){
		if(!canRedo()) return;
		
		index ++;
		stack.get(stack.size - 1 + index).apply(canvas, true);
		
		update();
		//print();
	}
	
	private void update(){
		Tool.undo.button.setDisabled(!canUndo());
		Tool.redo.button.setDisabled(!canRedo());
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
