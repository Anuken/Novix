package net.pixelstatic.pixeleditor.ui;

import static net.pixelstatic.pixeleditor.modules.Main.s;

import java.util.Arrays;

import net.pixelstatic.gdxutils.graphics.Textures;
import net.pixelstatic.pixeleditor.graphics.Palette;
import net.pixelstatic.pixeleditor.modules.Main;
import net.pixelstatic.pixeleditor.scene2D.DialogClasses;
import net.pixelstatic.pixeleditor.scene2D.PaletteWidget;
import net.pixelstatic.pixeleditor.scene2D.TallMenuItem;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.MiscUtils.TextFieldEmptyListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;

public class PaletteMenu extends VisDialog{
	private Main main;
	
	public PaletteMenu(Main main){
		super("Palettes", "dialog");
		this.main = main;
		setMovable(false);
		getTitleLabel().setColor(Color.CORAL);
		MiscUtils.addHideButton(this);
		setStage(main.stage);
	}
	
	public void update(){
		float scrolly = getContentTable().getChildren().size == 0 ? 0 : ((ScrollPane)getContentTable().getChildren().first()).getScrollPercentY();

		getContentTable().clearChildren();

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

								update();
								main.updateColorMenu();
							}
						}.show(getStage());
					}
				}));
				menu.addItem(new TallMenuItem("rename", new ChangeListener(){
					public void changed(ChangeEvent event, Actor actor){
						new DialogClasses.InputDialog("Rename Palette", palette.name, "Name: "){
							public void result(String string){
								main.palettemanager.removePalette(palette);
								palette.name = string;
								main.palettemanager.addPalette(palette);
								if(palette == main.getCurrentPalette()){
									main.prefs.putString("currentpalette", palette.name);
									main.prefs.flush();
								}
								update();
							}
						}.show(getStage());
					}
				}));
				menu.addItem(new TallMenuItem("delete", new ChangeListener(){
					public void changed(ChangeEvent event, Actor actor){
						new DialogClasses.ConfirmDialog("Delete Palette", "Are you sure you want\nto delete this palette?"){
							public void result(){
								main.palettemanager.removePalette(palette);
								update();
							}
						}.show(getStage());
					}
				}));

				Vector2 coords = widget.extrabutton.localToStageCoordinates(new Vector2());
				menu.showMenu(getStage(), coords.x - menu.getWidth() + widget.extrabutton.getWidth(), coords.y);
			}
		}

		VisTable palettetable = new VisTable();

		final VisScrollPane pane = new VisScrollPane(palettetable);
		pane.setFadeScrollBars(false);
		pane.setOverscroll(false, false);

		getContentTable().add(pane).left().grow().maxHeight(Gdx.graphics.getHeight() / 2);

		for(final Palette palette : main.palettemanager.getPalettes()){
			final PaletteWidget widget = new PaletteWidget(palette, palette == main.getCurrentPalette());

			widget.setTouchable(palette == main.getCurrentPalette() ? Touchable.childrenOnly : Touchable.enabled);

			widget.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					if( !widget.extrabutton.isOver()) main.setPalette(palette);
				}
			});

			widget.addExtraButtonListener(new PaletteListener(widget));

			palettetable.add(widget).padBottom(6);
			palettetable.row();
		}

		VisTextButton backbutton = new VisTextButton("Back");

		backbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				hide();
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
						main.palettemanager.addPalette(new Palette(string, Integer.parseInt(numberfield.getText())));
						update();
					}

				}.show(getStage());
			}
		});

		MiscUtils.addIconToButton(addpalettebutton, new Image(Textures.get("icon-plus")), 40);
		MiscUtils.addIconToButton(backbutton, new Image(Textures.get("icon-arrow-left")), 40);

		getButtonsTable().add(backbutton).size(150 * s, 50 * s);
		getButtonsTable().add(addpalettebutton).size(200 * s, 50 * s);

		pack();

		getStage().setScrollFocus(pane);

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
	
	public VisDialog show(Stage stage){
		super.show(stage);
		stage.setScrollFocus(getContentTable().getChildren().first());
		return this;
	}
}
