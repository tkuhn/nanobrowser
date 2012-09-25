package ch.tkuhn.nanobrowser;

import java.net.URLDecoder;

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

}
