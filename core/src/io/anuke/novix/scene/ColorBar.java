package io.anuke.novix.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.NumberUtils;
import com.kotcrab.vis.ui.VisUI;

import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.graphics.Textures;
import io.anuke.utools.MiscUtils;

public class ColorBar extends BarActor{
	protected Sprite sprite;
	protected Color leftColor, rightColor;
	protected static Color temp = new Color();

	public ColorBar(boolean vertical){
		super(vertical);
		sprite = new Sprite(VisUI.getSkin().getAtlas().findRegion("white"));
	}
	
	public ColorBar(){
		this(false);
	}

	@Override
	public void draw(Batch batch, float alpha){

		drawBorder(batch, alpha);

		setSpriteBounds(sprite);

		internalSetColors(leftColor.cpy().sub(1f - brightness, 1f - brightness, 1f - brightness, 0f), rightColor.cpy().sub(1f - brightness, 1f - brightness, 1f - brightness, 0f));

		setAlpha(alpha);

		if(leftColor.a <= 0.99f || rightColor.a <= 0.99f) batch.draw(Textures.get("alpha"), getX() + border, getY() + border, getWidth() - border * 2, getHeight() - border * 2, 0f, 0f, (getWidth() - border * 2) / MiscUtils.densityScale(20f), (getHeight() - border * 2) / MiscUtils.densityScale(20f));
		
		sprite.draw(batch);
		
		drawDisabled(batch, sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
		
		internalSetColors(leftColor, rightColor);

		drawSelection(batch, alpha);
	}

	public void setColors(Color left, Color right){
		leftColor = left;
		rightColor = right;
		internalSetColors(left, right);
	}

	private void internalSetColors(Color left, Color right){
		sprite.getVertices()[SpriteBatch.C1] = left.toFloatBits();
		sprite.getVertices()[SpriteBatch.C2] = left.toFloatBits();
		sprite.getVertices()[SpriteBatch.C3] = right.toFloatBits();
		sprite.getVertices()[SpriteBatch.C4] = right.toFloatBits();
	}

	public void setRightColor(Color right){
		rightColor = right;
		sprite.getVertices()[SpriteBatch.C3] = right.toFloatBits();
		sprite.getVertices()[SpriteBatch.C4] = right.toFloatBits();
	}
	
	public Color getRightColor(){
		return rightColor;
	}

	public void setAlpha(float a){
		for(int colorvertice : colors){

			int intBits = NumberUtils.floatToIntColor(sprite.getVertices()[colorvertice]);
			Color color = temp;
			color.r = (intBits & 0xff) / 255f;
			color.g = ((intBits >>> 8) & 0xff) / 255f;
			color.b = ((intBits >>> 16) & 0xff) / 255f;
			color.a = ((intBits >>> 24) & 0xff) / 255f;
			
			temp.a *= a;

			sprite.getVertices()[colorvertice] = temp.toFloatBits();
		}
	}

	public void setSelection(float s){
		selection = s;
	}

	public float getSelection(){
		return selection;
	}

	@Override
	public Color getSelectedColor(){
		return Hue.mix(leftColor, rightColor, selection);
	}
}
