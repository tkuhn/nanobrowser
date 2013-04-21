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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class AgentElement extends ThingElement {
	
	private static final long serialVersionUID = -4281747788959702687L;
	
	public static final String TYPE_URI = "http://xmlns.com/foaf/0.1/Agent";
	
	public AgentElement(String uri) {
		super(uri);
	}
	
	private static final String allPersonsQuery =
		"select distinct ?p where { { ?a pav:authoredBy ?p } union { ?a pav:createdBy ?p } union { ?p a foaf:Person } " +
		"union { ?p a foaf:Agent } . optional { ?p a npx:Bot . ?p a ?x } filter (!bound(?x)) }";
	
	public static List<AgentElement> getAllPersons(int limit) {
		String lm = (limit >= 0) ? " limit " + limit : "";
		List<BindingSet> result = TripleStoreAccess.getTuples(allPersonsQuery + lm);
		List<AgentElement> l = new ArrayList<AgentElement>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			l.add(new AgentElement(v.stringValue()));
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
		"select distinct ?pub where { ?pub a np:Nanopublication . ?pub np:hasPublicationInfo ?info . " +
		"graph ?info { ?pub pav:authoredBy <@> . ?pub dc:created ?d } " +
		"filter not exists { ?pub a npx:MetaNanopub } } order by desc(?d)";
	
	public List<NanopubElement> getAuthoredNanopubs() {
		String query = authoredNanopubsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<NanopubElement> l = new ArrayList<NanopubElement>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("pub");
			if (v instanceof BNode) continue;
			l.add(new NanopubElement(v.stringValue()));
		}
		return l;
	}

	private static final String opinionsQuery =
		"select ?s ?t ?pub where { " +
		"?pub np:hasAssertion ?ass . ?pub np:hasPublicationInfo ?info . " +
		"graph ?info { ?pub dc:created ?d } . " +
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
				SentenceElement sentence = new SentenceElement(s.stringValue());
				NanopubElement nanopub = new NanopubElement(pub.stringValue());
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
		if (name == null) name = getShortName();
		return name;
	}

	private static final String commandersQuery =
		"select distinct ?c where { ?c npx:commands <@> }";
	
	public List<AgentElement> getCommanders() {
		String query = commandersQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<AgentElement> l = new ArrayList<AgentElement>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("c");
			if (v instanceof BNode) continue;
			l.add(new AgentElement(v.stringValue()));
		}
		return l;
	}
	
	public AgentItem createGUIItem(String id, int guiItemStyle) {
		return new AgentItem(id, this, guiItemStyle);
	}

}
