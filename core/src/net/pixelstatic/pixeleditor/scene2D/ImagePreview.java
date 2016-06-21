package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.pixeleditor.modules.GUI;
import net.pixelstatic.utils.MiscUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

public class ImagePreview extends Actor{
	private Stack stack;
	public final PixmapImage image;
	
	public ImagePreview(Pixmap pixmap){
		stack = new Stack();
		
		image = new PixmapImage(pixmap);
		
		AlphaImage alpha = new AlphaImage(GUI.gui.drawgrid.canvas.width(), GUI.gui.drawgrid.canvas.height());
		GridImage grid = new GridImage(GUI.gui.drawgrid.canvas.width(), GUI.gui.drawgrid.canvas.height());

		stack.add(alpha);
		stack.add(image);
		stack.add(grid);
		
	}
	
	public void draw(Batch batch, float alpha){
		stack.setBounds(getX(), getY(), getWidth(), getHeight());
		
		stack.draw(batch, alpha);
		
		Color color = Color.CORAL.cpy();
		color.a = alpha;
		batch.setColor(color);
		MiscUtils.drawBorder(batch, getX(), getY(), getWidth(), getHeight(), 2, 2);
		batch.setColor(Color.WHITE);
		
	}
	
	public PixmapImage getImage(){
		return image;
	}
}
