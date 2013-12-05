package Test;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class Reset extends SelectorComposer<Component>{
	
	// Textbox containing inputted username
	@Wire 
	Textbox username;
	
    @Listen("onClick = #reset")
    public void LoginUser(){
    	DBManager manager = new DBManager();
    	String user = username.getText();
    	// Check to see if username is already registered
    	if(manager.IsValidUser(user)) {
    		int id = manager.GetID(user);
    		if(manager.ResetPasswordKey(id)){
                Messagebox.show("Password key generated!");
    			//Add email sending code
    		}
    		else{
    			Messagebox.show("There was an error resetting your password");
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
