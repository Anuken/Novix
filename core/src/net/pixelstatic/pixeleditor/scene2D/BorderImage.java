package net.pixelstatic.pixeleditor.scene2D;

import net.pixelstatic.utils.MiscUtils;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class BorderImage extends Actor{
	
	public void draw(Batch batch, float alpha){
		batch.setColor(getColor());
		
		MiscUtils.setBatchAlpha(batch, alpha);
		
		MiscUtils.drawBorder(batch, getX(), getY(), getWidth(), getHeight(), 2, 2);
	}
	
}
