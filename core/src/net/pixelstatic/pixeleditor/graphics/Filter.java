package net.pixelstatic.pixeleditor.graphics;

import net.pixelstatic.utils.graphics.Hue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.math.Vector2;
import com.kotcrab.vis.ui.util.ColorUtils;

public enum Filter{
	flip{
		/**args: vertical - boolean*/
		@Override
		public void applyTo(Pixmap input, Pixmap pixmap, Object...args){
			boolean vertical = (Boolean)args[0];

			for(int x = 0;x < pixmap.getWidth();x ++){
				for(int y = 0;y < pixmap.getHeight();y ++){
					if(vertical){
						pixmap.drawPixel(x, y, input.getPixel(input.getWidth() - 1 - x, y));
					}else{
						pixmap.drawPixel(x, y, input.getPixel(x, input.getHeight() - 1 - y));
					}
				}
			}
		}
	},
	rotate{
		/**args: rotation - float*/
		@Override
		public void applyTo(Pixmap input, Pixmap pixmap, Object...args){
			float angle = (Float)args[0];
			Vector2 vector = new Vector2();

			for(int x = 0;x < input.getWidth();x ++){
				for(int y = 0;y < input.getHeight();y ++){
					vector.set(x - input.getWidth() / 2f + 0.5f, y - input.getHeight() / 2f + 0.5f);
					vector.rotate(angle);
					pixmap.drawPixel(x, y, input.getPixel((int)(vector.x + input.getHeight() / 2f), (int)(vector.y + input.getHeight() / 2f)));
				}
			}
		}
	},
	colorize{
		float[] hsb = new float[3];

		protected void applyPixel(Pixmap input, Pixmap pixmap, int x, int y, Color color, Object...args){
			float h = (Float)args[0], s = (Float)args[1], b = (Float)args[2];
			Hue.RGBtoHSB((int)(color.r * 255), (int)(color.g * 255), (int)(color.b * 255), hsb);

			hsb[0] += h-0.5f;
			if(hsb[0] < 0) hsb[0] += 1f;
			if(hsb[0] > 1f) hsb[0] -= 1f;
			hsb[1] *= s * 2f;
			hsb[2] *= b * 2f;

			ColorUtils.HSVtoRGB(hsb[0] * 360f, hsb[1] * 100f, hsb[2] * 100f, color);

			pixmap.setColor(color);
			pixmap.drawPixel(x, y);
		}
	},
	invert{
		protected void applyPixel(Pixmap input, Pixmap pixmap, int x, int y, Color color, Object...args){
			color.set(1f - color.r, 1f - color.g, 1f - color.b, color.a);
			pixmap.setColor(color);
			pixmap.drawPixel(x, y);
		}
	},
	replace{
		protected void applyPixel(Pixmap input, Pixmap pixmap, int x, int y, Color color, Object...args){
			Color from = (Color)args[0];
			Color to = (Color)args[1];

			if(color.equals(from)){
				pixmap.setColor(to);
				pixmap.drawPixel(x, y);
			}
		}
	},
	contrast{
		@Override
		protected void applyPixel(Pixmap input, Pixmap pixmap, int x, int y, Color color, Object...args){
			float contrast = (Float)args[0];
			
			float f = 0.44f;
			float factor = (259 / 255f * (contrast + 1f)) / (1f * (259 / 255f - contrast));
			float newRed = (factor * (color.r - f) + f);
			float newGreen = (factor * (color.g - f) + f);
			float newBlue = (factor * (color.b - f) + f);

			pixmap.setColor(color.set(newRed, newGreen, newBlue, color.a));
			pixmap.drawPixel(x, y);

			/*
			float i = (color.r + color.g + color.b)/3f;
			color.set(i, i, i, color.a);
			pixmap.setColor(color);
			pixmap.drawPixel(x, y);
			*/
		}
	};
	protected static Color color = new Color();

	protected void applyTo(Pixmap input, Pixmap pixmap, Object...args){
		for(int x = 0;x < input.getWidth();x ++){
			for(int y = 0;y < input.getHeight();y ++){
				applyPixel(input, pixmap, x, y, color.set(input.getPixel(x, y)), args);
			}
		}
	}

	protected void applyPixel(Pixmap input, Pixmap pixmap, int x, int y, Color color, Object...args){

	}

	public void apply(Pixmap input, Pixmap pixmap, Object...args){
		Pixmap.setBlending(Blending.None);
		applyTo(input, pixmap, args);
		Pixmap.setBlending(Blending.SourceOver);
	}
}
