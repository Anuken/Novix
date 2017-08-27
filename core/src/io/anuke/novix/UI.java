package io.anuke.novix;

import static io.anuke.novix.Vars.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;

import io.anuke.novix.dialogs.FilterDialogs.FilterMenu;
import io.anuke.novix.ui.*;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.modules.SceneModule;
import io.anuke.ucore.scene.ui.layout.Unit;

public class UI extends SceneModule{
	TopMenu top;
	BottomMenu bottom;
	Canvas canvas;
	ProjectMenu projects;
	PaletteMenu palettes;
	FilterMenu filter;
	ToolMenu tools;
	
	@Override
	public void init(){
		setColors();
		DrawContext.font.setUseIntegerPositions(true);
		DrawContext.font.getData().setScale((int)(Unit.dp.inPixels(1f)+0.001f));
		DrawContext.atlas = DrawContext.skin.getAtlas();
		
		setup();
		
		top.updateDisplay(control.palettes().current().colors);
		
		Inputs.addProcessor(drawing.getGestureDetector());
		Inputs.addProcessor(drawing);
	}
	
	void setColors(){
		Colors.put("accent", Color.ROYAL);
		Colors.put("title", Color.CORAL);
		Colors.put("text", Color.CORAL);
		Colors.put("shading", Color.GRAY);
		Colors.put("border", Color.ROYAL);
	}
	
	void setup(){
		projects = new ProjectMenu();
		
		palettes = new PaletteMenu();
		
		tools = new ToolMenu();
		
		canvas = new Canvas();
		canvas.setFillParent(true);
		scene.add(canvas);
		
		top = new TopMenu();
		scene.add(top);
		
		bottom = new BottomMenu();
		scene.add(bottom);
		
		bottom.updateLayerDisplay();
		
		top.setVisible(()->filter == null);
		bottom.setVisible(()->filter == null);
	}
	
	public void showToolMenu(){
		tools.show();
	}
	
	public void hideToolMenu(){
		tools.hide();
	}
	
	public void showProjectMenu(){
		projects.show();
	}
	
	public void showPaletteMenu(){
		palettes.show();
	}
	
	public void updatePaletteMenu(){
		palettes.rebuild();
	}
	
	public boolean menuOpen(){
		return top.open() || (bottom.open() && bottom.hasMouse());
	}
	
	public TopMenu top(){
		return top;
	}
	
	public BottomMenu bottom(){
		return bottom;
	}
	
	public boolean hasFilter(){
		return filter != null;
	}
	
	public void setFilterMenu(FilterMenu menu){
		this.filter = menu;
	}
	
	public FilterMenu getFilterMenu(){
		return filter;
	}
	
	public ProjectMenu getProjectMenu(){
		return projects;
	}
	
}
