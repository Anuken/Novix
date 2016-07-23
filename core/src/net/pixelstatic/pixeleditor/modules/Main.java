package net.pixelstatic.pixeleditor.modules;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

import net.pixelstatic.pixeleditor.PixelEditor;
import net.pixelstatic.pixeleditor.graphics.Palette;
import net.pixelstatic.pixeleditor.graphics.PixelCanvas;
import net.pixelstatic.pixeleditor.graphics.Project;
import net.pixelstatic.pixeleditor.scene2D.*;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ClearDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ColorAlphaDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ColorizeDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ContrastDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.CropDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ExportScaledDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.FlipDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.InvertDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.NamedSizeDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ReplaceDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.RotateDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ScaleDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.ShiftDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.SizeDialog;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses.SymmetryDialog;
import net.pixelstatic.pixeleditor.tools.Tool;
import net.pixelstatic.utils.*;
import net.pixelstatic.utils.AndroidKeyboard.AndroidKeyboardListener;
import net.pixelstatic.utils.MiscUtils.TextFieldEmptyListener;
import net.pixelstatic.utils.dialogs.AndroidDialogs;
import net.pixelstatic.utils.dialogs.AndroidTextFieldDialog;
import net.pixelstatic.utils.dialogs.TextFieldDialog;
import net.pixelstatic.utils.graphics.Hue;
import net.pixelstatic.utils.graphics.Textures;
import net.pixelstatic.utils.modules.Module;
import net.pixelstatic.utils.scene2D.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Pixmap.Format;
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
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;
import com.kotcrab.vis.ui.widget.file.FileChooser;

import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;

public class Main extends Module<PixelEditor>{
	public static Main gui;
	public static float s = 1f; //density scale
	public DrawingGrid drawgrid;
	public Stage stage;
	public FileHandle projectDirectory;
	public boolean savingProject = false;
	ObjectMap<String, Project> projects = new ObjectMap<String, Project>();
	public Project currentProject;
	Palette currentPalette;
	public int paletteColor;
	Skin skin;
	Preferences prefs;
	VisTable tooltable;
	VisTable colortable;
	VisTable extratable;
	VisTable projecttable;
	VisDialog settingsmenu, projectmenu, palettedialog;
	CollapseButton colorcollapsebutton, toolcollapsebutton;
	SmoothCollapsibleWidget colorcollapser, toolcollapser;
	BrushSizeWidget brush;
	ColorBar alphabar;
	Table menutable, optionstable, tooloptiontable, extratooltable;
	FileChooser currentChooser;
	ObjectMap<String, Palette> palettes = new ObjectMap<String, Palette>();
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

		settingsmenu = new VisDialog("Settings"){
			public void result(Object o){
				prefs.flush();
			}
		};
		settingsmenu.setFillParent(true);

		settingsmenu.getTitleLabel().setColor(Color.CORAL);
		settingsmenu.getTitleTable().row();
		settingsmenu.getTitleTable().add(new Separator()).expandX().fillX().padTop(3 * s);

		Table settings = settingsmenu.getContentTable();

		settings.add().height(20).row();

		VisTextButton back = new VisTextButton("Back");
		back.add(new Image(Textures.getDrawable("icon-arrow-left"))).size(40 * s).center();

		back.getCells().reverse();
		back.getLabelCell().padRight(40f * s);

		settingsmenu.getButtonsTable().add(back).width(Gdx.graphics.getWidth()).height(60 * s);
		settingsmenu.setObject(back, false);

		addScrollSetting(settings, "Cursor Size", 1, 10, 5);

		addCheckSetting(settings, "Autosave", true);

		VisTable scrolltable = new VisTable();

		final VisScrollPane pane = new VisScrollPane(scrolltable){
			public float getPrefHeight(){
				return Gdx.graphics.getHeight();
			}
		};
		pane.setFadeScrollBars(false);
		pane.setOverscroll(false, false);

