package com.example.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 * Servlet implementation class Test
 */
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(Test.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Test() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	    String realPath = getServletContext().getRealPath("/");
	    String fileSep = System.getProperty("file.separator");

	    if (realPath != null && (!realPath.endsWith(fileSep)))
	      realPath = realPath + fileSep;

	    System.out.println(realPath);

	    //load the configuration for this application's loggers using the
	    // servletLog.properties file
	    PropertyConfigurator.configure(realPath + "mytest/src/main/resources/log4j.properties");
	        //+ "WEB-INF/classes/servletLog.properties");
	    //create the logger for this servlet class
	    //it will use the configuration for the logger com.java2s.LoggerServlet
	    //or inherit from the logger com.java2s if one exists, and so on

	    
		//final String path = "src/main/resources/log4j.properties";
		//PropertyConfigurator.configure(path);
		logger.info("\n\nSuccessfully initialized logger.");
		ActiveCampaignPOCount objActiveCampaignCount = new ActiveCampaignPOCount();
		String outFilePath = "/downloads/" ;

		objActiveCampaignCount.executeAudit("/Users/neelghodasara/OneDrive - Cox Communications/nghodasa/NormalizedCampaigns-20200312.csv",outFilePath, new HashMap<String, String>());
		
		response.getWriter().append("Heelllooo testing ho: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
