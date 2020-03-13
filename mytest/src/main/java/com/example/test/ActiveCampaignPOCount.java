package com.example.test;

import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

public class ActiveCampaignPOCount {
	final static Logger logger = Logger.getLogger(ActiveCampaignPOCount.class);
	Connect_db connect_db = new Connect_db();
	Connection con = connect_db.getPLM1Connection();
	Map<String, String> needOffers; 

	void executeAudit(String inFilePath, String outFilePath, Map<String, String> tagMap) {
		ArrayList<String[]> materialImpactsForPlmMissingOfferAudit = new ArrayList<String[]>();

		parseCsv(inFilePath);
		exportAuditFile(outFilePath, tagMap, materialImpactsForPlmMissingOfferAudit);

		Collections.sort(materialImpactsForPlmMissingOfferAudit,new Comparator<String[]>() {
			public int compare(String[] strings, String[] otherStrings) {
				int discountCodeComp = strings[1].compareTo(otherStrings[1]);
				return ((discountCodeComp == 0) ? strings[0].compareTo(otherStrings[0]) : discountCodeComp);
			}
		});


	}

	void parseCsv(String inFilePath) 
	{
		try {
			FileReader filereader = new FileReader(inFilePath); 
			needOffers = new TreeMap<String,String>();

			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
			List<String[]> allData = csvReader.readAll(); 

			for (String[] row : allData) 
				needOffers.put(row[0].trim(), row[5].trim());
		}
		catch (Exception e) {
			logger.error("Error in compareUtility() method: "+e.getMessage());
		}
	}

	void exportAuditFile(String outFilePath, Map<String, String> tagMap, ArrayList<String[]> materialImpactsForPlmMissingOfferAudit) {
		try {
			PreparedStatement stmtActiveCampaigns = null;
			///Connection con = null;
			Date date = new Date();
			SimpleDateFormat outDateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
			String today = outDateFormatter.format(date);

			//con = connect_db.getPLM2Connection();
			String sql = "select d.discount_ID,d.discount_CODE,d.description,count(t.discount_id) as cnt from PLM_DBO.DISCOUNT_TXN_MASTER d left outer join(select d.discount_code,d.discount_id from PLM_DBO.DISCOUNT_TXN_MASTER d,PLM_DBO.OFFER_DISC_MAP_TXN_DET c, plm_dbo.OFFER_MASTER_TXN_DET a where c.discount_id = d.discount_id and a.offer_id = c.offer_id and a.prpostn_id is not null and c.PRIMARY_FLAG='Y' and d.start_date <= 'TODAY' and d.end_date > 'TODAY' and a.start_date <= 'TODAY' and a.end_date >= 'TODAY' and a.distro_preference in ('5.0','BOTH'))t on t.discount_id = d.discount_id where d.start_date <= 'TODAY' and d.end_date > 'TODAY' group by d.discount_ID,d.discount_code,d.description order by cnt";
			sql = sql.replace("TODAY", today);

			logger.info("SQL ---> " + sql.toString());
			stmtActiveCampaigns = con.prepareStatement(sql);
			ResultSet rsCampaigns = stmtActiveCampaigns.executeQuery();
			CSVWriter writer = new CSVWriter(new FileWriter(outFilePath));
			if (rsCampaigns.next() == false) {
				logger.info("No active campaigns present in PLM3 DB!");
			} 
			else {
				do {
					String discountId = rsCampaigns.getString("discount_id");
					String discountCode = rsCampaigns.getString("DISCOUNT_CODE");
					String description = rsCampaigns.getString("description");
					String cnt = rsCampaigns.getString("cnt");
					Boolean isMaterialImpact = false;
					StringBuilder tag = new StringBuilder();

					/*if(discountCode.equals("RADVX0001"))
						System.out.println("watchout..");*/

					for(String s : tagMap.keySet())
						if(discountCode.startsWith(s.trim()))
							tag.append(tagMap.get(s));

					String[] data = { discountId, discountCode, description, tag.toString(), needOffers.get(discountCode), cnt }; 
					//writer.writeNext(data);
					if(cnt.equals("0")) {
						if(needOffers.containsKey(discountCode)) {
							if(!(needOffers.get(discountCode).toUpperCase().equals("NO") || needOffers.get(discountCode).toUpperCase().equals("IGNORE"))) {
								materialImpactsForPlmMissingOfferAudit.add(data);
								isMaterialImpact = true;
							}
						}
						else {
							materialImpactsForPlmMissingOfferAudit.add(data);
							isMaterialImpact = true;
						}
					}
					String[] data2 = { discountId, discountCode, description, tag.toString(), needOffers.get(discountCode), cnt, isMaterialImpact.toString() };
					writer.writeNext(data2);

				} while (rsCampaigns.next());
			}
			writer.flush();
			writer.close();
			logger.info("CampaignToPO-Count output file created successfully at " + outFilePath);
		}
		catch (SQLException e) {
			logger.error("SQL Error in exportAuditFile() method: "+e.getMessage());		
		}
		catch (Exception e) {
			logger.error("Error in exportAuditFile() method: "+e.getMessage());		
		}
	}
}
