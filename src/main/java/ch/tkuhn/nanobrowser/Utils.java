package ch.tkuhn.nanobrowser;

import java.net.URLDecoder;

public class Utils {
	
	// No instances allowed:
	private Utils() {}
	
	public static String getLastPartOfURL(String urlString) {
		return urlString.replaceFirst("^.*[/#]([^/#]*)$", "$1");
	}
	
	public static String getSentenceFromURL(String urlString) {
		try {
			return URLDecoder.decode(getLastPartOfURL(urlString), "UTF8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
