package io.anuke.pixeleditor.scene2D;


import io.anuke.gdxutils.graphics.Textures;
import io.anuke.utils.MiscUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.kotcrab.vis.ui.VisUI;

public class BrushSizeWidget extends Widget{
	int gridsize = 9, brushsize = 1;

	{
		setSize(gridsize * 15*MiscUtils.densityScale(), gridsize * 15*MiscUtils.densityScale());

		addListener(new InputListener(){
			
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				updateSize(x,y);
				return true;
			}

			public void touchUp(InputEvent event, float x, float y, int pointer, int button){
			//	mouseDown = false;
			}

			public void touchDragged(InputEvent event, float x, float y, int pointer){
				updateSize(x,y);
			}
		});

	}

	@Override
	public void draw(Batch batch, float alpha){
		batch.draw(Textures.get("alpha"), getX(), getY(), getWidth(), getHeight(), 0f, 0f, gridsize, gridsize);

		batch.setColor(getColor());

		for(int x = 0;x < gridsize;x ++){
			for(int y = 0;y < gridsize;y ++){
				if(Vector2.dst(gridsize / 2, gridsize / 2, x, y) < brushsize - 0.5f){
					batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), getX() + x * blockSize(), getY() + y * blockSize(), blockSize(), blockSize());
				}
			}
		}

		batch.setColor(Color.WHITE);

		batch.draw(Textures.get("grid_25"), getX(), getY(), getWidth(), getHeight(), 0f, 0f, gridsize, gridsize);

	}
	
	private void updateSize(float x, float y){
		
		//brushsize = (int)(Vector2.dst(x, y, getX() + getHeight()/2f, getY() + getWidth()/2f) / blockSize())+1;
		//if(brushsize > gridsize/2+1)brushsize = gridsize/2+1;
	}

	public int getBrushSize(){
		return brushsize;
	}
	
	public void setBrushSize(int size){
		this.brushsize = size;
	}

	public float blockSize(){
		return getWidth() / gridsize;
	}

	public float getPrefWidth(){
		return getWidth();
	}

	public float getPrefHeight(){
		return getHeight();
	}
}
