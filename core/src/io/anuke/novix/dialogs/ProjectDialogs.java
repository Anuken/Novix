package io.anuke.novix.dialogs;

import com.badlogic.gdx.graphics.Colors;

import io.anuke.novix.Vars;
import io.anuke.novix.element.FloatingMenu;
import io.anuke.novix.internal.Project;
import io.anuke.ucore.util.Strings;

public class ProjectDialogs{

	public static final FloatingMenu newProject = new FloatingMenu("New Project"){
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
	};
}
