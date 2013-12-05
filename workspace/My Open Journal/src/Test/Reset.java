package Test;


import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;

public class Reset extends SelectorComposer<Component>{
	
	// Textbox containing inputted username
	@Wire 
	Textbox username;
	
	// Checks to see if the password matches the username. If so it
	// logs the user in and redirects to homepage
    @Listen("onClick = #reset")
    public void LoginUser(){
    	DBManager manager = new DBManager();
    	String user = username.getText();
    	// Check to see if username is already registered
    	if(manager.IsValidUser(user)) {
    		int id = manager.GetID(user);
    		
    		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event)
				{
					Executions.sendRedirect("index.zul");
				}
    		};
    		
    		if(manager.ResetPasswordKey(id)){
    			int resetCode = manager.GetResetCode(manager.GetID(user));
    			//Add email sending code
    			Email.SendPasswordReset(manager.GetEmail(user), resetCode);
    			Messagebox.show("Your password code has been sent to your email.", "", new Messagebox.Button[] {
	    				Messagebox.Button.OK}, Messagebox.INFORMATION,clickListener);
    		}
    		else{
    			Messagebox.show("Your password code has been sent to your email.");
    		}
    		
    	}
    	else
			Messagebox.show("Invalid username!!");    	
    }
    
    // Redirect user to register page if they click register button
    @Listen("onClick = #register")
    public void RegisterUser() {
    	Executions.sendRedirect("register.zul");
    }

}
