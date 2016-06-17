package net.pixelstatic.pixeleditor.modules;

import java.lang.reflect.Field;

import net.pixelstatic.pixeleditor.PixelEditor;
import net.pixelstatic.pixeleditor.graphics.PixelCanvas;
import net.pixelstatic.pixeleditor.scene2D.BrushSizeWidget;
import net.pixelstatic.pixeleditor.scene2D.DrawingGrid;
import net.pixelstatic.pixeleditor.tools.Tool;
import net.pixelstatic.utils.AndroidKeyboard;
import net.pixelstatic.utils.AndroidKeyboard.AndroidKeyboardListener;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.dialogs.AndroidTextFieldDialog;
import net.pixelstatic.utils.dialogs.TextFieldDialog;
import net.pixelstatic.utils.graphics.Hue;
import net.pixelstatic.utils.graphics.Textures;
import net.pixelstatic.utils.modules.Module;
import net.pixelstatic.utils.scene2D.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.FocusManager;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.InputValidator;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.file.FileChooser;

import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;

public class GUI extends Module<PixelEditor>{
	public static GUI gui;
	public static float s = 1f; //density scale
	public DrawingGrid drawgrid;
	public Stage stage;
	int palettewidth = 8;
	Skin skin;
	VisTable tooltable;
	VisTable colortable;
	VisTable extratable;
	BrushSizeWidget brush;
	Table menutable, optionstable, tooloptiontable;
	Array<Tool> tools = new Array<Tool>();
	FileChooser currentChooser;
	public ColorBox colorbox;
	ColorBox[] boxes;
	ColorPicker picker;
	AndroidColorPicker apicker;
	public Tool tool = Tool.pencil;

