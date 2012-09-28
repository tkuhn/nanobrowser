package ch.tkuhn.nanobrowser;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class NanopubAccess {
	
	// No instances allowed:
	private NanopubAccess() {}
	
	public static List<String> getAllNanopubs(int limit) {
		String nanopubsQuery = "select distinct ?p where {?p a <http://www.nanopub.org/nschema#Nanopublication>}";
		if (limit >= 0) nanopubsQuery += " limit " + limit;
		List<BindingSet> result = TripleStoreAccess.getTuples(nanopubsQuery);
		List<String> nanopubs = new ArrayList<String>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			nanopubs.add(v.stringValue());
		}
		return nanopubs;
	}
	
	public static List<String> getNanopubs(String assertionURI) {
		String query = "select distinct ?p where { { " +
			"?p <http://www.nanopub.org/nschema#hasAssertion> ?a . " +
			"?a <http://krauthammerlab.med.yale.edu/nanopub/extensions/asSentence> <" + assertionURI + "> . " +
			"} union { " +
			"?p <http://www.nanopub.org/nschema#hasAssertion> <" + assertionURI + "> . " +
			" } }";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<String> nanopubs = new ArrayList<String>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			nanopubs.add(v.stringValue());
		}
		return nanopubs;
	}
	
	public static List<String> getAllSentenceAssertions(int limit) {
		String query = "select distinct ?c where {?s <http://krauthammerlab.med.yale.edu/nanopub/extensions/asSentence> ?c}";
		if (limit >= 0) query += " limit " + limit;
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<String> l = new ArrayList<String>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("c");
			if (v instanceof BNode) continue;
			l.add(v.stringValue());
		}
		return l;
	}
	
	public static List<String> getSentenceAssertions(String pubURI) {
		String query = "select distinct ?c where {" +
			"<" + pubURI + "> <http://www.nanopub.org/nschema#hasAssertion> ?a . " +
			"?a <http://krauthammerlab.med.yale.edu/nanopub/extensions/asSentence> ?c . " +
			"}";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<String> l = new ArrayList<String>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("c");
			if (v instanceof BNode) continue;
			l.add(v.stringValue());
		}
		return l;
	}
	
	public static List<String> getAllFormulaAssertions(int limit) {
		String query = "select distinct ?f where { { " +
			"{?s <http://www.nanopub.org/nschema#hasAssertion> ?f}" +
			" union " +
			"{?s <http://krauthammerlab.med.yale.edu/nanopub/extensions/asFormula> ?f}" +
			" } graph ?f {?a ?b ?c} }";
		if (limit >= 0) query += " limit " + limit;
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<String> l = new ArrayList<String>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("f");
			if (v instanceof BNode) continue;
			l.add(v.stringValue());
		}
		return l;
	}
	
	public static List<String> getFormulaAssertions(String pubURI) {
		String query = "select distinct ?f where { { " +
			"{<" + pubURI + "> <http://www.nanopub.org/nschema#hasAssertion> ?f}" +
			" union " +
			"{<" + pubURI + "> <http://krauthammerlab.med.yale.edu/nanopub/extensions/asFormula> ?f}" +
			" } graph ?f {?a ?b ?c} }";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<String> l = new ArrayList<String>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("f");
			if (v instanceof BNode) continue;
			l.add(v.stringValue());
		}
		return l;
	}
	
	public static String getCreateDateString(String pubURI) {
		String query = "select ?d where {" +
			"<" + pubURI + "> <http://purl.org/dc/terms/created> ?d . " +
			"}";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		if (result.size() == 0) return "(unknown)";
		return result.get(0).getValue("d").stringValue();
	}
	
	public static List<String> getAllPersons(int limit) {
		String query = "select distinct ?a where {" +
			"?p <http://swan.mindinformatics.org/ontologies/1.2/pav/authoredBy> ?a . " +
			"}";
		if (limit >= 0) query += " limit " + limit;
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<String> l = new ArrayList<String>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("a");
			if (v instanceof BNode) continue;
			l.add(v.stringValue());
		}
		return l;
	}
	
	public static List<String> getAuthors(String pubURI) {
		String query = "select distinct ?a where {" +
			"<" + pubURI + "> <http://swan.mindinformatics.org/ontologies/1.2/pav/authoredBy> ?a . " +
			"}";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<String> l = new ArrayList<String>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("a");
			if (v instanceof BNode) continue;
			l.add(v.stringValue());
		}
		return l;
	}

}
