package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.pixeleditor.modules.GUI;
import net.pixelstatic.utils.MiscUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

public class RotatedImage extends Actor{
	Stack stack;
	Image image;
	
	public RotatedImage(){
		stack = new Stack();
		
		image = new Image(GUI.gui.drawgrid.canvas.texture);
		
		AlphaImage alpha = new AlphaImage(GUI.gui.drawgrid.canvas.width(), GUI.gui.drawgrid.canvas.height());

		stack.add(alpha);
		stack.add(image);
		
	}
	
	public void draw(Batch batch, float alpha){
		stack.setBounds(getX(), getY(), getWidth(), getHeight());
		
		
		stack.draw(batch, alpha);
		
		batch.setColor(Color.CORAL);
		MiscUtils.drawBorder(batch, getX(), getY(), getWidth(), getHeight());
		batch.setColor(Color.WHITE);
		
	}
	
	public Image getImage(){
		return image;
	}
	
	public void setImageRotation(float rot){
		image.setRotation(rot);
	}
	
}
