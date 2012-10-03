package ch.tkuhn.nanobrowser;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class Person extends Thing {
	
	public Person(String uri) {
		super(uri);
	}
	
	public static List<Person> getAllPersons(int limit) {
		String query = "select distinct ?p where {" +
			"?a <http://swan.mindinformatics.org/ontologies/1.2/pav/authoredBy> ?p . " +
			"}";
		if (limit >= 0) query += " limit " + limit;
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Person> l = new ArrayList<Person>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			l.add(new Person(v.stringValue()));
		}
		return l;
	}
	
	public List<Nanopub> getAuthoredNanopubs() {
		String query = "select distinct ?pub where { { " +
			"{ ?pub <http://swan.mindinformatics.org/ontologies/1.2/pav/authoredBy> <" + getURI() + "> . }" +
			" } union { " +
			"{ ?pub <http://www.nanopub.org/nschema#hasProvenance> ?prov } " +
			"{ ?prov <http://www.nanopub.org/nschema#hasAttribution> ?att } " +
			"graph ?att { ?p <http://swan.mindinformatics.org/ontologies/1.2/pav/authoredBy> <" + getURI() + "> } " +
			" } }";
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Nanopub> l = new ArrayList<Nanopub>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("pub");
			if (v instanceof BNode) continue;
			l.add(new Nanopub(v.stringValue()));
		}
		return l;
	}
	
	public String getName() {
		String name = getLabel();
		if (name == null) name = getLastPartOfURI();
		return name;
	}
	
	public void publishAgreement(Sentence sentence) {
		// TODO publish as proper nanopublication
		TripleStoreAccess.runUpdateQuery("insert data " +
			"into graph identified by <http://foo.com> " +
			"{ <" + getURI() + "> <http://krauthammerlab.med.yale.edu/nanopub/extensions/agreeswith> <" + sentence.getURI() + "> . }\n");
	}

}
