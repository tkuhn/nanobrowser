package ch.tkuhn.nanobrowser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class Agent extends Thing {
	
	private static final long serialVersionUID = -4281747788959702687L;
	
	public static final String TYPE_URI = "http://xmlns.com/foaf/0.1/Agent";
	
	public Agent(String uri) {
		super(uri);
	}
	
	private static final String allPersonsQuery =
		"select distinct ?p where { { ?a pav:authoredBy ?p } union { ?a pav:createdBy ?p } union { ?p a foaf:Person } " +
		"union { ?p a foaf:Agent } . optional { ?p a npx:Bot . ?p a ?x } filter (!bound(?x)) }";
	
	public static List<Agent> getAllPersons(int limit) {
		String lm = (limit >= 0) ? " limit " + limit : "";
		List<BindingSet> result = TripleStoreAccess.getTuples(allPersonsQuery + lm);
		List<Agent> l = new ArrayList<Agent>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			l.add(new Agent(v.stringValue()));
		}
		return l;
	}

	private static final String isAgentQuery =
		"ask { { ?a pav:createdBy <@> } union { ?a pav:authoredBy <@> } union { <@> a foaf:Person } " +
		"union { <@> a foaf:Agent } union { <@> a npx:Bot } }";
	
	public static boolean isAgent(String uri) {
		return TripleStoreAccess.isTrue(isAgentQuery.replaceAll("@", uri));
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

	private static final String isBotQuery =
		"ask { <@> a npx:Bot }";
	
	public boolean isBot() {
		return TripleStoreAccess.isTrue(isBotQuery.replaceAll("@", getURI()));
	}
	
	public String getName() {
		String name = getLabel();
		if (name == null) name = getLastPartOfURI();
		return name;
	}
	
	public AgentItem createGUIItem(String id) {
		return new AgentItem(id, this);
	}

}
