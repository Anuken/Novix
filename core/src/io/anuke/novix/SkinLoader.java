package io.anuke.novix;

import static io.anuke.ucore.UCore.s;

import java.lang.reflect.Field;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.kotcrab.vis.ui.VisUI;

import io.anuke.novix.modules.Core;

public class SkinLoader{
	
	public static Skin load(){
		FileHandle skinFile = Gdx.files.internal("ui/uiskin.json");
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
		// Color shadowcolor = new Color(0, 0, 0, 0.6f);
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/smooth.ttf"));

		FreeTypeFontParameter normalparameter = new FreeTypeFontParameter();
		normalparameter.size = (int) (22 * s);

		FreeTypeFontParameter largeparameter = new FreeTypeFontParameter();
		largeparameter.size = (int) (26 * s);

		FreeTypeFontParameter borderparameter = new FreeTypeFontParameter();
		borderparameter.size = (int) (26 * s);
		borderparameter.borderWidth = 2 * s;
		borderparameter.borderColor = Core.clearcolor;
		borderparameter.spaceX = -2;

		BitmapFont font = generator.generateFont(normalparameter);
		font.getData().markupEnabled = true;
		BitmapFont largefont = generator.generateFont(largeparameter);
		BitmapFont borderfont = generator.generateFont(borderparameter);
		borderfont.getData().markupEnabled = true;

		skin.add("default-font", font);
		skin.add("large-font", largefont);
		skin.add("border-font", borderfont);

		skin.load(skinFile);

		VisUI.load(skin);
		skin.get(Window.WindowStyle.class).titleFont = largefont;
		skin.get(Window.WindowStyle.class).titleFontColor = Color.CORAL;

		skin.get("dialog", Window.WindowStyle.class).titleFont = largefont;
		skin.get("dialog", Window.WindowStyle.class).titleFontColor = Color.CORAL;

		generator.dispose();
		
		return skin;
	}
}
