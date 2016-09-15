package io.anuke.pixeleditor.scene2D;

import io.anuke.pixeleditor.scene2D.DialogClasses.BaseDialog;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

import net.pixelstatic.utils.MiscUtils;
import net.pixelstatic.utils.scene2D.TextFieldDialogListener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

public class FileChooser extends BaseDialog{
	private static float s = MiscUtils.densityScale();
	private Table files;
	private FileHandle homeDirectory = directory = MiscUtils.getHomeDirectory();//Gdx.app.getType() == ApplicationType.Desktop ? Gdx.files.absolute(System.getProperty("user.home")) : Gdx.files.absolute(Environment.getExternalStorageDirectory().getAbsolutePath());
	private FileHandle directory = homeDirectory;
	private VisScrollPane pane;
	private VisTextField navigation, filefield;
	private VisTextButton ok;
	private FileHistory stack = new FileHistory();
	private FileHandleFilter filter;
	private boolean open;
	
	public FileChooser(boolean open){
		this(defaultFilter, open);
	}

	public FileChooser(FileHandleFilter filter, boolean open){
		super("Choose File");
		this.open = open;
		this.filter = filter;
		setMovable(false);
		addCloseButton();
		setupWidgets();
	}

	private void setupWidgets(){
		filefield = new VisTextField();
		if(!open)TextFieldDialogListener.add(filefield);
		filefield.setDisabled(open);

		ok = new VisTextButton(open ? "Open" : "Save");
		ok.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				if(ok.isDisabled()) return;
				fileSelected(directory.child(filefield.getText()));
				close();
			}
		});
		
		filefield.addListener(new ChangeListener(){
			@Override
			public void changed(ChangeEvent event, Actor actor){
				ok.setDisabled(filefield.getText().replace(" ", "").isEmpty());
			}
		});
		
		filefield.fire(new ChangeListener.ChangeEvent());
		
		VisTextButton cancel = new VisTextButton("Cancel");
		cancel.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				close();
			}
		});

		navigation = new VisTextField("");
		navigation.setTouchable(Touchable.disabled);

		files = new VisTable();

		pane = new VisScrollPane(files);
		pane.setOverscroll(false, false);
		pane.setFadeScrollBars(false);

		updateFiles(true);

		VisTable icontable = new VisTable();

		VisImageButton up = new VisImageButton(VisUI.getSkin().getDrawable("icon-folder-parent"));
		up.getImageCell().size(40*s);
		up.addListener(new UpListener());

		VisImageButton back = new VisImageButton(VisUI.getSkin().getDrawable("icon-arrow-left"));
		back.getImageCell().size(40*s);
		back.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				stack.back();
			}
		});
		VisImageButton forward = new VisImageButton(VisUI.getSkin().getDrawable("icon-arrow-right"));
		forward.getImageCell().size(40*s);
		forward.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				stack.forward();
			}
		});

		VisImageButton home = new VisImageButton(VisUI.getSkin().getDrawable("icon-home"));
		home.getImageCell().size(40*s);
		home.addListener(new ClickListener(){
			public void clicked(InputEvent event, float x, float y){
				directory = homeDirectory;
				updateFiles(true);
			}
		});

		//VisImageButton newfolder = new VisImageButton(VisUI.getSkin().getDrawable("icon-folder-new"));

		Table table = getContentTable();
		//	table.setDebug(true, true);

		table.top().left();
		table.add(icontable).expandX().fillX();

		icontable.add(up).expandX().fillX().height(60*s).padBottom(10f*s).uniform();
		icontable.add(back).expandX().fillX().height(60*s).padBottom(10f*s).uniform();
		icontable.add(forward).expandX().fillX().height(60*s).padBottom(10f*s).uniform();
		icontable.add(home).expandX().fillX().height(60*s).padBottom(10f*s).uniform();
		//icontable.add(newfolder).expandX().fillX().height(60).padBottom(10f).uniform();

		table.row();
		table.add(navigation).colspan(3).left().padBottom(10f*s).expandX().fillX().height(40f*s);
		table.row();

		table.center().add(pane).colspan(3).expand().fill();
		table.row();

		Table fieldtable = new VisTable();
		table.bottom().left().add(fieldtable).colspan(3).expand().fill();

		fieldtable.bottom().left().add(new VisLabel("File Name:")).padTop(20);
		fieldtable.add(filefield).padTop(20*s).height(50f*s).fillX().expandX().padLeft(10f*s).padRight(10f*s);
		table.row();

		getButtonsTable().add(cancel).size(Gdx.graphics.getWidth() / 2f, 50f*s).expandX().fillX().padBottom(10*s).padTop(10f*s);
		getButtonsTable().add(ok).expandX().fillX().size(Gdx.graphics.getWidth() / 2f, 50f*s).padBottom(10*s).padTop(10f*s);

	}
	
	private void updateFileFieldStatus(){
		if(!open){
			ok.setDisabled(filefield.getText().replace(" ", "").isEmpty());
		}else{
			ok.setDisabled(!directory.child(filefield.getText()).exists() || directory.child(filefield.getText()).isDirectory());
		}
	}

	private class UpListener extends ClickListener{
		public void clicked(InputEvent event, float x, float y){
			directory = directory.parent();
			updateFiles(true);
		}
	}

	private FileHandle[] getFileNames(){
		FileHandle[] handles = directory.list(new FileFilter(){
			@Override
			public boolean accept(File file){
				return !file.getName().startsWith(".");
			}
		});

		Arrays.sort(handles, new Comparator<FileHandle>(){
			@Override
			public int compare(FileHandle a, FileHandle b){
				if(a.isDirectory() && !b.isDirectory()) return -1;
				if( !a.isDirectory() && b.isDirectory()) return 1;
				return a.name().compareTo(b.name());
			}
		});
		return handles;
	}

	private void updateFiles(boolean push){
		if(push) stack.push(directory);

		navigation.setText(directory.toString());
		
		GlyphLayout layout = Pools.obtain(GlyphLayout.class);
		
		layout.setText(VisUI.getSkin().getFont("default-font"), navigation.getText());
		
		if(layout.width < navigation.getWidth()){
			navigation.setCursorPosition(0);
		}else{
			navigation.setCursorPosition(navigation.getText().length());
		}
		
		Pools.free(layout);
		
		//navigation.setCursorPosition(0);
		//navigation.setCursorPosition(navigation.getText().length());

		files.clearChildren();
		FileHandle[] names = getFileNames();

		Image upimage = new Image(VisUI.getSkin().getDrawable("icon-folder-parent"));

		VisTextButton upbutton = new VisTextButton("...");
		upbutton.addListener(new UpListener());
		
		upbutton.left().add(upimage).padRight(4f*s).size(40*s);
		upbutton.getCells().reverse();
		
		files.top().left().add(upbutton).align(Align.topLeft).fillX().expandX().height(50*s).pad(2).colspan(2);
		upbutton.getLabel().setAlignment(Align.left);

		files.row();
		
		ButtonGroup<VisTextButton> group = new ButtonGroup<VisTextButton>();
		group.setMinCheckCount(0);

		for(final FileHandle file : names){
			if( !file.isDirectory() && !filter.accept(file)) continue; //skip non-filtered files

			final String filename = file.name();

			final VisTextButton button = new VisTextButton(shorten(filename), "toggle");
			group.add(button);
			
			button.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					if( !file.isDirectory()){
						filefield.setText(filename);
						updateFileFieldStatus();
					}else{
						directory = directory.child(filename);
						updateFiles(true);
					}
				}
			});
			
			filefield.addListener(new ChangeListener(){
				@Override
				public void changed(ChangeEvent event, Actor actor){
					button.setChecked(filename.equals(filefield.getText()));
				}
			});
			
			final Image image = new Image(VisUI.getSkin().getDrawable(file.isDirectory() ? "icon-folder" : "icon-file-text"));
			
			button.add(image).padRight(4f*s).size(40*s);
			button.getCells().reverse();
			files.top().left().add(button).align(Align.topLeft).fillX().expandX().height(50*s).pad(2*s).colspan(2);
			button.getLabel().setAlignment(Align.left);
			files.row();
		}

		pane.setScrollY(0f);
		updateFileFieldStatus();
		
		if(open) filefield.clearText();
	}

	private String shorten(String string){
		int max = 30;
		if(string.length() <= max){
			return string;
		}else{
			return string.substring(0, max - 3).concat("...");
		}
	}

	@Override
	public VisDialog show(Stage stage){
		super.show(stage);
		stage.setScrollFocus(pane);
		return this;
	}

	public void fileSelected(FileHandle file){

	}

	public float getPrefWidth(){
		return Gdx.graphics.getWidth();
	}

	public float getPrefHeight(){
		return Gdx.graphics.getHeight() - 1;
	}

	public class FileHistory{
		private Array<FileHandle> history = new Array<FileHandle>();
		private int index;

		public FileHistory(){

		}

		public void push(FileHandle file){
			if(index != history.size) history.truncate(index);
			history.add(file);
			index ++;

			//print();
		}

		public void back(){
			if( !canBack()) return;
			index --;
			directory = history.get(index - 1);
			updateFiles(false);

			//print();
		}

		public void forward(){
			if( !canForward()) return;
			directory = history.get(index);
			index ++;
			updateFiles(false);

			//print();
		}

		public boolean canForward(){
			return !(index >= history.size);
		}

		public boolean canBack(){
			return !(index == 1);
		}

		void print(){

			System.out.println("\n\n\n\n\n\n");
			int i = 0;
			for(FileHandle file : history){
				i ++;
				if(index == i){
					System.out.println("[[" + file.toString() + "]]");
				}else{
					System.out.println("--" + file.toString() + "--");
				}
			}

		}
	}

	public static interface FileHandleFilter{
		public boolean accept(FileHandle file);
	}

	public static FileHandleFilter pngFilter = new FileHandleFilter(){
		@Override
		public boolean accept(FileHandle file){
			return file.extension().equalsIgnoreCase("png");
		}
	};
	
	public static FileHandleFilter jpegFilter = new FileHandleFilter(){
		@Override
		public boolean accept(FileHandle file){
			return file.extension().equalsIgnoreCase("png") || file.extension().equalsIgnoreCase("jpg") || file.extension().equalsIgnoreCase("jpeg");
		}
	};
	
	public static FileHandleFilter defaultFilter = new FileHandleFilter(){
		@Override
		public boolean accept(FileHandle file){
			return true;
		}
	};
}
