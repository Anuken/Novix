package io.anuke.novix.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.kotcrab.vis.ui.VisUI;

import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.graphics.Textures;
import io.anuke.utools.MiscUtils;

public class ColorBox extends Widget implements Disableable{
	public boolean selected, hovered;
	public Color backgroundColor = Color.valueOf("29323d"),
	disabledColor = Color.valueOf("0f1317"),
	hoverColor = Color.GRAY,
	selectedColor = Color.CORAL;
	private boolean disabled;

	public ColorBox(){
		
	}

	public ColorBox(Color color){
		this();
		setColor(color);
	}
	
	public void addSelectListener(){
		addListener(new SelectListener());
	}

	public void draw(Batch batch, float alpha){
		int grayborder = (int)getWidth()/14;
		int border = 0;

		if(selected) border = getBorderThickness();
		if(hovered) border = (int)getWidth() / 14;

		batch.setColor( !selected ? backgroundColor : selectedColor);
		if(hovered) batch.setColor(hoverColor);

		MiscUtils.setBatchAlpha(batch, alpha);

		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), getX() - border, getY() - border, getWidth() + border * 2, getHeight() + border * 2);

		if(MathUtils.isEqual(alpha, 1f) || getColor().a < 1f){
			batch.setColor(1, 1, 1, alpha);
			batch.draw(Textures.get("alpha"), getX() + grayborder, getY() + grayborder, getWidth() - grayborder * 2, getHeight() - grayborder * 2, 0, 0, 2, 2);
		}
		
		batch.setColor(disabled ? disabledColor : getColor());

		MiscUtils.setBatchAlpha(batch, alpha);

		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), getX() + grayborder, getY() + grayborder, getWidth() - grayborder * 2, getHeight() - grayborder * 2+1);
	}
	
	private class SelectListener extends InputListener{
		
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
			hovered = true;
			toFront();
			return true;
		}

		public void touchUp(InputEvent event, float x, float y, int pointer, int button){
			hovered = false;
		}
	}

	@Override
	public boolean isDisabled(){
		return disabled;
	}

	@Override
	public void setDisabled(boolean disabled){
		this.setTouchable(disabled ? Touchable.disabled : Touchable.enabled);
		this.disabled = disabled;
	}
	
	public int getBorderThickness(){
		return (int)getWidth() / 6;
	}
	
	public static class Style{
		public Color box = Color.DARK_GRAY;
		public Color hovered = Color.GRAY;
		public Color selected = Color.CORAL;
		public Color disabled = Hue.lightness(0.15f);
	}
}