		projectmenu = new VisDialog("Projects"){
			public VisDialog show(Stage stage){
				super.show(stage);
				stage.setScrollFocus(pane);
				return this;
			}
		};
		projectmenu.setFillParent(true);
		projectmenu.getTitleLabel().setColor(Color.CORAL);
		projectmenu.getTitleTable().row();
		projectmenu.getTitleTable().add(new Separator()).expandX().fillX().padTop(3 * s);
		

		VisTable newtable = new VisTable();

		VisTextButton newbutton = new VisTextButton("New Project");
		newbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				newProject();
			}
		});

		addIconToButton(newbutton, new Image(Textures.get("icon-plus")), 40 * s);
		
		VisTextButton settingsbutton = new VisTextButton("Settings");
		settingsbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				settingsmenu.show(stage);
			}
		});
		

		newtable.left().add(newbutton).padBottom(6 * s).size(190 * s, 60 * s);
		newtable.left().add().grow();
		newtable.left().add(settingsbutton).padBottom(6 * s).size(120 * s, 60 * s).align(Align.topRight);

		projectmenu.getContentTable().add(newtable).grow().row();

		projectmenu.getContentTable().top().left().add(pane).align(Align.topLeft).grow();

		VisTextButton projectback = new VisTextButton("Back");
		projectback.add(new Image(Textures.getDrawable("icon-arrow-left"))).size(40 * s).center();

		projectback.getCells().reverse();
		projectback.getLabelCell().padRight(40f * s);

		projectmenu.getButtonsTable().add(projectback).width(Gdx.graphics.getWidth()).height(60 * s);
		projectmenu.setObject(projectback, false);

		updateProjectMenu(true);
	}

	public ProjectTable updateProjectMenu(boolean loaded){
		VisTable scrolltable = ((VisTable)((VisScrollPane)projectmenu.getContentTable().getCells().get(1).getActor()).getChildren().first());

		scrolltable.clearChildren();

		ProjectTable current = null;

		for(Project project : projects.values()){
			ProjectTable table = new ProjectTable(project, project == currentProject ? loaded : true);
			scrolltable.top().left().add(table).padTop(8).growX().padRight(10 * s).row();
			if(project == currentProject) current = table;
		}

		return current;
	}

	class ProjectTable extends VisTable{
		public final Project project;
		public boolean loaded;
		private boolean created;
		private Label sizelabel;
		private Cell<?> imagecell;

		public ProjectTable(final Project project, boolean startloaded){
			this.project = project;
			this.loaded = startloaded;

			VisLabel namelabel = new VisLabel(project.name);

			sizelabel = new VisLabel("Loading...");
			sizelabel.setColor(Color.GRAY);

			int imagesize = 40;

			VisImageButton openbutton = new VisImageButton(Textures.getDrawable(project == currentProject ? "icon-open-gray" : "icon-open"));
			VisImageButton copybutton = new VisImageButton(Textures.getDrawable("icon-copy"));
			VisImageButton renamebutton = new VisImageButton(Textures.getDrawable("icon-rename"));
			VisImageButton deletebutton = new VisImageButton(Textures.getDrawable("icon-trash"));

			if(project == currentProject){
				openbutton.setDisabled(true);
				openbutton.setColor(Hue.lightness(0.94f));
			}

			openbutton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					if(project != currentProject) Main.gui.openProject(project);
				}
			});

			copybutton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					Main.gui.copyProject(project);
				}
			});

			renamebutton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					Main.gui.renameProject(project);
				}
			});

			deletebutton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					Main.gui.deleteProject(project);
				}
			});

			openbutton.getImageCell().size(imagesize);
			copybutton.getImageCell().size(imagesize);
			renamebutton.getImageCell().size(imagesize);
			deletebutton.getImageCell().size(imagesize);

			VisTable texttable = new VisTable();
			VisTable buttontable = new VisTable();

			float bheight = 50, space = 4;

			buttontable.bottom().left().add(openbutton).align(Align.bottomLeft).height(bheight).growX().space(space);
			buttontable.add(copybutton).height(bheight).growX().space(space);
			buttontable.add(deletebutton).height(bheight).growX().space(space);
			buttontable.add(renamebutton).height(bheight).growX().space(space);

			top().left();

			background("button");
			setColor(Hue.lightness(0.87f));

			imagecell = stack(new AnimatedImage(Textures.getDrawable("icon-load-1"), Textures.getDrawable("icon-load-2"), Textures.getDrawable("icon-load-3")), new BorderImage());
			imagecell.padTop(imagecell.getPadTop() + 4).padBottom(imagecell.getPadBottom() + 4);

			MiscUtils.fitCell(imagecell, 128 * s, 1);

			add(texttable).grow();
			texttable.top().left().add(namelabel).padLeft(8).align(Align.topLeft);
			texttable.row();
			texttable.add(sizelabel).padLeft(8).padTop(10 * s).align(Align.topLeft);
			texttable.row();
			texttable.add(buttontable).grow().padLeft(8);

			addAction(new Action(){

				public boolean act(float delta){
					if(created) return true;
					if( !loaded) return false;

					if(project == currentProject) project.reloadTexture();

					Texture texture = project.cachedTexture;

					sizelabel.setText("Size: " + texture.getWidth() + "x" + texture.getHeight());

					StaticPreviewImage image = new StaticPreviewImage(texture);
					imagecell.setActor(image);

					MiscUtils.fitCell(imagecell, 128 * s, (float)texture.getWidth() / texture.getHeight());

					imagecell.padTop(imagecell.getPadTop() + 4).padBottom(imagecell.getPadBottom() + 4);

					pack();

					created = true;
					return true;
				}
			});

		}

	}

	void addIconToButton(VisTextButton button, Image image, float size){
		button.add(image).size(size).center();
		button.getCells().reverse();
	}

	void newProject(){

		new NamedSizeDialog("New Project"){

			public void result(String name, int width, int height){
				if(validateProjectName(name)) return;

				Project project = createNewProject(name, width, height);

				openProject(project);

			}
		}.show(stage);
	}

	Project createNewProject(String name, int width, int height){
		Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
		PixmapIO.writePNG(projectDirectory.child(name + ".png"), pixmap);

		Project project = loadProject(projectDirectory.child(name + ".png"));
		Gdx.app.log("pedebugging", "Created new project with name " + name);

		return project;
	}

	void openProject(Project project){
		prefs.putString("lastproject", project.name);
		prefs.flush();
		currentProject = project;

		Gdx.app.log("pedebugging", "Opening project \"" + project.name + "\"...");

		PixelCanvas canvas = new PixelCanvas(project.getCachedPixmap());

		drawgrid.setCanvas(canvas);
		updateToolColor();
		projectmenu.hide();
	}

	void copyProject(final Project project){

		new DialogClasses.InputDialog("Rename Copied Dialog", project.name, "New Copy Name: "){
			public void result(String text){
				if(validateProjectName(text)) return;

				try{
					FileHandle newhandle = project.file.parent().child(text + ".png");
					MiscUtils.copyFile(project.file.file(), newhandle.file());

					projects.put(text, new Project(newhandle));
					updateProjectMenu(true);
				}catch(IOException e){
					AndroidDialogs.showError(stage, "Error copying file!", e);
					e.printStackTrace();
				}
			}
		}.show(stage);
	}

	void renameProject(final Project project){
		new DialogClasses.InputDialog("Rename Project", project.name, "Name: "){
			public void result(String text){
				if(validateProjectName(text, project)) return;
				projects.remove(project.name);
				project.name = text;
				projects.put(text, project);
				updateProjectMenu(true);
			}
		}.show(stage);
	}

	void deleteProject(final Project project){
		if(project == currentProject){
			AndroidDialogs.showInfo(stage, "You cannot delete the canvas you are currently using!");
			return;
		}

		new DialogClasses.ConfirmDialog("Confirm", "Are you sure you want\nto delete this canvas?"){
			public void result(){
				try{
					project.file.file().delete();
					project.dispose();
					projects.remove(project.name);
					updateProjectMenu(true);
				}catch(Exception e){
					AndroidDialogs.showError(stage, "Error deleting file!", e);
					e.printStackTrace();
				}
			}
		}.show(stage);
	}

	public void saveProject(){
		savingProject = true;
		Gdx.app.log("pedebugging", "Starting save..");
		PixmapIO.writePNG(currentProject.file, drawgrid.canvas.pixmap);
		Gdx.app.log("pedebugging", "Saving project.");
		savingProject = false;
	}

	void loadProjects(){
		FileHandle[] files = projectDirectory.list();

		for(FileHandle file : files){
			if(file.extension().equals("png")){
				try{
					loadProject(file);
				}catch(Exception e){
					Gdx.app.error("pedebugging", "Error loading project \"" + file.nameWithoutExtension() + " \", corrupt file?", e);
					//projects.remove(file.nameWithoutExtension());
				}
			}
		}

		if(projects.size == 0){
			currentProject = createNewProject("Untitled", 16, 16);
		}else{
			String last = prefs.getString("lastproject", "Untitled");
			
			try{
				currentProject = projects.get(last);
				currentProject.reloadTexture();
			}catch (Exception e){
				e.printStackTrace();
				currentProject = createNewProject("Untitled", 16, 16);
			}
		}
	}

	Project loadProject(FileHandle file){
		Project project = new Project(file);
		projects.put(project.name, project);
		return project;
	}

	boolean checkIfProjectExists(String name, Project ignored){
		for(Project project : projects.values()){
			if(project == ignored) continue;
			if(project.name.equals(name)) return true;
		}
		return false;
	}

	boolean validateProjectName(String name){
		boolean exists = checkIfProjectExists(name, null);

		if( !MiscUtils.isFileNameValid(name)){
			AndroidDialogs.showError(stage, "Project name is invalid!");
			return true;
		}

		if(exists){
			AndroidDialogs.showError(stage, "A project with that name already exists!");
		}

		return exists;
	}

	boolean validateProjectName(String name, Project project){
		if( !MiscUtils.isFileNameValid(name)){
			AndroidDialogs.showError(stage, "Project name is invalid!");
			return true;
		}

		boolean exists = checkIfProjectExists(name, project);

		if(exists){
			AndroidDialogs.showError(stage, "A project with that name already exists!");
		}

		return exists;
	}

	void addScrollSetting(Table table, final String name, int min, int max, int value){
		final VisLabel label = new VisLabel(name + ": " + prefs.getInteger(name, value));
		final VisSlider slider = new VisSlider(min, max, 1, false);
		slider.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				label.setText(name + ": " + slider.getValue());
				prefs.putInteger(name, (int)slider.getValue());
			}
		});
		slider.setValue(prefs.getInteger(name));
		table.top().left().add(label).align(Align.left);
		table.row();
		table.add(slider).width(200 * s).padBottom(40f);
		table.row();
	}

	void addCheckSetting(Table table, final String name, boolean value){
		final VisLabel label = new VisLabel(name);
		final VisCheckBox box = new VisCheckBox("", prefs.getBoolean(name, value));
		box.getImageStackCell().size(40 * s);
		box.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				prefs.putBoolean(name, box.isChecked());
			}
		});
		table.top().left().add(label).align(Align.left);
		table.add(box);
		table.row();
	}

	void setupMenu(){
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

		VisTextButton tbutton = addMenuButton("transform..");

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
		fileMenu.addItem(new ExtraMenuItem(fibutton, "save", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				saveProject();
			}
		}));
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

		final VisLabel opacity = new VisLabel("opacity: " + alphabar.getSelection());

		alphabar.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				opacity.setText("opacity: " + MiscUtils.limit(alphabar.getSelection() + "", 5));
				infolabel.setText("Brush Size: " + brush.getBrushSize() + "\nOpacity: " + (int)(alphabar.getSelection()*100));
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
		
		
		
		//TODO
		
		menutable.top().left();
		
		menutable.add(menubutton).size(70*s).align(Align.topLeft).row();
		menutable.add(modebutton).size(70*s).align(Align.topLeft).row();
		menutable.add(gridbutton).size(70*s).align(Align.topLeft);
		
		othertable.bottom().right();
		
		othertable.add(infolabel).align(Align.topLeft).padTop(20);
		
		othertable.add(brushslider).growY().padTop(20).padBottom(20).padRight(20);
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

	private static class TallMenuItem extends MenuItem{

		public TallMenuItem(String text, ChangeListener listener){
			super(text, listener);
		}

		public float getPrefHeight(){
			return super.getPrefHeight() * 1.4f;
		}
	}

	VisTextButton addMenuButton(String text){
		float height = 70f;

		VisTextButton button = new VisTextButton(text);
		menutable.top().left().add(button).width(Gdx.graphics.getWidth() / 4f - 4).height(height).expandX().fillX().padTop(5f * s).align(Align.topLeft);
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
		//extratable.top().left().add(menutable).align(Align.topLeft).expand().fill().row();
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

	void setupColors(){

		VisTable pickertable = new VisTable(){
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

		palettedialog = new VisDialog("Palettes", "dialog"){
			public VisDialog show(Stage stage){
				super.show(stage);
				stage.setScrollFocus(getContentTable().getChildren().first());
				return this;
			}
		};
		palettedialog.setMovable(false);
		palettedialog.getTitleLabel().setColor(Color.CORAL);
		MiscUtils.addHideButton(palettedialog);

		palettebutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				updatePaletteDialog();
				palettedialog.show(stage);
			}
		});
		
		pickertable.add(apicker).expand().fill().padBottom(10f * s);
		pickertable.row();
		pickertable.center().add(palettebutton).align(Align.center).padBottom(10f * s).height(60 * s).growX();
		
		//TODO COLOR TABLE
		colorcollapser.setY(Gdx.graphics.getWidth() / Tool.values().length + toolcollapsebutton.getHeight()*2);
		colorcollapser.toBack();
		colorcollapser.resetY();
		colorcollapser.setCollapsed(true, false);
		setupBoxColors();
	}

	void updateColorMenu(){
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
	}

	void updatePaletteDialog(){
		float scrolly = palettedialog.getContentTable().getChildren().size == 0 ? 0 : ((ScrollPane)palettedialog.getContentTable().getChildren().first()).getScrollPercentY();

		palettedialog.getContentTable().clearChildren();
		palettedialog.getButtonsTable().clearChildren();

		class PaletteListener extends ClickListener{
			PaletteWidget widget;
			Palette palette;

			public PaletteListener(PaletteWidget palette){
				widget = palette;
				this.palette = widget.palette;
			}

			public void clicked(InputEvent event, float x, float y){
				PopupMenu menu = new PopupMenu();
				menu.addItem(new TallMenuItem("resize", new ChangeListener(){
					public void changed(ChangeEvent event, Actor actor){
						new DialogClasses.NumberInputDialog("Resize Palette", palette.size() + "", "Size: "){
							public void result(int size){
								Color[] newcolors = new Color[size];

								Arrays.fill(newcolors, Color.WHITE.cpy());

								for(int i = 0;i < size && i < palette.size();i ++){
									newcolors[i] = palette.colors[i];
								}

								palette.colors = newcolors;

								updatePaletteDialog();
								updateColorMenu();
							}
						}.show(stage);
					}
				}));
				menu.addItem(new TallMenuItem("rename", new ChangeListener(){
					public void changed(ChangeEvent event, Actor actor){
						new DialogClasses.InputDialog("Rename Palette", palette.name, "Name: "){
							public void result(String string){
								palettes.remove(palette.name);
								palette.name = string;
								palettes.put(string, palette);
								if(palette == currentPalette){
									prefs.putString("currentpalette", palette.name);
									prefs.flush();
								}
								updatePaletteDialog();
							}
						}.show(stage);
					}
				}));
				menu.addItem(new TallMenuItem("delete", new ChangeListener(){
					public void changed(ChangeEvent event, Actor actor){
						new DialogClasses.ConfirmDialog("Delete Palette", "Are you sure you want\nto delete this palette?"){
							public void result(){
								palettes.remove(palette.name);
								updatePaletteDialog();
							}
						}.show(stage);
					}
				}));

				Vector2 coords = widget.extrabutton.localToStageCoordinates(new Vector2());
				menu.showMenu(stage, coords.x - menu.getWidth() + widget.extrabutton.getWidth(), coords.y);
			}
		}

		VisTable palettetable = new VisTable();

		final VisScrollPane pane = new VisScrollPane(palettetable);
		pane.setFadeScrollBars(false);
		pane.setOverscroll(false, false);

		palettedialog.getContentTable().add(pane).left().grow().maxHeight(Gdx.graphics.getHeight() / 2);

		for(final Palette palette : palettes.values()){
			final PaletteWidget widget = new PaletteWidget(palette, palette == Main.gui.currentPalette);

			widget.setTouchable(palette == currentPalette ? Touchable.childrenOnly : Touchable.enabled);

			widget.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					if( !widget.extrabutton.isOver()) setPalette(palette);
				}
			});

			widget.addExtraButtonListener(new PaletteListener(widget));

			palettetable.add(widget).padBottom(6);
			palettetable.row();
		}

		VisTextButton backbutton = new VisTextButton("Back");

		backbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				palettedialog.hide();
			}
		});

		VisTextButton addpalettebutton = new VisTextButton("New Palette");

		addpalettebutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				new DialogClasses.InputDialog("New Palette", "", "Name:"){
					protected VisTextField numberfield;

					{
						numberfield = new VisTextField("8");
						numberfield.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());

						getContentTable().row();

						getContentTable().center().add(new VisLabel("Size:")).padTop(0f).padBottom(20f * s);
						getContentTable().center().add(numberfield).pad(20 * s).padLeft(0f).padTop(0);

						new TextFieldEmptyListener(ok, textfield, numberfield);
					}

					public void result(String string){
						palettes.put(string, new Palette(string, Integer.parseInt(numberfield.getText())));
						updatePaletteDialog();
					}

				}.show(stage);
			}
		});

		addIconToButton(addpalettebutton, new Image(Textures.get("icon-plus")), 40);
		addIconToButton(backbutton, new Image(Textures.get("icon-arrow-left")), 40);

		palettedialog.getButtonsTable().add(backbutton).size(150 * s, 50 * s);
		palettedialog.getButtonsTable().add(addpalettebutton).size(200 * s, 50 * s);

		palettedialog.pack();

		stage.setScrollFocus(pane);

		pane.setSmoothScrolling(false);
		pane.setScrollPercentY(scrolly);

		pane.addAction(Actions.sequence(Actions.delay(0.01f), new Action(){
			@Override
			public boolean act(float delta){
				pane.setSmoothScrolling(true);
				return true;
			}
		}));
	}

	void setPalette(Palette palette){
		paletteColor = 0;
		currentPalette = palette;
		prefs.putString("lastpalette", palette.name);
		prefs.flush();
		updateColorMenu();
		setSelectedColor(palette.colors[0]);
		setupBoxColors();
		updatePaletteDialog();
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
		drawgrid.setCanvas(new PixelCanvas(currentProject.getCachedPixmap()));

		stage.addActor(drawgrid);
	}

	public Main(){
		Gdx.graphics.setContinuousRendering(false);

		gui = this;
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
		loadFonts();
		skin = new Skin(Gdx.files.internal("gui/uiskin.json"));

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

		loadProjects();

		setup();
		setupTools();
		setupColors();
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
						saveProject();
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
		final ProjectTable table = updateProjectMenu(false);
		projectmenu.show(stage);

		new Thread(new Runnable(){
			public void run(){
				saveProject();
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

	@Override
	public void pause(){
		Gdx.app.log("pedebugging", "Pausing and saving everything.");
		saveProject();
		savePalettes();
		if(currentProject != null) prefs.putString("lastproject", currentProject.name);
		prefs.flush();
	}

	@Override
	public void dispose(){
		/*if(Gdx.app.getType() == ApplicationType.Android)*/ pause();
		VisUI.dispose();
		Textures.dispose();
	}
}
