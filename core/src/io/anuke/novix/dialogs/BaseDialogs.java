package io.anuke.novix.dialogs;

import io.anuke.novix.element.FloatingMenu;
import io.anuke.ucore.function.Listenable;

public class BaseDialogs{
	
	public static class ConfirmDialog extends FloatingMenu{

		public ConfirmDialog(String text, String detail, Listenable confirm) {
			super(text);
			
			content.add(detail).colspan(2);
			
			content.row();
			
			content.addButton("Cancel ", ()->{
				hide();
			}).padTop(60).size(140, 58).padRight(30);
			
			content.addButton("OK", ()->{
				confirm.listen();
				hide();
			}).padTop(60).size(140, 58);
		}
		
	}
}
