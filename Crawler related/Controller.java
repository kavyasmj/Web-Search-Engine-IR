import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.examples.localdata.CrawlStat;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	
 private static ArrayList<String[]> visitList = new ArrayList<String[]>();
 private static ArrayList<String[]> fetchList = new ArrayList<String[]>();
 private static ArrayList<String[]> urlList = new ArrayList<String[]>();
	
 static int maxPagesToFetch = 10000; //default is no limit
 static int maxDepthOfCrawling = 16; //default is no limit
 static int politenessDelay = 500; //default is 200ms
 static String URL = "http://www.huffingtonpost.com/";
  
 static int fa = 0,fs = 0,fab = 0,ff = 0, turl = 0 , uurl = 0, uurl_in = 0, uurl_out = 0;
 static int range_1kb = 0, range_1to10kb = 0, range_10to100kb = 0,range_100kbto1mb = 0, range_g1mb = 0; 
 
 static Map<Integer, Integer> statCode = new TreeMap<Integer, Integer>();		
 static HashMap<String, Integer> conType = new HashMap<String, Integer>();
 static HashSet<String> uniqUrlList_OK = new HashSet<>();
 static HashSet<String> uniqUrlList_NOK = new HashSet<>();
 static HashSet<String> uniqUrlList = new HashSet<>();
 
 public static void main(String[] args) throws Exception {
	 
	 String crawlStorageFolder = "/data/crawl";
	 int numberOfCrawlers = 7;
	 CrawlConfig config = new CrawlConfig();
	 config.setCrawlStorageFolder(crawlStorageFolder);
	 
	 config.setMaxDepthOfCrawling(maxDepthOfCrawling);
	 config.setMaxPagesToFetch(maxPagesToFetch);
	 config.setPolitenessDelay(politenessDelay);
	 
	 config.setIncludeBinaryContentInCrawling(true);
	 /*
	  * config.setPolitenessDelay(politenessDelay);	//200ms is default
	  * config.setUserAgentString(userAgentString);
	  */
	 /*
	 * Instantiate the controller for this crawl.
	 */
	 PageFetcher pageFetcher = new PageFetcher(config);
	 RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
	 RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
	 CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
	 /*
	 * For each crawl, you need to add some seed urls. These are the first
	 * URLs that are fetched and then the crawler starts following links
	 * which are found in these pages
	 */
	 controller.addSeed(URL);
	 /*
	 * Start the crawl. This is a blocking operation, meaning that your code
	 * will reach the line after this only when crawling is finished.
	 */
	 controller.start(MyCrawler.class, numberOfCrawlers);
	 
	 /*
	 * your code will reach the line after this only when crawling is finished.
	 */
	 
	 List<Object> crawlersLocalData = controller.getCrawlersLocalData();

	 for (Object localData : crawlersLocalData) {
       CrawlStat stat = (CrawlStat) localData;
       visitList.addAll(stat.getVisitList());
       fetchList.addAll(stat.getFetchList());
       urlList.addAll(stat.getUrlList());
	 }
	 
	 output_fetch();
	 output_visit();
	 output_url();
		 
	 System.out.println("\nName: Kavyashree Mysore Jagadeesh");
	 System.out.println("USC ID: 3554137619");
	 System.out.println("News site crawled: " + URL);
	 System.out.println("\nFetch Statistics");
	 System.out.println("================");
	 System.out.println("# fetches attempted: " + fa);
	 System.out.println("# fetches succeeded: " + fs);
	 System.out.println("# fetches aborted: " + fab); 
	 System.out.println("# fetches failed: " + ff);
	 
	 System.out.println("\nOutgoing URLs:");
	 System.out.println("==============");
	 System.out.println("Total URLs extracted: " + turl);
	 System.out.println("# unique URLs extracted: " + uurl);
	 System.out.println("# unique URLs within News Site: " + uurl_in); 
	 System.out.println("# unique URLs outside News Site: " + uurl_out);
	 
	 System.out.println("\nStatus Codes:");
	 System.out.println("=============");
	  
	 for (int key : statCode.keySet()) {
		 String stat_info = Integer.toString(key);
		 switch(key){
		 	case 200: stat_info = stat_info.concat(" OK: ");
		 	  break;
		 	case 301: stat_info = stat_info.concat(" Moved Permanently: ");
			  break;
		 	case 302: stat_info = stat_info.concat(" Moved Temporarily: ");
			  break;
		 	case 401: stat_info = stat_info.concat(" Unauthorized: ");
			  break;
		 	case 403: stat_info = stat_info.concat(" Forbidden: ");
			  break;
		 	case 404: stat_info = stat_info.concat(" Not Found: ");
			  break;
		 	default:  stat_info = stat_info.concat(" : ");
		 }
		    System.out.println(stat_info + statCode.get(key));
	}
	
	 System.out.println("\nFile Sizes:");
	 System.out.println("===========");
	 System.out.println("< 1KB: " + range_1kb);
	 System.out.println("1KB ~ <10KB: " + range_1to10kb);
	 System.out.println("10KB ~ <100KB: " + range_10to100kb); 
	 System.out.println("100KB ~ <1MB: " + range_100kbto1mb);
	 System.out.println(">= 1MB: " + range_g1mb);

	 System.out.println("\nContent Types:");
	 System.out.println("==============");
	 for (String key : conType.keySet()) {
		    System.out.println( key + " : " + conType.get(key));
	 }
  
 } //end of method main
 
 
		public static void output_visit() {
		
		  XSSFWorkbook workbook = new XSSFWorkbook();
		  XSSFSheet sheet = workbook.createSheet("visit");	      
		  String[] visitRow = new String[4];
		  
		  for(int i=0; i<visitList.size();i++){
			  visitRow = visitList.get(i);
			  Row row = sheet.createRow(i);
			  
			  Cell cell = row.createCell(0);
		      cell.setCellValue(visitRow[0]);
		      cell = row.createCell(1);
		      try{
		    	  int bytes= Integer.parseInt(visitRow[1]);
		    	  cell.setCellValue(bytes);
		    	  if(bytes < 1024){
		    		  range_1kb++;
		    	  }
		    	  else if(bytes > 1024 && bytes < 10240){
		    		  range_1to10kb++;
		    	  }
		    	  else if(bytes > 10240 && bytes < 102400){
		    		  range_10to100kb++;
		    	  }
		    	  else if(bytes > 102400 && bytes < 1048576){
		    		  range_100kbto1mb++;
		    	  }
		    	  else{
		    		  range_g1mb++;
		    	  }  
		      }
		      catch (NumberFormatException e) {
		    	  cell.setCellValue(visitRow[1]);
		      }
		      cell = row.createCell(2);
		      try{
		    	  cell.setCellValue(Integer.parseInt(visitRow[2]));
		      }
		      catch (NumberFormatException e) {
		    	  cell.setCellValue(visitRow[2]);
		      }
		      cell = row.createCell(3);
		      String ct = visitRow[3];
		      cell.setCellValue(ct);
		      
	    	  if(conType.get(ct) == null){
	    		  conType.put(ct, 1);
	    	  }
	    	  else{
	    		  conType.put(ct, conType.get(ct)+1);
	    	  }
		  }
	
	   String outputFileName = "visit.xlsx";
		  File f = new File(outputFileName);
		  try {
			OutputStream outputStream = (OutputStream) new FileOutputStream(f);
			workbook.write(outputStream);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
	} //end of output visit method


	public static void output_fetch() {
		
		  XSSFWorkbook workbook = new XSSFWorkbook();
	      XSSFSheet sheet = workbook.createSheet("fetch");	      
		  String[] fetchRow = new String[2];
		  int sc = 0;
		  fa =  fetchList.size();
		  
		  for(int i=0; i<fa;i++){
			  fetchRow = fetchList.get(i);
			  
			  if (fetchRow[1].equals("200")){
				  fs++;
			  }
			  else if(fetchRow[1].equals("APM") || fetchRow[1].equals("ACFE") || fetchRow[1].equals("AUE") || fetchRow[1].startsWith("3") ){
				  fab++;
			  }
			  else{
				  ff++;
			  }
				  
			  Row row = sheet.createRow(i);		  
			  Cell cell = row.createCell(0);
		      cell.setCellValue(fetchRow[0]);
		      cell = row.createCell(1);
		      try{
		    	  sc = Integer.parseInt(fetchRow[1]);
		    	  cell.setCellValue(sc);
		    	  if(statCode.get(sc) == null){
		    		  statCode.put(sc, 1);
		    	  }
		    	  else{
		    		  statCode.put(sc, statCode.get(sc)+1);
		    	  }
		    	  
		      }
		      catch (NumberFormatException e) {
		    	  cell.setCellValue(fetchRow[1]);
		      }
		  }
	
	   String outputFileName = "fetch.xlsx";
		  File f = new File(outputFileName);
		  try {
			OutputStream outputStream = (OutputStream) new FileOutputStream(f);
			workbook.write(outputStream);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
	} //end of output fetch method

	public static void output_url() {
		
		  XSSFWorkbook workbook = new XSSFWorkbook();
	      XSSFSheet sheet = workbook.createSheet("urls");	      
		  String[] urlRow = new String[2];
		  
		  turl =  urlList.size();
		  
		  for(int i=0; i<turl;i++){
			  urlRow = urlList.get(i);
			  Row row = sheet.createRow(i);
			  
			  Cell cell = row.createCell(0);
		      cell.setCellValue(urlRow[0]);
		      cell = row.createCell(1);
		      cell.setCellValue(urlRow[1]);
		      
		      if(urlRow[1].equals("OK")){
		    	  uniqUrlList_OK.add(urlRow[0]);
		      }
		      else{
		    	  uniqUrlList_NOK.add(urlRow[0]);
		      }
		      uniqUrlList.add(urlRow[0]);
		  }
		  
		  uurl_in = uniqUrlList_OK.size();
		  uurl_out = uniqUrlList_NOK.size();
		  uurl = uniqUrlList.size();
		  
	      String outputFileName = "url.xlsx";
		  File f = new File(outputFileName);
		  try {
			OutputStream outputStream = (OutputStream) new FileOutputStream(f);
			workbook.write(outputStream);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		  
	
	} //end of output url method
 
}