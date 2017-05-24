package io.anuke.novix.managers;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

import io.anuke.novix.Core;
import io.anuke.novix.Novix;
import io.anuke.novix.tools.PixelCanvas;
import io.anuke.novix.tools.Project;
import io.anuke.novix.ui.DialogClasses;
import io.anuke.novix.ui.DialogClasses.InfoDialog;
import io.anuke.novix.ui.DialogClasses.NamedSizeDialog;

public class ProjectManager{
	private ObjectMap<Long, Project> projects = new ObjectMap<Long, Project>();
	private Json json = new Json();
	private Core main;
	private Project currentProject;
	private boolean savingProject = false;
	private Array<Project> projectsort = new Array<Project>();
	private boolean backedup;

	public ProjectManager(Core main){
		this.main = main;
	}

	public Iterable<Project> getProjects(){
		projectsort.clear();
		for(Project project : projects.values())
			projectsort.add(project);
		projectsort.sort();
		return projectsort;
	}

	public boolean isSavingProject(){
		return savingProject;
	}

	public Project getCurrentProject(){
		return currentProject;
	}

	public void newProject(){

		new NamedSizeDialog("New Project"){

			public void result(String name, int width, int height){
				//if(validateProjectName(name)) return;

				Project project = createNewProject(name, width, height);

				openProject(project);

			}
		}.show(main.stage);
	}

	public Project createNewProject(String name, int width, int height){
		long id = generateProjectID();

		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		PixmapIO.writePNG(getFile(id), pixmap);

		Project project = loadProject(name, id);

		Novix.log("Created new project with name " + name);

		return project;
	}

	public void openProject(Project project){
		main.prefs.put("lastproject", project.id);
		project.lastloadtime = System.currentTimeMillis();
		currentProject = project;

		Novix.log("Opening project \"" + project.name + "\"...");

		PixelCanvas canvas = new PixelCanvas(project.getCachedPixmap());

		if(canvas.width() > 100 || canvas.height() > 100){
			main.prefs.put("grid", false);
		}

		main.prefs.save();
		
		main.drawgrid.clearActionStack();
		main.drawgrid.setCanvas(canvas, false);
		main.updateToolColor();
		main.projectmenu.hide();
	}

	public void copyProject(final Project project){

		new DialogClasses.InputDialog("Copy Project", project.name, "Copy Name: "){
			public void result(String text){

				try{
					long id = generateProjectID();

					getFile(project.id).copyTo(getFile(id));

					Project newproject = new Project(text, id);

					projects.put(newproject.id, newproject);
					main.projectmenu.update(true);
				}catch(Exception e){
					DialogClasses.showError(main.stage, "Error copying file!", e);
					e.printStackTrace();
				}
			}
		}.show(main.stage);
	}

	public void renameProject(final Project project){
		new DialogClasses.InputDialog("Rename Project", project.name, "Name: "){
			public void result(String text){
				project.name = text;
				main.projectmenu.update(true);
			}
		}.show(main.stage);
	}

	public void deleteProject(final Project project){
		if(project == currentProject){
			DialogClasses.showInfo(main.stage, "You cannot delete the canvas you are currently using!");
			return;
		}

		new DialogClasses.ConfirmDialog("Confirm", "Are you sure you want\nto delete this canvas?"){
			public void result(){
				try{
					project.getFile().delete();
					if(getBackupFile(project.id).exists()) getBackupFile(project.id).delete();
					project.dispose();
					projects.remove(project.id);
					main.projectmenu.update(true);
				}catch(Exception e){
					DialogClasses.showError(main.stage, "Error deleting file!", e);
					e.printStackTrace();
				}
			}
		}.show(main.stage);
	}

	public void saveProject(){
		saveProjectsFile();
		io.anuke.novix.i.prefs.put("lastproject", getCurrentProject().id);
		savingProject = true;
		Novix.log("Starting save..");
		PixmapIO.writePNG(currentProject.getFile(), main.drawgrid.canvas.pixmap);
		Novix.log("Saving project.");
		savingProject = false;
	}

	@SuppressWarnings("unchecked")
	private void loadProjectFile(){
		try{
			ObjectMap<String, Project> map = json.fromJson(ObjectMap.class, io.anuke.novix.i.projectFile);
			projects = new ObjectMap<Long, Project>();
			for(String key : map.keys()){
				projects.put(Long.parseLong(key), map.get(key));
			}
		}catch(Exception e){
			e.printStackTrace();
			Novix.log("Project file nonexistant or corrupt.");
		}
	}

	private void saveProjectsFile(){
		io.anuke.novix.i.projectFile.writeString(json.toJson(projects), false);
	}

	public void loadProjects(){
		loadProjectFile();
		
		long last = main.prefs.getLong("lastproject");

		currentProject = projects.get(last);

		for(Project project : projects.values()){
			try{
				project.reloadTexture();
			}catch(Exception e){
				e.printStackTrace();
				Novix.log("Error loading project \"" + project.name + "\": corrupt file?");
				
				//remove project, it's dead to me. don't mention it, the user doesn't need to know about this
				if(project != currentProject){
					projects.remove(project.id);
				}
			}
		}

		saveProjectsFile();
		
		if(projects.get(last) == null){ // no project selected
			tryLoadAnotherProject();
		}else{
			backedup = false;
			
			try{

				currentProject = projects.get(last);
				currentProject.reloadTexture();

				getFile(currentProject.id).copyTo(getBackupFile(currentProject.id));

				Novix.log("Loaded and backed up current project.");

			}catch(Exception e){ //corruption!
				e.printStackTrace();
				Novix.log("Project file corrupted?");
				projects.remove(currentProject.id); //remove project since it's corrupted
				//try to fix this mess
				if(getBackupFile(currentProject.id).exists()){
					try{
						getBackupFile(currentProject.id).copyTo(getFile(currentProject.id));
						currentProject.reloadTexture();
						backedup = true;
					}catch(Exception e2){ //well, we tried
						e2.printStackTrace();
						Novix.log("Backup attempt failed.");
						tryLoadAnotherProject();
					}
				//there is no backup, nowhere else to turn
				}else{
					tryLoadAnotherProject();
				}
				
				//show the result
				io.anuke.novix.i.stage.addAction(Actions.sequence(Actions.delay(0.01f), new Action(){
					@Override
					public boolean act(float delta){
						new InfoDialog("Info", backedup ? "[ORANGE]Your project file has been either corrupted or deleted.\n\n[GREEN]Fortunately, a backup has been found and loaded." : "[RED]Your project file has been either corrupted or deleted.\n\n[ORANGE]A backup has not been found.\n\n[ROYAL]If you believe this is an error, try reporting the circumstances under which you last closed the app at the Google Play store listing. This could help the developer fix the problem."){

							public void result(){
								currentProject.reloadTexture();
							}
						}.show(io.anuke.novix.i.stage);
						return true;
					}
				}));
			}
		}

	}

	void tryLoadAnotherProject(){
		if(projects.size == 0){
			currentProject = createNewProject("Untitled", 16, 16);
		}else{
			currentProject = projects.values().next();
		}
	}

	public Project loadProject(String name, long id){
		Project project = new Project(name, id);
		projects.put(project.id, project);
		return project;
	}

	public FileHandle getFile(long id){
		return io.anuke.novix.i.projectDirectory.child(id + ".png");
	}

	public FileHandle getBackupFile(long id){
		return io.anuke.novix.i.projectDirectory.child(id + "-backup.png");
	}

	public long generateProjectID(){
		long id = MathUtils.random(Long.MAX_VALUE - 1);
		return id;
	}
}
