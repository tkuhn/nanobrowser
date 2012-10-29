package ch.tkuhn.nanobrowser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class Person extends Thing {
	
	private static final long serialVersionUID = -4281747788959702687L;
	
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
		"select distinct ?pub where { ?pub a np:Nanopublication . ?pub np:hasProvenance ?prov . " +
		"?prov np:hasAttribution ?att . graph ?att { ?pub pav:authoredBy <@> . ?pub dc:created ?d } " +
		"filter not exists { ?pub a npx:MetaNanopub } } order by desc(?d)";
	
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

	private static final String opinionsQuery =
		"select ?s ?t ?pub where { " +
		"?pub np:hasAssertion ?ass . ?pub np:hasProvenance ?prov . " +
		"?prov np:hasAttribution ?att . graph ?att { ?pub dc:created ?d } . " +
		"graph ?ass { <@> npx:hasOpinion ?o . ?o rdf:type ?t . ?o npx:opinionOn ?s } } order by asc(?d)";
	
	public List<Opinion> getOpinions(boolean excludeNullOpinions) {
		String query = opinionsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		Map<String, Opinion> opinionMap = new HashMap<String, Opinion>();
		for (BindingSet bs : result) {
			Value s = bs.getValue("s");
			Value t = bs.getValue("t");
			Value pub = bs.getValue("pub");
			if (s instanceof BNode || t instanceof BNode || pub instanceof BNode) continue;
			if (excludeNullOpinions && t.stringValue().equals(Opinion.NULL_TYPE)) {
				opinionMap.remove(s.stringValue());
			} else {
				Sentence sentence = new Sentence(s.stringValue());
				Nanopub nanopub = new Nanopub(pub.stringValue());
				Opinion opinion = new Opinion(this, t.stringValue(), sentence, nanopub);
				opinionMap.put(s.stringValue(), opinion);
			}
		}
		return new ArrayList<Opinion>(opinionMap.values());
	}
	
	public String getName() {
		String name = getLabel();
		if (name == null) name = getLastPartOfURI();
		return name;
	}
	
	public PersonItem createGUIItem(String id) {
		return new PersonItem(id, this);
	}

}
