package net.pixelstatic.pixeleditor.modules;

import java.lang.reflect.Field;
import java.util.HashMap;

import net.pixelstatic.gdxutils.graphics.Textures;
import net.pixelstatic.pixeleditor.PixelEditor;
import net.pixelstatic.pixeleditor.graphics.Palette;
import net.pixelstatic.pixeleditor.graphics.PixelCanvas;
import net.pixelstatic.pixeleditor.graphics.Project;
import net.pixelstatic.pixeleditor.managers.ProjectManager;
import net.pixelstatic.pixeleditor.scene2D.*;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ClearDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ColorAlphaDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ColorizeDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ContrastDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.CropDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ExportScaledDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.FlipDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.InvertDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ReplaceDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.RotateDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ScaleDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ShiftDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.SizeDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.SymmetryDialog;
import net.pixelstatic.pixeleditor.tools.Tool;
import net.pixelstatic.pixeleditor.ui.*;
import net.pixelstatic.pixeleditor.ui.ProjectMenu.ProjectTable;
import net.pixelstatic.utils.AndroidKeyboard;
import net.pixelstatic.utils.AndroidKeyboard.AndroidKeyboardListener;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.dialogs.AndroidDialogs;
import net.pixelstatic.utils.dialogs.AndroidTextFieldDialog;
import net.pixelstatic.utils.dialogs.TextFieldDialog;
import net.pixelstatic.utils.modules.Module;
import net.pixelstatic.utils.scene2D.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.Focusable;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;
import com.kotcrab.vis.ui.widget.file.FileChooser;

import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;

public class Main extends Module<PixelEditor>{
	public static Main i;
	public static float s = 1f; //density scale
	public DrawingGrid drawgrid;
	public Stage stage;
	public FileHandle projectDirectory;
	public Palette currentPalette;
	public int paletteColor;
	public Preferences prefs;
	public ProjectManager projectmanager;
	VisTable tooltable;
	VisTable colortable;
	VisTable extratable;
	VisTable projecttable;
	VisTable pickertable;
	SettingsMenu settingsmenu;
	public ProjectMenu projectmenu;
	PaletteMenu palettemenu;
	Cell<AndroidColorPicker> pickercell;
	CollapseButton colorcollapsebutton, toolcollapsebutton;
	SmoothCollapsibleWidget colorcollapser, toolcollapser;
	BrushSizeWidget brush;
	ColorBar alphabar;
	Table menutable, optionstable, tooloptiontable, extratooltable;
	FileChooser currentChooser;
	public ObjectMap<String, Palette> palettes = new ObjectMap<String, Palette>();
	final FileHandle paletteDirectory = Gdx.files.local("palettes.json");
	Json json;
	ColorBox[] boxes;
	public AndroidColorPicker apicker;
	public Tool tool = Tool.pencil;

