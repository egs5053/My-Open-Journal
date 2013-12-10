package Test;

import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.Set;
 

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ext.Selectable;

public class SearchResults extends SelectorComposer<Component> {
	
	@Wire
	Listbox Results;

	@Override
	public void doAfterCompose(Component comp) {
		DBManager manager = new DBManager();
		try {
			super.doAfterCompose(comp);
			String keyword = "";
			String title = "";
			boolean titleAnd = false;
			int authID = 0;
			boolean authIDAnd = false;
			String Abstract = "";
			boolean absAnd = false;
			String category = "";
			boolean catAnd = false;
			String tags = "";
			
			if (Executions.getCurrent().getParameter("keyword") != null) {
	            String par = Executions.getCurrent().getParameter("keyword");
	            keyword = par;
	            List<PaperData> result = manager.KeywordSearch(keyword);
	            Results.setModel(new ListModelList<PaperData>(result));
	        }
			else {
				if (Executions.getCurrent().getParameter("title") != null) {
					title = Executions.getCurrent().getParameter("title");
					if (Executions.getCurrent().getParameter("titleAnd") == "true") {
						titleAnd = true;
					}
				}
					if (Executions.getCurrent().getParameter("authorID") != null) {
						authID = Integer.parseInt(Executions.getCurrent().getParameter("authorID"));
						if (Executions.getCurrent().getParameter("authorIDAnd") == "true") {
							authIDAnd = true;
						}
					}
					if (Executions.getCurrent().getParameter("Abstract") != null) {
						Abstract = Executions.getCurrent().getParameter("Abstract");
						if (Executions.getCurrent().getParameter("abstractAnd") == "true") {
							absAnd = true;
						}
					}
					if (Executions.getCurrent().getParameter("category") != null) {
						category = Executions.getCurrent().getParameter("category");
						if (Executions.getCurrent().getParameter("categoryAnd") == "true") {
							catAnd = true;
						}
					}
					if (Executions.getCurrent().getParameter("tags") != null) {
						tags = Executions.getCurrent().getParameter("tags");
					}
					List<PaperData> result = manager.AdvancedSearch(title, titleAnd, authID, authIDAnd, Abstract, absAnd, category, catAnd, tags);
		            Results.setModel(new ListModelList<PaperData>(result));
			}
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
