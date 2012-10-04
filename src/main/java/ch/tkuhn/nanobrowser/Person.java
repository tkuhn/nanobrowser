package ch.tkuhn.nanobrowser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class Person extends Thing {
	
	public Person(String uri) {
		super(uri);
	}
	
	private static final String allPersonsQuery =
		"select distinct ?p where { ?a pav:authoredBy ?p }";
	
	public static List<Person> getAllPersons(int limit) {
		String lm = (limit >= 0) ? " limit " + limit : "";
		List<BindingSet> result = TripleStoreAccess.getTuples(allPersonsQuery + lm);
		List<Person> l = new ArrayList<Person>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			l.add(new Person(v.stringValue()));
		}
		return l;
	}
	
	private static final String authoredNanopubsQuery =
		"select distinct ?pub where { { ?pub pav:authoredBy <@> } union { " +
		"?pub np:hasProvenance ?prov . ?prov np:hasAttribution ?att . " +
		"graph ?att { ?p pav:authoredBy <@> } } }";
	
	public List<Nanopub> getAuthoredNanopubs() {
		String query = authoredNanopubsQuery.replaceAll("@", getURI());
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
	
	private static final String publishAgreementQuery =
		"prefix : <@I> insert data into graph : { :Pub np:hasAssertion :Ass . :Pub np:hasProvenance :Prov . " +
		":Prov np:hasAttribution :Att . :Prov np:hasSupporting :Supp } \n\n" +
		"prefix : <@I> insert data into graph :Ass { <@P> ex:agreeswith <@S> } \n\n" +
		"prefix : <@I> insert data into graph :Att { :Pub pav:authoredBy <@P> }";
	
	public void publishAgreement(Sentence sentence) {
		String query = publishAgreementQuery
				.replaceAll("@I", "http://foo.org/" + (new Random()).nextInt(1000000000))
				.replaceAll("@P", getURI())
				.replaceAll("@S", sentence.getURI());
		TripleStoreAccess.runUpdateQuery(query);
	}

}
