package net.pixelstatic.pixeleditor.tools;

import static net.pixelstatic.pixeleditor.modules.Core.s;
import net.pixelstatic.gdxutils.graphics.ShapeUtils;
import net.pixelstatic.pixeleditor.modules.Core;
import net.pixelstatic.pixeleditor.modules.Tutorial;
import net.pixelstatic.pixeleditor.scene2D.CollapseButton;
import net.pixelstatic.pixeleditor.ui.ProjectMenu.ProjectTable;
import net.pixelstatic.utils.scene2D.ColorBar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTextButton;

public enum TutorialStage{

	colormenu{
		float arrowlength = 20*s;

		@Override
		protected void draw(){
			CollapseButton button = Core.i.colorcollapsebutton;
			button.draw(batch, 1f);

			shade(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - button.getHeight());

			color(Color.WHITE);
			text(width / 2, height / 2 - 10, "To start,tap or\nswipe down on the top menu.", Align.center);

			color(select);
			rect(button.getX(), button.getY(), button.getWidth(), button.getHeight());
			line(width / 2, height - button.getHeight(), width / 2, height / 2);
			line(width / 2, height / 2, width / 2 - arrowlength, height / 2 + arrowlength);
			line(width / 2, height / 2, width / 2 + arrowlength, height / 2 + arrowlength);

			if( !Core.i.colorMenuCollapsed()){
				next();
			}
		}
	},
	colors{

		@Override
		protected void draw(){
			CollapseButton button = Core.i.colorcollapsebutton;
			
			shade(button.getX(), button.getY(), button.getWidth(), button.getHeight());
			shade(0, 0, Gdx.graphics.getWidth(), Core.i.toolcollapsebutton.getTop());
			
			Core.i.boxes[Core.i.paletteColor].draw(batch, 1f);
			
			color(Color.WHITE);

			text(width / 2, height - 120*s, "This is this the color menu.\nHere you can edit the color palette.");

			color(Color.PURPLE);
			text(width / 2, height / 2, "<tap to continue>");

			

		}

		public void tap(int x, int y){
			next();
		}
	},
	colors2{
		@Override
		protected void draw(){
			CollapseButton button = Core.i.colorcollapsebutton;
			
			shade(button.getX(), button.getY(), button.getWidth(), button.getHeight());
			shade(0, 0, Gdx.graphics.getWidth(), Core.i.toolcollapsebutton.getTop());

			color(Color.WHITE);

			Core.i.boxes[Core.i.paletteColor].draw(batch, 1f);

			text(width / 2, height - 70*s, "< Tap any color in this bar to edit it. >");

			color(Color.PURPLE);
			text(width / 2, height / 2, "<tap to continue>");
		}

		public void tap(int x, int y){
			next();
		}
	},
	colors3{
		@Override
		protected void draw(){
			CollapseButton button = Core.i.colorcollapsebutton;
			VisTextButton palettebutton = (VisTextButton)Core.i.colorcollapser.getTable().getChildren().peek();

			palettebutton.localToStageCoordinates(temp.set(0, 0));

			shade(button.getX(), button.getY(), button.getWidth(), button.getHeight());
			shade(0, 0, Gdx.graphics.getWidth(), Core.i.toolcollapsebutton.getTop());

			color(Color.WHITE);
			text(width / 2, temp.y + palettebutton.getHeight() * 1.5f, "Tap this button to access your palettes.");

			color(select);
			rect(palettebutton);

			if(palettebutton.getClickListener().getTapCount() > 0){
				next();
			}

			Core.i.boxes[Core.i.paletteColor].draw(batch, 1f);
		}
	},
	palettes{
		@Override
		protected void draw(){
			color(Color.WHITE);
			text(width / 2, height - 90*s, "You can use this menu to resize,\ndelete and add new palettes.");

			color(Color.PURPLE);
			text(width / 2, 120*s, "<tap to continue>");
			//rect(palettebutton);
		}

		public void tap(int x, int y){
			if(Core.i.getCurrentDialog() != null) Core.i.getCurrentDialog().hide();
			Core.i.collapseColorMenu();
			next();
		}
	},
	selectcolors{
		protected void draw(){
			shade(0,0,width,height);
			color(Color.WHITE);
			text(width / 2, height - 55*s, "< Tap any color in this bar to set>\nit as your current color.");
			
			color(Color.PURPLE);
			text(width / 2, 120*s, "<tap to continue>");
		}
		
		public void tap(int x, int y){
			next();
		}
	},
	canvas{
		protected void draw(){
			ColorBar bar = Core.i.stage.getRoot().findActor("alphabar");
			if(!MathUtils.isEqual(bar.getSelection(), 1f)){
				bar.setSelection(1f);
				bar.fire(new ChangeListener.ChangeEvent());
			}
			
			shade(0,0, width, Core.i.toolcollapsebutton.getTop());
			float h = Core.i.colortable.getPrefHeight()+ Core.i.boxes[0].getBorderThickness();
			shade(0,height-h, width, h);
			
			if(Core.i.getCurrentDialog() != null){
				Core.i.getCurrentDialog().hide();
			}
			
			if(!Core.i.colorMenuCollapsed()) Core.i.collapseColorMenu();
			if(!Core.i.toolMenuCollapsed()) Core.i.collapseToolMenu();
			
			Core.i.drawgrid.zoom = 1f;
			
			color(Color.WHITE);
			text(width / 2, height/2, "This is the canvas.");
			
			color(Color.PURPLE);
			text(width / 2, height / 2-80*s, "<tap to continue>");
		}
		
		public void tap(int x, int y){
			next();
		}
	},
	canvasmodes{
		protected void draw(){
			shade(0,0, width, Core.i.toolcollapsebutton.getTop());
			float h = Core.i.colortable.getPrefHeight()+ Core.i.boxes[0].getBorderThickness();
			shade(0,height-h, width, h);
			
			color(Color.WHITE);
			text(width / 2, height/2+80*s, "There are two drawing modes:\n[CORAL]cursor[] and [PURPLE]touch.");
			
			color(Color.PURPLE);
			text(width / 2, height/2-80*s, "<tap to continue>");
		}
		
		public void tap(int x, int y){
			next();
		}
	},
	canvascursormode{
		
		protected void draw(){
			VisImageButton button = Core.i.stage.getRoot().findActor("modebutton");
			if(!button.isChecked()){
				((ClickListener)button.getListeners().get(0)).clicked(null,0,0);
			}
			
			shade(0,0, width, Core.i.toolcollapsebutton.getTop());
			float h = Core.i.colortable.getPrefHeight()+ Core.i.boxes[0].getBorderThickness();
			shade(0,height-h, width, h);
			
			color(Color.WHITE);
			text(width/2, height-15, "The [CORAL]cursor mode[] works like this:\n"
					+ "Use one finger to [PURPLE]move the cursor[]\n"
					+ "and hold another finger anywhere on\nthe screen to[PURPLE] draw[].\n"
					+ "[GREEN]Try it out.");
			
			color(Color.PURPLE);
			text(width / 2, 70*s, "<tap here to continue>");
		}
		
		public void tap(int x, int y){
			if(y < 132*s) next();
		}
	},
	canvastouchmode{
		
		protected void draw(){
			VisImageButton button = Core.i.stage.getRoot().findActor("modebutton");
			if(button.isChecked()){
				((ClickListener)button.getListeners().get(0)).clicked(null,0,0);
			}
			
			shade(0,0, width, Core.i.toolcollapsebutton.getTop());
			float h = Core.i.colortable.getPrefHeight()+ Core.i.boxes[0].getBorderThickness();
			shade(0,height-h, width, h);
			
			color(Color.WHITE);
			text(width/2, height-50*s, "The [PURPLE]touch mode[] is simple:\n"
					+ "just touch the place where you\nwant to draw.\n"
					+ "[GREEN]Try it out.");
			
			color(Color.PURPLE);
			text(width / 2, 70*s, "<tap here to continue>");
		}
		
		public void tap(int x, int y){
			if(y < 132*s) next();
		}
	},
	tools{
		Tool selected = null;

		@Override
		protected void draw(){
			if(Core.i.toolMenuCollapsed()){
				Tool.pencil.button.setChecked(false);
				Core.i.collapseToolMenu();
			}

			float f = (float)width / Tool.values().length;
			shade(0, f, width, height-f);

			color(Color.WHITE);
			text(width / 2, height / 2 + 20*s, "These are the drawing tools you can use.\nTap one of the icons to see what it does.");

			if(selected != null){
				color(select);
				text(width / 2, height / 2 - 80*s, "This is the " + selected.name() + " tool.");
				rect(selected.ordinal() * (f + 0.5f), 0, f + 1, f + 1);
			}

			color(Color.PURPLE);
			text(width / 2, height / 2 + 180*s, "[tap to continue]");

		}

		public void tap(int x, int y){
			if(selected != null) selected.button.setChecked(false);
			if(y < width / Tool.values().length){
				selected = Tool.values()[x / (width / Tool.values().length)];
				selected.button.setChecked(false);
			}

			if(y > height / 2 + 40*s){
				Core.i.tool.button.setChecked(true);
				next();
			}
		}
	},
	tooloptions{
		@Override
		protected void draw(){

			float f = Core.i.toolcollapsebutton.getTop();
			shade(0, 0, width, f);
			shade(0, f + Core.i.toolcollapser.getDone() * Core.i.toolmenu.getPrefHeight() - 78*s, width, height - f);
			
			Actor modebutton = Core.i.stage.getRoot().findActor("modebutton");
			Actor gridbutton = Core.i.stage.getRoot().findActor("gridbutton");
			modebutton.localToStageCoordinates(temp.set(0,0));

			color(Color.WHITE);
			
			text(temp.x + modebutton.getWidth() + 5*s, temp.y + modebutton.getHeight(), "Use this button\nto change the [CORAL]draw\nmode.", Align.left);
			
			gridbutton.localToStageCoordinates(temp.set(0,0));
			
			text(temp.x + modebutton.getWidth() + 5*s, temp.y + modebutton.getHeight(), "Use this button\nto toggle the [GREEN]grid[].", Align.left);

			color(Color.PURPLE);
			text(width / 2, height / 2 + 180*s, "[tap to continue]");
		}

		public void tap(int x, int y){
			next();
		}
	},
	toolmenu{
		protected void draw(){
			if(Core.i.toolMenuCollapsed()) Core.i.collapseToolMenu();
			VisTextButton button = (VisTextButton)((Table)Core.i.toolmenu.getChildren().first()).getChildren().first();
			float f = width / Tool.values().length + 61*s;
			
			color(select);
			rect(button);

			shade(0, 0, width, f + Core.i.toolmenu.getPrefHeight() - 77*s);
			shade(0, f + Core.i.toolmenu.getPrefHeight(), width, height - f);
			
			shade(button.getWidth() + 4*s, f + Core.i.toolmenu.getPrefHeight() - 77*s, width, button.getHeight() + 7*s);

			color(select);
			rect(button);

			color(Color.WHITE);
			text(width / 2, height / 2 + 220*s, "Press this button to\ncontinue to the menu.");

			if(Core.i.projectmenu.getStage() != null){
				next();
			}
		}
	},
	projectmenu{
		//TODO MISALIGNED
		protected void draw(){

			shade(0, 0, width, height);
			//shade(0, height-120, width,120);
			color(Color.WHITE);
			text(width / 2, height - 20*s, "This is the project menu.\nYou can use it to easily store\nand switch canvases.\n\n[GOLD]Projects are saved [GREEN]automatically[ROYAL], so\nyou don't have to worry\nabout losing your work.");

			color(Color.PURPLE);
			text(width / 2, height / 2, "<tap to continue>");

		}

		public void tap(int x, int y){
			next();
		}
	},
	projectsettings{
		String[] names = {"open", "copy", "rename", "delete"};
		int stage = 0;
		protected void draw(){
			VisScrollPane pane = ((VisScrollPane)Core.i.stage.getRoot().findActor("projectpane"));
			pane.setSmoothScrolling(false);
			pane.setScrollPercentY(0);
			
			ProjectTable table = Core.i.projectmenu.getFirstTable();
			project(table);

			shade(0, 0, width, temp.y);
			clearshade(0, 0, width, height);
			shade(0, (temp.y + table.getHeight()), width, height -(temp.y + table.getHeight()));

			color(Color.WHITE);
			
			VisImageButton button = pane.findActor(Core.i.projectmanager.getProjects().iterator().next().name+names[stage]+"button");
			
			text(width / 2, height/2+50*s, "Use this button to " + names[stage]+ " a project.");
			color(select);
			rectarrow(button);
			
			color(Color.PURPLE);
			text(width / 2, height / 2, "<tap to continue>");
		}
		
		public void tap(int x, int y){
			stage ++;
			if(stage >= names.length){
				stage = names.length -1;
				next();
			}
		}

		public void end(){
			(((VisScrollPane)Core.i.getCurrentDialog().getContentTable().findActor("projectpane"))).setSmoothScrolling(true);
		}
	},
	newproject{
		protected void draw(){
			VisTextButton button = Core.i.stage.getRoot().findActor("newproject");
			
			button.localToStageCoordinates(temp.set(0,0));
			temp.y -= 6;
			
			shade(0, 0, width, temp.y);
			
			clearshade(0, temp.y, width, height - temp.y);
			
			color(Color.WHITE);
			text(width/2, height/2 + 180*s, "This button allows you\nto create a new project.\nYou can either specify the project\nsize, or load an image file.");
			
			color(select);
			rectarrow(button);
			
			color(Color.PURPLE);
			text(width / 2, height / 2-40*s, "<tap to continue>");
		}
		
		public void tap(int x, int y){
			next();
		}
	},
	settings{
		protected void draw(){
			VisTextButton button = Core.i.stage.getRoot().findActor("settings");
			
			button.localToStageCoordinates(temp.set(0,0));
			temp.y -= 6;
			
			shade(0, 0, width, temp.y);
			
			clearshade(0, temp.y, width, height - temp.y);
			
			color(Color.WHITE);
			text(width/2, height/2 + 180*s, "This button opens up the settings.\nThere, you can customize\ncursor speed, size, and so on.");
			
			color(select);
			rectarrow(button);
			
			color(Color.PURPLE);
			text(width / 2, height / 2-40*s, "<tap to continue>");
		}
		
		public void tap(int x, int y){
			next();
		}
	}, 
	tutorialend{
		protected void draw(){
			shade(0,0,width,height);
			
			color(Color.WHITE);
			text(width/2, height/2, "And that concludes the tutorial!\nIf you'd like to re-take it,\nyou can do so in the\n[GREEN]setting menu.");
		}
		
		public void tap(int x, int y){
			((ClickListener)Core.i.stage.getRoot().findActor("modebutton").getListeners().get(0)).clicked(null,0,0);
			if(Core.i.getCurrentDialog() != null) Core.i.getCurrentDialog().hide();
			Core.i.collapseToolMenu();
			Core.i.getModule(Tutorial.class).end();
		}
	};
	public static final Color select = Color.CORAL;
	protected Vector2 temp = new Vector2();
	private int shades = 0;
	public boolean next;
	public float trans = 1f;
	static int width, height;
	public static Rectangle[] cliprects = new Rectangle[]{new Rectangle(), new Rectangle(), new Rectangle()};
	protected Batch batch;

