	package Test;

	import java.util.List;

	import org.zkoss.zk.ui.Executions;
	import org.zkoss.zk.ui.event.Event;
	import org.zkoss.zk.ui.event.EventListener;
	import org.zkoss.zk.ui.select.SelectorComposer;
	import org.zkoss.zk.ui.select.annotation.Wire;
	import org.zkoss.zul.*;

	public class UserProfile extends SelectorComposer<Grid>{
		@Wire
		Grid papersCol;

		@Wire
		Label name;	

		@Wire
		Label registrationDate;
		
		public static void GoHome()
		{
			Executions.sendRedirect("index.zul");
		}

		public void onCreate$name() {
			String user;
			String first;
			String last;
			DBManager manager = new DBManager();

			user = SessionManager.GetUser();
			first = manager.GetFirstName(user);
			last = manager.GetLastName(user);

			name.setValue(first + " " + last);
		}

		// Displays the top 10 papers
		public void DisplayResult(Grid myGrid, List<Data> data)
		{
		    Rows rows = new Rows();
		    rows.setParent(myGrid);
		    for(Data d : data)
		    {
		    	final int id = d.GetID();
		        Label title= new Label(d.GetTitle());
		        title.addEventListener("onClick", new EventListener<Event>()
		        {
					@Override
					public void onEvent(Event event) throws Exception {
						SessionManager.SetPaper(id);
						Executions.sendRedirect("paper.zul");
					}
		        }
		        );
		        Label upvotes = new Label(d.GetUpvotes());
		        Label downvotes = new Label(d.GetDownvotes());

		        Row row = new Row();    

		        title.setParent(row);
		        upvotes.setParent(row);
		        downvotes.setParent(row);

		        row.setParent(rows);
		    }
		}
		
		public void doAfterCompose(Grid comp) {
			DBManager manager = new DBManager();
			String user;
			int id;
			try {
				super.doAfterCompose(comp);
				user = SessionManager.GetUser();
				id = manager.GetID(user);
				Messagebox.show("Username: " + user + ", ID = " + id, "", new Messagebox.Button[]{
		        Messagebox.Button.OK}, Messagebox.INFORMATION, clickListener);
				DisplayResult(papersCol, manager.GetUserPapers(id));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //wire variables and event listners
			//do whatever you want (you could access wired variables here)
		}

	}
