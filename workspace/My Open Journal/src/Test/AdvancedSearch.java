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
        String catLabel = "";
        try {
        	catLabel = category.getSelectedItem().getLabel();
        }
        catch (Exception e) {
        	//catLabel stays the same
        }
        String tagsText = tags.getValue();
        int whitespacecount = 0;
        int whitespacecount2 = 0;
        int whitespacecount3 = 0;
        for (int i = 0; i < titleText.length(); i++) {
			if(Character.isWhitespace(titleText.charAt(i))){
				whitespacecount++;
			}
		}
        if (whitespacecount > 0) {
        	titleText = titleText.replace(" ", "%20");
        }
        for (int i = 0; i < absText.length(); i++) {
			if(Character.isWhitespace(absText.charAt(i))){
				whitespacecount2++;
			}
		}
        if (whitespacecount2 > 0) {
        	absText = absText.replace(" ", "%20");
        }
        for (int i = 0; i < tagsText.length(); i++) {
			if(Character.isWhitespace(tagsText.charAt(i))){
				whitespacecount3++;
			}
		}
        if (whitespacecount3 > 0) {
        	tagsText = tagsText.replace(" ", "%20");
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
        
        String redirect = "searchresults.zul?";
        if (titleText != "") {
        	redirect += "title=" + titleText + "&titleAnd=" + titleBool;
        }
        if (authIDText != "") {
        	redirect += "&authorID=" + authIDText + "&authorIDAnd=" + authBool;
        }
        if (absText != "") {
        	redirect += "&Abstract=" + absText + "&abstractAnd=" + absBool;
        }
        if (catLabel != "") {
        	redirect += "&category=" + catLabel + "&categoryAnd=" + catBool;
        }
        if (tagsText != "") {
        	redirect += "&tags=" + tagsText;
        }
        Executions.sendRedirect(redirect);
	}
}
