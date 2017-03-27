package io.anuke.novix.ui;

import com.badlogic.gdx.Gdx;
import com.kotcrab.vis.ui.widget.VisTable;

import io.anuke.novix.scene2D.ColorBox;
import io.anuke.novix.scene2D.ColorWidget;

public class ColorMenu extends VisTable{
	public ColorBox[] boxes;
	public ColorWidget picker;
	public VisTable colortable, pickertable;
	
	public ColorMenu(){
		background("menu");
		
		add().grow().row();

	}
	
	public float getPrefWidth(){
		return Gdx.graphics.getWidth();
	}
}
