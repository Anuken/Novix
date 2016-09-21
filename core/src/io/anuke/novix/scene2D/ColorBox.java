package io.anuke.novix.scene2D;

import io.anuke.gdxutils.graphics.Hue;
import io.anuke.gdxutils.graphics.Textures;
import io.anuke.utils.MiscUtils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.kotcrab.vis.ui.VisUI;

public class ColorBox extends Widget implements Disableable{
	public static final Style defaultStyle = new Style();

	public boolean selected, hovered;
	private boolean disabled;
	private Style style;

	public ColorBox(){
		style = defaultStyle;
	}

	public ColorBox(Color color){
		this();
		setColor(color);
	}
	
	public Style getStyle(){
		return style;
	}
	
	public void addSelectListener(){
		addListener(new SelectListener());
	}

	public void draw(Batch batch, float alpha){
		int grayborder = (int)getWidth()/14;
		int border = 0;

		if(selected) border = getBorderThickness();
		if(hovered) border = (int)getWidth() / 14;

		batch.setColor( !selected ? style.box : style.selected);
		if(hovered) batch.setColor(style.hovered);

		MiscUtils.setBatchAlpha(batch, alpha);

		batch.draw(VisUI.getSkin().getAtlas().findRegion("white"), getX() - border, getY() - border, getWidth() + border * 2, getHeight() + border * 2);

		if(MathUtils.isEqual(alpha, 1f) || getColor().a < 1f){
			batch.setColor(1, 1, 1, alpha);
			batch.draw(Textures.get("alpha"), getX() + grayborder, getY() + grayborder, getWidth() - grayborder * 2, getHeight() - grayborder * 2, 0, 0, 2, 2);
		}
		
		batch.setColor(disabled ? style.disabled : getColor());

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
