package ch.tkuhn.nanobrowser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PaperElement extends ThingElement {

	private static final long serialVersionUID = -1784269169556697743L;

	public static final String DOI_URI_BASE = "http://dx.doi.org/";

	public PaperElement(String uri) {
		super(uri);
	}

	public static PaperElement forDoi(String doi) throws IllegalArgumentException {
		if (doi == null) {
			throw new IllegalArgumentException("Empty DOI.");
		}
		if (doi.startsWith(DOI_URI_BASE)) doi = doi.substring(DOI_URI_BASE.length());
		doi = doi.replaceAll("\\s+", " ").replaceAll("^ ", "").replaceAll(" $", "");
		if (!doi.matches("10\\.[0-9][0-9][0-9][0-9]/.*")) {
			throw new IllegalArgumentException("Invalid DOI format.");
		}
		try {
			String d = DOI_URI_BASE + URLEncoder.encode(doi, "UTF8");
			d = d.replaceAll("%2F", "/");
			return new PaperElement(d);
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static boolean isDoiUri(String uri) {
		return uri.startsWith(DOI_URI_BASE);
	}

	public String getDoiString() {
		return getURI().replaceFirst("^[a-z]*://[^/]*/(.*)$", "$1");
	}

	public PaperItem createGUIItem(String id, int guiItemStyle) {
		return new PaperItem(id, this, guiItemStyle);
	}

}