	public final void draw(Batch batch){
		shades = 0;
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		this.batch = batch;
		draw();
		
		color(select);
		text(width - 30*s, 30*s, "Exit");
	}

	public void color(Color color){
		color(color.r, color.g, color.b, color.a);
	}

	public void color(float r, float g, float b, float a){
		batch.setColor(r, g, b, a * trans);
		VisUI.getSkin().getFont("border-font").setColor(r, g, b, a);
	}

	protected void next(){
		next = true;
	}

	public void shade(float x, float y, float width, float height){
		color(0, 0, 0, 0.7f * trans);
		tex(x, y, width, height);
		clearshade(x, y, width, height);
	}
	
	public void clearshade(float x, float y, float width, float height){
		cliprects[shades].set(x, y, width, height);
		shades ++;
	}
	
	public void project(Actor actor){
		actor.localToStageCoordinates(temp.set(0,0));
	}

	public void tex(float x, float y, float width, float height){
		batch.draw(VisUI.getSkin().getRegion("white"), x, y, width, height);
	}

	public void rect(Actor actor){
		actor.localToStageCoordinates(temp.set(0, 0));
		rect(temp.x, temp.y, actor.getWidth(), actor.getHeight());
	}
	
	public void rectarrow(Actor actor){
		rect(actor);
		
		arrow(temp.x + actor.getWidth()/2, temp.y - 80, 60, true);
	}
	
	public void arrow(float x, float y, float length, boolean up){
		float l = 20;
		line(x,y, x, y + length);
		if(up){
			line(x,y + length, x + l, y - l + length);
			line(x,y + length, x - l, y - l + length);
		}else{
			line(x,y, x + l, y + l);
			line(x,y, x - l, y + l);
		}
	}

	public void rect(float x, float y, float width, float height){
		ShapeUtils.rect(batch, x, y, width, height, 4);
	}

	public void line(float x, float y, float x2, float y2){
		ShapeUtils.line(batch, x, y, x2, y2);
	}

	public void text(float x, float y, String text, int align){
		VisUI.getSkin().getFont("border-font").getCache().clear();
		VisUI.getSkin().getFont("border-font").getCache().addText(text, x, y, 0, align, false);
		VisUI.getSkin().getFont("border-font").getCache().setAlphas(Core.i.stage.getBatch().getColor().a);
		VisUI.getSkin().getFont("border-font").getCache().draw(batch);
	}

	public void text(float x, float y, String text){
		text(x, y, text, Align.center);
	}

	public void tap(int x, int y){

	}


	public void end(){

	}

	protected abstract void draw();
}
