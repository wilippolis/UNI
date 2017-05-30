import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/geoloc")
public class Geoloc extends HttpServlet {
	static final String user = "WLIPPOLI_codefest", 
						pass = "zhUFsTWI29uy",
						db_url = "asdf";
	
	public static final double ACCEPTABLE_DISTANCE = 0.05;
	// lat & long
	public static final BigDecimal[] DAVIS_CENTER = {new BigDecimal(44.4757914),new BigDecimal(-73.1964633)};
	public static final BigDecimal[] MUDDY_WATERS = {new BigDecimal(44.4761728),new BigDecimal(-73.2119901)};
	public static final BigDecimal[] FLETCHER_LIB = {new BigDecimal(44.4768642), new BigDecimal(-73.210435)};
	public static final BigDecimal[] BAILEY_HOWE = {new BigDecimal(44.4772649),new BigDecimal(-73.1967532)};
	public static final BigDecimal[] WATERMAN = {new BigDecimal(44.478283),new BigDecimal(-73.201157)};
	public static final String[] LOCATION_NAMES = 
		{"DAVIS_CENTER", "MUDDY_WATERS", "FLETCHER LIBRARY", "BAILEY HOWE", "WATERMAN"};
	public static final BigDecimal[][] LOCATIONS = {
			DAVIS_CENTER, MUDDY_WATERS, FLETCHER_LIB, BAILEY_HOWE, WATERMAN
	};
	//public static final BigDecimal RADIUS = new BigDecimal(0.000425383239);
	
	
	public static final BigDecimal RADIUS = new BigDecimal(0.000425383239);
	
	private BigDecimal userLat;
	private BigDecimal userLong;
	
	private static final BigDecimal SQRT_DIG = new BigDecimal(150);
	private static final BigDecimal SQRT_PRE = new BigDecimal(10).pow(SQRT_DIG.intValue());

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
        response.setContentType("text/html");
        response.getWriter().print("Lat = " + request.getParameter("latitude"));
        response.getWriter().print("Long = " + request.getParameter("longitude"));
        
        double userLat_ = Double.parseDouble(request.getParameter("latitude"));
        double userLong_ = Double.parseDouble(request.getParameter("longitude"));
        
        userLong = new BigDecimal(userLong_);
        userLat = new BigDecimal(userLat_);
        boolean[] hits = checkLocation(response);
        for (int i = 0; i < hits.length; i++){
        		if (hits[i]){
        			response.getWriter().print(LOCATION_NAMES[i]);
        		}
        	
        }
        //response.getWriter().write(s);
        
        File file = new File("test.txt");
        FileWriter writer = new FileWriter(file);
        PrintWriter w = new PrintWriter(writer);
      
	}
	
	public boolean[] checkLocation(HttpServletResponse response) throws IOException{
		boolean[] closeLocations = new boolean[5];
		for (int i = 0; i < 5; ++i){
			closeLocations[i] = inRange(LOCATIONS[i], response);
		}
		return closeLocations;
		
	}
	public boolean inRange(BigDecimal[] location, HttpServletResponse response) throws IOException{
		BigDecimal lat_dif = location[0].subtract(userLat);
		BigDecimal long_dif = location[1].subtract(userLong);
		
		lat_dif = lat_dif.multiply(lat_dif);
		long_dif = long_dif.multiply(long_dif);
		
		BigDecimal sumOfDifs = lat_dif.add(long_dif);
		
		BigDecimal distance = bigSqrt(sumOfDifs);
		//response.getWriter().print("Distance: " + distance + "   ");
		return (distance.compareTo(RADIUS) <= 0);
		
		
	}

	private static BigDecimal sqrtNewtonRaphson  (BigDecimal c, BigDecimal xn, BigDecimal precision){
	    BigDecimal fx = xn.pow(2).add(c.negate());
	    BigDecimal fpx = xn.multiply(new BigDecimal(2));
	    BigDecimal xn1 = fx.divide(fpx,2*SQRT_DIG.intValue(),RoundingMode.HALF_DOWN);
	    xn1 = xn.add(xn1.negate());
	    BigDecimal currentSquare = xn1.pow(2);
	    BigDecimal currentPrecision = currentSquare.subtract(c);
	    currentPrecision = currentPrecision.abs();
	    if (currentPrecision.compareTo(precision) <= -1){
	        return xn1;
	    }
	    return sqrtNewtonRaphson(c, xn1, precision);
	}

	/**
	 * Uses Newton Raphson to compute the square root of a BigDecimal.
	 * 
	 * shhhh nothing here
	 * @author Luciano Culacciatti 
	 * @url http://www.codeproject.com/Tips/257031/Implementing-SqrtRoot-in-BigDecimal
	 */
	public static BigDecimal bigSqrt(BigDecimal c){
	    return sqrtNewtonRaphson(c,new BigDecimal(1),new BigDecimal(1).divide(SQRT_PRE));
	}

	
}