package helpers;

import generated.*;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/test")
public class MyServlet extends HttpServlet 
{
	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		try {
			PrintWriter out = response.getWriter();
			out.println("Hello :)");
			
			String numParam = request.getParameter("num");
			
			int numOfExceptions = parseInt(numParam, 1);
			
			out.println("Throwing " + numOfExceptions + " exceptions...");
		
			for(int i = 1; i <= numOfExceptions; i++) {
				try {
					out.println("Threw " + i + " exception");
					@SWITCHER_CLASS_NAME@.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			out.println("done!");
			
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int parseInt(String str, int defaultValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
