package io.anuke.novix;

import static io.anuke.novix.Vars.*;

import io.anuke.novix.ui.BottomMenu;
import io.anuke.novix.ui.Canvas;
import io.anuke.novix.ui.TopMenu;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.modules.SceneModule;

public class UI extends SceneModule{
	TopMenu top;
	BottomMenu bottom;
	Canvas canvas;
	
	@Override
	public void init(){
		DrawContext.atlas = DrawContext.skin.getAtlas();
		setup();
		
		top.updateDisplay(control.palettes().current().colors);
		
		Inputs.addProcessor(drawing.getGestureDetector());
		Inputs.addProcessor(drawing);
	}
	
	void setup(){
		canvas = new Canvas();
		canvas.setFillParent(true);
		scene.add(canvas);
		
		top = new TopMenu();
		scene.add(top);
		
		bottom = new BottomMenu();
		scene.add(bottom);
		
		bottom.updateLayerDisplay();
	}
	
	public boolean menuOpen(){
		return top.open() || bottom.open();
	}
	
	public TopMenu top(){
		return top;
	}
	
	public BottomMenu bottom(){
		return bottom;
	}
	
}
