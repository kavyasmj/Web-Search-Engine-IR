//import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.examples.localdata.CrawlStat;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.nio.ByteBuffer;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class MyCrawler extends WebCrawler {
	
//	private static final Logger logger = LoggerFactory.getLogger(MyCrawler.class);
	
	CrawlStat myCrawlStat;

    public MyCrawler() {
        myCrawlStat = new CrawlStat();
    }

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|php|docx|mp2|mid|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|rm|smil|wmv|swf|wma|zip|rar|gz|xml))$");
			 /**
			 * This method receives two parameters. The first parameter is the page
			 * in which we have discovered this new url and the second parameter is
			 * the new url. You should implement this function to specify whether
			 * the given url should be crawled or not (based on your crawling logic).
			 * In this example, we are instructing the crawler to ignore urls that
			 * have css, js, git, ... extensions and to only accept urls that start
			 * with "http://www.viterbi.usc.edu/". In this case, we didn't need the
			 * referringPage parameter to make the decision.
			 */
			 @Override
			 public boolean shouldVisit(Page referringPage, WebURL url) {
			 String href = url.getURL().toLowerCase();

			 Boolean b = href.startsWith(Controller.URL);
//			 Boolean b = href.startsWith("http://www.latimes.com/");
			 
			 String[] urlRow  = new String[2];
			 urlRow[0]= url.getURL();
			 if(b == true){
				 urlRow[1]= "OK";
			 }
			 else{
				 urlRow[1]= "N_OK";
			 }
			 myCrawlStat.addToUrlList(urlRow);
			  
			 return !FILTERS.matcher(href).matches()
//			 && href.startsWith("http://www.viterbi.usc.edu/");
			 && href.startsWith(Controller.URL);
			 }
			 
	
	 /**
     * This function is called once the header of a page is fetched. It can be
	 * overridden by sub-classes to perform custom logic for different status
	 * codes. For example, 404 pages can be logged, etc.
	 *
	 * @param webUrl WebUrl containing the statusCode
	 * @param statusCode Html Status Code number
	 * @param statusDescription Html Status COde description
	 */
	 @Override
	 protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
	       // Do nothing by default
	       // Sub-classed can override this to add their custom functionality
		 String[] fetchRow = new String[2];
		 fetchRow[0]= webUrl.getURL();
		 fetchRow[1]= Integer.toString(statusCode);
		 myCrawlStat.addToFetchList(fetchRow);
	  }
	 
	 
	 /**
	     * This function is called if the content of a url is bigger than allowed size.
	     *
	     * @param urlStr - The URL which it's content is bigger than allowed size
	     */
	 @Override
	    protected void onPageBiggerThanMaxSize(String urlStr, long pageSize) {
//	        logger.warn("Skipping a URL: {} which was bigger ( {} ) than max allowed size", urlStr,
//	                    pageSize);
	        String[] fetchRow = new String[2];
			fetchRow[0]= urlStr;
//			fetchRow[1]= "Abort! Page>MaxSize";
			fetchRow[1]= "APM";
			myCrawlStat.addToFetchList(fetchRow);
	    }
	    
	 /**
	     * This function is called if the crawler encountered an unexpected http status code ( a
	     * status code other than 3xx)
	     *
	     * @param urlStr URL in which an unexpected error was encountered while crawling
	     * @param statusCode Html StatusCode
	     * @param contentType Type of Content
	     * @param description Error Description
	     */
//	   @Override
//	    protected void onUnexpectedStatusCode(String urlStr, int statusCode, String contentType,
//	                                          String description) {
//	    	
////	        logger.warn("Skipping URL: {}, StatusCode: {}, {}, {}", urlStr, statusCode, contentType,
////	                    description);
//	        
//	        String[] fetchRow = new String[2];
//			fetchRow[0]= urlStr;
//			fetchRow[1]= Integer.toString(statusCode);
//			myCrawlStat.addToFetchList(fetchRow);
//	        // Do nothing by default (except basic logging)
//	        // Sub-classed can override this to add their custom functionality
//	    } 
	    
	    /**
	     * This function is called if the content of a url could not be fetched.
	     *
	     * @param webUrl URL which content failed to be fetched
	     */
	    @Override
	    protected void onContentFetchError(WebURL webUrl) {
//	        logger.warn("Can't fetch content of: {}", webUrl.getURL());
	        
	        String[] fetchRow = new String[2];
			fetchRow[0]= webUrl.getURL();
//			fetchRow[1]= "Abort! Content Fetch Error";
			fetchRow[1]= "ACFE";
			myCrawlStat.addToFetchList(fetchRow);
			
	        // Do nothing by default (except basic logging)
	        // Sub-classed can override this to add their custom functionality
	    }
	    
	    
	    /**
	     * This function is called when a unhandled exception was encountered during fetching
	     *
	     * @param webUrl URL where a unhandled exception occured
	     */
	    protected void onUnhandledException(WebURL webUrl, Throwable e) {
//	        String urlStr = (webUrl == null ? "NULL" : webUrl.getURL());
//	        logger.warn("Unhandled exception while fetching {}: {}", urlStr, e.getMessage());
//	        logger.info("Stacktrace: ", e);
	        
	        String[] fetchRow = new String[2];
			fetchRow[0]= webUrl.getURL();
//			fetchRow[1]= "Abort! Unhandled Exception";
			fetchRow[1]= "AUE";
			myCrawlStat.addToFetchList(fetchRow);
			
	        // Do nothing by default (except basic logging)
	        // Sub-classed can override this to add their custom functionality
	    }
	    
	 /**
	  * This function is called when a page is fetched and ready
	  * to be processed by your program.
	  */
	  @Override
	  public void visit(Page page) {
		  
//		  logger.info("Visited: {}", page.getWebURL().getURL());
//	      myCrawlStat.incProcessedPages();
		  
		  String[] visitRow = new String[4];
		  String url = page.getWebURL().getURL();
		  byte[] bytesSize = page.getContentData();
		  
		  
		  visitRow[0]= url;
		  visitRow[1]= Integer.toString(ByteBuffer.wrap(bytesSize).capacity());
		  
		  
		  String cont = page.getContentType();		 
		  if(cont.contains(";") && cont != ""){
			  visitRow[3] =  cont.split(";")[0].trim();
		  }
		  else{
			  visitRow[3] =  cont;  
		  }
		  
		  if (page.getParseData() instanceof HtmlParseData) {
			  HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
	//		  String text = htmlParseData.getText();
	//		  String html = htmlParseData.getHtml();
			  Set<WebURL> links = htmlParseData.getOutgoingUrls();
	//		  System.out.println("Text length: " + text.length());
	//		  System.out.println("Html length: " + html.length());
	//		  System.out.println("Number of outgoing links: " + links.size());
			  
			  visitRow[2]= Integer.toString(links.size());
			  
//			  myCrawlStat.incTotalLinks(links.size());
//	            try {
//	                myCrawlStat.incTotalTextSize(htmlParseData.getText().getBytes("UTF-8").length);
//	            } catch (UnsupportedEncodingException ignored) {
//	                // Do nothing
//	            }
		  	}
		  
		  myCrawlStat.addToVisitList(visitRow);  
	  } //end of visit method

    /**
     * This function is called by controller to get the local data of this crawler when job is
     * finished
     */
    @Override
    public Object getMyLocalData() {
        return myCrawlStat;
    }
}