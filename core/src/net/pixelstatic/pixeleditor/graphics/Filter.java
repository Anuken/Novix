package net.pixelstatic.pixeleditor.graphics;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

public enum Filter{
	flip {
		
		/**args: vertical - boolean*/
		@Override
		public Pixmap apply(Pixmap input, Object... args){ 
			Pixmap pixmap = new Pixmap(input.getWidth(), input.getHeight(), Format.RGBA8888);
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
			
			return pixmap;
		}
	};
	
	public abstract Pixmap apply(Pixmap pixmap, Object... args);
		
	
}
