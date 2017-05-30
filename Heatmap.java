import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.json.simple.JSONObject;

import com.ibm.bluemix.hack.util.VcapServicesHelper;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.sql.Connection;
import java.util.Iterator;

@WebServlet("/heatmap")
public class Heatmap extends HttpServlet {
	
	static private DataSource _datasource = null;
	
	private Font font = new Font("Heatmap-Font",Font.BOLD,24);
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String relativeWebPath = "./images/heatmap.jpg";
		String absoluteDiskPath = getServletContext().getRealPath(relativeWebPath);
		File f = new File(absoluteDiskPath);
		
		response.setContentType("image/jpeg");
        BufferedImage image;
        image = ImageIO.read(f);
        
        /*
        //*************************************For Testing Use*******************************
        Location testLib = new Location("Fletcher Library",660,415,Color.green);
        drawLocation(image,testLib);
        
        Location testUVMLib = new Location("Biley-Howl Library",1245,345,Color.red);
        drawLocation(image,testUVMLib);
        
        Location testDavis = new Location("Davis Center",1300, 475,Color.yellow);
        drawLocation(image,testDavis);
        
        LinkedList<Location> locs = getLocations(image);
        Iterator<Location> it = locs.iterator();
        while(it.hasNext())
        	addText(image,2000,50,it.next().name);
        //***********************************************************************************
        */
        LinkedList<Location> locs = getLocations();
        Iterator<Location> it = locs.iterator();
        while(it.hasNext())
        	drawLocation(image,it.next());
        
        ImageIO.write(image, "jpeg", response.getOutputStream());
	}
	
	public LinkedList<Location> getLocations()
	{
		String dbName = "locations";
		
		LinkedList<Location> locs = new LinkedList<Location>();
		
		Connection database = null;
		Statement stmt;
		String query = "select LOC_NAME, XCORD, YCORD from locations" ;
		
		try 
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			database = DriverManager.getConnection("jdbc:mysql://us-cdbr-iron-east-03.cleardb.net/ad_893f572ea7ffde6?user=bc3189df35a503&password=08ca16b8");
			stmt = database.createStatement();
			ResultSet results = stmt.executeQuery(query);
			
			while(results.next())
			{
				int lat = results.getInt("XCORD");
				int lon = results.getInt("YCORD");
				String name = results.getString("LOC_NAME");
				
				Location loc = new Location(name,lat,lon,null);
				
				locs.add(loc);
			}
			
			Iterator<Location> it = locs.iterator();
			while(it.hasNext())
			{
				Location l = it.next();
				l.setColor(getColor(dbName,stmt,l.name));
			}
				
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally 
		{
			try 
			{
				if( database != null ) database.close();
			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		
		return locs;
	}
	
	public Color getColor(String dbName,Statement stmt,String locName)
	{
		double ave = 0;
		int numScores = 0;
		Timestamp expireTime = new Timestamp(System.currentTimeMillis()-(int)(1.5*60*60*1000));
		
		try
		{
			String query = "select SCORE, TIME from scores s WHERE s.LOC_NAME = \"" + locName + "\"";
			ResultSet results = stmt.executeQuery(query);
			while(results.next())
			{
				int score = results.getInt("SCORE");
				Timestamp created = results.getTimestamp("TIME");
				
				if(created.after(expireTime))
				{
					ave += score;
					numScores++;
				}	
			}
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		} 
		
		Color color;
		if(ave>0)
		{
			ave = ave/numScores;
			
			if(ave<1.8)
				color = Color.green;
			else if(ave<2.6)
				color = new Color(127,255,0);
			else if(ave<3.4)
				color = Color.yellow;
			else if(ave<4.2)
				color = Color.orange;
			else
				color = Color.red;
		}
		else
		{
			color = Color.gray;
		}
		
		return color;			
	}
	
	public void drawLocation(BufferedImage image, Location loc)
	{
		int squareWidth = 50;
		int coloredSquareWidth = 40;
		
		colorSquare(image, loc.xCord-squareWidth/2, loc.yCord-squareWidth/2, squareWidth, Color.black);
		colorSquare(image, loc.xCord-coloredSquareWidth/2, loc.yCord-coloredSquareWidth/2, coloredSquareWidth, loc.fill);
		addText(image, loc.xCord, loc.yCord-squareWidth/2, loc.name);
	}
	
	public void addText(BufferedImage image,int xCord,int yCord,String text)
	{
		if(text.equals("Muddy Waters"))
			yCord+=75;
		
		Graphics2D g = image.createGraphics();
		
		int width = g.getFontMetrics(font).stringWidth(text);
		
		g.setColor(Color.black);
		g.fillRect(xCord-width/2-6, yCord-font.getSize()-6, width+12, font.getSize()+11);
		
		g.setColor(Color.white);
		g.setFont(font);
		g.drawString(text, xCord-width/2, yCord-3);
	}
	
	public void colorSquare(BufferedImage image,int xCord,int yCord,int width,Color color)
	{
		Graphics2D g = image.createGraphics();
		g.setColor(color);
		g.fillRect(xCord, yCord, width, width);
	}
	
	static public synchronized DataSource getDataSource(){
		
		if( _datasource == null ) {
			//JSONObject creds = VcapServicesHelper.getCredentials("compose-for-mysql", null);
			//String connectionString = "jdbc:" + creds.get("uri").toString();
	        
	        PoolProperties p = new PoolProperties();
	        p.setUrl("jdbc:mysql://us-cdbr-iron-east-03.cleardb.net/ad_893f572ea7ffde6?user=bc3189df35a503&password=08ca16b8");//connectionString);
	        p.setDriverClassName( "com.mysql.cj.jdbc.Driver" );

	        _datasource = new org.apache.tomcat.jdbc.pool.DataSource( p );
	        _datasource.setPoolProperties(p);
		} 
        
        return _datasource;
	}

	
}