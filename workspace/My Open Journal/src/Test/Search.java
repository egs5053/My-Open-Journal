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
        Executions.sendRedirect("search_results.zul?keyword=" + keyword);
        //DBManager manager = new DBManager();
        //List<PaperData> result = manager.KeywordSearch(keyword);
        
	}
	
}