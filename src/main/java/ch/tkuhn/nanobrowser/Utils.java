package ch.tkuhn.nanobrowser;

import java.net.URLDecoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openrdf.model.Statement;

public class Utils {
	
	// No instances allowed:
	private Utils() {}
	
	public static String getLastPartOfURI(String uriString) {
		return uriString.replaceFirst("^.*[/#]([^/#]*)$", "$1");
	}
	
	public static String getSentenceFromURI(String uriString) {
		try {
			return URLDecoder.decode(getLastPartOfURI(uriString), "UTF8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static String getGraphSummary(List<Statement> graph) {
		Collections.sort(graph, triplesComparator);
		
		String out = "";
		String subject = "";
		String predicate = "";
		
		for (Statement t : graph) {
			String s = t.getSubject().stringValue();
			String p = t.getPredicate().stringValue();
			String o = t.getObject().stringValue();
			
			if (p.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) continue;
			if (p.equals("http://www.w3.org/2000/01/rdf-schema#comment")) continue;
			
			String sl = " " + getLastPartOfURI(s);
			String pl = " " + getLastPartOfURI(p);
			String ol = " " + getLastPartOfURI(o);
			
			if ((subject + " " + predicate).equals(s + " " + p)) {
				out += "," + ol;
			} else if (subject.equals(s)) {
				out += ";" + pl + ol;
			} else {
				out += "." + sl + pl + ol;
			}
			
			subject = s;
			predicate = p;
		}
		
		out = out.replaceFirst("^\\. ", "") + ".";
		
		return out;
	}
	
	public static Comparator<Statement> triplesComparator = new Comparator<Statement>() {
		public int compare(Statement o1, Statement o2) {
			int d = o1.getSubject().stringValue().compareTo(o2.getSubject().stringValue());
			if (d == 0) {
				d = o1.getPredicate().stringValue().compareTo(o2.getPredicate().stringValue());
			}
			if (d == 0) {
				d = o1.getObject().stringValue().compareTo(o2.getObject().stringValue());
			}
			return d;
		};
	};

}
