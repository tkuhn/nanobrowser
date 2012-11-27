package ch.tkuhn.nanobrowser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class Thing implements Serializable {
	
	private static final long serialVersionUID = -6229734698248756258L;

	public static final String TYPE_URI = "http://www.w3.org/2002/07/owl#Thing";
	
	private final String uri;
	
	public static Thing getThing(String uri) {
		List<String> types = getTypes(uri);
		if (types.contains(Agent.TYPE_URI)) return new Agent(uri);
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
	
	public String getTruncatedURI() {
		if (uri.length() > 120) {
			return uri.substring(0, 117) + "...";
		} else {
			return uri;
		}
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

	private static final String typesQuery =
		"select distinct ?t where { <@> a ?t }";
	
	public List<String> getTypes() {
		return getTypes(getURI());
	}
	
	public static List<String> getTypes(String uri) {
		// TODO improve this; should be just one SPARQL query
		String query = typesQuery.replaceAll("@", uri);
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		Set<String> types = new HashSet<String>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("t");
			if (v instanceof BNode) continue;
			types.add(v.stringValue());
		}
		if (Sentence.isSentenceURI(uri)) types.add(Sentence.TYPE_URI);
		if (Agent.isAgent(uri)) types.add(Agent.TYPE_URI);
		if (Nanopub.isNanopub(uri)) types.add(Nanopub.TYPE_URI);
		return new ArrayList<String>(types);
	}
	
	public String toString() {
		return getURI();
	}
	
	public boolean equals(Object other) {
		if (other instanceof Thing) {
			return getURI().equals(((Thing) other).getURI());
		} else {
			return false;
		}
	}

}
