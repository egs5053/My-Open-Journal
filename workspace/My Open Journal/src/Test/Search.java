package Test;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
 

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Textbox;

public class Search extends SelectorComposer<Component> {
	
	@Wire
	Textbox keywordBox;
	
	@Listen("onClick = #searchButton")
    public void search(){
        String keyword = keywordBox.getValue();
        int whitespacecount = 0;
		for (int i = 0; i < keyword.length(); i++) {
			if(Character.isWhitespace(keyword.charAt(i))){
				whitespacecount++;
			}
		}
		if (whitespacecount == 0) {
			Executions.sendRedirect("searchresults.zul?keyword=" + keyword);
		}
		else {
			keyword = keyword.replace(" ", "%20");
			Executions.sendRedirect("searchresults.zul?keyword=" + keyword);
		}
	}
	
}
