package io.anuke.novix.internal;

public abstract class DrawOperation{
	protected Layer layer;
	
	public DrawOperation(Layer layer){
		this.layer = layer;
	}
	
	public abstract void undo();
	public abstract void redo();
}
