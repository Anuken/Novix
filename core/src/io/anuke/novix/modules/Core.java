package io.anuke.novix.modules;

import static io.anuke.ucore.UCore.s;

import java.lang.reflect.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

import io.anuke.novix.Novix;
import io.anuke.novix.android.AndroidKeyboard;
import io.anuke.novix.graphics.Palette;
import io.anuke.novix.managers.PaletteManager;
import io.anuke.novix.managers.PrefsManager;
import io.anuke.novix.managers.ProjectManager;
import io.anuke.novix.scene2D.ColorBox;
import io.anuke.novix.scene2D.DrawingGrid;
import io.anuke.novix.tools.*;
import io.anuke.novix.ui.*;
import io.anuke.novix.ui.DialogClasses.MenuDialog;
import io.anuke.novix.ui.ProjectMenu.ProjectTable;
import io.anuke.ucore.graphics.Textures;
import io.anuke.ucore.modules.Module;
import io.anuke.utools.SceneUtils;

public class Core extends Module<Novix>{
	public static Core i;
	
	public final int largeImageSize = 100 * 100;
	public final Color clearcolor = Color.valueOf("12161b");
	
	public final FileHandle paletteFile = Gdx.files.local("palettes.json");
	public final FileHandle projectFile = Gdx.files.local("projects.json");
	public final FileHandle projectDirectory = Gdx.files.absolute(Gdx.files.getExternalStoragePath()).child("NovixProjects");
	
	public Stage stage;
	public DrawingGrid drawgrid;
	
	public ProjectManager projectmanager;
	public PaletteManager palettemanager;
	public PrefsManager prefs;
	
	public SettingsMenu settingsmenu;
	public ProjectMenu projectmenu;
	
	public ToolTable toolmenu;
	public ColorTable colormenu;

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

		settingsmenu = new SettingsMenu(this);

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

		projectmenu = new ProjectMenu(this);
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
			MenuDialog dialog = new MenuDialog("Tutorial"){
				{
					VisLabel header = new VisLabel("Welcome to Novix!");
					LabelStyle style = new LabelStyle(header.getStyle());
					style.font = VisUI.getSkin().getFont("large-font");
					style.fontColor = Color.CORAL;
					header.setStyle(style);

					getContentTable().add(header).pad(20 * s).row();

					VisImage image = new VisImage("icon");

					getContentTable().add(image).size(image.getPrefWidth() * s, image.getPrefHeight() * s).row();

					getContentTable().add("Would you like to take the tutorial?").pad(20 * s);
					// setFillParent(true);
				}

				public void result(){
					getModule(Tutorial.class).begin();
				}
			};
			dialog.addTitleSeperator();
		
			dialog.show(stage);
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
			Actor actor = SceneUtils.getTopParent(Core.i.stage.getScrollFocus());
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
	
	public PixelCanvas canvas(){
		return drawgrid.canvas;
	}
	
	public ActionStack actionStack(){
		return drawgrid.actions;
	}

	public void loadSkin(){
		FileHandle skinFile = Gdx.files.internal("ui/uiskin.json");
		Skin skin = new Skin();

		FileHandle atlasFile = skinFile.sibling(skinFile.nameWithoutExtension() + ".atlas");
		if(atlasFile.exists()){
			TextureAtlas atlas = new TextureAtlas(atlasFile);
			try{
				Field field = skin.getClass().getDeclaredField("atlas");
				field.setAccessible(true);
				field.set(skin, atlas);
			}catch(Exception e){
				throw new RuntimeException(e);
			}
			skin.addRegions(atlas);
		}
		// Color shadowcolor = new Color(0, 0, 0, 0.6f);
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/smooth.ttf"));

		FreeTypeFontParameter normalparameter = new FreeTypeFontParameter();
		normalparameter.size = (int) (22 * s);

		FreeTypeFontParameter largeparameter = new FreeTypeFontParameter();
		largeparameter.size = (int) (26 * s);

		FreeTypeFontParameter borderparameter = new FreeTypeFontParameter();
		borderparameter.size = (int) (26 * s);
		borderparameter.borderWidth = 2 * s;
		borderparameter.borderColor = clearcolor;
		borderparameter.spaceX = -2;

		BitmapFont font = generator.generateFont(normalparameter);
		font.getData().markupEnabled = true;
		BitmapFont largefont = generator.generateFont(largeparameter);
		BitmapFont borderfont = generator.generateFont(borderparameter);
		borderfont.getData().markupEnabled = true;

		skin.add("default-font", font);
		skin.add("large-font", largefont);
		skin.add("border-font", borderfont);

		skin.load(skinFile);

		VisUI.load(skin);
		skin.get(Window.WindowStyle.class).titleFont = largefont;
		skin.get(Window.WindowStyle.class).titleFontColor = Color.CORAL;

		skin.get("dialog", Window.WindowStyle.class).titleFont = largefont;
		skin.get("dialog", Window.WindowStyle.class).titleFontColor = Color.CORAL;

		generator.dispose();
	}

	public Core() {
		Gdx.graphics.setContinuousRendering(false);

		i = this;

		projectDirectory.mkdirs();
		prefs = new PrefsManager(this);

		palettemanager = new PaletteManager(this);
		palettemanager.loadPalettes();

		Textures.load("textures/");
		Textures.repeatWrap("alpha", "stripe");
		stage = new Stage();
		stage.setViewport(new ScreenViewport());
		projectmanager = new ProjectManager(this);
		loadSkin();

		AndroidKeyboard.setListener(new DialogKeyboardMoveListener());

		projectmanager.loadProjects();

		setupTools();
		setupCanvas();
		setupExtraMenus();

		updateToolColor();

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
		
		//delated tutorial
		Timer.schedule(new Task(){
			@Override
			public void run(){
				checkTutorial();
			}
		}, 0.1f);
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
