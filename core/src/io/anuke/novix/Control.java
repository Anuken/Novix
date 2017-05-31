package io.anuke.novix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import io.anuke.novix.handlers.Palettes;
import io.anuke.novix.handlers.Projects;
import io.anuke.novix.internal.Tool;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.core.Settings;
import io.anuke.ucore.modules.RendererModule;

public class Control extends RendererModule{
	private Palettes palettes;
	private Projects projects;
	private Tool tool = Tool.pencil;
	
	public Control(){
		Settings.defaultList(
			"grid", true,
			"gestures", true,
			"cursormode", true,
			"cursorsize", 1f,
			"cursorspeed", 1f,
			
			"hsymmetry", false,
			"vsymmetry", false,
			
			"tutorial", false,
			
			"brushsize", 1,
			"opacity", 1f,
			"alpha", 1f,
			
			"palettecolor", 0,
			"genpalettes", true,
			
			"lock", false,
			
			"lastpalette", null,
			"lastproject", null
		);
		
		Settings.loadAll("io.anuke.novix");
		
		palettes = new Palettes();
		projects = new Projects();
		
		palettes.load();
	}
	
	@Override
	public void init(){
		projects.loadProjects();
	}
	
	@Override
	public void update(){
		if(Inputs.keyUp(Keys.ESCAPE))
			Gdx.app.exit();
		
		clearScreen();
	}
	
	public void setTool(Tool tool){
		this.tool = tool;
	}
	
	public Tool tool(){
		return tool;
	}
	
	public Palettes palettes(){
		return palettes;
	}
	
	public Projects projects(){
		return projects;
	}
	
	public boolean saving(){
		//TODO
		return projects.isSaving();
	}
	
}
