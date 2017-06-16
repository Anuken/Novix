package io.anuke.novix;

import static io.anuke.novix.Vars.*;

import io.anuke.novix.ui.*;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.modules.SceneModule;

public class UI extends SceneModule{
	TopMenu top;
	BottomMenu bottom;
	Canvas canvas;
	ProjectMenu projects;
	
	@Override
	public void init(){
		DrawContext.atlas = DrawContext.skin.getAtlas();
		setup();
		
		top.updateDisplay(control.palettes().current().colors);
		
		Inputs.addProcessor(drawing.getGestureDetector());
		Inputs.addProcessor(drawing);
	}
	
	void setup(){
		projects = new ProjectMenu();
		projects.setVisible(false);
		scene.add(projects);
		
		canvas = new Canvas();
		canvas.setFillParent(true);
		scene.add(canvas);
		
		top = new TopMenu();
		scene.add(top);
		
		bottom = new BottomMenu();
		scene.add(bottom);
		
		bottom.updateLayerDisplay();
	}
	
	public void showProjectMenu(){
		projects.show();
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
