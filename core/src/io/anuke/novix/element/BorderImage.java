package io.anuke.novix.element;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.scene.Element;
import io.anuke.ucore.scene.ui.layout.Unit;

public class BorderImage extends Element{
	
	public void draw(){
		Draw.color(Color.CORAL);
		Draw.thick(Unit.px.inPixels(2));
		Draw.linerect(x, y, width, height);
		Draw.color();
	}
}