	@Override
	public void update(){
		Gdx.gl.glClearColor(0.13f, 0.13f, 0.13f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(FocusManager.getFocusedWidget() != null && ( !(FocusManager.getFocusedWidget() instanceof VisTextField))) FocusManager.resetFocus(stage);

		stage.act(Gdx.graphics.getDeltaTime() > 2 / 60f ? 1 / 60f : Gdx.graphics.getDeltaTime());
		stage.draw();

		tool.update(drawgrid);

		//pc debugging
		if(stage.getKeyboardFocus() instanceof Button || stage.getKeyboardFocus() == null || stage.getKeyboardFocus() instanceof VisDialog) stage.setKeyboardFocus(drawgrid);

	}

	void setup(){
		extratable = new VisTable();
		extratable.setFillParent(true);
		stage.addActor(extratable);

		colortable = new VisTable();
		colortable.setFillParent(true);
		stage.addActor(colortable);

		tooltable = new VisTable();
		tooltable.setFillParent(true);
		stage.addActor(tooltable);

	}

	void setupExtraMenus(){

		settingsmenu = new SettingsMenu(this);
		
		
		settingsmenu.addScrollSetting("Cursor Size", 1, 10, 5);

		settingsmenu.addCheckSetting("Autosave", true);
		
		projectmenu = new ProjectMenu(this);

		projectmenu.update(true);
	}

	
	public void openSettingsMenu(){
		settingsmenu.show(stage);
	}

	void setupMenu(){
		VisTextButton menu = addMenuButton("menu");
		menu.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				showProjectMenu();
			}
		});

		
		VisTextButton ibutton = addMenuButton("image...");

		final PopupMenu imageMenu = new PopupMenu();

		imageMenu.addItem(new ExtraMenuItem(ibutton, "resize", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new SizeDialog("Resize Canvas"){
					@Override
					public void result(int width, int height){
						drawgrid.setCanvas(drawgrid.canvas.asResized(width, height));
						updateToolColor();
					}
				}.show(stage);
			}
		}));

		imageMenu.addItem(new ExtraMenuItem(ibutton, "crop", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new CropDialog().show(stage);
			}
		}));

		imageMenu.addItem(new ExtraMenuItem(ibutton, "clear", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new ClearDialog().show(stage);
			}
		}));
		imageMenu.addItem(new ExtraMenuItem(ibutton, "symmetry", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new SymmetryDialog().show(stage);
			}
		}));

		ibutton.addListener(new MenuListener(imageMenu, ibutton));

		VisTextButton fbutton = addMenuButton("filters...");

		final PopupMenu filterMenu = new PopupMenu();
		filterMenu.addItem(new ExtraMenuItem(fbutton, "colorize", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new ColorizeDialog().show(stage);
			}
		}));
		filterMenu.addItem(new ExtraMenuItem(fbutton, "invert", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new InvertDialog().show(stage);
			}
		}));
		filterMenu.addItem(new ExtraMenuItem(fbutton, "replace", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new ReplaceDialog().show(stage);
			}
		}));
		filterMenu.addItem(new ExtraMenuItem(fbutton, "contrast", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new ContrastDialog().show(stage);
			}
		}));
		filterMenu.addItem(new ExtraMenuItem(fbutton, "color to alpha", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new ColorAlphaDialog().show(stage);
			}
		}));

		fbutton.addListener(new MenuListener(filterMenu, fbutton));

		VisTextButton tbutton = addMenuButton("edit..");

		final PopupMenu transformMenu = new PopupMenu();
		transformMenu.addItem(new ExtraMenuItem(tbutton, "flip", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new FlipDialog().show(stage);
			}
		}));
		transformMenu.addItem(new ExtraMenuItem(tbutton, "rotate", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new RotateDialog().show(stage);
			}
		}));
		transformMenu.addItem(new ExtraMenuItem(tbutton, "scale", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new ScaleDialog().show(stage);
			}
		}));
		transformMenu.addItem(new ExtraMenuItem(tbutton, "shift", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new ShiftDialog().show(stage);
			}
		}));

		tbutton.addListener(new MenuListener(transformMenu, tbutton));

		VisTextButton fibutton = addMenuButton("file..");

		final PopupMenu fileMenu = new PopupMenu();
		/*
		fileMenu.addItem(new ExtraMenuItem(fibutton, "save", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				saveProject();
			}
		}));
		*/
		fileMenu.addItem(new ExtraMenuItem(fibutton, "export", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new AndroidFileChooser(AndroidFileChooser.imageFilter, false){
					public void fileSelected(FileHandle file){
						exportPixmap(drawgrid.canvas.pixmap, file);
					}
				}.show(stage);
			}
		}));
		fileMenu.addItem(new ExtraMenuItem(fibutton, "export x", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new ExportScaledDialog().show(stage);
			}
		}));
		fileMenu.addItem(new ExtraMenuItem(fibutton, "open", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new AndroidFileChooser(AndroidFileChooser.imageFilter, true){
					public void fileSelected(FileHandle file){
						try{
							drawgrid.setCanvas(new PixelCanvas(new Pixmap(file)));
							tool.onColorChange(selectedColor(), drawgrid.canvas);
						}catch(Exception e){
							e.printStackTrace();
							AndroidDialogs.showError(stage, e);
						}
					}
				}.show(stage);
			}
		}));
		
		fibutton.addListener(new MenuListener(fileMenu, fibutton));
		
		final VisLabel infolabel = new VisLabel();
		
		brush = new BrushSizeWidget();
	
		final VisSlider brushslider = new VisSlider(1, 10, 0.01f, true);
		brushslider.setValue(prefs.getInteger("brushsize", 1));
		final VisLabel brushlabel = new VisLabel("Brush Size: " + brushslider.getValue());

		brushslider.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				brush.setBrushSize((int)brushslider.getValue());
				brushlabel.setText("Brush Size: " + brush.getBrushSize());
				infolabel.setText("Brush Size: " + brush.getBrushSize() + "\nOpacity: " + (int)(alphabar.getSelection()*100));
				prefs.putInteger("brushsize", (int)brushslider.getValue());
			}
		});

