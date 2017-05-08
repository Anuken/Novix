package io.anuke.novix.tools;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import io.anuke.novix.modules.Core;
import io.anuke.ucore.graphics.PixmapUtils;

public class ActionStack implements Disposable{
	private Array<DrawAction> stack = new Array<DrawAction>();
	private int index = 0;
	
	public ActionStack(){
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

	public void undo(PixelCanvas canvas){
		if(!canUndo()) return;

		stack.get(stack.size - 1 + index).apply(canvas, false);
		index --;
		
		update();
		//print();
	}

	public void redo(PixelCanvas canvas){
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

	@Override
	public void dispose(){
		for(DrawAction action : stack){
			if(action.toCanvas != null && action.toCanvas != Core.i.canvas()) action.toCanvas.dispose();
			
			if(action.fromCanvas != null  && action.fromCanvas != Core.i.canvas() 
					&& !PixmapUtils.isDisposed(action.fromCanvas.pixmap)) action.fromCanvas.dispose();
		}
	}
}
