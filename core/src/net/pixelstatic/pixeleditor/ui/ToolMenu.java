package net.pixelstatic.pixeleditor.ui;

import static net.pixelstatic.pixeleditor.modules.Main.s;
import net.pixelstatic.gdxutils.graphics.Textures;
import net.pixelstatic.pixeleditor.modules.Main;
import net.pixelstatic.utils.scene2D.ColorBar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;

public class ToolMenu extends VisTable{
	private Main main;
	private VisTable menutable, optionstable;
	private VisSlider brushslider;
	private ColorBar alphabar;
	
	public ToolMenu(Main main){
		this.main = main;
		setBackground("button-window-bg");
		
		menutable = new VisTable();
		optionstable = new VisTable();
		
		top().left().add(menutable).align(Align.topLeft).expand().fill().row();
		top().left().add(optionstable).expand().fill().row();
		optionstable.row();
		
		setupMenu();
	}
	
	public void updateColor(Color color){
		alphabar.setRightColor(color);
	}
	
	public void initialize(){
		alphabar.fire(new ChangeListener.ChangeEvent()); //update alpha
		brushslider.fire(new ChangeListener.ChangeEvent());
	}
	
	private VisTextButton addMenuButton(String text){
		float height = 70f;

		VisTextButton button = new VisTextButton(text);
		menutable.top().left().add(button).width(Gdx.graphics.getWidth() / 5 - 3).height(height).expandX().fillX().padTop(5f * s).align(Align.topLeft);
		return button;
	}
	
	private void setupMenu(){
		VisTextButton menu = addMenuButton("menu");
		menu.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				main.openProjectMenu();
			}
		});
		/*
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
		*/
		final VisLabel infolabel = new VisLabel();
	
		brushslider = new VisSlider(1, 10, 0.01f, true);
		brushslider.setValue(main.prefs.getInteger("brushsize", 1));
		final VisLabel brushlabel = new VisLabel("Brush Size: " + brushslider.getValue());

		brushslider.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				brushlabel.setText("Brush Size: " + (int)brushslider.getValue());
				main.prefs.putInteger("brushsize", (int)brushslider.getValue());
				main.drawgrid.brushSize = (int)brushslider.getValue();
			}
		});

		alphabar = new ColorBar(true);

		alphabar.setColors(Color.CLEAR.cpy(), Color.WHITE);
		alphabar.setSize(50 * s, 300 * s);
		alphabar.setSelection(main.prefs.getFloat("opacity", 1f));

		optionstable.bottom().left();

		final VisLabel opacity = new VisLabel("Opacity: " + (int)(alphabar.getSelection()*100) + "%");

		alphabar.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				opacity.setText("Opacity: " + (int)(alphabar.getSelection()*100) + "%");
				main.drawgrid.canvas.setAlpha(alphabar.getSelection());
				main.prefs.putFloat("opacity", alphabar.getSelection());
			}
		});

		final VisCheckBox grid = new VisCheckBox("Grid");

		grid.getImageStackCell().size(40 * s);

		grid.setChecked(main.prefs.getBoolean("grid", true));

		grid.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				main.drawgrid.grid = grid.isChecked();
				main.prefs.putBoolean("grid", main.drawgrid.cursormode);
				
			}
		});

		VisTextButton menubutton = new VisTextButton("Menu");
		VisTextButton settingsbutton = new VisTextButton("Settings");

		menubutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				main.openProjectMenu();
			}
		});

		settingsbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				main.openSettingsMenu();
			}
		});

		final VisRadioButton cbox = new VisRadioButton("cursor mode");
		final VisRadioButton tbox = new VisRadioButton("tap mode");

		if(main.prefs.getBoolean("cursormode", true)){
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
				main.drawgrid.cursormode = cbox.isChecked();
				main.prefs.putBoolean("cursormode", main.drawgrid.cursormode);
			}
		});
		
		VisImageButtonStyle style = VisUI.getSkin().get("toggle", VisImageButtonStyle.class);
		
		VisImageButtonStyle modestyle = new VisImageButtonStyle(style);
		VisImageButtonStyle gridstyle = new VisImageButtonStyle(style);
		
		modestyle.imageUp = Textures.getDrawable("icon-cursor");
		gridstyle.imageUp = Textures.getDrawable("icon-grid");
		
		final VisImageButton modebutton = new VisImageButton(modestyle);
		modebutton.setChecked(main.prefs.getBoolean("cursormode", true));
		
		modebutton.getImageCell().size(50*s);
		
		modebutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				main.prefs.putBoolean("cursormode", modebutton.isChecked());
				main.drawgrid.cursormode = modebutton.isChecked();
			}
		});
		
		final VisImageButton gridbutton = new VisImageButton(gridstyle);
		gridbutton.setChecked(main.prefs.getBoolean("grid", true));
		
		gridbutton.getImageCell().size(50*s);
		
		gridbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				main.prefs.putBoolean("grid", gridbutton.isChecked());
				main.drawgrid.grid = gridbutton.isChecked();
				
			}
		});
		
		Table menutable = new VisTable();
		Table othertable = new VisTable();

		optionstable.top().left();
		optionstable.add(menutable).growY();
		optionstable.add(othertable).grow();
		
		//TODO
		
		menutable.top().left();
		menutable.add(modebutton).size(80*s).align(Align.topLeft).padTop(8*s).row();
		menutable.add(gridbutton).size(80*s).align(Align.topLeft);
		
		othertable.bottom().right();
		
		infolabel.setAlignment(Align.topLeft, Align.left);
		
		othertable.add(brushlabel).padRight(10).minWidth(150).align(Align.center);
		othertable.add(opacity).minWidth(150).align(Align.center);
		othertable.row();
		othertable.add(brushslider).growY().padTop(20).padBottom(20).padRight(15);
		othertable.add(alphabar).padTop(20).padBottom(20);

	}
	
	public float getPrefWidth(){
		return Gdx.graphics.getWidth();
	}
}
