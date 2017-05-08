package io.anuke.novix.ui;


import static io.anuke.ucore.UCore.s;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;

import io.anuke.novix.modules.Core;
import io.anuke.novix.scene2D.*;
import io.anuke.novix.tools.PixelCanvas;
import io.anuke.novix.tools.Tool;
import io.anuke.novix.ui.DialogClasses.*;
import io.anuke.ucore.graphics.PixmapUtils;
import io.anuke.utools.SceneUtils;

public class ToolTable extends VisTable{
	private static ButtonMenu currentMenu;
	public final String selectcolor = "7aaceaff";
	
	private Core main;
	private VisTable menutable, optionstable;
	private VisSlider brushslider;
	private ColorBar alphabar;
	private VisImageButton gridbutton;
	private CollapseButton collapsebutton;
	private SmoothCollapsibleWidget collapser;
	private Tool tool;
	
	
	public ToolTable(Core main){
		final VisTable tooltable = new VisTable();
		tooltable.setFillParent(true);
		main.stage.addActor(tooltable);

		this.main = main;
		setBackground("menu");
		
		menutable = new VisTable();
		optionstable = new VisTable();
		
		top().left().add(menutable).align(Align.topLeft).padBottom(10).expand().fill().row();
		top().left().add(optionstable).expand().fill().row();
		optionstable.row();
		
		setupMenu();
		setupTable(tooltable);
	}
	
