package io.anuke.novix.ui;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

import io.anuke.novix.element.FloatingMenu;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.function.Consumer;
import io.anuke.ucore.scene.event.Touchable;
import io.anuke.ucore.scene.ui.*;
import io.anuke.ucore.scene.ui.layout.Table;

public class FileChooser extends FloatingMenu{
	
	private static FileChooser instance;
	
	private Table files;
	private FileHandle homeDirectory = Gdx.files.absolute(System.getProperty("user.home"));
	private FileHandle directory = homeDirectory;
	private ScrollPane pane;
	private TextField navigation, filefield;
	private TextButton ok;
	private FileHistory stack = new FileHistory();
	private FileHandleFilter filter;
	private Consumer<FileHandle> selectListener;
	private boolean open;
	
	public static void open(String title, Consumer<FileHandle> result){
		FileChooser chooser = new FileChooser(title, jpegFilter, true);
		chooser.fileSelected(result);
		chooser.show();
	}
	
	public FileChooser(String title, boolean open){
		this(title, defaultFilter, open);
	}

	public FileChooser(String title, FileHandleFilter filter, boolean open){
		super(false, title);
		this.open = open;
		this.filter = filter;
		setupWidgets();
	}

	private void setupWidgets(){
		
		content.pad(10);
		
		filefield = new TextField();
		//if(!open) TextFieldDialogListener.add(filefield);
		filefield.setDisabled(open);

		ok = new TextButton(open ? "Open" : "Save");
		
		ok.clicked(()->{
			if(ok.isDisabled()) return;
			if(selectListener != null)
				selectListener.accept(directory.child(filefield.getText()));
			hide();
		});
		
		filefield.changed(()->{
			ok.setDisabled(filefield.getText().replace(" ", "").isEmpty());
		});
		
		filefield.change();
		
		TextButton cancel = new TextButton("Cancel");
		cancel.clicked(()->{
			hide();
		});

		navigation = new TextField("");
		navigation.setTouchable(Touchable.disabled);

		files = new Table();

		pane = new ScrollPane(files);
		pane.setOverscroll(false, false);
		pane.setFadeScrollBars(false);

		updateFiles(true);

		Table icontable = new Table();

		ImageButton up = new ImageButton("icon-folder-parent");
		up.getImageCell().size(42);
		up.clicked(()->{
			directory = directory.parent();
			updateFiles(true);
		});

		final ImageButton back = new ImageButton("icon-arrow-left");
		back.getImageCell().size(42);
		
		final ImageButton forward = new ImageButton("icon-arrow-right");
		forward.getImageCell().size(42);
		
		forward.clicked(()->{
			stack.forward();
		});
		
		back.clicked(()->{
			stack.back();
		});
		
		ImageButton home = new ImageButton("icon-home");
		home.getImageCell().size(42);
		home.clicked(()->{
			directory = homeDirectory;
			updateFiles(true);
		});

		content.top().left();
		content.add(icontable).expandX().fillX();

		icontable.add(up).expandX().fillX().height(60).padBottom(10f).uniform();
		icontable.add(back).expandX().fillX().height(60).padBottom(10f).uniform();
		icontable.add(forward).expandX().fillX().height(60).padBottom(10f).uniform();
		icontable.add(home).expandX().fillX().height(60).padBottom(10f).uniform();

		content.row();
		content.add(navigation).colspan(3).left().padBottom(10f).expandX().fillX().height(40f);
		content.row();

		content.center().add(pane).colspan(3).expand().fill();
		content.row();

		Table fieldcontent = new Table();
		content.bottom().left().add(fieldcontent).colspan(3).expand().fill();

		fieldcontent.bottom().left().add(new Label("File Name:")).padTop(20);
		fieldcontent.add(filefield).padTop(20).height(50f).fillX().expandX().padLeft(10f).padRight(10f);
		content.row();
		
		Table buttons = new Table();
		
		content.add(buttons).growX();

		buttons.add(cancel).growX().padBottom(10).padTop(10f).height(50);
		buttons.add(ok).growX().padBottom(10).padTop(10f).height(50);

	}
	
	private void updateFileFieldStatus(){
		if(!open){
			ok.setDisabled(filefield.getText().replace(" ", "").isEmpty());
		}else{
			ok.setDisabled(!directory.child(filefield.getText()).exists() || directory.child(filefield.getText()).isDirectory());
		}
	}

	/*
	private class UpListener extends ClickListener{
		public void clicked(InputEvent event, float x, float y){
			directory = directory.parent();
			updateFiles(true);
		}
	}
	*/

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
		
		layout.setText(DrawContext.font, navigation.getText());
		
		if(layout.width < navigation.getWidth()){
			navigation.setCursorPosition(0);
		}else{
			navigation.setCursorPosition(navigation.getText().length());
		}
		
		Pools.free(layout);

		files.clearChildren();
		FileHandle[] names = getFileNames();

		Image upimage = new Image("icon-folder-parent");

		TextButton upbutton = new TextButton("...");
		upbutton.clicked(()->{
			directory = directory.parent();
			updateFiles(true);
		});
		
		upbutton.left().add(upimage).padRight(4f).size(42);
		upbutton.getCells().reverse();
		
		files.top().left().add(upbutton).align(Align.topLeft).fillX().expandX().height(50).pad(2).colspan(2);
		upbutton.getLabel().setAlignment(Align.left);

		files.row();
		
		ButtonGroup<TextButton> group = new ButtonGroup<TextButton>();
		group.setMinCheckCount(0);

		for(final FileHandle file : names){
			if( !file.isDirectory() && !filter.accept(file)) continue; //skip non-filtered files

			final String filename = file.name();

			final TextButton button = new TextButton(shorten(filename), "toggle");
			group.add(button);
			
			button.clicked(()->{
				if( !file.isDirectory()){
					filefield.setText(filename);
					updateFileFieldStatus();
				}else{
					directory = directory.child(filename);
					updateFiles(true);
				}
			});
			
			filefield.changed(()->{
				button.setChecked(filename.equals(filefield.getText()));
			});
			
			final Image image = new Image(file.isDirectory() ? "icon-folder" : "icon-file-text");
			
			button.add(image).padRight(4f).size(42);
			button.getCells().reverse();
			files.top().left().add(button).align(Align.topLeft).fillX().expandX().height(50).pad(2).colspan(2);
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
	public void show(){
		super.show();
		DrawContext.scene.setScrollFocus(pane);
	}

	public void fileSelected(Consumer<FileHandle> listener){
		this.selectListener = listener;
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
		}

		public void back(){
			if( !canBack()) return;
			index --;
			directory = history.get(index - 1);
			updateFiles(false);
		}

		public void forward(){
			if( !canForward()) return;
			directory = history.get(index);
			index ++;
			updateFiles(false);
		}

		public boolean canForward(){
			return !(index >= history.size);
		}

		public boolean canBack(){
			return !(index == 1) && index > 0;
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
