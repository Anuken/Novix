package io.anuke.novix.modules;

import static io.anuke.novix.Var.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTextField;

import io.anuke.novix.Novix;
import io.anuke.novix.SkinLoader;
import io.anuke.novix.Var;
import io.anuke.novix.android.AndroidKeyboard;
import io.anuke.novix.graphics.Palette;
import io.anuke.novix.managers.PaletteManager;
import io.anuke.novix.managers.PrefsManager;
import io.anuke.novix.managers.ProjectManager;
import io.anuke.novix.scene.ColorBox;
import io.anuke.novix.tools.DialogKeyboardMoveListener;
import io.anuke.novix.tools.Project;
import io.anuke.novix.tools.Tool;
import io.anuke.novix.ui.*;
import io.anuke.novix.ui.ProjectMenu.ProjectTable;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.Module;
import io.anuke.utools.SceneUtils;

public class Core extends Module<Novix>{
	Stage stage;
	
	public ProjectManager projectmanager;
	public PaletteManager palettemanager;
	public PrefsManager prefs;
	
	private SettingsMenu settingsmenu;
	private ProjectMenu projectmenu;
	private TutorialDialog tutorialmenu;
	
	private ToolTable toolmenu;
	private ColorTable colormenu;
	
	public Core() {
		stage = new Stage(new ScreenViewport());
		Var.load(this);
		
		Gdx.graphics.setContinuousRendering(false);

		projectDirectory.mkdirs();
		prefs = new PrefsManager();

		palettemanager = new PaletteManager();
		palettemanager.loadPalettes();

		Textures.load("textures/");
		Textures.repeatWrap("alpha", "stripe");
		
		projectmanager = new ProjectManager();
		
		SkinLoader.load();

		AndroidKeyboard.setListener(new DialogKeyboardMoveListener());
		
		tutorialmenu = new TutorialDialog();

		setupTools();
		setupExtraMenus();

		toolmenu.initialize();

		// autosave
		Timer.schedule(new Task(){
			@Override
			public void run(){
				new Thread(new Runnable(){
					public void run(){
						projectmanager.saveProject();
						palettemanager.savePalettes();
						prefs.save();
					}
				}).start();
			}
		}, 20, 20);
		
		//delayed tutorial
		Timer.schedule(new Task(){
			@Override
			public void run(){
				checkTutorial();
			}
		}, 0.1f);
	}
	
	@Override
	public void init(){
		projectmanager.loadProjects();
	}

	@Override
	public void update(){
		clearScreen(clearcolor);
		
		if(FocusManager.getFocusedWidget() != null && (!(FocusManager.getFocusedWidget() instanceof VisTextField)))
			FocusManager.resetFocus(stage);
		
		stage.act(Gdx.graphics.getDeltaTime() > 2 / 60f ? 1 / 60f : Gdx.graphics.getDeltaTime());
		stage.draw();

	}

	void setupExtraMenus(){

		settingsmenu = new SettingsMenu();

		settingsmenu.addPercentScrollSetting("Cursor Size");
		settingsmenu.addPercentScrollSetting("Cursor Speed");
		settingsmenu.addCheckSetting("Gestures", true);
		settingsmenu.addButton("Re-take Tutorial", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				settingsmenu.hide();
				projectmenu.hide();
				collapseToolMenu();
				getModule(Tutorial.class).begin();
			}
		});

		projectmenu = new ProjectMenu();
		projectmenu.update(true);
		
		colormenu = new ColorTable();
	}

	void setupTools(){
		toolmenu = new ToolTable();
	}

	public void openSettingsMenu(){
		settingsmenu.show(stage);
	}

	void checkTutorial(){
		if(!prefs.getBoolean("tutorial")){
			tutorialmenu.show(stage);
		}
		
		prefs.put("tutorial", true);
	}

	public void setPalette(Palette palette){
		colormenu.resetPaletteColor();
		palettemanager.setCurrentPalette(palette);
		prefs.put("palettecolor", 0);
		prefs.put("lastpalette", palette.id);
		prefs.save();
		colormenu.updateColorMenu();
		colormenu.setSelectedColor(palette.colors[0]);
		colormenu.setupBoxColors();
	}

	public void openProjectMenu(){
		final ProjectTable table = projectmenu.update(false);
		projectmenu.startLoading();
		projectmenu.show(stage);

		new Thread(new Runnable(){
			public void run(){
				projectmanager.saveProject();
				projectmenu.notifyLoaded();
				table.loaded = true;
			}
		}).start();
	}
	
	/**Updates the project menu.*/
	public void updateProjects(){
		projectmenu.update(true);
	}
	
	public void updateColorMenu(){
		colormenu.updateColorMenu();
	}
	
	public void hideProjects(){
		projectmenu.hide();
	}
	
	public boolean projectsShown(){
		return projectmenu.getStage() != null;
	}
	
	public boolean loadingProject(){
		return projectmenu.isLoading();
	}

	public Color selectedColor(){
		return colormenu.getSelectedColor().cpy();
	}

	public Project getCurrentProject(){
		return projectmanager.getCurrentProject();
	}

	public Palette getCurrentPalette(){
		return palettemanager.getCurrentPalette();
	}

	public boolean toolMenuCollapsed(){
		return toolmenu.collapsed();
	}

	public boolean colorMenuCollapsed(){
		return colormenu.collapsed();
	}

	public void collapseToolMenu(){
		if(!colormenu.collapsed() && toolmenu.collapsed())
			collapseColorMenu();

		toolmenu.collapse();
	}

	public void collapseColorMenu(){
		if(colormenu.collapsed() && !toolmenu.collapsed())
			collapseToolMenu();
		colormenu.collapse();
	}

	public boolean menuOpen(){
		return !colorMenuCollapsed() || !toolMenuCollapsed();
	}
	
	public Tool tool(){
		return toolmenu.getTool();
	}
	
	public void setSelectedColor(Color color){
		colormenu.setSelectedColor(color);
	}
	
	public void setSelectedColor(int index){
		colormenu.setSelectedColor(index);
	}
	
	public void addRecentColor(Color color){
		colormenu.addRecentColor(color);
	}
	
	public int getColorIndex(){
		return colormenu.getPaletteColor();
	}
	
	public ColorBox getSelectedBox(){
		return colormenu.getSelectedBox();
	}

	public VisDialog getCurrentDialog(){
		if(stage.getScrollFocus() != null){
			Actor actor = SceneUtils.getTopParent(stage.getScrollFocus());
			if(actor instanceof VisDialog){
				return (VisDialog) actor;
			}
		}
		return null;
	}

	public void checkGridResize(){
		((VisImageButton) stage.getRoot().findActor("gridbutton")).setProgrammaticChangeEvents(true);
		
		if(drawing.isImageLarge())
			((VisImageButton) stage.getRoot().findActor("gridbutton")).setChecked(false);
	}
	
	public boolean saving(){
		return projectmanager.isSavingProject();
	}
	
	public Stage stage(){
		return stage;
	}

	@Override
	public void resize(int width, int height){
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause(){
		Novix.log("Pausing and saving everything.");
		projectmanager.saveProject();
		palettemanager.savePalettes();
		prefs.save();
	}

	@Override
	public void dispose(){
		pause();
		VisUI.dispose();
		Textures.dispose();
	}
}
