package io.anuke.novix.element;

import com.badlogic.gdx.graphics.Colors;

import io.anuke.ucore.scene.ui.layout.Table;

public class Border extends Table{
	
	public Border(){
		setColor(Colors.get("border"));
		background("white");
	}
}
