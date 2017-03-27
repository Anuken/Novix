package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.ui.widget.VisTable;

public class ColorMenu extends VisTable{
	
	public ColorMenu(){
		background("menu");
		
		add().grow().row();

	}
	
	public float getPrefWidth(){
		return Gdx.graphics.getWidth();
	}
}
