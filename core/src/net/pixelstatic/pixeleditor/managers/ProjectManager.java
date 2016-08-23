package net.pixelstatic.pixeleditor.managers;

import net.pixelstatic.pixeleditor.modules.Core;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.NamedSizeDialog;
import net.pixelstatic.pixeleditor.tools.PixelCanvas;
import net.pixelstatic.pixeleditor.tools.Project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class ProjectManager{
	private ObjectMap<Long, Project> projects = new ObjectMap<Long, Project>();
	private Json json = new Json();
	private Core main;
	private Project currentProject;
	private boolean savingProject = false;
	private Array<Project> projectsort = new Array<Project>();
	
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
		
		Gdx.app.log("pedebugging", "Created new project with name " + name);

		return project;
	}

	public void openProject(Project project){
		main.prefs.put("lastproject", project.id);
		project.lastloadtime = System.currentTimeMillis();
		currentProject = project;

		Gdx.app.log("pedebugging", "Opening project \"" + project.name + "\"...");

		PixelCanvas canvas = new PixelCanvas(project.getCachedPixmap());
		
		if(canvas.width() > 100 || canvas.height() > 100){
			main.prefs.put("grid", false);
		}
		
		main.prefs.save();
		
		main.drawgrid.setCanvas(canvas);
		main.updateToolColor();
		main.projectmenu.hide();
	}

	public void copyProject(final Project project){

		new DialogClasses.InputDialog("Rename Copied Dialog", project.name, "New Copy Name: "){
			public void result(String text){
				//if(validateProjectName(text)) return;

				try{
					//TODO
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
				//if(validateProjectName(text, project)) return;
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
					project.getFile().file().delete();
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
		Core.i.prefs.put("lastproject", getCurrentProject().id);
		savingProject = true;
		Gdx.app.log("pedebugging", "Starting save..");
		PixmapIO.writePNG(currentProject.getFile(), main.drawgrid.canvas.pixmap);
		Gdx.app.log("pedebugging", "Saving project.");
		savingProject = false;
	}
	
	@SuppressWarnings("unchecked")
	private void loadProjectFile(){
		try{
			ObjectMap<String, Project> map = json.fromJson(ObjectMap.class, Core.i.projectFile);
			projects = new ObjectMap<Long, Project>();
			for(String key : map.keys()){
				projects.put(Long.parseLong(key), map.get(key));
			}
		}catch (Exception e){
			e.printStackTrace();
			Gdx.app.error("pedebugging", "Project file nonexistant or corrupt.");
		}
	}
	
	private void saveProjectsFile(){
		Core.i.projectFile.writeString(json.toJson(projects), false);
	}

	public void loadProjects(){
		loadProjectFile();
		
		for(Project project : projects.values()){
			try{
				project.reloadTexture();
			}catch (Exception e){
				e.printStackTrace();
				Gdx.app.error("pedebugging", "Error loading project \"" + project.name + "\": corrupt file?");
				projects.remove(project.id);
			}
		}
		
		saveProjectsFile();
		
		if(projects.size == 0){
			currentProject = createNewProject("Untitled", 16, 16);
		}else{
			long last = main.prefs.getLong("lastproject");
			
			System.out.println(last);
			try{
				currentProject = projects.get(last);
				currentProject.reloadTexture();
			}catch (Exception e){
				e.printStackTrace();
				currentProject = createNewProject("Untitled", 16, 16);
			}
		}
	}

	public Project loadProject(String name, long id){
		Project project = new Project(name, id);
		projects.put(project.id, project);
		return project;
	}
/*
	boolean checkIfProjectExists(String name, Project ignored){
		for(Project project : projects.values()){
			if(project == ignored) continue;
			if(project.name.equals(name)) return true;
		}
		return false;
	}

	boolean validateProjectName(String name){
		boolean exists = checkIfProjectExists(name, null);

		if( !MiscUtils.isFileNameValid(name)){
			DialogClasses.showError(main.stage, "Project name is invalid!");
			return true;
		}

		if(exists){
			DialogClasses.showError(main.stage, "A project with that name already exists!");
		}

		return exists;
	}

	boolean validateProjectName(String name, Project project){
		if( !MiscUtils.isFileNameValid(name)){
			DialogClasses.showError(main.stage, "Project name is invalid!");
			return true;
		}

		boolean exists = checkIfProjectExists(name, project);

		if(exists){
			DialogClasses.showError(main.stage, "A project with that name already exists!");
		}

		return exists;
	}
	*/
	
	public FileHandle getFile(long id){
		return Core.i.projectDirectory.child(id + ".png");
	}
	
	//TODO
	public long generateProjectID(){
		long id = MathUtils.random(Long.MAX_VALUE-1);
		return id;
	}
}
