package net.pixelstatic.pixeleditor.scene2D;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ShiftedImage extends Image{
	int offsetx, offsety;
	
	public ShiftedImage(Texture tex){
		super(tex);
	}
}
