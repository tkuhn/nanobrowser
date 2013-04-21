// This file is part of Nanobrowser.
// Copyright 2012, Tobias Kuhn, http://www.tkuhn.ch
//
// Nanobrowser is free software: you can redistribute it and/or modify it under the terms of the
// GNU Lesser General Public License as published by the Free Software Foundation, either version
// 3 of the License, or (at your option) any later version.
//
// Nanobrowser is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
// even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License along with Nanobrowser.
// If not, see http://www.gnu.org/licenses/.

package ch.tkuhn.nanobrowser;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.util.io.IOUtils;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

import ch.tkuhn.hashuri.rdf.CheckNanopub;
import ch.tkuhn.nanopub.Nanopub;

public class NanopubElement extends ThingElement {
	
	private static final long serialVersionUID = -62516765710631807L;
	
	public static final String TYPE_URI = Nanopub.NANOPUB_TYPE_URI.toString();

	private Nanopub nanopub;
	private Boolean isValid;

	public NanopubElement(String uri) {
		super(uri);
		nanopub = TripleStoreAccess.getNanopub(uri);
	}

	public Nanopub getNanopub() {
		return nanopub;
	}

	public boolean isValid() {
		if (isValid != null) return isValid;
		if (nanopub == null) return false;
		isValid = CheckNanopub.isValid(nanopub);
		return isValid;
	}

	private static final String nonmetaNanopubsQuery =
		"select distinct ?p where { ?p a np:Nanopublication . ?p np:hasPublicationInfo ?info . " +
		"graph ?info { ?p dc:created ?d } ." +
		"filter not exists { ?p a npx:MetaNanopub } } order by desc(?d)";
	
	public static List<NanopubElement> getNonmetaNanopubs(int limit) {
		String lm = (limit >= 0) ? " limit " + limit : "";
		List<BindingSet> result = TripleStoreAccess.getTuples(nonmetaNanopubsQuery + lm);
		List<NanopubElement> nanopubs = new ArrayList<NanopubElement>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			nanopubs.add(new NanopubElement(v.stringValue()));
		}
		return nanopubs;
	}

	public static boolean isNanopub(String uri) {
		return TripleStoreAccess.getNanopub(uri) != null;
	}
	
	private static final String sentenceAssertionsQuery =
		"select distinct ?c where { <@> np:hasAssertion ?a . ?a npx:asSentence ?c }";
	
	public List<SentenceElement> getSentenceAssertions() {
		String query = sentenceAssertionsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<SentenceElement> l = new ArrayList<SentenceElement>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("c");
			if (v instanceof BNode) continue;
			l.add(new SentenceElement(v.stringValue()));
		}
		return l;
	}
	
	private static final String assertionTriplesQuery =
		"construct {?a ?b ?c} where { { <@> np:hasAssertion ?f } union " +
		"{ <@> np:hasAssertion ?g . ?g npx:asFormula ?f } . graph ?f {?a ?b ?c} }";
	
	public List<Triple<?,?>> getAssertionTriples() {
		String query = assertionTriplesQuery.replaceAll("@", getURI());
		return TripleStoreAccess.getGraph(query);
	}

	public List<Triple<?,?>> getProvenanceTriples() {
		List<Triple<?,?>> triples = new ArrayList<Triple<?,?>>();
		if (nanopub == null) return triples;
		for (Statement st : nanopub.getProvenance()) {
			triples.add(new Triple<ThingElement,Object>(st));
		}
		return triples;
	}
	
	private static final String createDateQuery =
		"select ?d where { <@> np:hasPublicationInfo ?info . " +
		"graph ?info { <@> dc:created ?d } }";
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
		"select distinct ?a where { <@> np:hasPublicationInfo ?info . " +
		"graph ?info { <@> pav:authoredBy ?a } }";
		//"select distinct ?a where { <@> pav:authoredBy ?a }";
	
	public List<AgentElement> getAuthors() {
		String query = authorsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<AgentElement> l = new ArrayList<AgentElement>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("a");
			if (v instanceof BNode) continue;
			l.add(new AgentElement(v.stringValue()));
		}
		return l;
	}
	
	private static final String creatorsQuery =
		"select distinct ?c where { <@> np:hasPublicationInfo ?info . " +
		"graph ?info { <@> pav:createdBy ?c } }";
		//"select distinct ?a where { <@> pav:createdBy ?a }";
	
	public List<AgentElement> getCreators() {
		String query = creatorsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<AgentElement> l = new ArrayList<AgentElement>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("c");
			if (v instanceof BNode) continue;
			l.add(new AgentElement(v.stringValue()));
		}
		return l;
	}

	private static final String opinionsQuery =
		"select ?p ?t ?pub ?s where { " +
		"?pub np:hasAssertion ?ass . ?pub np:hasPublicationInfo ?info . " +
		"graph ?info { ?pub dc:created ?d } . " +
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
				AgentElement agent = new AgentElement(p.stringValue());
				NanopubElement nanopub = new NanopubElement(pub.stringValue());
				SentenceElement sentence = new SentenceElement(s.stringValue());
				Opinion opinion = new Opinion(agent, t.stringValue(), sentence, nanopub);
				opinionMap.put(p.stringValue(), opinion);
			}
		}
		return new ArrayList<Opinion>(opinionMap.values());
	}
	
	private static final String getNanopubGraphsQuery =
		"select ?g ?ass ?prov ?info ?f where { " +
		"graph ?g { <@> np:hasAssertion ?ass . <@> np:hasPublicationInfo ?info . " +
		"optional { <@> np:hasProvenance ?prov } . optional { ?f rdfg:subGraphOf ?ass } } }";
	private static final String deleteGraphQuery =
		"clear graph <@>";
	
	public void delete() {
		String query = getNanopubGraphsQuery.replaceAll("@", getURI());
		for (BindingSet bs : TripleStoreAccess.getTuples(query)) {
			Value g = bs.getValue("g");
			Value ass = bs.getValue("ass");
			Value prov = bs.getValue("prov");
			Value info = bs.getValue("info");
			Value f = bs.getValue("f");
			if (g != null) TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", g.stringValue()));
			if (ass != null) TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", ass.stringValue()));
			if (prov != null) TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", prov.stringValue()));
			if (info != null) TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", info.stringValue()));
			if (f != null) TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", f.stringValue()));
		}
	}
	
	private static final String getNanopubGraphsWithPropertyQuery =
		"select ?pub where { graph ?ass { ?x @P ?y } . graph ?g { ?pub np:hasAssertion ?ass } }";
	
	public static void deleteAllNanopubsWithProperty(String propertyURI) {
		if (propertyURI.matches("^[a-z]+://")) {
			propertyURI = "<" + propertyURI + ">";
		}
		String query = getNanopubGraphsWithPropertyQuery.replaceAll("@P", propertyURI);
		for (BindingSet bs : TripleStoreAccess.getTuples(query)) {
			(new NanopubElement(bs.getValue("pub").stringValue())).delete();
		}
	}
	
	public NanopubItem createGUIItem(String id, int guiItemStyle) {
		return new NanopubItem(id, this, guiItemStyle);
	}

	public static String getTemplate(String name) {
		try {
			String f = "/templates/" + name + ".template.trig";
			InputStream in = TripleStoreAccess.class.getResourceAsStream(f);
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer, "UTF-8");
			return writer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
