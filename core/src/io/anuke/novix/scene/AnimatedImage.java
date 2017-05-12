package io.anuke.novix.scene;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class AnimatedImage extends Actor{
	final float frametime = 10;
	Drawable[] drawables;
	float time;
	int index;
	
	public AnimatedImage(Drawable... drawables){
		this.drawables = drawables;
	}
	
	@Override
	public void draw(Batch batch, float alpha){
		batch.setColor(1,1,1,alpha);
		drawables[index].draw(batch, getX(), getY(), getWidth(), getHeight());
	}
	
	@Override
	public void act(float delta){
		time += delta*60f;
		if(time > frametime){
			time = 0;
			index ++;
			if(index >= drawables.length) index = 0;
		}
	}
}