	private void setupTable(final Table tooltable){
		final float size = Gdx.graphics.getWidth() / (Tool.values().length);
		
		collapser = new SmoothCollapsibleWidget(this, false);
		collapser.setName("toolcollapser");

		collapser.setCollapsed(true);

		collapsebutton = new CollapseButton();
		collapsebutton.setName("toolcollapsebutton");

		collapsebutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				collapser.setCollapsed(!collapser.isCollapsed());
				collapsebutton.flip();

				if(!main.colorMenuCollapsed() && event != null){
					main.colormenu.collapse();
				}
			}
		});

		tooltable.bottom().left().add(collapser).height(20 * s).colspan(Tool.values().length).fillX().expandX();
		tooltable.row();
		tooltable.bottom().left().add(collapsebutton).height(60 * s).colspan(Tool.values().length).fillX()
				.expandX();
		tooltable.row();

		Tool[] tools = Tool.values();

		for(int i = 0; i < tools.length; i++){
			final Tool ctool = tools[i];

			final VisImageButton button = new VisImageButton((Drawable) null);
			button.setStyle(new VisImageButtonStyle(VisUI.getSkin().get("toggle", VisImageButtonStyle.class)));
			button.getStyle().imageUp = VisUI.getSkin().getDrawable("icon-" + ctool.name());
			button.setGenerateDisabledImage(true);
			button.getImageCell().size(50 * s);
			ctool.button = button;
			button.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					ctool.onSelected();
					if(!ctool.selectable()){
						button.setChecked(false);
						return;
					}
					tool = ctool;
					tool.onColorChange(main.selectedColor(), main.drawgrid.canvas);
					if(!button.isChecked())
						button.setChecked(true);
					
					for(Actor actor : tooltable.getChildren()){
						if(!(actor instanceof VisImageButton))
							continue;
						VisImageButton other = (VisImageButton) actor;
						if(other != button)
							other.setChecked(false);
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
	
	public void collapse(){
		((ClickListener) collapsebutton.getListeners().get(2)).clicked(null, 0, 0);
	}
	
	public boolean collapsed(){
		return collapser.isCollapsed();
	}
	
	public CollapseButton getButton(){
		return collapsebutton;
	}
	
	public Tool getTool(){
		return tool;
	}
	
	@Override
	protected void drawBackground (Batch batch, float parentAlpha, float x, float y) {
		super.drawBackground(batch, parentAlpha, x, y);
		
		float pad = 8;
		
		VisUI.getSkin().getDrawable("menu-bg").draw(batch, 0, menutable.getY() - pad, getWidth(), menutable.getHeight() + pad);
	}
	
	public void updateColor(Color color){
		alphabar.setRightColor(color);
	}
	
	public void initialize(){
		alphabar.fire(new ChangeListener.ChangeEvent()); //update alpha
		brushslider.fire(new ChangeListener.ChangeEvent());
	}
	
	private VisTextButton addMenuButton(String text, String icon){
		float height = 70f*s;
		VisTextButton button = new VisTextButton(text);
		button.setStyle(new TextButtonStyle(button.getStyle()));
		
		button.getLabelCell().expand(false, false).fill(false, false);
		
		SceneUtils.addIconToButton(button, new VisImage("icon-" + icon), 20*s);
		menutable.top().left().add(button)/*.width(Gdx.graphics.getWidth() / 5 - 3)*/.height(height).growX().padTop(5f * s).align(Align.topLeft);
		return button;
	}
	
	private void addMenu(String name, String icon, MenuButton... buttonlist){
		final ButtonMenu buttons = new ButtonMenu(name);
		buttons.getContentTable().top().left();
		for(MenuButton button : buttonlist){
			buttons.getContentTable().add(button).width(350*s).padTop(10*s).row();
		}
		
		VisTextButton backbutton = new VisTextButton("Back");
		backbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				currentMenu.hide();
			}
		});
		
		SceneUtils.addIconToButton(backbutton, new Image(VisUI.getSkin().getDrawable("icon-arrow-left")), 40*s);
		backbutton.getLabelCell().padRight(40);
		
		buttons.getContentTable().row();
		buttons.getContentTable().add(new Separator()).padTop(10*s).padBottom(5*s).growX().row();
		buttons.getButtonsTable().add(backbutton).height(60*s).width(350*s);
		
		addMenuButton(name, icon).addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				buttons.show(getStage());
				currentMenu = buttons;
			}
		});
	}
	
	private static class MenuButton extends VisTextButton{
		public MenuButton(String name, String desc){
			super(name);
			
			String icon = "icon-" + name.toLowerCase().replace(" ", "");
			
			if(VisUI.getSkin().getAtlas().findRegion(icon) != null){
				SceneUtils.addIconToButton(this, new VisImage(icon), 20*s);
				getCells().first().padRight(4*s);
			}
			
			getLabel().setAlignment(Align.topLeft);
			left();
			
			row(); //this is necessary for a new row... apparently?
			add();
			row();
			
			VisLabel desclabel = new VisLabel(desc);
			desclabel.setColor(Color.GRAY);
			
			add(desclabel).align(Align.topLeft).padTop(15*s).padBottom(5*s);
			
			addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					MenuButton.this.clicked();
					currentMenu.hide();
				}
			});
			
			BaseDialog.addPadding(this);
		}
		
		public void clicked(){
			
		}
	}
	
	private static class ButtonMenu extends BaseDialog{
		public ButtonMenu(String name){
			super(name);
			addTitleSeperator();
		}
	}
	
	private void setupMenu(){
		final Stage stage = main.stage;
		
		VisTextButton menu = addMenuButton("Menu", "menu");
		menu.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				main.openProjectMenu();
			}
		});
		
		addMenu("Image", "image",
		new MenuButton("Resize", "Change the canvas size."){
			public void clicked(){
				new SizeDialog("Resize Canvas"){
					@Override
					public void result(int width, int height){
						
						PixelCanvas canvas = main.canvas();
						PixelCanvas ncanvas = new PixelCanvas(PixmapUtils.resize(canvas.pixmap, width, height));
						main.drawgrid.setCanvas(ncanvas, true);
						
						main.checkGridResize();
						
						main.updateToolColor();
					}
				}.show(stage);
			}
		},
		new MenuButton("Crop", "Cut out a part of the image."){
			public void clicked(){
				new CropDialog().show(stage);
			}
		},
		new MenuButton("Clear", "Clear the image."){
			public void clicked(){
				new ClearDialog().show(stage);
			}
		},
		new MenuButton("Symmetry", "Configure symmetry."){
			public void clicked(){
				new SymmetryDialog().show(stage);
			}
		});
		
		addMenu("Filters", "filter",
		new MenuButton("Colorize", "Configure image hue,\nbrightness and saturation."){
			public void clicked(){
				new ColorizeDialog().show(stage);
			}
		},
		new MenuButton("Invert", "Invert the image color."){
			public void clicked(){
				new InvertDialog().show(stage);
			}
		},
		new MenuButton("Replace", "Replace a color with\nanother color."){
			public void clicked(){
				new ReplaceDialog().show(stage);
			}
		},
		new MenuButton("Contrast", "Change image contrast."){
			public void clicked(){
				new ContrastDialog().show(stage);
			}
		},
		new MenuButton("Outline", "Add an outline around\nthe image."){
			public void clicked(){
				new OutlineDialog().show(stage);
			}
		},
		new MenuButton("Erase Color", "Remove a certain color\nfrom the image."){
			public void clicked(){
				new ColorAlphaDialog().show(stage);
			}
		});
		
		addMenu("Edit", "edit",
		new MenuButton("Flip", "Flip the image."){
			public void clicked(){
				new FlipDialog().show(stage);
			}
		},
		new MenuButton("Rotate", "Rotate the image."){
			public void clicked(){
				new RotateDialog().show(stage);
			}
		},
		new MenuButton("Scale", "Scale the image."){
			public void clicked(){
				new ScaleDialog().show(stage);
			}
		},
		new MenuButton("Shift", "Move the image."){
			public void clicked(){
				new ShiftDialog().show(stage);
			}
		});
		
		addMenu("File", "file",
		/*new MenuButton("Export", "Export the image as a PNG."){
			public void clicked(){
				new FileChooser(FileChooser.pngFilter, false){
					public void fileSelected(FileHandle file){
						DialogClasses.exportPixmap(main.drawgrid.canvas.pixmap, file);
					}
				}.show(stage);
			}
		},*/
		new MenuButton("Export", "Export the image as a PNG."){
			public void clicked(){
				new FileChooser(FileChooser.pngFilter, false){
					public void fileSelected(FileHandle file){
						new ExportDialog(file).show(stage);
					}
				}.show(stage);
				
			}
		},
		new MenuButton("Open", "Load an image file\ninto this project."){
			public void clicked(){
				new FileChooser(FileChooser.jpegFilter, true){
					public void fileSelected(FileHandle file){
						try{
							main.drawgrid.setCanvas(new PixelCanvas(new Pixmap(file)), true);
							tool.onColorChange(main.selectedColor(), main.drawgrid.canvas);
						}catch(Exception e){
							e.printStackTrace();
							DialogClasses.showError(stage, e);
						}
					}
				}.show(stage);
			}
		});
		
		
		
		brushslider = new VisSlider(1, 10, 0.01f, true){
			public float getPrefWidth(){
				return 30*s;
			}
		};
		
		DialogClasses.scaleSlider(brushslider);
		
		brushslider.setValue(main.prefs.getInteger("brushsize", 1));
		final VisLabel brushlabel = new VisLabel("Brush Size: " + brushslider.getValue());

		brushslider.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				brushlabel.setText("Brush Size: [#"+selectcolor +"]" + (int)brushslider.getValue());
				main.prefs.put("brushsize", (int)brushslider.getValue());
				main.drawgrid.brushSize = (int)brushslider.getValue();
			}
		});

		alphabar = new ColorBar(true);
		alphabar.setName("alphabar");
		
		alphabar.setColors(Color.CLEAR.cpy(), Color.WHITE);
		alphabar.setSize(50 * s, 300 * s);
		alphabar.setSelection(main.prefs.getFloat("opacity", 1f));

		optionstable.bottom().left();

		final VisLabel opacity = new VisLabel("Opacity: " + (int)(alphabar.getSelection()*100) + "%");

		alphabar.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				opacity.setText("Opacity: [#"+selectcolor +"]" + (int)(alphabar.getSelection()*100) + "%");
				main.drawgrid.canvas.setAlpha(alphabar.getSelection());
				main.prefs.put("opacity", alphabar.getSelection());
			}
		});


		VisTextButton menubutton = new VisTextButton("Menu");
		menubutton.setName("menubutton");
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

		
		VisImageButtonStyle style = VisUI.getSkin().get("toggle", VisImageButtonStyle.class);
		
		VisImageButtonStyle modestyle = new VisImageButtonStyle(style);
		VisImageButtonStyle gridstyle = new VisImageButtonStyle(style);
		
		modestyle.imageUp = VisUI.getSkin().getDrawable("icon-cursor");
		gridstyle.imageUp = VisUI.getSkin().getDrawable("icon-grid");
		final VisLabel cursorlabel = new VisLabel();
		final VisLabel gridlabel = new VisLabel();
		
		final VisImageButton modebutton = new VisImageButton(modestyle);
		modebutton.setChecked(main.prefs.getBoolean("cursormode", true));
		modebutton.setName("modebutton");
		
		modebutton.getImageCell().size(50*s);
		
		modebutton.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				cursorlabel.setText("Mode: " + (modebutton.isChecked() ? "[CORAL]Cursor" : "[PURPLE]Touch"));
				main.prefs.put("cursormode", modebutton.isChecked());
				main.prefs.save();
			}
		});
		modebutton.fire(new ChangeListener.ChangeEvent());
		
		gridbutton = new VisImageButton(gridstyle);
		gridbutton.setChecked(main.prefs.getBoolean("grid", true));
		gridbutton.setName("gridbutton");
		
		gridbutton.getImageCell().size(50*s);
		
		gridbutton.addListener(new ChangeListener(){
			public void changed(ChangeEvent event, Actor actor){
				gridlabel.setText("Grid: " + (gridbutton.isChecked() ? "[CORAL]On" : "[PURPLE]Off"));
				main.prefs.put("grid", gridbutton.isChecked());
				main.prefs.save();
			}
		});
		gridbutton.fire(new ChangeListener.ChangeEvent());
		
		Table menutable = new VisTable();
		Table othertable = new VisTable();

		optionstable.top().left();
		optionstable.add(menutable).growY();
		optionstable.add(othertable).grow();
		
		menutable.top().left();
		
		menutable.add(cursorlabel).align(Align.topLeft).padTop(12*s).row();;
		menutable.add(modebutton).size(80*s).align(Align.topLeft).padTop(12*s).row();
		
		menutable.add(gridlabel).align(Align.topLeft).padTop(8*s).row();;
		menutable.add(gridbutton).size(80*s).align(Align.topLeft).padTop(12*s).row();
		
		othertable.bottom().right();
		
		othertable.add(brushlabel).padRight(10*s).minWidth(150*s).align(Align.center);
		othertable.add(opacity).minWidth(150*s).align(Align.center);
		othertable.row();
		othertable.add(brushslider).growY().padTop(20*s).padBottom(20*s).padRight(15*s);
		othertable.add(alphabar).padTop(20*s).padBottom(20*s);
	}
	
	public float getBarAlphaValue(){
		return alphabar.selection;
	}

	public VisImageButton getGridButton(){
		return gridbutton;
	}

	@Override
	public float getPrefWidth(){
		return Gdx.graphics.getWidth();
	}
}
