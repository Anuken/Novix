package io.anuke.novix.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Texture;

import io.anuke.novix.Vars;
import io.anuke.novix.dialogs.ProjectDialogs;
import io.anuke.novix.element.*;
import io.anuke.novix.internal.Project;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.scene.builders.*;
import io.anuke.ucore.scene.ui.Image;
import io.anuke.ucore.scene.ui.ScrollPane;
import io.anuke.ucore.scene.ui.layout.Stack;
import io.anuke.ucore.scene.ui.layout.Table;

public class ProjectMenu extends FloatingMenu{
	private Table pcontent;
	private FloatingMenu newProject;
	private ScrollPane pane;
	
	public ProjectMenu(){
		super("Projects");
		
		background(Core.skin.newDrawable("white", new Color(0, 0, 0, 0.9f)));
		
		pad(10);
		
		clearChildren();
		
		setupMenus();
		
		top().left();
		
		//setFillParent(true);
		///background("button");
		//setTouchable(Touchable.enabled);
		
		pcontent = new Table();
		pane = new ScrollPane(pcontent);
		
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
		
		add(pane).padTop(6).grow();
		
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
		Core.scene.setScrollFocus(pane);
		rebuild();
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
	public void rebuild(){
		Iterable<Project> projects = Vars.control.projects().getProjects();
		
		pcontent.clearChildren();
		pcontent.top();
		
		for(Project p : projects){
			
			ProjectTable table = new ProjectTable(p);
			pcontent.add(table).growX().padTop(4);
			
			pcontent.row();
		}
	}
	
	class ProjectTable extends Table{
		
		public ProjectTable(Project project){
			
			Stack stack = new Stack();
			
			project.reloadTextures();
			
			Texture[] tex = project.getCachedTextures();
			
			stack.add(new AlphaImage());
			
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
					ProjectMenu.this.hide();
					Vars.control.projects().openProject(project);
				});
				
				new imagebutton("icon-copy", isize, ()->{
					Project copy = Vars.control.projects().copyProject(project);
					
					Vars.control.projects().addProject(copy);
					
					rebuild();
				});
				
				new imagebutton("icon-rename", isize, ()->{
					ProjectDialogs.project = project;
					ProjectDialogs.rename.show();
				});
				
				new imagebutton("icon-trash", isize, ()->{
					ProjectDialogs.project = project;
					ProjectDialogs.confirmDelete.show();
				});
				
			}}.end().colspan(2).left();
			
			build.end();
		}
	}
}
