package net.pixelstatic.pixeleditor.modules;

import java.lang.reflect.Field;

import net.pixelstatic.gdxutils.graphics.Hue;
import net.pixelstatic.gdxutils.graphics.ShapeUtils;
import net.pixelstatic.gdxutils.graphics.Textures;
import net.pixelstatic.pixeleditor.PixelEditor;
import net.pixelstatic.pixeleditor.graphics.Palette;
import net.pixelstatic.pixeleditor.managers.PaletteManager;
import net.pixelstatic.pixeleditor.managers.PrefsManager;
import net.pixelstatic.pixeleditor.managers.ProjectManager;
import net.pixelstatic.pixeleditor.scene2D.*;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.MenuDialog;
import net.pixelstatic.pixeleditor.tools.*;
import net.pixelstatic.pixeleditor.ui.*;
import net.pixelstatic.pixeleditor.ui.ProjectMenu.ProjectTable;
import net.pixelstatic.utils.AndroidKeyboard;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.dialogs.AndroidTextFieldDialog;
import net.pixelstatic.utils.dialogs.TextFieldDialog;
import net.pixelstatic.utils.modules.Module;
import net.pixelstatic.utils.scene2D.ColorBar;
import net.pixelstatic.utils.scene2D.ColorBox;
import net.pixelstatic.utils.scene2D.SmoothCollapsibleWidget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;

import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;

public class Core extends Module<PixelEditor>{
	public static Core i;
	public static float s = 1f; //density scale
	public final int largeImageSize = 128 * 128;
	public final Color clearcolor = Color.valueOf("12161b");
	public final String selectcolor = "7aaceaff";
	public DrawingGrid drawgrid;
	public Stage stage;
	public FileHandle projectDirectory;
	public final FileHandle paletteFile = Gdx.files.local("palettes.json");
	public final FileHandle projectFile = Gdx.files.local("projects.json");
	public int paletteColor;
	public ProjectManager projectmanager;
	public PaletteManager palettemanager;
	public PrefsManager prefs;
	public VisTable colortable, pickertable;
	public SettingsMenu settingsmenu;
	public ProjectMenu projectmenu;
	public PaletteMenu palettemenu;
	public ToolMenu toolmenu;
	public CollapseButton colorcollapsebutton, toolcollapsebutton;
	public SmoothCollapsibleWidget colorcollapser, toolcollapser;
	public ColorBox[] boxes;
	public ColorWidget picker;
	public Tool tool = Tool.pencil;

	@Override
	public void update(){
		Hue.clearScreen(clearcolor);
		
		if(FocusManager.getFocusedWidget() != null && ( !(FocusManager.getFocusedWidget() instanceof VisTextField))) FocusManager.resetFocus(stage);
		tool.update(drawgrid);
		stage.act(Gdx.graphics.getDeltaTime() > 2 / 60f ? 1 / 60f : Gdx.graphics.getDeltaTime());
		stage.draw();

		//pc debugging
		if(stage.getKeyboardFocus() instanceof Button || stage.getKeyboardFocus() == null || stage.getKeyboardFocus() instanceof VisDialog) stage.setKeyboardFocus(drawgrid);
	}

	void setupExtraMenus(){

		settingsmenu = new SettingsMenu(this);

		settingsmenu.addPercentScrollSetting("Cursor Size");
		settingsmenu.addPercentScrollSetting("Cursor Speed");
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
	}

