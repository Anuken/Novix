package io.anuke.novix.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Texture;

import io.anuke.novix.Vars;
import io.anuke.novix.dialogs.ProjectDialogs;
import io.anuke.novix.element.*;
import io.anuke.novix.internal.Project;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.scene.builders.*;
import io.anuke.ucore.scene.ui.Image;
import io.anuke.ucore.scene.ui.layout.Stack;
import io.anuke.ucore.scene.ui.layout.Table;

public class ProjectMenu extends FloatingMenu{
	private Table content;
	private FloatingMenu newProject;
	
	public ProjectMenu(){
		super("Projects");
		
		background(DrawContext.skin.newDrawable("white", new Color(0, 0, 0, 0.9f)));
		
		pad(10);
		
		clearChildren();
		
		setupMenus();
		
		top().left();
		
		//setFillParent(true);
		///background("button");
		//setTouchable(Touchable.enabled);
		
		content = new Table();
		
		add("Projects", Colors.get("title")).left();
		row();
		
		add(new Border()).growX().height(6).padBottom(6).padTop(6);
		
		row();
		
		Table tasks = new Table();
		
		tasks.defaults().size(175, 50);
		
		tasks.top().left();
		
		tasks.addImageTextButton("New Project", "icon-plus", 32, ()->{
			newProject.show();
		}).left();
		
		tasks.add().size(0, 0).growX();
		
		tasks.addImageTextButton("Settings", "icon-settings", 32, ()->{
			
		});
		
		add(tasks).growX();
		
		row().growX();
		
		add(content).padTop(6).grow();
		
		row();
		
		addCenteredImageTextButton("Back", "icon-arrow-left", 40, ()->{
			hide();
		}).growX().height(60).padBottom(0);
	}
	
	void setupMenus(){
		newProject = new FloatingMenu("New Project");
		
		newProject.addMenuItem("New", "icon-file", "Create an entirely new project.", ()->{
			newProject.hide();
			ProjectDialogs.newProject.show();
		});
		
		newProject.addMenuItem("From File", "icon-project-open", "Loading an already existing image file.", ()->{
			newProject.hide();
		});
		
	}
	
	public void show(){
		super.show();
		rebuildList();
	}
	/*
	public void show(){
		rebuildList();
		toFront();
		setVisible(true);
	}
	
	public void hide(){
		setVisible(false);
	}
	*/
	public void rebuildList(){
		Iterable<Project> projects = Vars.control.projects().getProjects();
		
		content.clearChildren();
		content.top();
		
		for(Project p : projects){
			
			ProjectTable table = new ProjectTable(p);
			content.add(table).growX().padTop(4);
			
			content.row();
		}
	}
	
	class ProjectTable extends Table{
		
		public ProjectTable(Project project){
			
			Stack stack = new Stack();
			
			project.reloadTextures();
			
			Texture[] tex = project.getCachedTextures();
			
			stack.add(new AlphaImage(tex[0].getWidth(), tex[0].getHeight()));
			
			for(Texture t : tex){
				stack.add(new Image(t));
			}
			
			stack.add(new BorderImage());
			
			build.begin(this);
			
			background("button");
			
			left();
			add(stack).padRight(4).padBottom(4).size(120).left();
			
			new table(){{
				
				aleft().atop();
				
				new label(project.name).color("title").left();
				
				row();
				
				new label(tex[0].getWidth() + "x" + tex[0].getHeight())
				.padTop(10).color("shading").left();
				
			}}.top().left().end().expandX();
			
			row();
			pad(10);
			
			float isize = 42;
			
			new table(){{
				aleft();
				defaults().size(80, 50);
				
				new imagebutton("icon-project-open", isize, ()->{
					
				});
				
				new imagebutton("icon-copy", isize, ()->{
					
				});
				
				new imagebutton("icon-rename", isize, ()->{
					
				});
				
				new imagebutton("icon-trash", isize, ()->{
					
				});
			}}.end().colspan(2).left();
			
			build.end();
		}
	}
}
