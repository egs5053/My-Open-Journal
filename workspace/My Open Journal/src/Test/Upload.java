package Test;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Messagebox.ClickEvent;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.zkoss.io.Files;

public class Upload extends SelectorComposer<Component> {
   
	@Wire
	Textbox title;
	
	@Wire
	Textbox description;
	
	@Wire
	Textbox filePath;
	
	@Wire 
	Textbox author;
	
	//
	@Listen("onClick = #submitPaper")
	public void InsertPaper()
	{
		int id;
		String path;
		String user;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
    	DBManager manager = new DBManager();
    	user = SessionManager.GetUser();
    	id = manager.GetID(user);
    	path = "papers\\" + user + "\\" + filePath.getText();
    	
    	manager.InsertPaper(id, title.getText(), path, description.getText(), dateFormat.format(date));
		Executions.sendRedirect("index.zul");
	}
	
	// Uploads file to the server to directory C:\tomcat\webapps\ROOT\papers
	@Listen("onUpload = #uploadPaper")
	public void UploadPaper(UploadEvent event){
		
		// Get the path to upload the file to
		String path = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/");
		
		
		path = path + "papers\\" + SessionManager.GetUser() + "\\";
		Media media = event.getMedia();
		if(media == null)
			System.out.println("NULL!!");

    	// Copy file to server
		try {
			Files.copy(new File(path + media.getName()), media.getStreamData());
			
			// Parse the PDF and store the information in the database
			String[] pdfInfo = PDFParse.parsePDF(new File(path + media.getName()));
	    	title.setText(pdfInfo[0]);
			author.setText(pdfInfo[1]);
			description.setText(pdfInfo[2]);
			
			filePath.setText(media.getName());
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("fail!!");
		  }
    }

}