	void setupTools(){
		final VisTable tooltable = new VisTable();
		tooltable.setFillParent(true);
		stage.addActor(tooltable);

		final float size = Gdx.graphics.getWidth() / (Tool.values().length);

		toolmenu = new ToolMenu(this);

		toolcollapser = new SmoothCollapsibleWidget(toolmenu, false);

		toolcollapser.setCollapsed(true);

		toolcollapsebutton = new CollapseButton();

		toolcollapsebutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				toolcollapser.setCollapsed( !toolcollapser.isCollapsed());
				toolcollapsebutton.flip();

				if( !colorcollapser.isCollapsed() && event != null){
					((ClickListener)colorcollapsebutton.getListeners().get(2)).clicked(null, x, y);
				}
			}
		});

		tooltable.bottom().left().add(toolcollapser).height(20 * s).colspan(Tool.values().length).fillX().expandX();
		tooltable.row();
		tooltable.bottom().left().add(toolcollapsebutton).height(60 * s).colspan(Tool.values().length).fillX().expandX();
		tooltable.row();

		Tool[] tools = Tool.values();

		for(int i = 0;i < tools.length;i ++){
			final Tool ctool = tools[i];

			final VisImageButton button = new VisImageButton((Drawable)null);
			button.setStyle(new VisImageButtonStyle(VisUI.getSkin().get("toggle", VisImageButtonStyle.class)));
			button.getStyle().imageUp = VisUI.getSkin().getDrawable("icon-" + ctool.name()); //whatever icon is needed
			button.setGenerateDisabledImage(true);
			button.getImageCell().size(50 * s);
			ctool.button = button;
			button.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					ctool.onSelected();
					if( !ctool.selectable()){
						button.setChecked(false);
						return;
					}
					tool = ctool;
					tool.onColorChange(selectedColor(), drawgrid.canvas);
					if( !button.isChecked()) button.setChecked(true);
					for(Actor actor : tooltable.getChildren()){
						if( !(actor instanceof VisImageButton)) continue;
						VisImageButton other = (VisImageButton)actor;
						if(other != button) other.setChecked(false);
					}

				}
			});

			if(i == 0){
				button.setChecked(true);
				tool = ctool;
			}

			tooltable.bottom().left().add(button).size(size + 1f);
		}
		
		tooltable.pack();
	}

	void setupColorPicker(){
		colortable = new VisTable();
		colortable.setFillParent(true);
		stage.addActor(colortable);

		pickertable = new VisTable(){
			public float getPrefWidth(){
				return Gdx.graphics.getWidth();
			}
			
			//public float getPrefHeight(){
			//	return Gdx.graphics.getHeight() - toolcollapsebutton.getTop() - colorcollapsebutton.getHeight();
			//}
		};

		pickertable.background("button-window-bg");

		picker = new ColorWidget(){
			public void onColorChanged(){
				updateSelectedColor(picker.getSelectedColor());
			}
		};

		colortable.top().left();

		colorcollapsebutton = new CollapseButton();
		colorcollapsebutton.flip();

		colorcollapser = new SmoothCollapsibleWidget(pickertable);
		stage.addActor(colorcollapser);

		colorcollapsebutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				if( !colorcollapser.isCollapsed()){
					picker.setSelectedColor(picker.getSelectedColor());
					tool.onColorChange(selectedColor(), drawgrid.canvas);
				}
				colorcollapser.setCollapsed( !colorcollapser.isCollapsed());
				colorcollapsebutton.flip();

				if( !toolcollapser.isCollapsed() && event != null){
					((ClickListener)toolcollapsebutton.getListeners().get(2)).clicked(null, x, y);
				}
			}
		});

		updateColorMenu();

		VisTextButton palettebutton = new VisTextButton("Palettes...");

		palettemenu = new PaletteMenu(this);

		palettebutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				openPaletteMenu();
			}
		});
		pickertable.add().grow().row();;
		picker.pack();
		Cell<?> cell = pickertable.add(picker).expand().fill().padBottom(10f * s).padTop(0f).padBottom(20 * s);
		pickertable.row();
		pickertable.center().add(palettebutton).align(Align.center).padBottom(10f * s).height(60 * s).growX();
		colorcollapser.setY(toolcollapsebutton.getTop()); 
		colorcollapser.toBack();
		colorcollapser.resetY();
		
		Vector2 pos = picker.localToStageCoordinates(new Vector2());
		cell.padTop(Gdx.graphics.getHeight() - (pos.y + picker.getPrefHeight()) - colorcollapsebutton.getPrefHeight());
		cell.padBottom((Gdx.graphics.getHeight() - (pos.y +picker.getPrefHeight() + 90*s) - colorcollapsebutton.getPrefHeight() -  65*s)/2);
		pickertable.pack();
		colorcollapser.setCollapsed(true, false);
		setupBoxColors();

	}

	public void openPaletteMenu(){
		palettemenu.update();
		palettemenu.show(stage);
	}

	public void openSettingsMenu(){
		settingsmenu.show(stage);
	}

	public void updateColorMenu(){
		colortable.clear();

		int maxcolorsize = (int)(65 * s);
		int mincolorsize = (int)(30 * s);

		int colorsize = Gdx.graphics.getWidth() / getCurrentPalette().size() - MiscUtils.densityScale(3);

		int perow = 0; //colors per row

		colorsize = Math.min(maxcolorsize, colorsize);

		if(colorsize < mincolorsize){
			colorsize = mincolorsize;
			perow = Gdx.graphics.getWidth() / colorsize;
		}

		colortable.add(colorcollapsebutton).expandX().fillX().colspan((perow == 0 ? getCurrentPalette().size() : perow) + 2).height(50f * s);
		colorcollapsebutton.setZIndex(colorcollapser.getZIndex() + 10);

		colortable.row();

		colortable.add().growX();

		boxes = new ColorBox[getCurrentPalette().size()];

		for(int i = 0;i < getCurrentPalette().size();i ++){
			final int index = i;
			final ColorBox box = new ColorBox();

			boxes[i] = box;
			colortable.add(box).size(colorsize);

			box.setColor(getCurrentPalette().colors[i]);

			box.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					picker.addRecentColor(boxes[paletteColor].getColor().cpy());
					boxes[paletteColor].selected = false;
					paletteColor = index;
					prefs.put("palettecolor", paletteColor);
					box.selected = true;
					box.toFront();
					setSelectedColor(box.getColor());
				}
			});

			if(perow != 0 && i % perow == perow - 1){
				colortable.add().growX();
				colortable.row();
				colortable.add().growX();
			}
		}

		if(perow == 0) colortable.add().growX();
	}

	void setupBoxColors(){
		paletteColor = prefs.getInteger("palettecolor", 0);
		for(ColorBox box : boxes)
			box.getColor().a = 1f;

		if(paletteColor > boxes.length) paletteColor = 0;

		picker.setRecentColors(boxes);
		boxes[paletteColor].selected = true;
		boxes[paletteColor].toFront();
		picker.setSelectedColor(getCurrentPalette().colors[paletteColor]);
	}

	void setupCanvas(){
		drawgrid = new DrawingGrid(this);

		drawgrid.setCanvas(new PixelCanvas(getCurrentProject().getCachedPixmap()));

		stage.addActor(drawgrid);
	}

	void setupStyles(){
		ColorBox.defaultStyle.box = Color.valueOf("29323d");
		ColorBox.defaultStyle.disabled = Color.valueOf("0f1317");

		ColorBar.borderColor = Color.valueOf("29323d");
	}

	void checkTutorial(){
		if(!prefs.getBoolean("tutorial")){
			new MenuDialog("Tutorial"){
				{
					getContentTable().add("Would you like to take the tutorial?").pad(20 * s);
				}

				public void result(){
					getModule(Tutorial.class).begin();
				}
			}.show(stage);
		}
		prefs.put("tutorial", true);
	}

	public void setPalette(Palette palette){
		paletteColor = 0;
		palettemanager.setCurrentPalette(palette);
		prefs.put("palettecolor", 0);
		prefs.put("lastpalette", palette.id);
		prefs.save();
		updateColorMenu();
		setSelectedColor(palette.colors[0]);
		setupBoxColors();
	}

	public void openProjectMenu(){
		final ProjectTable table = projectmenu.update(false);
		projectmenu.show(stage);

		new Thread(new Runnable(){
			public void run(){
				projectmanager.saveProject();
				table.loaded = true;
			}
		}).start();
	}

	public Color selectedColor(){
		return getCurrentPalette().colors[paletteColor].cpy();
	}

	public void updateSelectedColor(Color color){
		boxes[paletteColor].setColor(color.cpy());
		getCurrentPalette().colors[paletteColor] = color.cpy();
		toolmenu.updateColor(color.cpy());
		updateToolColor();
	}

	public void setSelectedColor(Color color){
		updateSelectedColor(color);
		picker.setSelectedColor(color);
		updateToolColor();
	}

	public void updateToolColor(){
		if(tool != null && drawgrid != null) tool.onColorChange(selectedColor(), drawgrid.canvas);
	}

	public Project getCurrentProject(){
		return projectmanager.getCurrentProject();
	}

	public Palette getCurrentPalette(){
		return palettemanager.getCurrentPalette();
	}

	public boolean toolMenuCollapsed(){
		return toolcollapser.isCollapsed();
	}

	public boolean colorMenuCollapsed(){
		return colorcollapser.isCollapsed();
	}

	public void collapseToolMenu(){
		if( !colorcollapser.isCollapsed() && toolcollapser.isCollapsed()) collapseColorMenu();

		((ClickListener)toolcollapsebutton.getListeners().get(2)).clicked(null, 0, 0);
	}

	public void collapseColorMenu(){
		if(colorcollapser.isCollapsed() && !toolcollapser.isCollapsed()) collapseToolMenu();
		((ClickListener)colorcollapsebutton.getListeners().get(2)).clicked(null, 0, 0);
	}

	public boolean menuOpen(){
		return !colorMenuCollapsed() || !toolMenuCollapsed();
	}

	public boolean isImageLarge(){
		return drawgrid.canvas.width() * drawgrid.canvas.height() > largeImageSize;
	}

	public VisDialog getCurrentDialog(){
		if(stage.getScrollFocus() != null){
			Actor actor = MiscUtils.getTopParent(Core.i.stage.getScrollFocus());
			if(actor instanceof VisDialog){
				return (VisDialog)actor;
			}
		}
		return null;
	}

	public void loadFonts(){
		FileHandle skinFile = Gdx.files.internal("x2/uiskin.json");
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
		//Color shadowcolor = new Color(0, 0, 0, 0.6f);
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/smooth.ttf"));

		FreeTypeFontParameter normalparameter = new FreeTypeFontParameter();
		normalparameter.size = (int)(22 * s);

		FreeTypeFontParameter largeparameter = new FreeTypeFontParameter();
		largeparameter.size = (int)(26 * s);

		FreeTypeFontParameter borderparameter = new FreeTypeFontParameter();
		borderparameter.size = (int)(26 * s);
		borderparameter.borderWidth = 2*s;
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

	public Core(){
		Gdx.graphics.setContinuousRendering(false);

		i = this;
		s = MiscUtils.densityScale();
		GDXDialogsSystem.install();

		GDXDialogsSystem.getDialogManager().registerDialog(TextFieldDialog.class.getCanonicalName(), AndroidTextFieldDialog.class.getCanonicalName());

		projectDirectory = Gdx.files.absolute(Gdx.files.getExternalStoragePath()).child("pixelprojects");
		projectDirectory.mkdirs();
		prefs = new PrefsManager(this);

		palettemanager = new PaletteManager(this);
		palettemanager.loadPalettes();

		Textures.load("textures/");
		Textures.repeatWrap("alpha", "stripe");
		stage = new Stage();
		stage.setViewport(new ScreenViewport());
		projectmanager = new ProjectManager(this);
		loadFonts();

		ShapeUtils.region = VisUI.getSkin().getRegion("white");
		AndroidKeyboard.setListener(new DialogKeyboardMoveListener());

		projectmanager.loadProjects();

		setupStyles();
		setupTools();
		setupColorPicker();
		setupCanvas();
		setupExtraMenus();

		updateToolColor();

		toolmenu.initialize();

		//autosave
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

		Timer.schedule(new Task(){
			@Override
			public void run(){
				checkTutorial();
			}
		}, 0.1f);

		//TextureUnpacker packer = new TextureUnpacker();

		//TextureAtlasData data = new TextureAtlasData(Gdx.files.absolute("/home/cobalt/PixelEditor/android/assets/x2/uiskin.atlas"), Gdx.files.absolute("/home/cobalt/PixelEditor/android/assets/x2/"), false);
		//packer.splitAtlas(data, Gdx.files.absolute("/home/cobalt/Documents/Sprites/uiout").file().getAbsoluteFile().toString());

	}

	@Override
	public void resize(int width, int height){
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause(){
		Gdx.app.log("pedebugging", "Pausing and saving everything.");
		projectmanager.saveProject();
		palettemanager.savePalettes();
		prefs.save();
	}

	@Override
	public void dispose(){
		/*if(Gdx.app.getType() == ApplicationType.Android)*/pause();
		VisUI.dispose();
		Textures.dispose();
	}
}
