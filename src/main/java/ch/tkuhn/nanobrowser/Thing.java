package ch.tkuhn.nanobrowser;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.query.BindingSet;

public class Thing {
	
	private final String uri;
	
	public Thing(String uri) {
		this.uri = uri;
	}
	
	public String getURI() {
		return uri;
	}
	
	public static String getLastPartOfURI(String uri) {
		return uri.replaceFirst("^.*[/#]([^/#]*)$", "$1");
	}
	
	public String getLastPartOfURI() {
		return getLastPartOfURI(uri);
	}
	
	public List<String> getLabels() {
		String query = "select ?l where { <" + getURI() + "> <http://www.w3.org/2000/01/rdf-schema#label> ?l }";
		List<String> labels = new ArrayList<String>();
		for (BindingSet bs : TripleStoreAccess.getTuples(query)) {
			labels.add(bs.getValue("l").stringValue());
		}
		return labels;
	}
	
	public String getLabel() {
		List<String> labels = getLabels();
		if (labels.size() == 0) return null;
		return labels.get(0);
	}

}