	@Override
	public void update(){
		Gdx.gl.glClearColor(0.13f, 0.13f, 0.13f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(FocusManager.getFocusedWidget() != null) FocusManager.resetFocus(stage);

		stage.act(Gdx.graphics.getDeltaTime());
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

	void setupMenu(){

		VisTextButton fbutton = addMenuButton("filters...");

		final PopupMenu filterMenu = new PopupMenu();
		filterMenu.addItem(new ExtraMenuItem("invert"));
		filterMenu.addItem(new ExtraMenuItem("colorize"));
		filterMenu.addItem(new ExtraMenuItem("replace"));
		filterMenu.addItem(new ExtraMenuItem("desaturate"));
		filterMenu.addItem(new ExtraMenuItem("burn"));

		fbutton.addListener(new MenuListener(filterMenu, fbutton));

		VisTextButton tbutton = addMenuButton("transform..");

		final PopupMenu transformMenu = new PopupMenu();
		transformMenu.addItem(new ExtraMenuItem("resize", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				showSizeDialog("Resize Canvas", new SizeDialogListener(){
					@Override
					public void result(int width, int height){
						drawgrid.setCanvas(drawgrid.canvas.asResized(width, height));
					}
				});
			}
		}));
		transformMenu.addItem(new ExtraMenuItem("flip", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				
			}
		}));
		transformMenu.addItem(new ExtraMenuItem("rotate", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){

			}
		}));
		transformMenu.addItem(new ExtraMenuItem("scale", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){

			}
		}));
		transformMenu.addItem(new ExtraMenuItem("symmetry", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){

			}
		}));

		tbutton.addListener(new MenuListener(transformMenu, tbutton));

		VisTextButton fibutton = addMenuButton("file..");

		final PopupMenu fileMenu = new PopupMenu();
		fileMenu.addItem(new ExtraMenuItem("new", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				showSizeDialog("New Canvas", new SizeDialogListener(){
					@Override
					public void result(int width, int height){
						PixelCanvas canvas = new PixelCanvas(width, height);
						drawgrid.setCanvas(canvas);
					}
				});
			}
		}));
		fileMenu.addItem(new ExtraMenuItem("save", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new AndroidFileChooser(false).show(stage);
			}
		}));
		fileMenu.addItem(new ExtraMenuItem("load", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				
			}
		}));
		fileMenu.addItem(new ExtraMenuItem("export GIF", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){

			}
		}));
		fileMenu.addItem(new ExtraMenuItem("open as layer", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){

			}
		}));

		fibutton.addListener(new MenuListener(fileMenu, fibutton));

		brush = new BrushSizeWidget();
		final VisLabel brushlabel = new VisLabel("Brush Size: 1");
		final VisSlider slider = new VisSlider(1, 5, 0.01f, false);

		slider.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				brush.setBrushSize((int)slider.getValue());
			}
		});

		brush.addAction(new Action(){
			@Override
			public boolean act(float delta){
				brushlabel.setText("Brush Size: " + brush.getBrushSize());
				brush.setColor(colorbox.getColor());
				return false;
			}
		});

		tooloptiontable.bottom().left().add(brushlabel).align(Align.bottomLeft);
		tooloptiontable.row();
		tooloptiontable.add(slider).align(Align.bottomLeft).spaceBottom(10f).width(brush.getWidth());
		tooloptiontable.row();
		tooloptiontable.add(brush).align(Align.bottomLeft);

		final ColorBar alpha = new ColorBar();

		alpha.addAction(new Action(){
			@Override
			public boolean act(float delta){
				alpha.setRightColor(colorbox.getColor());
				return false;
			}
		});

		alpha.setColors(Color.CLEAR, Color.WHITE);
		alpha.setSize(Gdx.graphics.getWidth() - 20 * s, 40 * s);

		optionstable.bottom().left();

		final VisLabel opacity = new VisLabel("opacity: 1.0");

		opacity.addAction(new Action(){
			@Override
			public boolean act(float delta){
				String string = alpha.getSelection() + "";
				opacity.setText("opacity: " + string.substring(0, Math.min(string.length(), 5)));
				return false;
			}
		});

		optionstable.add(opacity).align(Align.left).padBottom(6f * s);
		optionstable.row();

		optionstable.add(alpha);
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

		public ExtraMenuItem(String text){
			super(text);
		}

		public ExtraMenuItem(String text, ChangeListener changeListener){
			super(text, changeListener);
		}

		public float getPrefWidth(){
			float buttons = 3f;
			return Gdx.graphics.getWidth() / buttons - 2f * buttons * s;
		}

		public float getPrefHeight(){
			return super.getPrefHeight() * 2f - 3f;
		}
	}

	VisTextButton addMenuButton(String text){
		float height = 70f;

		VisTextButton button = new VisTextButton(text);
		menutable.top().left().add(button).height(height).fillX().width(Gdx.graphics.getWidth() / 3f - 6f * s).expandX().pad(2f * s).padTop(6f * s);
		return button;
	}

	void setupTools(){
		final float size = Gdx.graphics.getWidth() / Tool.values().length;

		final VisDialog extradialog = new VisDialog("lel"){
			public float getPrefWidth(){
				return Gdx.graphics.getWidth();
			}

			public float getPrefHeight(){
				return Gdx.graphics.getHeight() / 2f;
			}
		};
		menutable = extradialog.getTitleTable();
		optionstable = extradialog.getContentTable();
		tooloptiontable = new VisTable();
		optionstable.add(tooloptiontable).expand().fill();
		optionstable.row();
		menutable.clear();
		setupMenu();
		extradialog.setMovable(false);

		final SmoothCollapsibleWidget collapser = new SmoothCollapsibleWidget(extradialog, false);

		collapser.setCollapsed(true);

		final VisTextButton expandbutton = new VisTextButton("^");
		expandbutton.setColor(Color.LIGHT_GRAY);
		expandbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				collapser.setCollapsed( !collapser.isCollapsed());
				expandbutton.setText(collapser.isCollapsed() ? "^" : "v");
			}
		});

		tooltable.bottom().left().add(collapser).height(20 * s).colspan(7).fillX().expandX();
		tooltable.row();
		tooltable.bottom().left().add(expandbutton).height(60 * s).colspan(7).fillX().expandX();
		tooltable.row();

		tools.addAll(Tool.values());

		for(int i = 0;i < tools.size;i ++){
			final Tool ctool = tools.get(i);

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

	VisTextButton menuButton(int height){
		VisTextButton ham = new VisTextButton("|||");
		ham.getLabel().setFontScale(0.8f, 1.2f);
		ham.setTransform(true);
		ham.setOrigin(height / 2, height / 2);
		ham.getLabelCell().padBottom(5.5f);
		ham.setRotation(90);
		return ham;
	}

	void setupColors(){

		picker = new ColorPicker(new ColorPickerAdapter(){
			@Override
			public void finished(Color color){
				colorbox.setColor(color);
				tool.onColorChange(color, drawgrid.canvas);
			}
		});

		picker.setShowHexFields(false);

		apicker = new AndroidColorPicker(){
			public void onColorChanged(){
				colorbox.setColor(apicker.getSelectedColor());
			}
		};

		colortable.top().left();

		//int height = 50;

		//	VisTable menu = new VisTable();
		/*

		VisTextButton ham = menuButton(height);
		menu.add(ham).size(height);

		VisTextButton undo = new VisTextButton("undo");
		undo.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				drawgrid.canvas.actions.undo(drawgrid.canvas);
			}
		});

		menu.add(undo).size(height);

		VisTextButton redo = new VisTextButton("redo");
		redo.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				drawgrid.canvas.actions.redo(drawgrid.canvas);
			}
		});
		menu.add(redo).size(height);

		final VisTextButton grid = new VisTextButton("grid", "toggle");
		grid.setChecked(true);
		grid.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				drawgrid.grid = !drawgrid.grid;
				grid.setChecked(drawgrid.grid);
			}
		});
		menu.add(grid).size(height);

		final VisTextButton export = new VisTextButton("export");
		export.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				FileChooser chooser = new FileChooser(Mode.SAVE);
				chooser.setSize(stage.getWidth(), 350);
				if(Gdx.app.getType() == ApplicationType.Android) chooser.setDirectory(Gdx.files.absolute(System.getProperty("user.home") + "/sdcard/"), HistoryPolicy.CLEAR);
				chooser.setMovable(false);
				chooser.setResizable(false);
				chooser.setListener(new FileChooserListener(){
					@Override
					public void selected(Array<FileHandle> files){
						try{
							PixmapIO.writePNG(files.first(), drawgrid.canvas.pixmap);
							Dialogs.showOKDialog(stage, "Info", "Image exported to " + files.first() + ".");
						}catch(Exception e){
							e.printStackTrace();
							Dialogs.showDetailsDialog(stage, "Error writing image!", "Info", e.getClass().getSimpleName() + ": " + e.getMessage());

							//Dialogs.showErrorDialog(stage, "Error writing file:\n" + e.toString());
						}
					}

					@Override
					public void canceled(){
						currentChooser = null;
					}
				});
				
				currentChooser = chooser;
				stage.addActor(chooser.fadeIn());
				chooser.setX(0);
			}
		});
		menu.add(export).size(height);

		final VisTextButton load = new VisTextButton("open");
		load.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				AndroidFileChooser chooser = new AndroidFileChooser();
				chooser.show(stage);
				/*
				FileChooser chooser = new FileChooser(Mode.OPEN);
				chooser.setSize(stage.getWidth(), 350);
				if(Gdx.app.getType() == ApplicationType.Android) chooser.setDirectory(Gdx.files.absolute(System.getProperty("user.home") + "/sdcard/"), HistoryPolicy.CLEAR);
				chooser.setMovable(false);
				chooser.setResizable(false);
				chooser.setListener(new FileChooserListener(){
					@Override
					public void selected(Array<FileHandle> files){
						try{
							drawgrid.setCanvas(new PixelCanvas(new Pixmap(files.first())));

							//Dialogs.showOKDialog(stage, "Info", "Image loaded.");
						}catch(Exception e){
							e.printStackTrace();
							Dialogs.showDetailsDialog(stage, "Error loading image!", "Info", e.getClass().getSimpleName() + ": " + e.getMessage());
						}
					}

					@Override
					public void canceled(){
						currentChooser = null;
					}
				});
				currentChooser = chooser;
				stage.addActor(chooser.fadeIn());
				chooser.setX(0);
				
			}
		});
		menu.add(load).size(height);

		VisTextButton resize = new VisTextButton("resize");
		resize.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				showSizeDialog("Resize Canvas", new SizeDialogListener(){
					@Override
					public void result(int width, int height){
						drawgrid.setCanvas(drawgrid.canvas.asResized(width, height));
					}
				});
			}
		});

		menu.add(resize).size(height);

		VisTextButton newcanvas = new VisTextButton("new");
		newcanvas.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				showSizeDialog("New Canvas", new SizeDialogListener(){
					@Override
					public void result(int width, int height){
						PixelCanvas canvas = new PixelCanvas(width, height);
						drawgrid.setCanvas(canvas);
					}
				});
			}
		});

		menu.add(newcanvas).size(height);

		float size = Gdx.graphics.getWidth() / menu.getCells().size;

		for(Cell<?> cell : menu.getCells()){
			cell.size(size);
		}

		ham.setOrigin(size / 2, size / 2);
		ham.setRotation(90);
		
		*/
		VisDialog filemenu = new VisDialog(""){
			public float getPrefWidth(){
				return Gdx.graphics.getWidth();
			}

			public float getPrefHeight(){
				return 300f;
			}
		};

		filemenu.setMovable(false);

		final VisTextButton expander = new VisTextButton("v");

		colortable.add(expander).expandX().fillX().colspan(10).height(MiscUtils.densityScale(50f));

		colortable.row();

		final SmoothCollapsibleWidget collapser = new SmoothCollapsibleWidget(apicker);

		expander.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				if( !collapser.isCollapsed()){
					apicker.setSelectedColor(apicker.getSelectedColor());
				}
				collapser.setCollapsed( !collapser.isCollapsed());
				expander.setText(collapser.isCollapsed() ? "v" : "^");
			}
		});

		extratable.top().left().add(collapser).fillX().expandX().padTop(50f);

		expander.setZIndex(collapser.getZIndex() + 10);

		int colorsize = Gdx.graphics.getWidth() / 8 - MiscUtils.densityScale(3);

		colortable.add().expandX().fillX();

		boxes = new ColorBox[palettewidth];

		for(int i = 0;i < palettewidth;i ++){

			final ColorBox box = new ColorBox();

			boxes[i] = box;
			colortable.add(box).size(colorsize);

			box.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					colorbox.selected = false;
					colorbox = box;
					box.selected = true;
					box.setZIndex(999);
					apicker.setSelectedColor(box.getColor());

					if(getTapCount() > 1){
						picker.setColor(box.getColor());
						stage.addActor(picker.fadeIn());
					}
					updateToolColor();
				}
			});

		}

		colortable.add().expandX().fillX();

		collapser.resetY();
		collapser.setCollapsed(true, false);
		setupBoxColors();
	}

	void setupBoxColors(){
		for(int i = 0;i < boxes.length;i ++){
			boxes[i].setColor(Hue.fromHSB((float)i / palettewidth, 1f, 1f));
		}

		apicker.setRecentColors(boxes);

		if(colorbox == null){
			colorbox = boxes[0];
			colorbox.selected = true;
			apicker.setSelectedColor(colorbox.getColor());
			colorbox.setZIndex(999);
		}
	}

	void setupCanvas(){
		drawgrid = new DrawingGrid();
		drawgrid.setCanvas(new PixelCanvas(20, 20));
		drawgrid.addAction(new Action(){
			public boolean act(float delta){
				drawgrid.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Align.center);
				drawgrid.setZIndex(0);
				return false;
			}
		});
		stage.addActor(drawgrid);

	}

	public GUI(){
		gui = this;
		s = MiscUtils.densityScale();
		GDXDialogsSystem.install();

		GDXDialogsSystem.getDialogManager().registerDialog(TextFieldDialog.class.getCanonicalName(), AndroidTextFieldDialog.class.getCanonicalName());

		Textures.load("textures/");
		Textures.repeatWrap("alpha", "grid_10", "grid_25");
		stage = new Stage();
		stage.setViewport(new ScreenViewport());
		//VisUI.load(Gdx.files.internal("x1/uiskin.json"));
		loadFonts();
		skin = new Skin(Gdx.files.internal("gui/uiskin.json"));
		FileChooser.setDefaultPrefsName(getClass().getPackage().getName());
		setup();
		setupTools();
		setupColors();
		setupCanvas();

		updateToolColor();

		AndroidKeyboard.setListener(new AndroidKeyboardListener(){
			@Override
			public void onSizeChange(int width, int height){
				float keyboardHeight = stage.getHeight() - height;
				if(currentChooser != null) currentChooser.setY(currentChooser.getY() + keyboardHeight);
			}

			@Override
			public void onKeyboardOpen(){

			}

			@Override
			public void onKeyboardClose(){

			}
		});
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

	void showSizeDialog(String title, final SizeDialogListener listener){
		final VisValidatableTextField widthfield = new VisValidatableTextField(new NumberValidator());
		final VisValidatableTextField heightfield = new VisValidatableTextField(new NumberValidator());
		widthfield.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());
		heightfield.setTextFieldFilter(new VisTextField.TextFieldFilter.DigitsOnlyFilter());

		widthfield.setText(drawgrid.canvas.width() + "");
		heightfield.setText(drawgrid.canvas.height() + "");

		TextFieldDialogListener.add(widthfield, true, 3);
		TextFieldDialogListener.add(heightfield, true, 3);

		final VisDialog dialog = new VisDialog(title, "dialog"){
			protected void result(Object object){
				if((Boolean)object != true) return;

				try{
					int width = Integer.parseInt(widthfield.getText());
					int height = Integer.parseInt(heightfield.getText());

					listener.result(width, height);
				}catch(Exception e){
					e.printStackTrace();
					Dialogs.showDetailsDialog(stage, "An exception has occured.", "Error", e.getClass().getSimpleName() + ": " + (e.getMessage() == null ? "" : e.getMessage()));
				}
			}
		};

		dialog.getButtonsTable().addAction(new Action(){
			@Override
			public boolean act(float delta){
				Cell<?> cell = dialog.getButtonsTable().getCells().peek();
				((Button)cell.getActor()).setDisabled( !widthfield.isInputValid() || !heightfield.isInputValid());
				return false;
			}
		});

		dialog.getContentTable().add(new VisLabel("Width: ")).padLeft(50 * s).padTop(40 * s);
		dialog.getContentTable().add(widthfield).size(140, 40).padRight(50 * s).padTop(40 * s);

		dialog.getContentTable().row();

		dialog.getContentTable().add(new VisLabel("Height: ")).padLeft(50 * s).padTop(40 * s).padBottom(40f * s);
		dialog.getContentTable().add(heightfield).size(140, 40).padRight(50 * s).padTop(40 * s).padBottom(40f * s);

		dialog.getContentTable().row();

		Button cancel = new VisTextButton("Cancel");
		Button ok = new VisTextButton("OK");

		dialog.setObject(ok, true);
		dialog.setObject(cancel, false);

		dialog.getButtonsTable().add(cancel).size(130 * s, 60 * s);
		dialog.getButtonsTable().add(ok).size(130 * s, 60 * s);
		dialog.addCloseButton();

		dialog.show(stage);
	}

	static class NumberValidator implements InputValidator{
		@Override
		public boolean validateInput(String input){
			if(input.equals("")) return false;
			int i = Integer.parseInt(input);
			return i > 0 && i < 513;
		}
	}

	static interface SizeDialogListener{
		public void result(int width, int height);
	}

	public static class FitAction extends Action{
		@Override
		public boolean act(float delta){
			this.getActor().setWidth(Gdx.graphics.getWidth());
			return false;
		}
	}

	public void resize(int width, int height){
		stage.getViewport().update(width, height, true);
	}

	public int getBrushSize(){
		return brush.getBrushSize();
	}

	public Color selectedColor(){
		return colorbox.getColor();
	}

	public void updateToolColor(){
		tool.onColorChange(selectedColor(), drawgrid.canvas);
	}

	public void dispose(){
		VisUI.dispose();
		picker.dispose();
		Textures.dispose();
	}
}
