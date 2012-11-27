package ch.tkuhn.nanobrowser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class Nanopub extends Thing {
	
	private static final long serialVersionUID = -62516765710631807L;
	
	public static final String TYPE_URI = "http://www.nanopub.org/nschema#Nanopublication";
	
	public Nanopub(String uri) {
		super(uri);
	}

	private static final String nonmetaNanopubsQuery =
		"select distinct ?p where { ?p a np:Nanopublication . ?p np:hasProvenance ?prov . " +
		"?prov np:hasAttribution ?att . graph ?att { ?p dc:created ?d } ." +
		"filter not exists { ?p a npx:MetaNanopub } } order by desc(?d)";
	
	public static List<Nanopub> getNonmetaNanopubs(int limit) {
		String lm = (limit >= 0) ? " limit " + limit : "";
		List<BindingSet> result = TripleStoreAccess.getTuples(nonmetaNanopubsQuery + lm);
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
		"select distinct ?c where { <@> np:hasAssertion ?a . ?a npx:asSentence ?c }";
	
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
		"{ <@> np:hasAssertion ?g . ?g npx:asFormula ?f } . graph ?f {?a ?b ?c} }";
	
	public List<Statement> getAssertionTriples() {
		String query = assertionTriplesQuery.replaceAll("@", getURI());
		return TripleStoreAccess.getGraph(query);
	}
	
	private static final String supportingTriplesQuery =
		"construct {?a ?b ?c} where { <@> np:hasProvenance ?p . ?p np:hasSupporting ?s . " +
		"graph ?s {?a ?b ?c} }";
	
	public List<Statement> getSupportingTriples() {
		String query = supportingTriplesQuery.replaceAll("@", getURI());
		return TripleStoreAccess.getGraph(query);
	}
	
	private static final String createDateQuery =
		"select ?d where { <@> np:hasProvenance ?prov . ?prov np:hasAttribution ?att . " +
		"graph ?att { <@> dc:created ?d } }";
		//"select ?d where { <@> dc:created ?d }";
	
	public String getCreateDateString() {
		String query = createDateQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		if (result.size() == 0) return null;
		String s = result.get(0).getValue("d").stringValue();
		if (s.equals("null")) return null;
		if (s.length() > 16) return s.substring(0, 16);
		return s;
	}
	
	private static final String authorsQuery =
		"select distinct ?a where { <@> np:hasProvenance ?prov . ?prov np:hasAttribution ?att . " +
		"graph ?att { <@> pav:authoredBy ?a } }";
		//"select distinct ?a where { <@> pav:authoredBy ?a }";
	
	public List<Agent> getAuthors() {
		String query = authorsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Agent> l = new ArrayList<Agent>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("a");
			if (v instanceof BNode) continue;
			l.add(new Agent(v.stringValue()));
		}
		return l;
	}
	
	private static final String creatorsQuery =
		"select distinct ?c where { <@> np:hasProvenance ?prov . ?prov np:hasAttribution ?att . " +
		"graph ?att { <@> pav:createdBy ?c } }";
		//"select distinct ?a where { <@> pav:createdBy ?a }";
	
	public List<Agent> getCreators() {
		String query = creatorsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Agent> l = new ArrayList<Agent>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("c");
			if (v instanceof BNode) continue;
			l.add(new Agent(v.stringValue()));
		}
		return l;
	}

	private static final String opinionsQuery =
		"select ?p ?t ?pub ?s where { " +
		"?pub np:hasAssertion ?ass . ?pub np:hasProvenance ?prov . " +
		"?prov np:hasAttribution ?att . graph ?att { ?pub dc:created ?d } . " +
		"graph ?ass { ?p npx:hasOpinion ?o . ?o rdf:type ?t . ?o npx:opinionOn ?s } ." +
		"<@> np:hasAssertion ?a . ?a npx:asSentence ?s } order by asc(?d)";
	
	public List<Opinion> getOpinions(boolean excludeNullOpinions) {
		String query = opinionsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		Map<String, Opinion> opinionMap = new HashMap<String, Opinion>();
		for (BindingSet bs : result) {
			Value p = bs.getValue("p");
			Value t = bs.getValue("t");
			Value s = bs.getValue("s");
			Value pub = bs.getValue("pub");
			if (p instanceof BNode || t instanceof BNode || s instanceof BNode || pub instanceof BNode) continue;
			if (excludeNullOpinions && t.stringValue().equals(Opinion.NULL_TYPE)) {
				opinionMap.remove(p.stringValue());
			} else {
				Agent agent = new Agent(p.stringValue());
				Nanopub nanopub = new Nanopub(pub.stringValue());
				Sentence sentence = new Sentence(s.stringValue());
				Opinion opinion = new Opinion(agent, t.stringValue(), sentence, nanopub);
				opinionMap.put(p.stringValue(), opinion);
			}
		}
		return new ArrayList<Opinion>(opinionMap.values());
	}
	
	private static final String getNanopubGraphsQuery =
		"select ?g ?ass ?att ?supp ?f where { " +
		"graph ?g { <@> np:hasAssertion ?ass . <@> np:hasProvenance ?prov . ?prov np:hasAttribution ?att . " +
		"optional { ?prov np:hasSupporting ?supp } . optional { ?ass np:containsGraph ?f } } }";
	private static final String deleteGraphQuery =
		"delete from graph identified by <@> { ?a ?b ?c } where  { ?a ?b ?c }";
	
	public void delete() {
		String query = getNanopubGraphsQuery.replaceAll("@", getURI());
		for (BindingSet bs : TripleStoreAccess.getTuples(query)) {
			Value g = bs.getValue("g");
			Value ass = bs.getValue("ass");
			Value att = bs.getValue("att");
			Value supp = bs.getValue("supp");
			Value f = bs.getValue("f");
			if (g != null) TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", g.stringValue()));
			if (ass != null) TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", ass.stringValue()));
			if (att != null) TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", att.stringValue()));
			if (supp != null) TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", supp.stringValue()));
			if (f != null) TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", f.stringValue()));
		}
	}
	
	private static final String getNanopubGraphsWithPropertyQuery =
		"select ?g ?ass ?att where { graph ?ass { ?x @P ?y } . " +
		"graph ?g { ?pub np:hasAssertion ?ass . ?pub np:hasProvenance ?prov . ?prov np:hasAttribution ?att } }";
	
	public static void deleteAllNanopubsWithProperty(String propertyURI) {
		if (propertyURI.matches("^[a-z]+://")) {
			propertyURI = "<" + propertyURI + ">";
		}
		String query = getNanopubGraphsWithPropertyQuery.replaceAll("@P", propertyURI);
		for (BindingSet bs : TripleStoreAccess.getTuples(query)) {
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("g").stringValue()));
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("ass").stringValue()));
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("att").stringValue()));
		}
	}
	
	public NanopubItem createGUIItem(String id) {
		return new NanopubItem(id, this, true, false);
	}

}
