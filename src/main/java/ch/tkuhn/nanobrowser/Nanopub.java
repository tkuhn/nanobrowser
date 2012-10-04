package ch.tkuhn.nanobrowser;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class Nanopub extends Thing {
	
	public static final String TYPE_URI = "http://www.nanopub.org/nschema#Nanopublication";
	
	public Nanopub(String uri) {
		super(uri);
	}

	private static final String allNanopubsQuery =
		"select distinct ?p where { ?p a np:Nanopublication }";
	
	public static List<Nanopub> getAllNanopubs(int limit) {
		String lm = (limit >= 0) ? " limit " + limit : "";
		List<BindingSet> result = TripleStoreAccess.getTuples(allNanopubsQuery + lm);
		List<Nanopub> nanopubs = new ArrayList<Nanopub>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			nanopubs.add(new Nanopub(v.stringValue()));
		}
		return nanopubs;
	}

	private static final String isNanopubQuery =
		"ask { <@> a np:Nanopublication }";
	
	public static boolean isNanopub(String uri) {
		return TripleStoreAccess.isTrue(isNanopubQuery.replaceAll("@", uri));
	}
	
	private static final String sentenceAssertionsQuery =
		"select distinct ?c where { <@> np:hasAssertion ?a . ?a ex:asSentence ?c }";
	
	public List<Sentence> getSentenceAssertions() {
		String query = sentenceAssertionsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Sentence> l = new ArrayList<Sentence>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("c");
			if (v instanceof BNode) continue;
			l.add(new Sentence(v.stringValue()));
		}
		return l;
	}
	
	private static final String assertionTriplesQuery =
		"construct {?a ?b ?c} where { { <@> np:hasAssertion ?f } union " +
		"{ <@> np:hasAssertion ?g . ?g ex:asFormula ?f } . graph ?f {?a ?b ?c} }";
	
	public List<Statement> getAssertionTriples() {
		String query = assertionTriplesQuery.replaceAll("@", getURI());
		return TripleStoreAccess.getGraph(query);
	}
	
	private static final String createDateQuery =
		"select ?d where { { <@> dc:created ?d } union { <@> np:hasProvenance ?prov . ?prov np:hasAttribution ?att . " +
		"graph ?att { ?p dc:created ?d } } }";
	
	public String getCreateDateString() {
		String query = createDateQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		if (result.size() == 0) return null;
		return result.get(0).getValue("d").stringValue().substring(0, 10);
	}
	
	private static final String authorsQuery =
		"select distinct ?a where { { <@> pav:authoredBy ?a } union { <@> np:hasProvenance ?prov . " +
		"?prov np:hasAttribution ?att . graph ?att { ?p pav:authoredBy ?a } } }";
	
	public List<Person> getAuthors() {
		String query = authorsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Person> l = new ArrayList<Person>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("a");
			if (v instanceof BNode) continue;
			l.add(new Person(v.stringValue()));
		}
		return l;
	}
	
	public NanopubItem createGUIItem(String id) {
		return new NanopubItem(id, this);
	}

}
