package Test;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
 

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Combobox;

public class AdvancedSearch extends SelectorComposer<Component> {
	
	@Wire
	Textbox title;
	
		@Wire
		Radio titleAnd;
	
		@Wire
		Radio titleOr;
	
	@Wire
	Textbox author;
	
		@Wire
		Radio authAnd;
	
		@Wire
		Radio authOr;
	
	@Wire
	Textbox Abstract;
	
		@Wire
		Radio absAnd;
	
		@Wire
		Radio absOr;
	
	@Wire
	Combobox category;
	
		@Wire
		Radio catAnd;
	
		@Wire
		Radio catOr;
	
	@Wire
	Textbox tags;
	

	
	@Listen("onClick = #search")
	public void search() {
		String titleText = title.getValue();
        DBManager manager = new DBManager();
        String authIDText = Integer.toString(manager.GetID(author.getValue()));
        String absText = Abstract.getValue();
        String catLabel = category.getSelectedItem().getLabel();
        String tagsText = tags.getValue();
        int whitespacecount = 0;
        for (int i = 0; i < tagsText.length(); i++) {
			if(Character.isWhitespace(tagsText.charAt(i))){
				whitespacecount++;
			}
		}
        if (whitespacecount > 0) {
        	tagsText.replace(" ", "%20");
        }
        String titleBool;
        String authBool;
        String absBool;
        String catBool;
        if(titleAnd.isSelected()) {
        	titleBool = "true";
        }
        else { titleBool = "false";}
        if(authAnd.isSelected()) {
        	authBool = "true";
        }
        else {authBool = "false";}
        if(absAnd.isSelected()) {
        	absBool = "false";
        }
        else {absBool = "true";}
        if(catAnd.isSelected()) {
        	catBool = "true";
        }
        else {catBool = "false";}
        
        String redirect = "searchresults.zul?title=" + titleText + "&titleAnd=" + titleBool + "&authorID=" + authIDText + "&authorIDAnd=" + authBool + "&Abstract=" + absText + "&abstractAnd=" + absBool + "&category=" + catLabel + "&categoryAnd=" + catBool + "&tags=" + tagsText;
        Executions.sendRedirect(redirect);
	}
}