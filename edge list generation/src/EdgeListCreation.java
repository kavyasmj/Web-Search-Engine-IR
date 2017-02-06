import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EdgeListCreation {
	public static void main(String[] args) throws Exception{
		
		
		File text_op = new File("edgeList.txt");
		// create a new writer
	    PrintWriter writer = new PrintWriter(text_op);
	    
	    // Create a hash map
	    HashMap<String , String> fileUrlMap = new HashMap<String , String>();
	    HashMap<String , String> urlFileMap = new HashMap<String , String>();
		
		URL path = ClassLoader.getSystemResource("mapLATimesDataFile.xlsx");
		File file = new File(path.toURI());
        FileInputStream inputStream = new FileInputStream(file);
         
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();
        
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            Cell cell = nextRow.cellIterator().next();            
            String string = cell.getStringCellValue();
            String[] parts = string.split(",", 2);
            String part1 = parts[0]; 
            String part2 = parts[1]; 
            
          //Add Key/Value pairs
            fileUrlMap.put(part1, part2);
            urlFileMap.put(part2, part1);
        }
         
        workbook.close();
        inputStream.close();
          
		File dir = new File("C:\\Users\\kavya\\Downloads\\LATimesHuffingtonPostData\\LATimesDownloadData");
		Set<String> edges = new HashSet<String>();
		for(File file1: dir.listFiles()){
			Document doc = Jsoup.parse(file1, "UTF-8", fileUrlMap.get(file1.getName()));
			Elements links = doc.select("a[href]");
			Elements pngs = doc.select("[src]");
			
			for(Element link: links){
				String url = link.attr("abs:href").trim();
//				String url = link.attr("href").trim();
				if(urlFileMap.containsKey(url)){
					edges.add(file1.getName() + " " + urlFileMap.get(url));
				}
			}
		}
		
		for(String s: edges){
			writer.println(s);
		}
		writer.flush();
		writer.close();
		
	}

}
