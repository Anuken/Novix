package io.anuke.novix.internal;

import com.badlogic.gdx.utils.Array;

public class OperationStack{
	private Array<DrawOperation> stack = new Array<DrawOperation>();
	private int index = 0;
	
	public OperationStack(){
		update();
	}
	
	public void clear(){
		stack.clear();
		index = 0;
		update();
	}
	
	public void add(DrawOperation action){
		stack.truncate(stack.size + index);
		index = 0;
		stack.add(action);
		
		update();
	}
	
	public boolean canUndo(){
		return !(stack.size - 1 + index < 1);
	}
	
	public boolean canRedo(){
		return !(index > -1 || stack.size - 1 + index < 0);
	}

	public void undo(){
		if(!canUndo()) return;

		stack.get(stack.size - 1 + index).apply();
		index --;
		
		update();
	}

	public void redo(){
		if(!canRedo()) return;
		
		index ++;
		stack.get(stack.size - 1 + index).reapply();
		
		update();
	}
	
	private void update(){
		Tool.undo.button.setDisabled(!canUndo());
		Tool.redo.button.setDisabled(!canRedo());
	}

	public void dispose(){
		//TODO
	}
}