/*
		tooloptiontable.bottom().left().add(brushlabel).align(Align.bottomLeft);
		tooloptiontable.row();
		tooloptiontable.add(brushslider).align(Align.bottomLeft).spaceBottom(10f).width(brush.getWidth());
		tooloptiontable.row();
		tooloptiontable.add(brush).align(Align.bottomLeft);
*/
		alphabar = new ColorBar(true);
		
		brushslider.fire(new ChangeListener.ChangeEvent());

		alphabar.setColors(Color.CLEAR.cpy(), Color.WHITE);
		alphabar.setSize(50 * s, 300 * s);
		alphabar.setSelection(prefs.getFloat("opacity", 1f));

		optionstable.bottom().left();

		final VisLabel opacity = new VisLabel("Opacity: " + (int)(alphabar.getSelection()*100) + "%");

		alphabar.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				opacity.setText("Opacity: " + (int)(alphabar.getSelection()*100) + "%");
				infolabel.setText("Brush Size: " + brush.getBrushSize() + "\nOpacity: " + (int)(alphabar.getSelection()*100) + "%");
				drawgrid.canvas.setAlpha(alphabar.getSelection());
				prefs.putFloat("opacity", alphabar.getSelection());
			}
		});

		final VisCheckBox grid = new VisCheckBox("Grid");

		grid.getImageStackCell().size(40 * s);

		grid.setChecked(prefs.getBoolean("grid", true));

		grid.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				drawgrid.grid = grid.isChecked();
				prefs.putBoolean("grid", drawgrid.cursormode);
				
			}
		});

		VisTextButton menubutton = new VisTextButton("Menu");
		VisTextButton settingsbutton = new VisTextButton("Settings");

		menubutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				showProjectMenu();
			}
		});

		settingsbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				settingsmenu.show(stage);
			}
		});

		final VisRadioButton cbox = new VisRadioButton("cursor mode");
		final VisRadioButton tbox = new VisRadioButton("tap mode");

		if(prefs.getBoolean("cursormode", true)){
			cbox.setChecked(true);
		}else{
			tbox.setChecked(true);
		}

		cbox.getImageStackCell().size(40 * s);
		tbox.getImageStackCell().size(40 * s);

		new ButtonGroup<VisRadioButton>(cbox, tbox);
		cbox.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				drawgrid.cursormode = cbox.isChecked();
				prefs.putBoolean("cursormode", drawgrid.cursormode);
			}
		});
		
		VisImageButtonStyle style = VisUI.getSkin().get("toggle", VisImageButtonStyle.class);
		
		VisImageButtonStyle modestyle = new VisImageButtonStyle(style);
		VisImageButtonStyle gridstyle = new VisImageButtonStyle(style);
		
		modestyle.imageUp = Textures.getDrawable("icon-cursor");
		gridstyle.imageUp = Textures.getDrawable("icon-grid");
		
		final VisImageButton modebutton = new VisImageButton(modestyle);
		modebutton.setChecked(prefs.getBoolean("cursormode", true));
		
		modebutton.getImageCell().size(50*s);
		
		modebutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				prefs.putBoolean("cursormode", modebutton.isChecked());
				drawgrid.cursormode = modebutton.isChecked();
			}
		});
		
		final VisImageButton gridbutton = new VisImageButton(gridstyle);
		gridbutton.setChecked(prefs.getBoolean("grid", true));
		
		gridbutton.getImageCell().size(50*s);
		
		gridbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				prefs.putBoolean("grid", gridbutton.isChecked());
				drawgrid.grid = gridbutton.isChecked();
				
			}
		});
		
		Table menutable = new VisTable();
		Table othertable = new VisTable();

		optionstable.top().left();
		optionstable.add(menutable).growY();
		optionstable.add(othertable).grow();
		
	//	optionstable.setDebug(true, true);
		
		//TODO
		
		menutable.top().left();
		
		//menutable.add(menubutton).size(80*s).align(Align.topLeft).padTop(8*s).row();
		menutable.add(modebutton).size(80*s).align(Align.topLeft).padTop(8*s).row();
		menutable.add(gridbutton).size(80*s).align(Align.topLeft);
		
		othertable.bottom().right();
		
		infolabel.setAlignment(Align.topLeft, Align.left);
		//othertable.add(infolabel).padTop(20).padRight(10).padLeft(40).grow().align(Align.top);
		
		othertable.add(brushlabel).padRight(10).minWidth(150).align(Align.center);
		othertable.add(opacity).minWidth(150).align(Align.center);
		othertable.row();
		othertable.add(brushslider).growY().padTop(20).padBottom(20).padRight(15);
		othertable.add(alphabar).padTop(20).padBottom(20);
		//optionstable.add(brush);
		//optionstable.add(brushslider);
		//optionstable.add(alphabar);
		
