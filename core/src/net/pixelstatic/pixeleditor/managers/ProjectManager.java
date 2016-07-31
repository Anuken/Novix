package net.pixelstatic.pixeleditor.managers;

import java.io.IOException;

import net.pixelstatic.pixeleditor.graphics.PixelCanvas;
import net.pixelstatic.pixeleditor.graphics.Project;
import net.pixelstatic.pixeleditor.modules.Main;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.NamedSizeDialog;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.dialogs.AndroidDialogs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ObjectMap;

public class ProjectManager{
	private ObjectMap<String, Project> projects = new ObjectMap<String, Project>();
	private Main main;
	private Project currentProject;
	private boolean savingProject = false;
	
	public ProjectManager(Main main){
		this.main = main;
	}
	
	public Iterable<Project> getProjects(){
		return projects.values();
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
				if(validateProjectName(name)) return;

				Project project = createNewProject(name, width, height);

				openProject(project);

			}
		}.show(main.stage);
	}
	
	public Project createNewProject(String name, int width, int height){
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		PixmapIO.writePNG(main.projectDirectory.child(name + ".png"), pixmap);

		Project project = loadProject(main.projectDirectory.child(name + ".png"));
		Gdx.app.log("pedebugging", "Created new project with name " + name);

		return project;
	}

	public void openProject(Project project){
		main.prefs.putString("lastproject", project.name);
		main.prefs.flush();
		currentProject = project;

		Gdx.app.log("pedebugging", "Opening project \"" + project.name + "\"...");

		PixelCanvas canvas = new PixelCanvas(project.getCachedPixmap());

		main.drawgrid.setCanvas(canvas);
		main.updateToolColor();
		main.projectmenu.hide();
	}

	public void copyProject(final Project project){

		new DialogClasses.InputDialog("Rename Copied Dialog", project.name, "New Copy Name: "){
			public void result(String text){
				if(validateProjectName(text)) return;

				try{
					FileHandle newhandle = project.file.parent().child(text + ".png");
					MiscUtils.copyFile(project.file.file(), newhandle.file());

					projects.put(text, new Project(newhandle));
					main.projectmenu.update(true);
				}catch(IOException e){
					AndroidDialogs.showError(main.stage, "Error copying file!", e);
					e.printStackTrace();
				}
			}
		}.show(main.stage);
	}

	public void renameProject(final Project project){
		new DialogClasses.InputDialog("Rename Project", project.name, "Name: "){
			public void result(String text){
				if(validateProjectName(text, project)) return;
				projects.remove(project.name);
				project.name = text;
				projects.put(text, project);
				main.projectmenu.update(true);
			}
		}.show(main.stage);
	}

	public void deleteProject(final Project project){
		if(project == currentProject){
			AndroidDialogs.showInfo(main.stage, "You cannot delete the canvas you are currently using!");
			return;
		}

		new DialogClasses.ConfirmDialog("Confirm", "Are you sure you want\nto delete this canvas?"){
			public void result(){
				try{
					project.file.file().delete();
					project.dispose();
					projects.remove(project.name);
					main.projectmenu.update(true);
				}catch(Exception e){
					AndroidDialogs.showError(main.stage, "Error deleting file!", e);
					e.printStackTrace();
				}
			}
		}.show(main.stage);
	}

	public void saveProject(){
		savingProject = true;
		Gdx.app.log("pedebugging", "Starting save..");
		PixmapIO.writePNG(currentProject.file, main.drawgrid.canvas.pixmap);
		Gdx.app.log("pedebugging", "Saving project.");
		savingProject = false;
	}

	public void loadProjects(){
		FileHandle[] files = main.projectDirectory.list();

		for(FileHandle file : files){
			if(file.extension().equals("png")){
				try{
					loadProject(file);
				}catch(Exception e){
					Gdx.app.error("pedebugging", "Error loading project \"" + file.nameWithoutExtension() + " \", corrupt file?", e);
					//projects.remove(file.nameWithoutExtension());
				}
			}
		}

		if(projects.size == 0){
			currentProject = createNewProject("Untitled", 16, 16);
		}else{
			String last = main.prefs.getString("lastproject", "Untitled");
			
			try{
				currentProject = projects.get(last);
				currentProject.reloadTexture();
			}catch (Exception e){
				e.printStackTrace();
				currentProject = createNewProject("Untitled", 16, 16);
			}
		}
	}

	public Project loadProject(FileHandle file){
		if(!file.parent().equals(main.projectDirectory)) throw new IllegalArgumentException("File " + file + " is not in the project directory!");
		Project project = new Project(file);
		projects.put(project.name, project);
		return project;
	}

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
			AndroidDialogs.showError(main.stage, "Project name is invalid!");
			return true;
		}

		if(exists){
			AndroidDialogs.showError(main.stage, "A project with that name already exists!");
		}

		return exists;
	}

	boolean validateProjectName(String name, Project project){
		if( !MiscUtils.isFileNameValid(name)){
			AndroidDialogs.showError(main.stage, "Project name is invalid!");
			return true;
		}

		boolean exists = checkIfProjectExists(name, project);

		if(exists){
			AndroidDialogs.showError(main.stage, "A project with that name already exists!");
		}

		return exists;
	}
}
