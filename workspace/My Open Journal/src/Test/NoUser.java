package Test;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class NoUser extends SelectorComposer<Component>{
	
	@Wire 
	Label username;
    
    // Redirect user to home page if they click home button
    @Listen("onClick = #home")
    public void ReturnToHome() {
    	Executions.sendRedirect("index.zul");
    }

    public void doAfterCompose(Grid comp) {
        if (Executions.getCurrent().getParameter("username") != null) {
            username.setValue("The user '" + Executions.getCurrent().getParameter("username") + "' does not exist");
        } else {
            username.setValue("No username provided");
        }
    }

}
