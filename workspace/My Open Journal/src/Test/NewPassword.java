package Test;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;

public class NewPassword extends SelectorComposer<Component> {
	
	// Textbox components from password reset page
	@Wire
	Textbox username;
	@Wire
	Textbox password;	
	@Wire
	Textbox password2;
	@Wire
	Textbox resetcode;

	// Runs when user clicks submit button
    @Listen("onClick = #submit")
    public void ResetPassword() {
    	if (password.getText() != password2.getText()) {
    		Messagebox.show("Passwords do not match");
    	}
    	else {    		
	    	DBManager manager = new DBManager();

	    	int id = manager.getID(username.getText());
	    	int code = Integer.parseInt(resetcode.getText());

	    	if (manager.CheckResetCode(id, code)) {
	    		String pass = SessionManager.saltAndHash(password.getText());
	    		manager.UpdatePassword(id, pass);
	    		Messagebox.show("Passwords successfully changed!");
	    	}
	    	else {
	    		Messagebox.show("Username or Reset Code incorrect");
	    	}
		}
    }
	
}
