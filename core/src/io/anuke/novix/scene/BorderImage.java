package io.anuke.novix.scene;


import static io.anuke.ucore.UCore.s;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import io.anuke.utools.MiscUtils;

public class BorderImage extends Actor{
	
	{
		setColor(Color.CORAL);
	}
	
	public void draw(Batch batch, float alpha){
		batch.setColor(getColor());
		
		MiscUtils.setBatchAlpha(batch, alpha);
		
		MiscUtils.drawBorder(batch, getX(), getY(), getWidth(), getHeight(), (int)(2*s), (int)(2*s));
	}
	
}
