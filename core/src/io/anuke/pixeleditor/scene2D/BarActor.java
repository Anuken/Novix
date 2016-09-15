package io.anuke.pixeleditor.scene2D;

import io.anuke.gdxutils.graphics.Hue;
import io.anuke.utils.MiscUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.kotcrab.vis.ui.VisUI;

public abstract class BarActor extends Actor implements Disableable{
	public static final float s = MiscUtils.densityScale();
	public static Color borderColor = Hue.lightness(0.24f);
	public static final float selectionWidth = 20f*s;
	protected static final float margin = 5f*s;
	protected static final float border = 5f*s;
	protected static final int[] colors = new int[]{Batch.C1, Batch.C2, Batch.C3, Batch.C4};
	public float selection = 1f;
	public float brightness = 1f;
	protected boolean selected;
	protected final boolean vertical;
	private boolean disabled;

	public BarActor(boolean vertical){
		this.vertical = vertical;
		addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if(disabled) return false;
				updateSelection(x, y);
				selected = true;
				return true;
			}

			public void touchDragged(InputEvent event, float x, float y, int pointer){
				if(disabled) return;
				updateSelection(x, y);
			}
			
			public void touchUp (InputEvent event, float x, float y, int pointer, int button){
				if(disabled) return;
				if(pointer == 0) selected = false;
			}
			
		});
		
		//onSelectionUpdated();
	}
	
	public void setDisabled (boolean isDisabled){
		disabled = isDisabled;
	}

	public boolean isDisabled (){
		return disabled;
	}
	
	public void drawDisabled(Batch batch, float x, float y, float width, float height){
		if(!disabled) return;
		float color = batch.getPackedColor();
		batch.setColor(0,0,0,batch.getColor().a*0.5f);
		batch.draw(VisUI.getSkin().getRegion("white"), x,y,width,height);
		batch.setColor(color);
	}

	private void updateSelection(float x, float y){
		selection = !vertical ? ((x-selectionWidth/2) / (getWidth()-selectionWidth)) : ((y-selectionWidth/2) / (getHeight()-selectionWidth));
		selection = MiscUtils.clamp(selection, 0f, 1f);
		fire(new ChangeListener.ChangeEvent());
		onSelectionUpdated();
	}

	abstract public Color getSelectedColor();

	public void onSelectionUpdated(){

	}

	protected void setSpriteBounds(Sprite sprite){
		
		if(vertical){
			sprite.setBounds(getX() + border + getWidth()/2 - (getHeight() )/2, getY() + border + getHeight()/2 - (getWidth())/2, getHeight() - border * 2, getWidth() - border * 2);
			sprite.setOriginCenter();
			sprite.setRotation(90);
		}else{
			sprite.setBounds(getX() + border, getY() + border, getWidth() - border * 2, getHeight() - border * 2);
		}
	}

	protected void drawBorder(Batch batch, float alpha){
		batch.setColor(borderColor);

		MiscUtils.setBatchAlpha(batch, alpha);

		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), getX(), getY(), getWidth(), getHeight());
		batch.setColor(Color.WHITE);
	}

	protected void drawSelection(Batch batch, float alpha){
		float size = selectionWidth;
		if(disabled) batch.setColor(0.7f,0.7f,0.7f,1);
		MiscUtils.setBatchAlpha(batch, alpha);
		String region = selected ? "slider-knob-down" : "slider-knob";

		if( !vertical){
		
			batch.draw(VisUI.getSkin().getAtlas().findRegion(region), getX() + selection * (getWidth()-size), getY() - margin, size, getHeight() + margin * 2f);
		
		}else{
			//batch.draw(VisUI.getSkin().getAtlas().findRegion(region), getX() - margin, getY() + selection * getHeight() - size / 2f, getWidth() + margin * 2f, size);
			batch.draw(VisUI.getSkin().getAtlas().findRegion(region), getX() - margin, getY() + selection * (getHeight()-size), getWidth() + margin * 2f, size);
			
		}
	}
}
