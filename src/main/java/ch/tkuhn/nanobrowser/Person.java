package ch.tkuhn.nanobrowser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class Person extends Thing {
	
	public static final String TYPE_URI = "http://xmlns.com/foaf/0.1/Person";
	
	public Person(String uri) {
		super(uri);
	}
	
	private static final String allPersonsQuery =
		"select distinct ?p where { { ?a pav:authoredBy ?p } union { ?a pav:createdBy ?p } union { ?p a foaf:Person } }";
	
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

	private static final String isPersonQuery =
		"ask { { ?a pav:authoredBy <@> } union { <@> a foaf:Person } }";
	
	public static boolean isPerson(String uri) {
		return TripleStoreAccess.isTrue(isPersonQuery.replaceAll("@", uri));
	}
	
	private static final String authoredNanopubsQuery =
		"select distinct ?pub where { ?pub np:hasProvenance ?prov . ?prov np:hasAttribution ?att . " +
		"graph ?att { ?p pav:authoredBy <@> . ?p dc:created ?d } } order by desc(?d)";
	
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
		"prefix : <@I> insert data into graph : { :Pub a np:Nanopublication . :Pub np:hasAssertion :Ass . " +
		":Pub np:hasProvenance :Prov . :Prov np:hasAttribution :Att . :Prov np:hasSupporting :Supp } \n\n" +
		"prefix : <@I> insert data into graph :Ass { <@P> ex:agreeswith <@S> } \n\n" +
		"prefix : <@I> insert data into graph :Att { :Pub pav:authoredBy <@P> . :Pub dc:created \"@D\"^^xsd:dateTime }";
	
	public void publishAgreement(Sentence sentence) {
		String query = publishAgreementQuery
				.replaceAll("@I", "http://foo.org/" + (new Random()).nextInt(1000000000))
				.replaceAll("@P", getURI())
				.replaceAll("@S", sentence.getURI())
				.replaceAll("@D", NanobrowserApplication.getTimestamp());
		TripleStoreAccess.runUpdateQuery(query);
	}
	
	public PersonItem createGUIItem(String id) {
		return new PersonItem(id, this);
	}

}
