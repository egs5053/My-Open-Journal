package Test;

public class PaperData
{
	private String upvotes;
	private String downvotes;
	private String title;
	private int authorID;
	private String author;
	private String description;
	private int id;
	private double paperWeight;
	
	public PaperData(String paperTitle, int authorid, String paperDescription, String numUpvotes, String numDownvotes, int paperID, double weight) {
		upvotes = numUpvotes;
		downvotes = numDownvotes;
		title = paperTitle;
		authorID = authorid;
		author = GetAuthor();
		description = paperDescription;
		id = paperID;
		paperWeight = weight;
	}
	public String GetDescription() {
		return description;
	}
	
	public String GetAuthor() {
		DBManager manager = new DBManager();
		return manager.GetUsername(authorID);
	}
	
	public String GetStringAuthor() {
		return author;
	}
	
	public int GetAuthorID() {
		return authorID;
	}
	
	public double GetWeight() {
		return paperWeight;
	}
	
	public String GetUpvotes() {
		return upvotes;
	}
	
	public int GetID() {
		return id;
	}
	
	public String GetDownvotes() {
		return downvotes;
		
	}
	
	public String GetTitle() {
		return title;
	}
	
	public void SetDescription(String newDesc) {
		description = newDesc;
	}
	
	public void SetAuthorID(int newID) {
		authorID = newID;
	}
	
	public void SetWeight(double newWeight) {
		paperWeight = newWeight;
	}
	
	public void SetTitle(String newTitle) {
		title = newTitle;
	}
	
	public void SetDownvotes(String votes) {
		downvotes = votes;
	}
	
	public void SetUpvotes(String votes) {
		upvotes = votes;
	}
}
