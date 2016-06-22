package net.pixelstatic.pixeleditor.modules;

import java.lang.reflect.Field;

import net.pixelstatic.pixeleditor.PixelEditor;
import net.pixelstatic.pixeleditor.graphics.PixelCanvas;
import net.pixelstatic.pixeleditor.scene2D.*;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ColorizeDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.DesaturateDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.FlipDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.InvertDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ReplaceDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.RotateDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.SizeDialog;
import net.pixelstatic.pixeleditor.tools.Tool;
import net.pixelstatic.utils.AndroidKeyboard;
import net.pixelstatic.utils.AndroidKeyboard.AndroidKeyboardListener;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.dialogs.AndroidDialogs;
import net.pixelstatic.utils.dialogs.AndroidTextFieldDialog;
import net.pixelstatic.utils.dialogs.TextFieldDialog;
import net.pixelstatic.utils.graphics.Hue;
import net.pixelstatic.utils.graphics.Textures;
import net.pixelstatic.utils.modules.Module;
import net.pixelstatic.utils.scene2D.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
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
	Table menutable, optionstable, tooloptiontable, extratooltable;
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
		filterMenu.addItem(new ExtraMenuItem("colorize", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new ColorizeDialog().show(stage);
			}
		}));
		filterMenu.addItem(new ExtraMenuItem("invert", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new InvertDialog().show(stage);
			}
		}));
		filterMenu.addItem(new ExtraMenuItem("replace", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new ReplaceDialog().show(stage);
			}
		}));
		filterMenu.addItem(new ExtraMenuItem("desaturate", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new DesaturateDialog().show(stage);
			}
		}));
		filterMenu.addItem(new ExtraMenuItem("burn", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				//new ColorizeDialog().show(stage);
			}
		}));

		fbutton.addListener(new MenuListener(filterMenu, fbutton));

		VisTextButton tbutton = addMenuButton("transform..");

		final PopupMenu transformMenu = new PopupMenu();
		transformMenu.addItem(new ExtraMenuItem("resize", new ChangeListener(){
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
		transformMenu.addItem(new ExtraMenuItem("flip", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new FlipDialog().show(stage);
			}
		}));
		transformMenu.addItem(new ExtraMenuItem("rotate", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new RotateDialog().show(stage);
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
				new SizeDialog("New Canvas"){
					@Override
					public void result(int width, int height){
						PixelCanvas canvas = new PixelCanvas(width, height);
						drawgrid.setCanvas(canvas);
						updateToolColor();
					}
				}.show(stage);
			}
		}));
		fileMenu.addItem(new ExtraMenuItem("save", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new AndroidFileChooser(AndroidFileChooser.imageFilter, false){
					public void fileSelected(FileHandle file){
						try{
							if( !file.extension().equalsIgnoreCase("png")) file = file.parent().child(file.nameWithoutExtension() + ".png");
							PixmapIO.writePNG(file, drawgrid.canvas.pixmap);
							AndroidDialogs.showInfo(stage, "Image exported to " + file + ".");
						}catch(Exception e){
							e.printStackTrace();
							AndroidDialogs.showError(stage, e);
						}
					}
				}.show(stage);
			}
		}));
		fileMenu.addItem(new ExtraMenuItem("open", new ChangeListener(){
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

		alpha.addListener(new InputListener(){
			public void touchUp(InputEvent event, float x, float y, int pointer, int button){

			}
		});

		alpha.setColors(Color.CLEAR, Color.WHITE);
		alpha.setSize(Gdx.graphics.getWidth() - 20 * s, 40 * s);

		optionstable.bottom().left();

		final VisLabel opacity = new VisLabel("opacity: 1.0");

		alpha.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				opacity.setText("opacity: " + MiscUtils.limit(alpha.getSelection() + "", 5));
				drawgrid.canvas.setAlpha(alpha.getSelection());
			}
		});

		final VisCheckBox grid = new VisCheckBox("Grid");

		grid.getImageStackCell().size(40 * s);

		grid.setChecked(true);

		grid.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				drawgrid.grid = grid.isChecked();
			}
		});

		final VisRadioButton cbox = new VisRadioButton("cursor mode");
		final VisRadioButton tbox = new VisRadioButton("tap mode");

		cbox.setChecked(true);

		cbox.getImageStackCell().size(40 * s);
		tbox.getImageStackCell().size(40 * s);

		new ButtonGroup<VisRadioButton>(cbox, tbox);
		cbox.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				drawgrid.cursormode = cbox.isChecked();
			}
		});

		extratooltable.top().left().add(cbox).padTop(50f * s).padLeft(20f * s);
		//extratooltable.add(grid).padTop(50f).padLeft(60f);
		extratooltable.row();
		extratooltable.add(tbox).align(Align.left).padTop(5f * s).padLeft(20f * s);
		extratooltable.row();
		extratooltable.add(grid).align(Align.left).padTop(55f * s).padLeft(20f * s);

		optionstable.add(opacity).align(Align.left).padBottom(6f * s).colspan(2);
		optionstable.row();

		optionstable.add(alpha).colspan(2);

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
		extratooltable = new VisTable();
		optionstable.add(tooloptiontable).minWidth(150 * s);
		optionstable.add(extratooltable).expand().fill();
		optionstable.row();
		menutable.clear();
		setupMenu();
		extradialog.setMovable(false);

		final SmoothCollapsibleWidget collapser = new SmoothCollapsibleWidget(extradialog, false);

		collapser.setCollapsed(true);

		final CollapseButton expandbutton = new CollapseButton();
		expandbutton.setColor(Color.LIGHT_GRAY);
		expandbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				collapser.setCollapsed( !collapser.isCollapsed());
				expandbutton.flip();
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

		VisDialog filemenu = new VisDialog(""){
			public float getPrefWidth(){
				return Gdx.graphics.getWidth();
			}

			public float getPrefHeight(){
				return 300f;
			}
		};

		filemenu.setMovable(false);

		final CollapseButton expander = new CollapseButton();
		expander.flip();

		colortable.add(expander).expandX().fillX().colspan(10).height(MiscUtils.densityScale(50f));

		colortable.row();

		final SmoothCollapsibleWidget collapser = new SmoothCollapsibleWidget(apicker);

		expander.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				if( !collapser.isCollapsed()){
					apicker.setSelectedColor(apicker.getSelectedColor());
					tool.onColorChange(selectedColor(), drawgrid.canvas);
				}
				collapser.setCollapsed( !collapser.isCollapsed());
				expander.flip();
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

	static class CollapseButton extends VisImageButton{
		Drawable up = new TextureRegionDrawable(new TextureRegion(Textures.get("icon-up")));
		Drawable down = new TextureRegionDrawable(new TextureRegion(Textures.get("icon-down")));

		public CollapseButton(){
			super("default");
			setStyle(new VisImageButtonStyle(getStyle()));
			this.getImageCell().size(getHeight());
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

	public void resize(int width, int height){
		stage.getViewport().update(width, height, true);
	}

	public int getBrushSize(){
		return brush.getBrushSize();
	}

	public Color selectedColor(){
		return colorbox.getColor().cpy();
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
