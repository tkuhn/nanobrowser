package ch.tkuhn.nanobrowser;

public class PaperElement extends ThingElement {

	private static final long serialVersionUID = -1784269169556697743L;

	public static final String DOI_URI_BASE = "http://dx.doi.org/";

	public PaperElement(String uri) {
		super(uri);
	}

	public String getDoiString() {
		return getURI().replaceFirst("^[a-z]*://[^/]*/(.*)$", "$1");
	}

	public PaperItem createGUIItem(String id, int guiItemStyle) {
		return new PaperItem(id, this, guiItemStyle);
	}

}
