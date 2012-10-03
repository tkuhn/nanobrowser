package ch.tkuhn.nanobrowser;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class Sentence extends Thing {
	
	public Sentence(String uri) {
		super(uri);
	}
	
	private static final String allSentencesQuery =
		"select distinct ?s where {?a ex:asSentence ?s}";
	
	public static List<Sentence> getAllSentences(int limit) {
		String lm = (limit >= 0) ? " limit " + limit : "";
		List<BindingSet> result = TripleStoreAccess.getTuples(allSentencesQuery + lm);
		List<Sentence> l = new ArrayList<Sentence>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("s");
			if (v instanceof BNode) continue;
			l.add(new Sentence(v.stringValue()));
		}
		return l;
	}
	
	private static final String nanopubsQuery =
		"select distinct ?p where { { ?p np:hasAssertion ?a . ?a ex:asSentence <@> } union { " +
		"?p np:hasAssertion <@> . } }";
	
	public List<Nanopub> getNanopubs() {
		String query = nanopubsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Nanopub> nanopubs = new ArrayList<Nanopub>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			nanopubs.add(new Nanopub(v.stringValue()));
		}
		return nanopubs;
	}

	// TODO replace by something more general like "opinions"
	
	private static final String agreersQuery =
		"select distinct ?p where { ?p ex:agreeswith <@> }";
	
	public List<Person> getAgreers() {
		String query = agreersQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Person> persons = new ArrayList<Person>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			persons.add(new Person(v.stringValue()));
		}
		return persons;
	}
	
	public String getSentenceText() {
		try {
			return URLDecoder.decode(getLastPartOfURI(), "UTF8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
