package net.pixelstatic.pixeleditor.tools;

import net.pixelstatic.gdxutils.graphics.ShapeUtils;
import net.pixelstatic.pixeleditor.modules.Core;
import net.pixelstatic.pixeleditor.scene2D.CollapseButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;

public enum TutorialStage{

	colors{
		float arrowlength = 20;

		@Override
		protected void draw(){
			CollapseButton button = Core.i.colorcollapsebutton;
			button.draw(batch, 1f);

			shade(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - button.getHeight());

			
			color(Color.WHITE);
			text(width / 2, height / 2 - 10, "To start,tap or\nswipe down on the top menu.", Align.center);
			
			color(Color.WHITE);
			rect(button.getX(), button.getY(), button.getWidth(), button.getHeight());
			line(width / 2, height - button.getHeight(), width / 2, height / 2);
			line(width / 2, height / 2, width / 2 - arrowlength, height / 2 + arrowlength);
			line(width / 2, height / 2, width / 2 + arrowlength, height / 2 + arrowlength);

			if( !Core.i.colorMenuCollapsed()){
				next();
			}
		}
	},
	palettes{
		@Override
		protected void draw(){
			CollapseButton button = Core.i.colorcollapsebutton;
			VisTextButton palettebutton = (VisTextButton)Core.i.colortable.getChildren().peek();
			
			shade(button.getX(), button.getY(), button.getWidth(), button.getHeight());
			
			shade(0, 0, Gdx.graphics.getWidth(), width / Tool.values().length + 61);
			
			color(Color.WHITE);
			text(width/2, height - 120, "This is this color menu.\nHere you can edit the palette.");
			text(width/2, height - 120, "This is this color menu.\nHere you can edit the palette.");
			
			Core.i.boxes[Core.i.paletteColor].draw(batch, 1f);
		}
	},
	tools{
		@Override
		protected void draw(){
			color(Color.CORAL);
			text(width / 2, height / 2, "wew", Align.center);
		}
	},
	tooloptions{
		@Override
		protected void draw(){
			color(Color.CORAL);
			text(width / 2, height / 2, "wew", Align.center);
		}
	},
	projects{
		@Override
		protected void draw(){
			color(Color.CORAL);
			text(width / 2, height / 2, "wew", Align.center);
		}
	};
	private int shades = 0;
	public boolean next;
	public float trans = 1f;
	static int width, height;
	public static Rectangle cliprect = new Rectangle();
	public static Rectangle cliprect2 = new Rectangle();
	protected Batch batch;

	public final void draw(Batch batch){
		shades = 0;
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		this.batch = batch;
		draw();
	}

	public void color(Color color){
		batch.setColor(color.r, color.g, color.b, color.a*trans);
		VisUI.getSkin().getFont("border-font").setColor(color.r, color.g, color.b, color.a*trans);
	}

	public void color(float r, float g, float b, float a){
		batch.setColor(r, g, b, a*trans);
		VisUI.getSkin().getFont("border-font").setColor(r, g, b, a*trans);
	}
	
	protected void next(){
		next = true;
	}

	public void shade(float x, float y, float width, float height){
		color(0, 0, 0, 0.5f*trans);
		tex(x, y, width, height);
		
		if(shades == 0){
			cliprect.set(x, y, width, height);
		}else{
			cliprect2.set(x, y, width, height);
		}
		shades ++;
	}

	public void tex(float x, float y, float width, float height){
		batch.draw(VisUI.getSkin().getRegion("white"), x, y, width, height);
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

	protected abstract void draw();
}
