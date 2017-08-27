package io.anuke.novix.element;

import com.badlogic.gdx.graphics.Texture;

import io.anuke.ucore.scene.ui.Image;
import io.anuke.ucore.scene.ui.layout.Stack;
import io.anuke.ucore.scene.ui.layout.Table;

public class LayerImage extends Table{
	
	public LayerImage(Texture texture){
		Stack stack = new Stack();
		
		stack.add(new AlphaImage());
		stack.add(new Image(texture));
		stack.add(new BorderImage());
		
		add(stack).grow();
	}
}
