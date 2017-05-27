package io.anuke.novix.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

import io.anuke.novix.internal.Project;

public class ProjectPreviewImage extends Group{
	private Stack stack;
	private Project project;
	
	public ProjectPreviewImage(Project project){
		this.project = project;
		
		Texture texture = project.cachedTextures[0];

		BorderImage border = new BorderImage();
		border.setColor(Color.CORAL);
		
		int scale = 16;
		float ratio = 1f/((float)texture.getWidth() / texture.getHeight());
		float scalex = 0, scaley = 0;
		
		if(texture.getWidth() < texture.getHeight()){
			scalex = scale/ratio;
			scaley = scale;
		}else{
			scalex = scale;
			scaley = scale*ratio;
		}
		
		AlphaImage alpha = new AlphaImage(scalex, scaley);

		stack = new Stack();

		stack.add(alpha);
		
		for(int i = 0; i < project.layers; i ++){
			Image image = new Image(project.cachedTextures[i]);
			stack.add(image);
		}
		
		stack.add(border);
		
		addActor(stack);
	}
	
	@Override
	public void act(float delta){
		super.act(delta);
		Texture texture = project.cachedTextures[0];
		
		float xscl = getWidth() / texture.getWidth();
		float yscl = getHeight() / texture.getHeight();
		
		if(MathUtils.isPowerOfTwo(texture.getWidth()) && MathUtils.isPowerOfTwo(texture.getHeight()))
		setSize(xscl * texture.getWidth(), yscl * texture.getHeight());
		
		stack.setBounds(0, 0, getWidth(), getHeight());
	}
}
