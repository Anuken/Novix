package io.anuke.novix.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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
import io.anuke.novix.tools.*;
import io.anuke.novix.ui.*;
import io.anuke.novix.ui.ProjectMenu.ProjectTable;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.Module;
import io.anuke.utools.SceneUtils;

public class Core extends Module<Novix>{
	public static final int largeImageSize = 100 * 100;
	public static final Color clearcolor = Color.valueOf("12161b");
	
	public final FileHandle paletteFile = Gdx.files.local("palettes.json");
	public final FileHandle projectFile = Gdx.files.local("projects.json");
	public final FileHandle projectDirectory = Gdx.files.absolute(Gdx.files.getExternalStoragePath()).child("NovixProjects");
	
	Stage stage;
	
	private ProjectManager projectmanager;
	private PaletteManager palettemanager;
	private PrefsManager prefs;
	
	private SettingsMenu settingsmenu;
	private ProjectMenu projectmenu;
	private TutorialDialog tutorialmenu;
	
	private ToolTable toolmenu;
	private ColorTable colormenu;
	
	public Core() {
		
		Gdx.graphics.setContinuousRendering(false);

		projectDirectory.mkdirs();
		prefs = new PrefsManager(this);

		palettemanager = new PaletteManager(this);
		palettemanager.loadPalettes();

		Textures.load("textures/");
		Textures.repeatWrap("alpha", "stripe");
		
		stage = new Stage(new ScreenViewport());
		projectmanager = new ProjectManager(this);
		
		SkinLoader.load();

		AndroidKeyboard.setListener(new DialogKeyboardMoveListener());

		projectmanager.loadProjects();
		
		tutorialmenu = new TutorialDialog();

		setupTools();
		setupCanvas();
		setupExtraMenus();

		updateToolColor();

		toolmenu.initialize();
		
		Var.load(this);

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
	public void update(){
		clearScreen(clearcolor);
		
		if(FocusManager.getFocusedWidget() != null && (!(FocusManager.getFocusedWidget() instanceof VisTextField)))
			FocusManager.resetFocus(stage);
		
		toolmenu.getTool().update(drawgrid);
		
		stage.act(Gdx.graphics.getDeltaTime() > 2 / 60f ? 1 / 60f : Gdx.graphics.getDeltaTime());
		stage.draw();

		// pc debugging
		if(stage.getKeyboardFocus() instanceof Button || stage.getKeyboardFocus() == null
				|| stage.getKeyboardFocus() instanceof VisDialog)
			stage.setKeyboardFocus(drawgrid);
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
		
		colormenu = new ColorTable(this);
	}

	void setupTools(){
		toolmenu = new ToolTable(this);
	}

	public void openSettingsMenu(){
		settingsmenu.show(stage);
	}

	void setupCanvas(){
		drawgrid = new DrawingGrid(this);

		drawgrid.setCanvas(new PixelCanvas(getCurrentProject().getCachedPixmap()), false);

		stage.addActor(drawgrid);
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
	
	public boolean loadingProject(){
		return projectmenu.isLoading();
	}

	public Color selectedColor(){
		return colormenu.getSelectedColor().cpy();
	}

	public void updateToolColor(){
		if(toolmenu.getTool() != null && drawgrid != null){
			toolmenu.getTool().onColorChange(selectedColor(), drawgrid.canvas);
			drawgrid.canvas.setAlpha(toolmenu.getBarAlphaValue());
		}
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

	public boolean isImageLarge(){
		return drawgrid.canvas.width() * drawgrid.canvas.height() > largeImageSize;
	}
	
	public Tool tool(){
		return toolmenu.getTool();
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
		if(drawgrid.canvas.width() * drawgrid.canvas.height() >= 100 * 100)
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
