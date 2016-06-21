package net.pixelstatic.pixeleditor.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.math.Vector2;

public enum Filter{
	flip {
		/**args: vertical - boolean*/
		@Override
		public void applyTo(Pixmap pixmap, Pixmap input, Object... args){ 
			boolean vertical = (Boolean)args[0];
			
			for(int x = 0; x < pixmap.getWidth(); x ++){
				for(int y = 0; y < pixmap.getHeight(); y ++){
					if(vertical){
						pixmap.drawPixel(x, y, input.getPixel(input.getWidth() - 1 - x, y));
					}else{
						pixmap.drawPixel(x, y, input.getPixel(x, input.getHeight() - 1 -y));
					}
				}
			}
			
		}
	}, 
	rotate{
		/**args: rotation - float*/
		@Override
		public void applyTo(Pixmap pixmap, Pixmap input, Object...args){
			float angle = (Float)args[0];
			Vector2 vector = new Vector2();
			
			for(int x = 0; x < pixmap.getWidth(); x ++){
				for(int y = 0; y < pixmap.getHeight(); y ++){
					vector.set(x - pixmap.getWidth()/2f + 0.5f, y - pixmap.getHeight()/2f + 0.5f);
					vector.rotate(angle);
					pixmap.drawPixel(x, y, input.getPixel((int)(vector.x + pixmap.getHeight()/2f), (int)(vector.y + pixmap.getHeight()/2f)));
				}
			}
		}
	}, 
	scale{
		@Override
		public void applyTo(Pixmap pixmap, Pixmap input, Object...args){
			// TODO Auto-generated method stub
			
		}
	};
	
	protected abstract void applyTo(Pixmap pixmap, Pixmap input, Object... args);

	public void apply(Pixmap pixmap, Pixmap input, Object... args){
		Pixmap.setBlending(Blending.None);
		applyTo(pixmap, input, args);
		Pixmap.setBlending(Blending.SourceOver);
	}
}
