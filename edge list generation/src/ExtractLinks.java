import java.io.File;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractLinks {
	
	public static void main(String[] args) throws Exception{
		
		URL path = ClassLoader.getSystemResource("9007aeae-e11c-410d-90aa-73b347668484.html");
		File file = new File(path.toURI());
//		File file = new File(path);
		Document doc = Jsoup.parse(file,"UTF-8","http://www.huffingtonpost.com/");
		Elements links = doc.select("a[href]");
//		Elements media = doc.select("[src]");
		Elements imports = doc.select("link[href]");
		
//		print("\nMedia: (%d", media.size());		
//		for(Element src : media){
//			if(src.tagName().equals("img"))
//				print(" * %s: <%s> %sx%s (%s)",
//						src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
//						trim(src.attr("alt"),20));
//			else
//				print(" * %s: <%s>", src.tagName(),src.attr("abs:src"));
//		}
		
		print("Imports: %d", imports.size());
		for(Element link: imports){
//			print(" * %s: <%s>(%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
			System.out.println(link.attr("abs:href"));
		}
		
		print("\nLinks: %d", links.size());
		for(Element link: links){
//			print(" * a: <%s>(%s)", link.attr("abs:href"), trim(link.text(),35));
			System.out.println(link.attr("abs:href"));
		}
	}

//	private static String trim(String s, int width) {
//		if(s.length() > width)
//			return s.substring(0,width-1) + ".";
//		else
//			return s;
//	}

	private static void print(String msg, Object...args) {
		System.out.println(String.format(msg, args));
	}
}
