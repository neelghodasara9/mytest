package com.example.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Properties;

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
		
		File configDir = new File(System.getProperty("catalina.base"), "conf");
		File configFile = new File(configDir, "log4j.properties");
		InputStream stream = new FileInputStream(configFile);
		Properties props = new Properties();
		props.load(stream);
	    
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
