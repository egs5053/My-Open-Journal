package Test;

import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.Set;
 

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.ext.Selectable;

public class SearchResults extends SelectorComposer<Component> {
	
	@Wire
	Listbox Results;
	
	/*public void DisplayResult(Grid myGrid, List<PaperData> data)
	{
	    Rows rows = new Rows();
	    rows.setParent(myGrid);
	    for(PaperData d : data)
	    {
	    	final int id = d.GetID();
	        Label Title= new Label(d.GetTitle());
	        Title.addEventListener("onClick", new EventListener<Event>()
	        {
	    		@Override
	    		public void onEvent(Event event) throws Exception {
	    			SessionManager.SetPaper(id);
	    			Executions.sendRedirect("paper.zul");
	    		}
	    	}
	    	);
	        Label Author = new Label(d.GetAuthor());
	        Label Description = new Label(d.GetDescription());

	        Row row = new Row();

	        Title.setParent(row);
	        Author.setParent(row);
	        Description.setParent(row);

	        row.setParent(rows);
	    }
	}*/
	
	/*public void doAfterCompose(Grid comp) {
		DBManager manager = new DBManager();
		try {
			super.doAfterCompose(comp);
			String keyword = keywordBox.getValue();
			List<PaperData> result = manager.KeywordSearch(keyword);
	        DisplayResult(Results, result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	/*@Listen("onClick = #searchButton")
    public void search(){
        String keyword = keywordBox.getValue();
        DBManager manager = new DBManager();
        List<PaperData> result = manager.KeywordSearch(keyword);
        //DisplayResult(Results, result);
        //ListModelList<PaperData> blah = new ListModelList<PaperData>(result);
        //Results.setModel(blah);
        Results.setModel(new ListModelList<PaperData>(result));
	}*/
	@Override
	public void doAfterCompose(Component comp) {
		DBManager manager = new DBManager();
		try {
			super.doAfterCompose(comp);
			String keyword;
			if (Executions.getCurrent().getParameter("keyword") != null) {
	            String par = Executions.getCurrent().getParameter("keyword");
	            keyword = par;
	        } else {
	            keyword = "";
	        }
			List<PaperData> result = manager.KeywordSearch(keyword);
			Results.setModel(new ListModelList<PaperData>(result));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Listen("onClick = #Results")
	public void SelectPaper() {
		Set<PaperData> selection = ((Selectable<PaperData>)Results.getModel()).getSelection();
		if (selection!=null && !selection.isEmpty()){
			PaperData selected = selection.iterator().next();
			final int id = selected.GetID();
			SessionManager.SetPaper(id);
			Executions.sendRedirect("paper.zul");
		}
		
	}
	
}
	
	
	