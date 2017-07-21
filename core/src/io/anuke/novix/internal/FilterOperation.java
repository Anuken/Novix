package io.anuke.novix.internal;

import com.badlogic.gdx.graphics.Pixmap;

public class FilterOperation extends DrawOperation{
	private final Pixmap from, to;
	
	public FilterOperation(Layer layer, Pixmap from, Pixmap to) {
		super(layer);
		this.from = from;
		this.to = to;
	}

	@Override
	public void undo(){
		layer.applyPixmap(from);
	}

	@Override
	public void redo(){
		layer.applyPixmap(to);
	}

}
