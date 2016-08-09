package net.pixelstatic.pixeleditor.tools;

import net.pixelstatic.gdxutils.graphics.ShapeUtils;
import net.pixelstatic.pixeleditor.modules.Core;
import net.pixelstatic.pixeleditor.scene2D.CollapseButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;

public enum TutorialStage{

	colormenu{
		float arrowlength = 20;

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
			shade(0, 0, Gdx.graphics.getWidth(), width / Tool.values().length + 61 + 80);

			color(Color.WHITE);

			text(width / 2, height - 120, "This is this color menu.\nHere you can edit the color palette.");

			color(Color.PURPLE);
			text(width / 2, height / 2, "<tap to continue>");
			
			Core.i.boxes[Core.i.paletteColor].draw(batch, 1f);

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
			shade(0, 0, Gdx.graphics.getWidth(), width / Tool.values().length + 61 + 80);
			
			color(Color.WHITE);
			
			Core.i.boxes[Core.i.paletteColor].draw(batch, 1f);
			
			text(width / 2, height - 70, "< Tap any color in this bar to edit it. >");
			
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
			shade(0, 0, Gdx.graphics.getWidth(), width / Tool.values().length + 61);
			
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
			text(width / 2, height-90, "You can use this menu to resize,\ndelete and add new palettes.");
			
			color(Color.PURPLE);
			text(width / 2, 120, "<tap to continue>");
			//rect(palettebutton);
		}
		
		public void tap(int x, int y){
			if(Core.i.getCurrentDialog() != null) Core.i.getCurrentDialog().hide();
			next();
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
			shade(0, f+1, width, height - f);
			
			color(Color.WHITE);
			text(width / 2, height/2+20, "These are the drawing tools you can use.\nTap one of the icons to see what it does.");
			
			if(selected != null){
				color(select);
				text(width / 2, height/2-80, "This is the " + selected.name() + " tool.");
				rect(selected.ordinal()*(f+0.5f), 0, f+1, f+1);
			}
			
			color(Color.PURPLE);
			text(width / 2, height/2+180, "[tap to continue]");
			
		}
		
		public void tap(int x, int y){
			if(selected != null)selected.button.setChecked(false);
			if(y < width / Tool.values().length){
				selected = Tool.values()[x / (width / Tool.values().length)];
				selected.button.setChecked(false);
			}
			
			if(y > height/2+40){
				Core.i.tool.button.setChecked(true);
				next();
			}
		}
	}, 
	tooloptions{
		@Override
		protected void draw(){
			
			float f= width / Tool.values().length + 61;
			shade(0, 0, width, f);
			shade(0, f + Core.i.toolcollapser.getDone()*Core.i.toolmenu.getPrefHeight() - 77, width, height - f);
			
			color(Color.WHITE);
			text(110,310, "Use this button\nto toggle the grid.", Align.left);
			text(110,450, "Use this button\nto change the draw\nmode.", Align.left);
			
			color(Color.PURPLE);
			text(width/2, height/2+180, "[tap to continue]");
		}
		
		public void tap(int x, int y){
			next();
		}
	}, 
	toolmenu{
		protected void draw(){
			if(Core.i.toolMenuCollapsed()) Core.i.collapseToolMenu();
			VisTextButton button = (VisTextButton)((Table)Core.i.toolmenu.getChildren().first()).getChildren().first();
			float f = width / Tool.values().length + 61;
			
			shade(0, 0, width, f+Core.i.toolmenu.getPrefHeight() - 77);
			shade(0, f + Core.i.toolmenu.getPrefHeight(), width, height - f);
			shade(button.getWidth() + 4, f + Core.i.toolmenu.getPrefHeight() - 77, width, button.getHeight()+7);
			
			
			
			color(select);
			rect(button);
			
			color(Color.WHITE);
			text(width/2, height/2+220, "You can use this toolbar to\nmanipulate the image. Press the\nmenu button to continue.");
			
			if(button.getClickListener().getTapCount() > 0){
				next();
			}
		}
	}, 
	projectmenu{
		protected void draw(){
			
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
	}

	public void color(Color color){
		batch.setColor(color.r, color.g, color.b, color.a * trans);
		VisUI.getSkin().getFont("border-font").setColor(color.r, color.g, color.b, color.a * trans);
	}

	public void color(float r, float g, float b, float a){
		batch.setColor(r, g, b, a * trans);
		VisUI.getSkin().getFont("border-font").setColor(r, g, b, a * trans);
	}

	protected void next(){
		next = true;
	}

	public void shade(float x, float y, float width, float height){
		color(0, 0, 0, 0.6f * trans);
		tex(x, y, width, height);
		
		cliprects[shades].set(x, y, width, height);
		shades ++;
	}

	public void tex(float x, float y, float width, float height){
		batch.draw(VisUI.getSkin().getRegion("white"), x, y, width, height);
	}

	public void rect(Actor actor){
		actor.localToStageCoordinates(temp.set(0, 0));
		rect(temp.x, temp.y, actor.getWidth(), actor.getHeight());
	}

	public void rect(float x, float y, float width, float height){
		ShapeUtils.rect(batch, x, y, width, height, 4);
	}

	public void line(float x, float y, float x2, float y2){
		ShapeUtils.line(batch, x, y, x2, y2);
	}

	public void text(float x, float y, String text, int align){
		VisUI.getSkin().getFont("border-font").draw(batch, text, x, y, 0, align, false);
	}

	public void text(float x, float y, String text){
		text(x, y, text, Align.center);
	}

	public void tap(int x, int y){

	}

	protected abstract void draw();
}
