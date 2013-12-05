package Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DBManager {

	private DBConnection connection;
	
	public List<PaperData> KeywordSearch(String keyword)
	{
		int whitespacecount = 0;
		for (int i = 0; i < keyword.length(); i++) {
			if(Character.isWhitespace(keyword.charAt(i))){
				whitespacecount++;
			}
		}
		if(whitespacecount > 0) {
			String[] keywords = keyword.split(" ");
			String query;
			ResultSet rs;
			connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
			query = "select * from Papers where contains(Title, ?) or contains(Description, ?)";
			try {
				for (int i = 1; i < keywords.length; i++) {
					query += "union select * from Papers where contains(Title, ?) or contains(Description, ?)";
				}
				PreparedStatement stmt = connection.GetConnection().prepareStatement(query + ";");
				int j = 1;
				for (int i = 0; i < keywords.length; i++) {
					stmt.setString(j, keywords[i]);
					j++;
					stmt.setString(j, keywords[i]);
					j++;
				}
				rs = stmt.executeQuery();
				List<PaperData> rowValues = new ArrayList<PaperData>();
				while (rs.next()) {
					PaperData data = new PaperData(rs.getString(4), rs.getInt(2), rs.getString(6), rs.getString(9), rs.getString(10), rs.getInt(1), rs.getDouble(8));
					rowValues.add(data);
				}
				rs.close();
				stmt.close();
				connection.Disconnect();
				return rowValues;
			}
			catch (SQLException e) {
				System.out.println("Failure to search Papers: " + e.getMessage());
				return null;
			}
		}
		if (whitespacecount == 0) {
			
			String query;
			ResultSet rs;
			connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
			query = "select * from Papers where contains(Title, ?) or contains(Description, ?);";
			try {
				PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
				stmt.setString(1, keyword);
				stmt.setString(2, keyword);
				rs = stmt.executeQuery();
				List<PaperData> rowValues = new ArrayList<PaperData>();
				while (rs.next()) {
					PaperData data = new PaperData(rs.getString(4), rs.getInt(2), rs.getString(6), rs.getString(9), rs.getString(10), rs.getInt(1), rs.getDouble(8));
					rowValues.add(data);
				}
				rs.close();
				stmt.close();
				connection.Disconnect();
				return rowValues;
			}
			catch (SQLException e) {
				System.out.println("Failure to search Papers: " + e.getMessage());
				return null;
			}
		}
		else { return null;}
	}

	public List<PaperData> AdvancedSearch(String title, boolean titleAnd,
											int authorID, boolean authorIDAnd, 
											String myAbstract, boolean abstractAnd, 
											String category, boolean categoryAnd,
											String tags)
	{
		String query = "select * from Papers where contains(title, ?) ? contains(Author_ID, ?) ? contains(Description, ?) ? contains(Category, ?) ? contains(Tags, ?);";
		ResultSet rs;
		try{
			connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setString(1, "'\"" + title.replace(" ", "\" OR \"") + "\"'");
			stmt.setString(2, (titleAnd)?"AND":"OR");
			stmt.setInt(3, authorID);
			stmt.setString(4, (authorIDAnd)?"AND":"OR");
			stmt.setString(5, "'\"" + myAbstract.replace(" ", "\" OR \"") + "\"'");
			stmt.setString(6, (abstractAnd)?"AND":"OR");
			stmt.setString(7, category);
			stmt.setString(8, (categoryAnd)?"AND":"OR");
			stmt.setString(9, "'\"" + tags.replace(" ", "\" OR \"") + "\"'");
			rs = stmt.executeQuery();
			List<PaperData> rowValues = new ArrayList<PaperData>();
			while (rs.next()) {
				PaperData data = new PaperData(rs.getString(4), rs.getInt(2), rs.getString(6), rs.getString(9), rs.getString(10), rs.getInt(1), rs.getDouble(8));
				rowValues.add(data);
			}
			rs.close();
			stmt.close();
			connection.Disconnect();
			return rowValues;
		}
		catch (SQLException e) {
			System.out.println("Failure to search Papers: " + e.getMessage());
			return null;
		}
	}

	public String GetPaperDate(int id)
	{
		String date;
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Upload_Date from Papers where Paper_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			date = rs.getString(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return date;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get date: " + e.getMessage());
			return null;
		}
	}

	public boolean ResetPasswordKey(int id){
		String query;
		int resetCode = 10000 + (int)(Math.random() * ((99999 - 10000) + 1));
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "INSERT INTO PWReset (User_ID, Reset_Code) VALUES (?, ?);";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			stmt.setInt(2, resetCode);
			stmt.executeUpdate();
			stmt.close();
	    	connection.Disconnect();
	    	return true;
		} 
		catch (SQLException e) {
			System.out.println("Failure to generate password reset code: " + e.getMessage());
			return false;
		}
	}
	
	public boolean UpdatePaperWeight(int paperID)
	{
		String query;
		double weight;
		
		weight = Rank.GetRank(paperID);
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "update Papers set Weight = ? where Paper_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setDouble(1, weight);
			stmt.setInt(2, paperID);
			stmt.executeUpdate();
			stmt.close();
	    	connection.Disconnect();
	    	return true;
		} 
		catch (SQLException e) {
			System.out.println("Failure to update paper weight: " + e.getMessage());
			return false;
		}
	}
	
	// Creates a new entry in the User database with the specified values
	public boolean InsertUser(String user, String firstName, String lastName, String pass, String email, String admin, String date) {
		String query;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "INSERT INTO Users (Username, First_Name, Last_Name,  Password, Email, Register_Date) VALUES (?, ?, ?, ?, ?, ?);";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setString(1, user);
			stmt.setString(2, firstName);
			stmt.setString(3, lastName);
			stmt.setString(4, pass);
			stmt.setString(5, email);
			stmt.setString(6, date);
			stmt.executeUpdate();
			stmt.close();
	    	connection.Disconnect();
		} 
		catch (SQLException e) {
			System.out.println("Failure to insert user: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean ChangeProfile(String user, String password, String firstName, String lastName)
	{
		String query;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "update Users set Password = ?, First_Name = ?, Last_Name = ? where Username = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setString(1, password);
			stmt.setString(2, firstName);
			stmt.setString(3, lastName);
			stmt.setString(4, user);
			stmt.executeUpdate();
			stmt.close();
	    	connection.Disconnect();
	    	return true;
		} 
		catch (SQLException e) {
			System.out.println("Failure to update profile: " + e.getMessage());
			return false;
		}

	}
	
	public int GetID(String user)
	{
		int id;
		String query;
		ResultSet rs;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select User_ID from Users where Username= ?;";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setString(1, user);
			rs = stmt.executeQuery();
			rs.next();
			id = rs.getInt(1);
	    	rs.close();
			stmt.close();
	    	connection.Disconnect();
    		return id;
		} 
		catch (SQLException e) {
	    	connection.Disconnect();
			return -1;
		}
	}

	public String GetEmail(String user)
	{
		String query;
		ResultSet rs;
		String email;

		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Email from Users where Username= ?;";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setString(1, user);
			rs = stmt.executeQuery();
			rs.next();
			email = rs.getString(1);
	    	rs.close();
			stmt.close();
	    	connection.Disconnect();
    		return email;
		} 
		catch (SQLException e) {
	    	connection.Disconnect();
			return "";
		}
	}

	public int GetResetCode(int id)
	{
		String query;
		ResultSet rs;
		int code;

		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Reset_Code from PWReset where User_ID= ?;";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			code = rs.getInt(1);
	    	rs.close();
			stmt.close();
	    	connection.Disconnect();
    		return code;
		} 
		catch (SQLException e) {
	    	connection.Disconnect();
			return -1;
		}
	}

	public boolean CheckResetCode(int id, int code)
	{
		String query;
		String query2;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
		query = "Select * from PWReset where User_ID= ? AND Reset_Code= ?;";
		query2 = "DELETE from PWReset where User_ID= ? AND Reset_Code = ?;";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			stmt.setInt(2, code);
			rs = stmt.executeQuery();
			if(rs.next()) {
				rs.close();
				stmt = connection.GetConnection().prepareStatement(query2);
				stmt.setInt(1, id);
				stmt.setInt(2, code);
				stmt.executeUpdate();
				stmt.close();
	    		connection.Disconnect();
				return true;
			}
			else {
				rs.close();
				stmt.close();
	    		connection.Disconnect();
				return false;
			}
		} 
		catch (SQLException e) {
	    	connection.Disconnect();
			return false;
		}
	}

	public boolean UpdatePassword(int id, String pass){
		String query1;
		DBConnection connection;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "UPDATE Users SET Password = ? WHERE User_ID = ?";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setString(1, pass);
			stmt.setInt(2, id);
			stmt.executeUpdate();
			stmt.close();
	    	connection.Disconnect();
	    	return true;
		}
		catch (SQLException e) {
			System.out.println("Failure to update password: " + e.getMessage());
			return false;
		}
	}
	
	public String GetUsername(int id)
	{
		String user;
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Username from Users where User_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			user = rs.getString(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return user;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Username: " + e.getMessage());
			return null;
		}
	}

	public String GetRegistrationDate(int id)
	{
		Date date;
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
		query = "select Register_Date from Users where User_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			date = rs.getDate(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return date.toString();
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Registration Date: " + e.getMessage());
			return null;
		}
	}
	
	public String GetPaperDescription(int id)
	{
		String description;
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Description from Papers where Paper_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			description = rs.getString(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return description;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Top Papers: " + e.getMessage());
			return null;
		}
	}
	
	public boolean InsertPaper(int authorID, String title, String path, String description, String date)
	{
		String query;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "INSERT INTO Papers (Author_ID, Title, File_Path, Description, Upload_Date, Weight, Upvotes, Downvotes, Reports) VALUES (?, ?, ?, ?, ?, 0, 0, 0, 0)";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, authorID);
			stmt.setString(2, title);
			stmt.setString(3, path);
			stmt.setString(4, description);
			stmt.setString(5, date);
			stmt.executeUpdate();
			stmt.close();
	    	connection.Disconnect();
	    	return true;
		} 
		catch (SQLException e) {
			System.out.println("Failure to insert user: " + e.getMessage());
			return false;
		}
	}
	
	public int GetPaperAuthor(int id)
	{
		int author;
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Author_ID from Papers where Paper_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			author = rs.getInt(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return author;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Top Papers: " + e.getMessage());
			return -1;
		}
	}
	
	public String GetText(int reviewID)
	{
		String text;
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Text from Reviews where Review_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, reviewID);
			rs = stmt.executeQuery();
			rs.next();
			text = rs.getString(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return text;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Top Papers: " + e.getMessage());
			return null;
		}
	}
	
	public String GetReviewAuthor(int reviewID)
	{
		String author;
		int authorID;
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Reviewer_ID from Reviews where Review_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, reviewID);
			rs = stmt.executeQuery();
			rs.next();
			authorID = rs.getInt(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	author = GetUsername(authorID);
	    	return author;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Top Papers: " + e.getMessage());
			return null;
		}
	}
	
	public String GetPaperTitle(int id)
	{
		String title;
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Title from Papers where Paper_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			title = rs.getString(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return title;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Top Papers: " + e.getMessage());
			return null;
		}
	}
	
	public String GetPaperPath(int id)
	{
		String path;
		String query;
		ResultSet rs;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select File_Path from Papers where Paper_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			path = rs.getString(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return path;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Top Papers: " + e.getMessage());
			return null;
		}
	}
	
	public List<ReviewData> GetReviews(int paperID){
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select * from Reviews where Paper_ID = ? order by Upvotes desc;";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, paperID);
			rs = stmt.executeQuery();
	    	List<ReviewData> rowValues = new ArrayList<ReviewData>();

	    	while (rs.next()) {
	    		int authorID = rs.getInt(3);
	    		ReviewData data = new ReviewData(GetUsername(authorID), rs.getString(7), rs.getString(8), rs.getInt(1));
	    	    rowValues.add(data);
	    	}

	    	rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return rowValues;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Reviews: " + e.getMessage());
		}
		return null;
	}
	
	public List<Data> GetTopPapers() {
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select top 10 * from Papers order by Upvotes desc;";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			rs = stmt.executeQuery();
	    	List<Data> rowValues = new ArrayList<Data>();

	    	while (rs.next()) {
	    		Data data = new Data(rs.getString(4), rs.getString(9), rs.getString(10), rs.getInt(1));
	    	    rowValues.add(data);
	    	}

	    	rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return rowValues;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Top Papers: " + e.getMessage());
		}
		return null;
	}

	public List<Data> GetUserPapers(int id) {
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select * from Papers where Author_ID= ?;";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
	    	List<Data> rowValues = new ArrayList<Data>();

	    	while (rs.next()) {
	    		Data data = new Data(rs.getString(4), rs.getString(9), rs.getString(10), rs.getInt(1));
	    		System.out.println(rs.getString(4) + ", " + rs.getString(9) + ", " + rs.getString(10) + ", " + rs.getInt(1));
	    	    rowValues.add(data);
	    	}

	    	rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return rowValues;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get User Papers: " + e.getMessage());
		}
		return null;
	}
	
	public String GetFirstName(String user)
	{
		String name;
		String query;
		ResultSet rs;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select First_Name from Users where Username= ?;";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setString(1, user);
			rs = stmt.executeQuery();
			rs.next();
			name = rs.getString(1);
	    	rs.close();
			stmt.close();
	    	connection.Disconnect();
    		return name;
		} 
		catch (SQLException e) {
	    	connection.Disconnect();
			return null;
		}
	}
		
	public String GetLastName(String user)
	{
		String name;
		String query;
		ResultSet rs;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Last_Name from Users where Username= ?;";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setString(1, user);
			rs = stmt.executeQuery();
			rs.next();
			name = rs.getString(1);
	    	rs.close();
			stmt.close();
	    	connection.Disconnect();
    		return name;
		} 
		catch (SQLException e) {
	    	connection.Disconnect();
			return null;
		}
	}
	
	public List<HighestData> GetHighestPapers()
	{
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select top 10 * from Papers order by Weight desc;";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			rs = stmt.executeQuery();
	    	List<HighestData> rowValues = new ArrayList<HighestData>();

	    	while (rs.next()) {
	    		HighestData data = new HighestData(rs.getString(4), rs.getString(9), rs.getString(10), rs.getInt(1), rs.getDouble(8));
	    	    rowValues.add(data);
	    	}

	    	rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return rowValues;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Top Papers: " + e.getMessage());
		}
		return null;
	}
	
	public List<Data> GetNewPapers()
	{
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select top 10 * from Papers order by Upload_Date desc;";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			rs = stmt.executeQuery();
	    	List<Data> rowValues = new ArrayList<Data>();

	    	while (rs.next()) {
	    		Data data = new Data(rs.getString(4), rs.getString(9), rs.getString(10), rs.getInt(1));
	    	    rowValues.add(data);
	    	}

	    	rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return rowValues;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Top Papers: " + e.getMessage());
		}
		return null;
	}
	
	public boolean IsValidPassword(String user, String pass){
		String query;
		ResultSet rs;
		String saltedAndHashedPass = SessionManager.saltAndHash(pass);
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Password from Users where Username= ?;";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setString(1, user);

			rs = stmt.executeQuery();
			rs.next();
			if(saltedAndHashedPass.equals(rs.getString(1)))
	    	{
		    	rs.close();
				stmt.close();
		    	connection.Disconnect();
	    		return true;
	    	}
	    	else
	    	{
		    	rs.close();
				stmt.close();
		    	connection.Disconnect();
	    		return false;
	    	}
		} 
		catch (SQLException e) {
	    	connection.Disconnect();
			return false;
		}
	}
	
	// checks to see if the specified username has been registered/exists in database
	public boolean IsValidUser(String user) {
		String query;
		ResultSet rs;
		boolean isEmpty;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Username from Users where Username= ?";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setString(1, user);
			rs = stmt.executeQuery();
			if(!rs.next())
				isEmpty = false;
			else
				isEmpty = true;
	    	rs.close();
	    	stmt.close();
	    	connection.Disconnect();
	    	return isEmpty;
		} 
		catch (SQLException e) {
			System.out.println("Invalid Username!!");
			return false;
		}
	}
	
	// This function inserts a new comment into the database given a review_ID the comment is
	// posted on, user_ID that is submitting the comment, and a String comment
	public boolean InsertComment(int reviewID, int userID, String comment) {
		String query;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "INSERT INTO Comments (Review_ID, Commenter_ID, Text) VALUES (?, ?, ?)";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, reviewID);
			stmt.setInt(2, userID);
			stmt.setString(3, comment);
			stmt.executeUpdate();
			stmt.close();
	    	connection.Disconnect();
	    	return true;
		} 
		catch (SQLException e) {
			System.out.println("Failure to insert user: " + e.getMessage());
			return false;
		}
	}
	
	// This function inserts a new review into the database given a paper_ID the review is
	// posted on, user_ID that is submitting the comment, and a String review
	public boolean InsertReview(int paperID, int userID, String review, String date) {
		String query;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "INSERT INTO Reviews (Paper_ID, Reviewer_ID, Text, Upload_Date, Upvotes, Downvotes) VALUES (?, ?, ?, ?, 0, 0)";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, paperID);
			stmt.setInt(2, userID);
			stmt.setString(3, review);
			stmt.setString(4, date);
			stmt.executeUpdate();
			stmt.close();
	    	connection.Disconnect();
	    	return true;
		} 
		catch (SQLException e) {
			System.out.println("Failure to insert user: " + e.getMessage());
			return false;
		}
	}
	
	public int GetPaperUpvotes(int id)
	{
		int upvotes;
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Upvotes from Papers where Paper_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			upvotes = rs.getInt(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return upvotes;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Top Papers: " + e.getMessage());
			return -1;
		}
	}
	
	public int GetPaperDownvotes(int id)
	{
		int downvotes;
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Downvotes from Papers where Paper_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			downvotes = rs.getInt(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return downvotes;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Top Papers: " + e.getMessage());
			return -1;
		}
	}
	
	public int GetReviewUpvotes(int id)
	{
		int upvotes;
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Upvotes from Reviews where Review_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			upvotes = rs.getInt(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return upvotes;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Top Papers: " + e.getMessage());
			return -1;
		}
	}
	
	public int GetReviewDownvotes(int id)
	{
		int downvotes;
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Downvotes from Reviews where Review_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			downvotes = rs.getInt(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return downvotes;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Top Papers: " + e.getMessage());
			return -1;
		}
	}
	
	// This function inserts a upvote and updates the corresponding paper table
	public boolean InsertPaperUpvote(int paperID, int userID) {
		String query1;
		String query2;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    		query1 = "INSERT INTO PaperVotes (Paper_ID, User_ID, Up_Down, Report) VALUES (?, ?, 1, 0)";
    		query2 = "UPDATE Papers SET Upvotes = Upvotes+1 WHERE Paper_ID = ?";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query1);
			stmt.setInt(1, paperID);
			stmt.setInt(2, userID);
			stmt.executeUpdate();
	    		stmt = connection.GetConnection().prepareStatement(query2);
			stmt.setInt(1, paperID);
			stmt.executeUpdate();
			stmt.close();
	    		connection.Disconnect();
	    		return true;
		}
		catch (SQLException e) {
			System.out.println("Failure to insert user: " + e.getMessage());
			return false;
		}
	}
	
	// This function inserts a downvote and updates the corresponding paper table
	public boolean InsertPaperDownvote(int paperID, int userID) {
		String query1;
		String query2;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    		query1 = "INSERT INTO PaperVotes (Paper_ID, User_ID, Up_Down, Report) VALUES (?, ?, 0, 0)";
    		query2 = "UPDATE Papers SET Downvotes = Downvotes+1 WHERE Paper_ID = ?";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query1);
			stmt.setInt(1, paperID);
			stmt.setInt(2, userID);
			stmt.executeUpdate();
	    		stmt = connection.GetConnection().prepareStatement(query2);
			stmt.setInt(1, paperID);
			stmt.executeUpdate();
			stmt.close();
	    		connection.Disconnect();
	    		return true;
		}
		catch (SQLException e) {
			System.out.println("Failure to insert user: " + e.getMessage());
			return false;
		}
	}
	
	// This function inserts a upvote and updates the corresponding paper table
	public boolean InsertReviewUpvote(int reviewID, int userID) {
		String query1;
		String query2;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    		query1 = "INSERT INTO ReviewVotes (Review_ID, User_ID, Up_Down, Report) VALUES (?, ?, 1, 0)";
    		query2 = "UPDATE Reviews SET Upvotes = Upvotes+1 WHERE Review_ID = ?";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query1);
			stmt.setInt(1, reviewID);
			stmt.setInt(2, userID);
			stmt.executeUpdate();
	    		stmt = connection.GetConnection().prepareStatement(query2);
			stmt.setInt(1, reviewID);
			stmt.executeUpdate();
			stmt.close();
	    		connection.Disconnect();
	    		return true;
		}
		catch (SQLException e) {
			System.out.println("Failure to insert user: " + e.getMessage());
			return false;
		}
	}
	
	// This function inserts a downvote and updates the corresponding paper table
	public boolean InsertReviewDownvote(int reviewID, int userID) {
		String query1;
		String query2;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    		query1 = "INSERT INTO ReviewVotes (Review_ID, User_ID, Up_Down, Report) VALUES (?, ?, 0, 0)";
    		query2 = "UPDATE Reviews SET Downvotes = Downvotes+1 WHERE Review_ID = ?";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query1);
			stmt.setInt(1, reviewID);
			stmt.setInt(2, userID);
			stmt.executeUpdate();
	    		stmt = connection.GetConnection().prepareStatement(query2);
			stmt.setInt(1, reviewID);
			stmt.executeUpdate();
			stmt.close();
	    		connection.Disconnect();
	    		return true;
		}
		catch (SQLException e) {
			System.out.println("Failure to insert user: " + e.getMessage());
			return false;
		}
	}
	
	// checks to see if the specified username has been registered/exists in database
	public boolean CanVotePaper(int paperID, int userID) {
		String query;
		ResultSet rs;
		boolean canVote;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    		query = "SELECT * FROM PaperVotes WHERE Paper_ID = ? AND User_ID = ?";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, paperID);
			stmt.setInt(2, userID);
			rs = stmt.executeQuery();
			if(rs.next())
				canVote = false;
			else
				canVote = true;
	    		rs.close();
	    		stmt.close();
	    		connection.Disconnect();
	    		return canVote;
		} 
		catch (SQLException e) {
			System.out.println("Invalid Username!!");
			return false;
		}
	}
	
	// checks to see if the specified username has been registered/exists in database
	public boolean CanVoteReview(int reviewID, int userID) {
		String query;
		ResultSet rs;
		boolean canVote;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    		query = "SELECT * FROM ReviewVotes WHERE Review_ID = ? AND User_ID = ?";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, reviewID);
			stmt.setInt(2, userID);
			rs = stmt.executeQuery();
			if(rs.next())
				canVote = false;
			else
				canVote = true;
	    		rs.close();
	    		stmt.close();
	    		connection.Disconnect();
	    		return canVote;
		} 
		catch (SQLException e) {
			System.out.println("Invalid Username!!");
			return false;
		}
	}
	
	public int GetCommentUpvotes(int id)
	{
		int upvotes;
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Upvotes from Comments where Comment_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			upvotes = rs.getInt(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return upvotes;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Top Papers: " + e.getMessage());
			return -1;
		}
	}
	
	public int GetCommentDownvotes(int id)
	{
		int downvotes;
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select Downvotes from Comments where Comment_ID = ?;";
    	try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			rs.next();
			downvotes = rs.getInt(1);
			rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return downvotes;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Comment Downvotes: " + e.getMessage());
			return -1;
		}
	}
	
	// checks to see if the specified username has been registered/exists in database
	public boolean CanVoteComment(int commentID, int userID) {
		String query;
		ResultSet rs;
		boolean canVote;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    		query = "SELECT * FROM CommentVotes WHERE Comment_ID = ? AND User_ID = ?";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, commentID);
			stmt.setInt(2, userID);
			rs = stmt.executeQuery();
			if(rs.next())
				canVote = false;
			else
				canVote = true;
	    		rs.close();
	    		stmt.close();
	    		connection.Disconnect();
	    		return canVote;
		} 
		catch (SQLException e) {
			System.out.println("Invalid Username!!");
			return false;
		}
	}
	
	// This function inserts a upvote and updates the corresponding paper table
	public boolean InsertCommentUpvote(int commentID, int userID) {
		String query1;
		String query2;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    		query1 = "INSERT INTO CommentVotes (Comment_ID, User_ID, Up_Down, Report) VALUES (?, ?, 1, 0)";
    		query2 = "UPDATE Comments SET Upvotes = Upvotes+1 WHERE Comment_ID = ?";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query1);
			stmt.setInt(1, commentID);
			stmt.setInt(2, userID);
			stmt.executeUpdate();
	    		stmt = connection.GetConnection().prepareStatement(query2);
			stmt.setInt(1, commentID);
			stmt.executeUpdate();
			stmt.close();
	    		connection.Disconnect();
	    		return true;
		}
		catch (SQLException e) {
			System.out.println("Failure to insert user: " + e.getMessage());
			return false;
		}
	}
	
	// This function inserts a downvote and updates the corresponding paper table
	public boolean InsertCommentDownvote(int commentID, int userID) {
		String query1;
		String query2;
		
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    		query1 = "INSERT INTO CommentVotes (Comment_ID, User_ID, Up_Down, Report) VALUES (?, ?, 0, 0)";
    		query2 = "UPDATE Comments SET Downvotes = Downvotes+1 WHERE Comment_ID = ?";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query1);
			stmt.setInt(1, commentID);
			stmt.setInt(2, userID);
			stmt.executeUpdate();
	    		stmt = connection.GetConnection().prepareStatement(query2);
			stmt.setInt(1, commentID);
			stmt.executeUpdate();
			stmt.close();
	    		connection.Disconnect();
	    		return true;
		}
		catch (SQLException e) {
			System.out.println("Failure to insert user: " + e.getMessage());
			return false;
		}
	}
	
	public List<CommentData> GetComments(int reviewID){
		String query;
		ResultSet rs;
		connection = new DBConnection("10.2.65.20", "myopenjournal", "sa", "umaxistheman");
    	query = "select * from Comments where Review_ID = ? order by Upvotes desc;";
		try {
			PreparedStatement stmt = connection.GetConnection().prepareStatement(query);
			stmt.setInt(1, reviewID);
			rs = stmt.executeQuery();
	    	List<CommentData> rowValues = new ArrayList<CommentData>();

	    	while (rs.next()) {
	    		CommentData data = new CommentData(rs.getString(4), GetUsername(rs.getInt(1)), rs.getInt(7), rs.getInt(8), reviewID, rs.getInt(1));
	    	    rowValues.add(data);
	    	}

	    	rs.close();
			stmt.close();
	    	connection.Disconnect();
	    	return rowValues;
		} 
		catch (SQLException e) {
			System.out.println("Failure to Get Comments: " + e.getMessage());
		}
		return null;
	}
}
