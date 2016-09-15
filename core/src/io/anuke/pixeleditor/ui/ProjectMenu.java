package io.anuke.pixeleditor.ui;

import static io.anuke.pixeleditor.modules.Core.s;
import io.anuke.pixeleditor.modules.Core;
import io.anuke.pixeleditor.scene2D.*;
import io.anuke.pixeleditor.scene2D.DialogClasses.BaseDialog;
import io.anuke.pixeleditor.scene2D.DialogClasses.OpenProjectFileDialog;
import io.anuke.pixeleditor.tools.Project;
import net.pixelstatic.gdxutils.graphics.Hue;
import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.scene2D.AnimatedImage;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

public class ProjectMenu extends BaseDialog{
	private Core main;
	private VisScrollPane pane;
	
	public ProjectMenu(Core mainref){
		super("Projects");
		this.main = mainref;
		
		addTitleSeperator().padBottom(10*s);
		padTop(getPadTop()+10*s);
		
		VisTable scrolltable = new VisTable();

		pane = new VisScrollPane(scrolltable){
			public float getPrefHeight(){
				return Gdx.graphics.getHeight();
			}
		};
		pane.setName("projectpane");
		pane.setFadeScrollBars(false);
		pane.setOverscroll(false, false);
		
		VisTable newtable = new VisTable();
		
		final float newbuttonwidth = 190*s;
		
		final VisTextButton newbutton = new VisTextButton("New Project");
		
		final PopupMenu popup = new PopupMenu(){
			public float getPrefWidth(){
				return newbuttonwidth;
			}
		};
		popup.addItem(new TallMenuItem("New...", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				main.projectmanager.newProject();
			}
		}){
			public float getPrefWidth(){
				return newbuttonwidth;
			}
		});
		popup.addItem(new TallMenuItem("From File..", new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				new OpenProjectFileDialog().show(main.stage);
			}
		}){
			public float getPrefWidth(){
				return newbuttonwidth;
			}
		});
		
		newbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				popup.showMenu(main.stage, newbutton);
			}
		});
		
		newbutton.setName("newproject");

		MiscUtils.addIconToButton(newbutton, new Image(VisUI.getSkin().getDrawable("icon-plus")), 40 * s);
		
		VisTextButton settingsbutton = new VisTextButton("Settings");
		settingsbutton.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				main.openSettingsMenu();
			}
		});
		settingsbutton.setName("settings");

		newtable.left().add(newbutton).padBottom(6 * s).size(newbuttonwidth, 60 * s);
		newtable.left().add().grow();
		newtable.left().add(settingsbutton).padBottom(6 * s).size(120 * s, 60 * s).align(Align.topRight);

		getContentTable().add(newtable).grow().row();

		getContentTable().top().left().add(pane).align(Align.topLeft).grow();

		VisTextButton projectback = new VisTextButton("Back");
		projectback.add(new Image(VisUI.getSkin().getDrawable("icon-arrow-left"))).size(40 * s).center();

		projectback.getCells().reverse();
		projectback.getLabelCell().padRight(40f * s);

		getButtonsTable().add(projectback).width(Gdx.graphics.getWidth() - getPadLeft() - getPadRight()).height(60 * s);
		setObject(projectback, false);
	}
	
	public ProjectTable update(boolean loaded){
		VisTable scrolltable = ((VisTable)((VisScrollPane)getContentTable().getCells().get(1).getActor()).getChildren().first());

		scrolltable.clearChildren();

		ProjectTable current = null;
		for(Project project : main.projectmanager.getProjects()){
			ProjectTable table = new ProjectTable(project, project == main.getCurrentProject() ? loaded : true);
			scrolltable.top().left().add(table).padTop(8*s).growX().padRight(10 * s).row();
			
			if(project == main.getCurrentProject()) current = table;
		}
		return current;
	}
	
	public ProjectTable getFirstTable(){
		VisTable scrolltable = ((VisTable)((VisScrollPane)getContentTable().getCells().get(1).getActor()).getChildren().first());
		return (ProjectTable)scrolltable.getCells().first().getActor();
	}
	
	public VisDialog show(Stage stage){
		super.show(stage);
		stage.setScrollFocus(pane);
		int i = Gdx.app.getType() == ApplicationType.Desktop ? 0 : 1;
		setSize(stage.getWidth(), stage.getHeight()-i);
		setY(i);
		return this;
	}
	
	public class ProjectTable extends VisTable{
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

			float imagesize = 40*s;

			VisImageButton openbutton = new VisImageButton(VisUI.getSkin().getDrawable("icon-open"));
			openbutton.setGenerateDisabledImage(true);
			VisImageButton copybutton = new VisImageButton(VisUI.getSkin().getDrawable("icon-copy"));
			VisImageButton renamebutton = new VisImageButton(VisUI.getSkin().getDrawable("icon-rename"));
			VisImageButton deletebutton = new VisImageButton(VisUI.getSkin().getDrawable("icon-trash"));
			
			openbutton.setName(project.name + "openbutton");
			copybutton.setName(project.name + "copybutton");
			renamebutton.setName(project.name + "renamebutton");
			deletebutton.setName(project.name + "deletebutton");

			if(project == main.getCurrentProject()){
				openbutton.setDisabled(true);
				openbutton.setColor(Hue.lightness(0.94f));
			}

			openbutton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					if(project != main.getCurrentProject()) main.projectmanager.openProject(project);
				}
			});

			copybutton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					main.projectmanager.copyProject(project);
				}
			});

			renamebutton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					main.projectmanager.renameProject(project);
				}
			});

			deletebutton.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					main.projectmanager.deleteProject(project);
				}
			});

			openbutton.getImageCell().size(imagesize);
			copybutton.getImageCell().size(imagesize);
			renamebutton.getImageCell().size(imagesize);
			deletebutton.getImageCell().size(imagesize);

			VisTable texttable = new VisTable();
			VisTable buttontable = new VisTable();

			float bheight = 50*s, space = 4*s, pad = 3*s;

			buttontable.bottom().left().add(openbutton).align(Align.bottomLeft).height(bheight).growX().space(space).padBottom(pad);
			buttontable.add(copybutton).height(bheight).growX().space(space).padBottom(pad);
			buttontable.add(renamebutton).height(bheight).growX().space(space).padBottom(pad);
			buttontable.add(deletebutton).height(bheight).growX().space(space).padBottom(pad);
			

			top().left();

			background("button");
			setColor(Hue.lightness(0.87f));

			imagecell = stack(new AnimatedImage(VisUI.getSkin().getDrawable("icon-load-1"), VisUI.getSkin().getDrawable("icon-load-2"), VisUI.getSkin().getDrawable("icon-load-3")), new BorderImage());
			imagecell.padTop(imagecell.getPadTop() + 4).padBottom(imagecell.getPadBottom() + 4);

			MiscUtils.fitCell(imagecell, 128 * s, 1);

			add(texttable).grow();
			texttable.top().left().add(namelabel).padLeft(8*s).align(Align.topLeft);
			texttable.row();
			texttable.add(sizelabel).padLeft(8*s).padTop(10 * s).align(Align.topLeft);
			texttable.row();
			texttable.add(buttontable).grow().padLeft(8);
			
			
			setName("projecttable" + project.name);
			addAction(new Action(){

				public boolean act(float delta){
					if(created) return true;
					if( !loaded) return false;

					if(project == main.getCurrentProject()) project.reloadTexture();

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
			BaseDialog.addPadding(this);
		}

	}
}
