package ch.tkuhn.nanobrowser;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.query.BindingSet;

public class Thing {
	
	public static final String TYPE_URI = "http://www.w3.org/2002/07/owl#Thing";
	
	private final String uri;
	
	public static Thing getThing(Resource r) {
		String uri = r.stringValue();
		List<String> types = getTypes(uri);
		if (types.contains(Person.TYPE_URI)) return new Person(uri);
		if (types.contains(Sentence.TYPE_URI)) return new Sentence(uri);
		if (types.contains(Nanopub.TYPE_URI)) return new Nanopub(uri);
		return new Thing(uri);
	}
	
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
	
	private static final String labelsQuery =
		"select ?l where { <@> rdfs:label ?l }";
	
	public List<String> getLabels() {
		String query = labelsQuery.replaceAll("@", getURI());
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
	
	public ThingItem createGUIItem(String id) {
		return new ThingItem(id, this);
	}
	
	public static List<String> getTypes(String uri) {
		// TODO improve this; should be just one SPARQL query
		List<String> types = new ArrayList<String>();
		if (Sentence.isSentence(uri)) types.add(Sentence.TYPE_URI);
		if (Person.isPerson(uri)) types.add(Person.TYPE_URI);
		if (Nanopub.isNanopub(uri)) types.add(Nanopub.TYPE_URI);
		return types;
	}

}
