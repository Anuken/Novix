package io.anuke.novix.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.function.ColorListenable;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.graphics.PixmapUtils;
import io.anuke.ucore.scene.Element;
import io.anuke.ucore.scene.event.InputEvent;
import io.anuke.ucore.scene.event.InputListener;
import io.anuke.ucore.scene.ui.Image;
import io.anuke.ucore.scene.ui.layout.Table;
import io.anuke.ucore.util.Mathf;

public class ColorPicker extends Table{
	private static Texture hue;
	
	private Bar hbar, sbar, bbar;
	private ColorListenable changed;
	private Color color = Color.CORAL.cpy(), tmp = new Color(1, 1, 1, 1);
	
	public ColorPicker(){
		setup();
	}
	
	private void setup(){
		int h = 50;
		float space = 20;
		
		if(hue == null)
			hue = PixmapUtils.hueTexture(300, 1);
		
		hbar = new Bar(hue);
		sbar = new Bar();
		bbar = new Bar();
		
		add(hbar).height(h).padBottom(space).growX();
		row();
		add(sbar).height(h).padBottom(space).growX();
		row();
		add(bbar).height(h).growX();
		row();
		
		Image image = new Image("white");
		image.update(()->{
			image.setColor(color);
		});
		
		Table c = new Table();
		c.background("button");
		c.pad(6);
		c.add(image).size(50);
		add(c).pad(10);
		
		setColor(color);
	}
	
	public void setColor(Color color){
		this.color = color;
		float[] f = Hue.RGBtoHSB(color);
		hbar.selection = f[0];
		sbar.selection = f[1];
		bbar.selection = f[2];
		updateColor();
	}
	
	private void updateColor(){
		float hue = hbar.selection;
		float sat = sbar.selection;
		float bri = bbar.selection;
		
		hbar.tint = Hue.lightness(bri);
		
		Hue.fromHSB(hue, 1f, bri, tmp);
		sbar.to.set(tmp);
		sbar.from.set(Hue.lightness(bri));
		
		Hue.fromHSB(hue, sat, 1f, tmp);
		bbar.from.set(Color.BLACK);
		bbar.to.set(tmp);
		
		Hue.fromHSB(hue, sat, bri, color);
	}
	
	public void colorChanged(ColorListenable cons){
		changed = cons;
	}
	
	public Color getColor(){
		return color;
	}
	
	private class Bar extends Element{
		Texture texture;
		Color tint = Color.WHITE.cpy();
		Color from = new Color();
		Color to = new Color();
		float selection = 0.5f;
		
		public Bar(Texture texture){
			this.texture = texture;
			
			addListener(new InputListener(){
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {return true;}
				
				public void touchDragged (InputEvent event, float x, float y, int pointer) {
					selection = x/width;
					selection = Mathf.clamp(selection);
					updateColor();
					if(changed != null)
						changed.changed(color);
				}
			});
		}
		
		public Bar(){
			this(null);
		}
		
		public void draw(){
			Draw.alpha(alpha);
			
			patch("button", -6);
			
			if(texture == null){
				Draw.gradient(from, to, alpha, x, y, width, height);
			}else{
				Draw.tint(tint);
				Draw.batch().draw(texture, x, y, width, height);
			}
			
			float nw = 20;
			float nh = 62;
			Draw.tint(Color.WHITE);
			Draw.patch("slider-knob", x-nw/2+selection*width, y-(nh-height)/2f, nw, nh);
			Draw.color();
		}
	}
}
