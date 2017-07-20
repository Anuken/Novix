package io.anuke.novix.dialogs;

import static io.anuke.novix.Vars.ui;

import com.badlogic.gdx.graphics.Colors;

import io.anuke.novix.Vars;
import io.anuke.novix.element.FloatingMenu;
import io.anuke.novix.internal.Project;
import io.anuke.ucore.scene.ui.TextField;
import io.anuke.ucore.util.Strings;

public class ProjectDialogs{
	public static Project project;

	public static final FloatingMenu 
	
	newProject = new FloatingMenu("New Project"){
		String name = "";
		String w = "16", h = "16";
		
		{
			content.defaults().height(42);

			content.add("Name:").left();

			content.addField("", c -> {
				name = c;
			}).colspan(2).fillX();

			content.row();

			content.add("Size: ").colspan(3).left();

			content.row();

			content.addField("16", c -> {
				w = c;
			});
			content.add("X", Colors.get("accent")).pad(4);
			
			content.addField("16", c -> {
				h = c;
			});

			content.row();
			
			//TODO layers
			
			content.addButton("Create", () -> {
				int width = Integer.parseInt(w), height = Integer.parseInt(h);
				Project project = Vars.control.projects().createNewProject(name, 1, width, height);
				
				Vars.control.projects().openProject(project);
				
				hide();
			}, b->{
				b.setDisabled(()->{
					return !(Strings.canParsePostiveInt(w) && Strings.canParsePostiveInt(h) && !name.trim().isEmpty());
				});
			}).colspan(3).padTop(100).fillX().height(50);
		}
	},
	confirmDelete = new BaseDialogs.ConfirmDialog("Confirm Delete Project", "Are you sure you want to delete this project?", ()->{
		Vars.control.projects().removeProject(project);
		
		rebuild();
	}),
	rename = new FloatingMenu("Rename Project"){
		String name = "";
		TextField field;
		
		{
			shown(()->{
				name = project.name;
				field.setText(name);
			});
			
			content.defaults().height(42);

			content.add("New name:").left().padRight(30);

			field = content.addField(name, c -> {
				name = c;
			}).colspan(2).fillX().getElement();

			content.row();
			
			content.addButton("Rename", () -> {
				project.name = name;
				hide();
				rebuild();
			}, b->{
				b.setDisabled(()-> name.trim().isEmpty());
			}).colspan(3).padTop(100).fillX().height(50);
		}
	};
	
	private static void rebuild(){
		ui.getProjectMenu().rebuild();
	}
}
