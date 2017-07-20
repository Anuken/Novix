package io.anuke.novix.handlers;

import static io.anuke.novix.Vars.*;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;

import io.anuke.novix.Novix;
import io.anuke.novix.internal.Layer;
import io.anuke.novix.internal.Project;
import io.anuke.ucore.core.Settings;

public class Projects{
	private ObjectMap<String, Project> projects = new ObjectMap<>();
	private Project currentProject;
	private boolean savingProject = false;
	private Array<Project> projectsort = new Array<Project>();
	private boolean backedup;

	public Iterable<Project> getProjects(){
		projectsort.clear();
		for(Project project : projects.values())
			projectsort.add(project);
		projectsort.sort();
		return projectsort;
	}

	public boolean isSaving(){
		return savingProject;
	}

	public Project current(){
		return currentProject;
	}
	
	public void addProject(Project project){
		projects.put(project.id, project);
	}
	
	public void removeProject(Project project){
		projects.remove(project.id);
	}
	
	public Project copyProject(Project project){
		String newname = project.name + "copy";
		Project copy = new Project(newname, project.layers, generateProjectID());
		copy.lastloadtime = TimeUtils.millis();
		
		int i = 0;
		for(FileHandle file : project.getFiles()){
			file.copyTo(copy.getFiles()[i++]);
		}
		
		return copy;
	}

	public Project createNewProject(String name, int layers, int width, int height){
		String id = generateProjectID();
		
		Project project = loadProject(name, layers, id);
		
		FileHandle[] files = project.getFiles();
		
		for(FileHandle file : files){
			Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
			PixmapIO.writePNG(file, pixmap);
		}

		Novix.log("Created new project with name " + name);

		return project;
	}

	public void openProject(Project project){
		Settings.putString("lastproject", project.id+"");
		
		project.lastloadtime = System.currentTimeMillis();
		currentProject = project;

		Novix.log("Opening project \"" + project.name + "\"...");
		
		FileHandle[] files = currentProject.getFiles();
		Layer[] layers = new Layer[files.length];
		for(int i = 0; i < files.length; i ++){
			layers[i] = new Layer(new Pixmap(files[i]));
		}
		
		drawing.loadLayers(layers);
		
		Settings.save();
	}
	
	/**This is should be run asynchronously.*/
	public void saveProject(){
		saveProjectsFile();
		
		//TODO only save if each layer has been modified
		Settings.putString("lastproject", current().id+"");
		savingProject = true;
		
		Novix.log("Starting save..");
		
		FileHandle[] files = currentProject.getFiles();
		for(int i = 0; i < files.length; i ++){
			PixmapIO.writePNG(files[i], drawing.getLayer(i).getPixmap());
		}
		
		Novix.log("Saving project.");
		savingProject = false;
	}

	private void loadProjectFile(){
		try{
			ObjectMap<String, Project> map = json.fromJson(ObjectMap.class, projectFile);
			projects = new ObjectMap<>();
			
			for(String key : map.keys()){
				projects.put(key, map.get(key));
			}
		}catch(Exception e){
			e.printStackTrace();
			Novix.log("Project file nonexistant or corrupt.");
		}
	}

	private void saveProjectsFile(){
		projectFile.writeString(json.toJson(projects), false);
	}

	public void loadProjects(){
		loadProjectFile();
		
		String last = Settings.getString("lastproject");

		currentProject = projects.get(last);

		for(Project project : projects.values()){
			try{
				project.reloadTextures();
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
			Novix.log("No project selected.");
			tryLoadAnotherProject();
		}else{
			backedup = false;
			
			try{

				currentProject = projects.get(last);
				currentProject.reloadTextures();
				
				int i = 0;
				for(FileHandle file : currentProject.getFiles()){
					file.copyTo(currentProject.getBackupFiles()[i++]);
				}
				
				Novix.log("Loaded and backed up current project.");
				
				openProject(currentProject);

			}catch(Exception e){ //corruption!
				e.printStackTrace();
				Novix.log("Project file corrupted?");
				projects.remove(currentProject.id); //remove project since it's corrupted
				//TODO backups
				/*
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
				
				stage.addAction(Actions.sequence(Actions.delay(0.01f), new Action(){
					@Override
					public boolean act(float delta){
						new InfoDialog("Info", backedup ? "[ORANGE]Your project file has been either corrupted or deleted.\n\n[GREEN]Fortunately, a backup has been found and loaded." : "[RED]Your project file has been either corrupted or deleted.\n\n[ORANGE]A backup has not been found.\n\n[ROYAL]If you believe this is an error, try reporting the circumstances under which you last closed the app at the Google Play store listing. This could help the developer fix the problem."){

							public void result(){
								currentProject.reloadTextures();
							}
						}.show(stage);
						return true;
					}
				}));
				*/
			}
		}

	}

	void tryLoadAnotherProject(){
		if(projects.size == 0){
			currentProject = createNewProject("Untitled", 1, 16, 16);
		}else{
			currentProject = projects.values().next();
		}
		openProject(currentProject);
	}

	private Project loadProject(String name, int layers, String id){
		Project project = new Project(name, layers, id);
		projects.put(project.id, project);
		return project;
	}
	
	public static String generateProjectID(){
		long id = MathUtils.random(Long.MAX_VALUE - 1);
		return id + "";
	}
}
