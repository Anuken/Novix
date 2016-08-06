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
import net.pixelstatic.pixeleditor.scene2D.CollapseButton;
import net.pixelstatic.pixeleditor.scene2D.DrawingGrid;
import net.pixelstatic.pixeleditor.tools.*;
import net.pixelstatic.pixeleditor.ui.*;
import net.pixelstatic.pixeleditor.ui.ProjectMenu.ProjectTable;
import net.pixelstatic.utils.AndroidKeyboard;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.dialogs.AndroidTextFieldDialog;
import net.pixelstatic.utils.dialogs.TextFieldDialog;
import net.pixelstatic.utils.modules.Module;
import net.pixelstatic.utils.scene2D.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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

//TODO CALL BACK <> WHEN <> BACK
public class Core extends Module<PixelEditor>{
	public static Core i;
	public static float s = 1f; //density scale
	public final int largeImageSize = 128 * 128;
	public final Color clearcolor = Color.valueOf("171c23");
	public DrawingGrid drawgrid;
	public Stage stage;
	public FileHandle projectDirectory;
	public final FileHandle paletteDirectory = Gdx.files.local("palettes.json");
	public int paletteColor;
	public ProjectManager projectmanager;
	public PaletteManager palettemanager;
	public PrefsManager prefs;
	public VisTable colortable, pickertable;
	public SettingsMenu settingsmenu;
	public ProjectMenu projectmenu;
	public PaletteMenu palettemenu;
	public ToolMenu toolmenu;
	//public VisImageButton gridbutton;
	CollapseButton colorcollapsebutton, toolcollapsebutton;
	SmoothCollapsibleWidget colorcollapser, toolcollapser;
	ColorBox[] boxes;
	public AndroidColorPicker apicker;
	public Tool tool = Tool.pencil;

	@Override
	public void update(){
		Hue.clearScreen(clearcolor);

		if(FocusManager.getFocusedWidget() != null && ( !(FocusManager.getFocusedWidget() instanceof VisTextField))) FocusManager.resetFocus(stage);

		stage.act(Gdx.graphics.getDeltaTime() > 2 / 60f ? 1 / 60f : Gdx.graphics.getDeltaTime());
		stage.draw();

		tool.update(drawgrid);

		//pc debugging
		if(stage.getKeyboardFocus() instanceof Button || stage.getKeyboardFocus() == null || stage.getKeyboardFocus() instanceof VisDialog) stage.setKeyboardFocus(drawgrid);
	}

	void setupExtraMenus(){

		settingsmenu = new SettingsMenu(this);

		settingsmenu.addPercentScrollSetting("Cursor Size");
		settingsmenu.addCheckSetting("Cursor Mode", true);

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

			button.getImageCell().size(50 * s);

			/*if(ctool == Tool.grid){
				button.setChecked(prefs.getBoolean("grid", true));
				button.addListener(new ClickListener(){
					public void clicked(InputEvent event, float x, float y){
						prefs.put("grid", button.isChecked());
					}
				});
				gridbutton = button;
			}else{
*/
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
			//}

			if(i == 0){
				button.setChecked(true);
				tool = ctool;
			}

			tooltable.bottom().left().add(button).size(size + 1f);
		}
	}

	void setupColorPicker(){
		colortable = new VisTable();
		colortable.setFillParent(true);
		stage.addActor(colortable);

		pickertable = new VisTable(){
			public float getPrefWidth(){
				return Gdx.graphics.getWidth();
			}
		};

		pickertable.background("button-window-bg");

		apicker = new AndroidColorPicker(){
			public void onColorChanged(){
				updateSelectedColor(apicker.getSelectedColor());
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
					apicker.setSelectedColor(apicker.getSelectedColor());
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

		pickertable.add(apicker).expand().fill().padBottom(10f * s).padTop(120f * s).padBottom(20 * s);
		pickertable.row();
		pickertable.center().add(palettebutton).align(Align.center).padBottom(10f * s).height(60 * s).growX();
		System.out.println(colorcollapser.getTop());
		//TODO COLOR TABLE
		colorcollapser.setY(0 + Gdx.graphics.getWidth() / Tool.values().length + toolcollapsebutton.getPrefHeight() + 20 * s);
		colorcollapser.toBack();
		colorcollapser.resetY();
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
					apicker.addColorToPalette(boxes[paletteColor].getColor().cpy());
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

		if(paletteColor > boxes.length) paletteColor = 0;

		apicker.setRecentColors(boxes);
		boxes[paletteColor].selected = true;
		boxes[paletteColor].toFront();
		apicker.setSelectedColor(getCurrentPalette().colors[paletteColor]);
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

	public void setPalette(Palette palette){
		paletteColor = 0;
		palettemanager.setCurrentPalette(palette);
		prefs.put("palettecolor", 0);
		prefs.put("lastpalette", palette.name);
		prefs.save();
		updateColorMenu();
		setSelectedColor(palette.colors[0]);
		setupBoxColors();
		//palettemenu.update();
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
		return getCurrentPalette().colors[paletteColor];
	}

	public void updateSelectedColor(Color color){
		boxes[paletteColor].setColor(color);
		getCurrentPalette().colors[paletteColor] = color.cpy();
		toolmenu.updateColor(color.cpy());
		updateToolColor();
	}

	public void setSelectedColor(Color color){
		updateSelectedColor(color);
		apicker.setSelectedColor(color);
		updateToolColor();
	}

	public void updateToolColor(){
		if(tool != null && drawgrid != null) tool.onColorChange(selectedColor().cpy(), drawgrid.canvas);
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

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/smooth.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = (int)(22 * MiscUtils.densityScale());
		parameter.shadowColor = new Color(0, 0, 0, 0.4f);
		//parameter.shadowOffsetY = 2;
		//parameter.shadowOffsetX = 2;
		BitmapFont font = generator.generateFont(parameter);

		skin.add("default-font", font);

		skin.load(skinFile);

		VisUI.load(skin);

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
		
		
		ShapeUtils.region = VisUI.getSkin().getRegion("white");//new TextureRegion(Textures.get("stripe"));
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
		if(getCurrentProject() != null) prefs.put("lastproject", getCurrentProject().name);
		prefs.save();
	}

	@Override
	public void dispose(){
		/*if(Gdx.app.getType() == ApplicationType.Android)*/pause();
		VisUI.dispose();
		Textures.dispose();
	}
}
