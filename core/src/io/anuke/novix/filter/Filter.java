package io.anuke.novix.filter;

import com.badlogic.gdx.graphics.Pixmap;

public enum Filter{
	colorize {
		@Override
		void process(Pixmap pixmap, Pixmap out){
			
		}
	};
	
	abstract void process(Pixmap pixmap, Pixmap out);
}
