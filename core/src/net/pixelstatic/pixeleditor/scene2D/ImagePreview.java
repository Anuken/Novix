package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.pixeleditor.modules.Core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

public class ImagePreview extends Group{
	protected Stack stack;
	public final PixmapImage image;
	
	public ImagePreview(Pixmap pixmap){
		stack = new Stack();
		
		image = new PixmapImage(pixmap);
		
		AlphaImage alpha = new AlphaImage(Core.i.drawgrid.canvas.width(), Core.i.drawgrid.canvas.height());
		GridImage grid = new GridImage(Core.i.drawgrid.canvas.width(), Core.i.drawgrid.canvas.height());
		BorderImage border = new BorderImage();
		border.setColor(Color.CORAL);
		
		stack.add(alpha);
		stack.add(image);
		if(Core.i.drawgrid.grid)stack.add(grid);
		stack.add(border);
		
		addActor(stack);
	}
	
	public void draw(Batch batch, float alpha){
		super.draw(batch, alpha);
		stack.setBounds(0, 0, getWidth(), getHeight());
	}
	
	public PixmapImage getImage(){
		return image;
	}
}
