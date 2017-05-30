import java.awt.Color;

public class Location
{
	public int xCord, yCord;
	public String name;
	public Color fill;
	
	public Location(String n,int x,int y,Color f)
	{
		xCord = x;
		yCord = y;
		name = n;
		fill = f;
	}
	
	public void setColor(Color f)
	{
		fill = f;
	}
}