/*
		extratooltable.top().left().add(cbox).padTop(50f * s).padLeft(00f * s);
		extratooltable.add(menubutton).padTop(50f * s).size(120, 50).padLeft(5f);
		extratooltable.row();
		extratooltable.add(tbox).align(Align.left).padTop(5f * s).padLeft(00f * s);
		extratooltable.add(settingsbutton).size(120, 50).padLeft(5f);
		extratooltable.row();
		extratooltable.add(grid).align(Align.left).padTop(55f * s).padLeft(20f * s);

		optionstable.add(opacity).align(Align.center).padBottom(6f * s).colspan(2);
		optionstable.row();

		optionstable.add(alphabar).colspan(2).pad(3 * s).padBottom(15f * s);
*/
	}

	public void exportPixmap(Pixmap pixmap, FileHandle file){
		try{
			if( !file.extension().equalsIgnoreCase("png")) file = file.parent().child(file.nameWithoutExtension() + ".png");
			PixmapIO.writePNG(file, drawgrid.canvas.pixmap);
			AndroidDialogs.showInfo(stage, "Image exported to " + file + ".");
		}catch(Exception e){
			e.printStackTrace();
			AndroidDialogs.showError(stage, e);
		}
	}

	private class MenuListener extends ClickListener{
		private final PopupMenu menu;
		private final Button button;

		public MenuListener(PopupMenu menu, Button button){
			this.menu = menu;
			this.button = button;
		}

		public void clicked(InputEvent event, float x, float y){
			Vector2 pos = button.localToStageCoordinates(new Vector2(0, 0));
			menu.showMenu(stage, pos.x, pos.y);
			menu.setPosition(pos.x, pos.y - menu.getPrefHeight());
		}
	}

	private static class ExtraMenuItem extends MenuItem{

		public ExtraMenuItem(Button button, String text){
			super(text);
			validate();
		}

		public ExtraMenuItem(Button button, String text, ChangeListener changeListener){
			super(text, changeListener);
			invalidate();
		}

		public float getPrefWidth(){
			return Gdx.graphics.getWidth() / 4f - 4;
		}

		public float getPrefHeight(){
			return super.getPrefHeight() * 2f - 3f;
		}
	}

	VisTextButton addMenuButton(String text){
		float height = 70f;

		VisTextButton button = new VisTextButton(text);
		menutable.top().left().add(button).width(Gdx.graphics.getWidth() / 5 - 3).height(height).expandX().fillX().padTop(5f * s).align(Align.topLeft);
		return button;
	}

	void setupTools(){
		final float size = Gdx.graphics.getWidth() / Tool.values().length;

		final VisTable extratable = new VisTable(){
			public float getPrefWidth(){
				return Gdx.graphics.getWidth();
			}

		};

		extratable.setBackground("button-window-bg");

		menutable = new VisTable();
		optionstable = new VisTable();
		tooloptiontable = new VisTable();
		extratooltable = new VisTable();
		extratable.top().left().add(menutable).align(Align.topLeft).expand().fill().row();
		extratable.top().left().add(optionstable).expand().fill().row();
		//optionstable.add(tooloptiontable).minWidth(150 * s);
		//optionstable.add(extratooltable).expand().fill();
		optionstable.row();

		setupMenu();

		toolcollapser = new SmoothCollapsibleWidget(extratable, false);

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

		tooltable.bottom().left().add(toolcollapser).height(20 * s).colspan(7).fillX().expandX();
		tooltable.row();
		tooltable.bottom().left().add(toolcollapsebutton).height(60 * s).colspan(7).fillX().expandX();
		tooltable.row();
		
		Tool[] tools = Tool.values();

		for(int i = 0;i < tools.length; i ++){
			final Tool ctool = tools[i];

			final VisImageButton button = new VisImageButton((Drawable)null);
			button.setStyle(new VisImageButtonStyle(VisUI.getSkin().get("toggle", VisImageButtonStyle.class)));
			button.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(Textures.get("icon-" + ctool.name()))); //whatever icon is needed

			button.getImageCell().size(50);

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
	}

	void setupColorPicker(){

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
		
		pickercell = pickertable.add(apicker).expand().fill().padBottom(10f * s).padTop(200f);
		pickertable.row();
		pickertable.center().add(palettebutton).align(Align.center).padBottom(10f * s).height(60 * s).growX();
		
		//TODO COLOR TABLE
		colorcollapser.setY(Gdx.graphics.getHeight() - pickertable.getPrefHeight());
		colorcollapser.toBack();
		colorcollapser.resetY();
		colorcollapser.setCollapsed(true, false);
		setupBoxColors();
	}
	
	void openPaletteMenu(){
		palettemenu.update();
		palettemenu.show(stage);
	}

	public void updateColorMenu(){
		colortable.clear();

		int maxcolorsize = 65;
		int mincolorsize = 30;

		int colorsize = Gdx.graphics.getWidth() / currentPalette.size() - MiscUtils.densityScale(3);
		
		int perow = 0; //colors per row

		colorsize = Math.min(maxcolorsize, colorsize);
		
		if(colorsize < mincolorsize){
			colorsize = mincolorsize;
			perow = Gdx.graphics.getWidth() / colorsize;
		}
		
		colortable.add(colorcollapsebutton).expandX().fillX().colspan((perow == 0 ? currentPalette.size() : perow) + 2).height(50f * s);
		colorcollapsebutton.setZIndex(colorcollapser.getZIndex() + 10);

		colortable.row();

		colortable.add().growX();

		boxes = new ColorBox[currentPalette.size()];

		for(int i = 0;i < currentPalette.size();i ++){
			final int index = i;
			final ColorBox box = new ColorBox();

			boxes[i] = box;
			colortable.add(box).size(colorsize);
			
			box.setColor(currentPalette.colors[i]);

			box.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					boxes[paletteColor].selected = false;
					paletteColor = index;
					prefs.putInteger("palettecolor", paletteColor);
					box.selected = true;
					box.toFront();
					updateSelectedColor(box.getColor()); 
				}
			});
			
			if(perow != 0 && i % perow == perow -1){
				colortable.add().growX();
				colortable.row();
				colortable.add().growX();
			}
		}

		if(perow == 0)colortable.add().growX();
		
		//if(pickercell != null)pickercell.padTop(1000);
		
		//pickertable.pack();
	}

	public void setPalette(Palette palette){
		paletteColor = 0;
		currentPalette = palette;
		prefs.putString("lastpalette", palette.name);
		prefs.flush();
		updateColorMenu();
		setSelectedColor(palette.colors[0]);
		setupBoxColors();
		palettemenu.update();
	}

	void setupBoxColors(){
		paletteColor = prefs.getInteger("palettecolor", 0);
		
		apicker.setRecentColors(boxes);
		boxes[paletteColor].selected = true;
		boxes[paletteColor].toFront();
		apicker.setSelectedColor(currentPalette.colors[paletteColor]);
	}

	void setupCanvas(){
		drawgrid = new DrawingGrid();

		drawgrid.grid = prefs.getBoolean("grid", true);
		drawgrid.cursormode = prefs.getBoolean("cursormode", true);
		drawgrid.setCanvas(new PixelCanvas(getCurrentProject().getCachedPixmap()));

		stage.addActor(drawgrid);
	}

	public Main(){
		Gdx.graphics.setContinuousRendering(false);

		i = this;
		s = MiscUtils.densityScale();
		GDXDialogsSystem.install();

		GDXDialogsSystem.getDialogManager().registerDialog(TextFieldDialog.class.getCanonicalName(), AndroidTextFieldDialog.class.getCanonicalName());

		projectDirectory = Gdx.files.absolute(Gdx.files.getExternalStoragePath()).child("pixelprojects");
		projectDirectory.mkdirs();
		json = new Json();
		prefs = Gdx.app.getPreferences("pixeleditor");
		loadPalettes();
		Textures.load("textures/");
		Textures.repeatWrap("alpha", "grid_10", "grid_25");
		stage = new Stage();
		stage.setViewport(new ScreenViewport());
		projectmanager = new ProjectManager(this);
		loadFonts();

		AndroidKeyboard.setListener(new AndroidKeyboardListener(){
			HashMap<Actor, Float> moved = new HashMap<Actor, Float>();

			void moveActor(final int height, boolean extra){
				Focusable focus = FocusManager.getFocusedWidget();

				if(focus == null){
					if(extra){
						Gdx.app.postRunnable(new Runnable(){
							public void run(){
								moveActor(height, false);
							}
						});
					}
					return;
				}

				Actor actor = (Actor)focus;

				if( !(actor instanceof VisTextField)) return;

				VisTextField field = (VisTextField)actor;

				for(EventListener listener : field.getListeners()){
					if(listener instanceof TextFieldDialogListener) return;
				}

				Actor parent = MiscUtils.getTopParent(field);

				float keypadding = 30;

				//float parenty = parent.getY();
				float actory = field.localToStageCoordinates(new Vector2(0, 0)).y;
				float keyheight = AndroidKeyboard.getCurrentKeyboardHeight() + keypadding;

				if(height > 0){
					moveActorDown(parent);
				}

				if(actory < keyheight){
					float diff = keyheight - actory;
					moveActorUp(parent, diff);
				}
			}

			@Override
			public void onSizeChange(int height){
				moveActor(height, true);
			}

			void moveActorUp(Actor actor, float move){
				actor.addAction(Actions.moveBy(0, move, 0.1f));
				if(moved.containsKey(actor)){
					moved.put(actor, moved.get(actor) + move);
				}else{
					moved.put(actor, move);
				}
			}

			void moveActorDown(Actor actor){
				if(moved.containsKey(actor)){
					float move = moved.get(actor);
					actor.addAction(Actions.moveBy(0, -move, 0.1f));
					moved.remove(actor);
				}
			}
		});

		projectmanager.loadProjects();

		setup();
		setupTools();
		setupColorPicker();
		setupCanvas();
		setupExtraMenus();

		updateToolColor();
		
		alphabar.fire(new ChangeListener.ChangeEvent()); //update alpha

		//autosave
		Timer.schedule(new Task(){
			@Override
			public void run(){
				new Thread(new Runnable(){
					public void run(){
						projectmanager.saveProject();
						savePalettes();
						prefs.flush();
					}
				}).start();
			}
		}, 20, 20);
		
		//TextureUnpacker packer = new TextureUnpacker();
		
		//TextureAtlasData data = new TextureAtlasData(Gdx.files.absolute("/home/cobalt/PixelEditor/android/assets/x2/uiskin.atlas"), Gdx.files.absolute("/home/cobalt/PixelEditor/android/assets/x2/"), false);
		//packer.splitAtlas(data, Gdx.files.absolute("/home/cobalt/Documents/Sprites/uiout").file().getAbsoluteFile().toString());
		
		//for unpacking the atlas
		//AtlasUnpacker.unpack(VisUI.getSkin().getAtlas(), MiscUtils.getHomeDirectory().child("unpacked"));
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
		//if(Gdx.app.getType() == ApplicationType.Desktop) parameter.size = 22;
		BitmapFont font = generator.generateFont(parameter);

		skin.add("default-font", font);

		skin.load(skinFile);

		VisUI.load(skin);

		generator.dispose();
	}

	static class CollapseButton extends VisImageButton{
		Drawable up = new TextureRegionDrawable(new TextureRegion(Textures.get("icon-up")));
		Drawable down = new TextureRegionDrawable(new TextureRegion(Textures.get("icon-down")));

		public CollapseButton(){
			super("default");
			setStyle(new VisImageButtonStyle(getStyle()));
			this.getImageCell().size(getHeight());
			getStyle().up = VisUI.getSkin().getDrawable("button");
			set(up);
		}

		public void flip(){
			if(getStyle().imageUp == up){
				set(down);
			}else{
				set(up);
			}
		}

		private void set(Drawable d){
			getStyle().imageUp = d;
		}
	}

	void showProjectMenu(){
		final ProjectTable table = 	projectmenu.update(false);
		projectmenu.show(stage);

		new Thread(new Runnable(){
			public void run(){
				projectmanager.saveProject();
				table.loaded = true;
			}
		}).start();
	}

	void savePalettes(){
		String string = json.toJson(palettes);
		paletteDirectory.writeString(string, false);
	}

	@SuppressWarnings("unchecked")
	void loadPalettes(){
		try{
			palettes = json.fromJson(ObjectMap.class, paletteDirectory);

			String name = prefs.getString("lastpalette");
			if(name != null){
				currentPalette = palettes.get(name);
			}

			Gdx.app.log("pedebugging", "Palettes loaded.");
		}catch(Exception e){
			e.printStackTrace();
			Gdx.app.error("pedebugging", "Palette file nonexistant or corrupt.");
		}

		if(currentPalette == null){
			if( !palettes.containsKey("Untitled")){
				currentPalette = new Palette("Untitled", 8);
				palettes.put("Untitled", currentPalette);
			}else{
				currentPalette = palettes.get("Untitled");
			}
		}
	}

	public void resize(int width, int height){
		stage.getViewport().update(width, height, true);
	}

	public int getBrushSize(){
		return brush.getBrushSize();
	}

	public Color selectedColor(){
		return currentPalette.colors[paletteColor];
	}

	public void updateSelectedColor(Color color){
		boxes[paletteColor].setColor(color);
		currentPalette.colors[paletteColor] = color.cpy();
		alphabar.setRightColor(color.cpy());
		brush.setColor(color);
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

	@Override
	public void pause(){
		Gdx.app.log("pedebugging", "Pausing and saving everything.");
		projectmanager.saveProject();
		savePalettes();
		if(getCurrentProject() != null) prefs.putString("lastproject", getCurrentProject().name);
		prefs.flush();
	}

	@Override
	public void dispose(){
		/*if(Gdx.app.getType() == ApplicationType.Android)*/ pause();
		VisUI.dispose();
		Textures.dispose();
	}
}
