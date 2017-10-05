package io.anuke.novix.internal;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.function.Event;

public class NovixEvent{
	
	public static interface LayerLoad extends Event{
		public void handle(Layer[] layers);
	}
	
	public static interface FileLoad extends Event{
		public void handle(Layer[] layers);
	}
	
	public static interface ColorChange extends Event{
		public void handle(Color newColor);
	}
	
	public static interface PaletteChange extends Event{
		public void handle(Palette newPalette);
	}
	
	public static interface AlphaChange extends Event{
		public void handle(float alpha);
	}
	
	public static interface ColorPick extends Event{
		public void handle(Color newColor);
	}
}
