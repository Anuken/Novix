package net.pixelstatic.pixeleditor.modules;

import net.pixelstatic.pixeleditor.PixelEditor;
import net.pixelstatic.pixeleditor.graphics.PixelCanvas;
import net.pixelstatic.pixeleditor.scene2D.ColorBox;
import net.pixelstatic.pixeleditor.scene2D.DrawingGrid;
import net.pixelstatic.pixeleditor.tools.Tool;
import net.pixelstatic.utils.AndroidKeyboard;
import net.pixelstatic.utils.AndroidKeyboard.AndroidKeyboardListener;
import net.pixelstatic.utils.graphics.Hue;
import net.pixelstatic.utils.graphics.Textures;
import net.pixelstatic.utils.modules.Module;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.file.*;
import com.kotcrab.vis.ui.widget.file.FileChooser.HistoryPolicy;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;

public class GUI extends Module<PixelEditor>{
	public static GUI gui;
	public DrawingGrid drawgrid;
	Stage stage;
	Skin skin;
	VisTable tooltable;
	VisTable colortable;
	Array<Tool> tools = new Array<Tool>();
	FileChooser currentChooser;
	public ColorBox selected;
	public ColorPicker picker;
	public Tool tool = Tool.pencil;

	@Override
	public void update(){
		Gdx.gl.glClearColor(20 / 255f, 33 / 255f, 52 / 255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		
		//pc debugging
		if(stage.getKeyboardFocus() instanceof Button || stage.getKeyboardFocus() == null || stage.getKeyboardFocus() instanceof VisDialog) 
			stage.setKeyboardFocus(drawgrid);

	}

	void setup(){
		
		colortable = new VisTable();
		colortable.setFillParent(true);
		stage.addActor(colortable);
		
		tooltable = new VisTable();
		tooltable.setFillParent(true);
		stage.addActor(tooltable);

	}

	void loadTools(){
		
		final VisDialog extradialog = new VisDialog(""){
			public float getPrefWidth(){
				return Gdx.graphics.getWidth();
			}
			
			public float getPrefHeight(){
				return 500;
			}
		};
		extradialog.setMovable(false);
		
		
		final VisCheckBox box = new VisCheckBox("Overwrite");
		extradialog.getButtonsTable().top().left().add(box).align(Align.topLeft);

		final CollapsibleWidget collapser = new CollapsibleWidget(extradialog);
		collapser.setCollapsed(true);

		final VisTextButton expandbutton = new VisTextButton("^");
		expandbutton.setColor(Color.LIGHT_GRAY);
		expandbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				if(tool.table == null){
					tool.initTable();
					tool.table.setName("table");
				}
				
				extradialog.getContentTable().clear();
				extradialog.getContentTable().top().left().add(tool.table);
				
				collapser.setCollapsed( !collapser.isCollapsed());
				expandbutton.setText(collapser.isCollapsed() ? "^" : "v");
			}
		});
		
		

		tooltable.bottom().left().add(collapser).height(20).colspan(7).fillX().expandX();
		tooltable.row();
		tooltable.bottom().left().add(expandbutton).height(50).colspan(7).fillX().expandX();
		tooltable.row();
		
		tools.addAll(Tool.values());


		float size = Gdx.graphics.getWidth() / tools.size;

		
		for(int i = 0;i < tools.size; i ++){
			final Tool ctool = tools.get(i);

			final VisTextButton button = new VisTextButton(ctool.toString(), "toggle");
			button.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					tool = ctool;
					if( !button.isChecked()) button.setChecked(true);
					for(Actor actor : tooltable.getChildren()){
						if( !(actor instanceof VisTextButton)) continue;
						VisTextButton other = (VisTextButton)actor;
						if(other != button) other.setChecked(false);
					}
					
					if(!collapser.isCollapsed()){
						if(tool.table == null)tool.initTable();
						extradialog.getContentTable().clear();
						extradialog.getContentTable().top().left().add(tool.table);
					}
				//((ClickListener)expandbutton.getListeners().get(2)).clicked(null, x, y);
					
				}
			});

			if(i == 0){
				button.setChecked(true);
				tool = ctool;
			}

			tooltable.bottom().left().add(button).size(size+1f);
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

	void loadColors(){

		picker = new ColorPicker(new ColorPickerAdapter(){
			@Override
			public void finished(Color color){
				selected.setColor(color);
			}
		});

		picker.setShowHexFields(false);

		/*
		VisTable colorselect = new VisTable();
		colorselect.setFillParent(true);
		colorselect.add(new Separator()).fillY().expandX().fillX();
		colorselect.add(picker).expandX().fillX();
		colorselect.add(new Separator()).fillY().expandY().expandX().fillX();
		final CollapsibleWidget collapse = new CollapsibleWidget(colorselect);
		collapse.setCollapsed(false);
		*/

		colortable.top().left();

		int height = 50;

		VisTable menu = new VisTable();

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
				
			}
		});

		menu.add(resize).size(height);
		
		VisTextButton settings = new VisTextButton("settings");
		settings.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				
			}
		});

		menu.add(settings).size(height);

		float size = Gdx.graphics.getWidth() / menu.getCells().size;

		for(Cell<?> cell : menu.getCells()){
			cell.size(size);
		}

		ham.setOrigin(size / 2, size / 2);
		ham.setRotation(90);

		colortable.add(menu).expandX().fillX().colspan(11).height(size);
		colortable.row();

		int colorsize = 52;

		colortable.add().expandX().fillX();
		for(int i = 0;i < 8;i ++){

			final ColorBox box = new ColorBox();
			if(selected == null) selected = box;

			colortable.add(box).size(colorsize);
			box.setColor(Hue.fromHSB(i / 8f, 1f, 1f));

			box.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					selected = box;
					box.setZIndex(999);

					if(getTapCount() > 1){
						picker.setColor(box.getColor());
						stage.addActor(picker.fadeIn());
						//collapse.setCollapsed(!collapse.isCollapsed());
					}
				}
			});

		}
		VisTextButton acolor = new VisTextButton("...");
		colortable.add(acolor).height(height).size(colorsize);
		acolor.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){

			}
		});

		colortable.add().expandX().fillX();

		//}
		selected.setZIndex(999);
		//VisTextButton add = new VisTextButton("+");
		//colortable.add(add).size(40);

		//colortable.row();
		//colortable.add(collapse).colspan(11).expandX().fillX();
	}

	void loadCanvas(){
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
		Textures.load("textures/");
		Textures.repeatWrap("alpha", "grid_10", "grid_25");
		stage = new Stage();
		stage.setViewport(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);
		VisUI.load();
		skin = new Skin(Gdx.files.internal("gui/uiskin.json"));
		FileChooser.setDefaultPrefsName(getClass().getPackage().getName());
		setup();
		loadTools();
		loadColors();
		loadCanvas();

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

	public void resize(int width, int height){
		stage.getViewport().update(width, height, true);
	}

	public void dispose(){
		VisUI.dispose();
		picker.dispose();
		Textures.dispose();
	}
}
