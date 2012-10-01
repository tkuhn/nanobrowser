package ch.tkuhn.nanobrowser;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class Nanopub extends Thing {
	
	public Nanopub(String uri) {
		super(uri);
	}
	
	public static List<Nanopub> getAllNanopubs(int limit) {
		String nanopubsQuery = "select distinct ?p where {?p a <http://www.nanopub.org/nschema#Nanopublication>}";
		if (limit >= 0) nanopubsQuery += " limit " + limit;
		List<BindingSet> result = TripleStoreAccess.getTuples(nanopubsQuery);
		List<Nanopub> nanopubs = new ArrayList<Nanopub>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			nanopubs.add(new Nanopub(v.stringValue()));
		}
		return nanopubs;
	}
	
	public List<Sentence> getSentenceAssertions() {
		String query = "select distinct ?c where {" +
			"<" + getURI() + "> <http://www.nanopub.org/nschema#hasAssertion> ?a . " +
			"?a <http://krauthammerlab.med.yale.edu/nanopub/extensions/asSentence> ?c . " +
			"}";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Sentence> l = new ArrayList<Sentence>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("c");
			if (v instanceof BNode) continue;
			l.add(new Sentence(v.stringValue()));
		}
		return l;
	}
	
	public List<Statement> getAssertionTriples() {
		String query = "construct {?a ?b ?c} where { { " +
			"{<" + getURI() + "> <http://www.nanopub.org/nschema#hasAssertion> ?f}" +
			" union " +
			"{<" + getURI() + "> <http://krauthammerlab.med.yale.edu/nanopub/extensions/asFormula> ?f}" +
			" } graph ?f {?a ?b ?c} }";
		return TripleStoreAccess.getGraph(query);
	}
	
	public String getCreateDateString() {
		String query = "select ?d where { { " +
			"{ <" + getURI() + "> <http://purl.org/dc/terms/created> ?d . }" +
			" } union { " +
			"{ <" + getURI() + "> <http://www.nanopub.org/nschema#hasProvenance> ?prov } " +
			"{ ?prov <http://www.nanopub.org/nschema#hasAttribution> ?att } " +
			"graph ?att { ?p <http://purl.org/dc/terms/created> ?d } " +
			" } }";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		if (result.size() == 0) return null;
		return result.get(0).getValue("d").stringValue().substring(0, 10);
	}
	
	public List<Person> getAuthors() {
		String query = "select distinct ?a where { { " +
			"{ <" + getURI() + "> <http://swan.mindinformatics.org/ontologies/1.2/pav/authoredBy> ?a . }" +
			" } union { " +
			"{ <" + getURI() + "> <http://www.nanopub.org/nschema#hasProvenance> ?prov } " +
			"{ ?prov <http://www.nanopub.org/nschema#hasAttribution> ?att } " +
			"graph ?att { ?p <http://swan.mindinformatics.org/ontologies/1.2/pav/authoredBy> ?a } " +
			" } }";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Person> l = new ArrayList<Person>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("a");
			if (v instanceof BNode) continue;
			l.add(new Person(v.stringValue()));
		}
		return l;
	}

}
