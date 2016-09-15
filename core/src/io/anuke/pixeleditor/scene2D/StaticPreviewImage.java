package io.anuke.pixeleditor.scene2D;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

public class StaticPreviewImage extends Group{
	private Stack stack;
	private Texture texture;
	
	public StaticPreviewImage(Texture texture){
		this.texture = texture;
		Image image = new Image(texture);

		BorderImage border = new BorderImage();
		border.setColor(Color.CORAL);
		int scale = 16;
		float ratio = 1f/((float)texture.getWidth() / texture.getHeight());
		AlphaImage alpha = new AlphaImage(scale, (int)(scale*ratio));

		stack = new Stack();

		stack.add(alpha);
		stack.add(image);
		stack.add(border);
		
		addActor(stack);
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
		float xscl = getWidth() / texture.getWidth();
		float yscl = getHeight() / texture.getHeight();
		
		if(MathUtils.isPowerOfTwo(texture.getWidth()) && MathUtils.isPowerOfTwo(texture.getHeight()))
		setSize((int)(xscl/2)*2 * texture.getWidth(), (int)(yscl/2)*2 * texture.getHeight());
		
		stack.setBounds(0, 0, getWidth(), getHeight());
	}
}
