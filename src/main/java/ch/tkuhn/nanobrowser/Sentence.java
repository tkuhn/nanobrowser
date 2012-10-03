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
	
	public static List<Sentence> getAllSentences(int limit) {
		String query = "select distinct ?s where {?a <http://krauthammerlab.med.yale.edu/nanopub/extensions/asSentence> ?s}";
		if (limit >= 0) query += " limit " + limit;
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Sentence> l = new ArrayList<Sentence>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("s");
			if (v instanceof BNode) continue;
			l.add(new Sentence(v.stringValue()));
		}
		return l;
	}
	
	public String getSentenceText() {
		try {
			return URLDecoder.decode(getLastPartOfURI(), "UTF8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public List<Nanopub> getNanopubs() {
		String query = "select distinct ?p where { { " +
			"?p <http://www.nanopub.org/nschema#hasAssertion> ?a . " +
			"?a <http://krauthammerlab.med.yale.edu/nanopub/extensions/asSentence> <" + getURI() + "> . " +
			"} union { " +
			"?p <http://www.nanopub.org/nschema#hasAssertion> <" + getURI() + "> . " +
			" } }";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Nanopub> nanopubs = new ArrayList<Nanopub>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			nanopubs.add(new Nanopub(v.stringValue()));
		}
		return nanopubs;
	}
	
	public List<Person> getAgreers() {
		// TODO replace by more general method like getOpinions
		String query = "select distinct ?p where { " +
			"?p <http://krauthammerlab.med.yale.edu/nanopub/extensions/agreeswith> <" + getURI() + "> ." +
			" }";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Person> persons = new ArrayList<Person>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			persons.add(new Person(v.stringValue()));
		}
		return persons;
	}

}
