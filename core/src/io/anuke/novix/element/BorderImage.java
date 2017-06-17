package io.anuke.novix.element;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.scene.Element;
import io.anuke.ucore.scene.ui.layout.Unit;

public class BorderImage extends Element{
	
	public void draw(){
		Color c = Colors.get("title");
		Draw.color(c.r, c.g, c.b, alpha);
		Draw.thick(Unit.px.inPixels(2));
		Draw.linerect(x, y, width, height);
		Draw.color();
	}
}
