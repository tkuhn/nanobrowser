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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.util.io.IOUtils;
import org.nanopub.Nanopub;
import org.nanopub.extra.server.GetNanopub;
import org.nanopub.trusty.TrustyNanopubUtils;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class NanopubElement extends ThingElement {
	
	private static final long serialVersionUID = -62516765710631807L;
	
	public static final String TYPE_URI = Nanopub.NANOPUB_TYPE_URI.toString();

	private Nanopub nanopub;
	private Boolean isValid;

	public NanopubElement(String uri) {
		super(uri);
		nanopub = TripleStoreAccess.getNanopub(uri);
		if (nanopub == null) {
			System.err.println(uri);
			nanopub = GetNanopub.get(uri);
		}
	}

	public Nanopub getNanopub() {
		return nanopub;
	}

	public boolean isValid() {
		if (isValid != null) return isValid;
		if (nanopub == null) return false;
		isValid = TrustyNanopubUtils.isValidTrustyNanopub(nanopub);
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
	
	public List<Triple<?,?>> getAssertionTriples() {
		List<Triple<?,?>> triples = new ArrayList<Triple<?,?>>();
		if (nanopub == null) return triples;
		for (Statement st : nanopub.getAssertion()) {
			String p = st.getPredicate().toString();
			if (p.equals("http://purl.org/nanopub/x/asFormula")) continue;
			if (p.equals("http://purl.org/nanopub/x/asSentence")) continue;
			triples.add(new Triple<ThingElement,Object>(st));
		}
		return triples;
	}

	public List<Triple<?,?>> getProvenanceTriples() {
		List<Triple<?,?>> triples = new ArrayList<Triple<?,?>>();
		if (nanopub == null) return triples;
		for (Statement st : nanopub.getProvenance()) {
			triples.add(new Triple<ThingElement,Object>(st));
		}
		return triples;
	}

	public String getCreateDateString() {
		if (nanopub == null) return null;
		Calendar c = nanopub.getCreationTime();
		if (c == null) return null;
		return DateFormatUtils.format(c, DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern());
	}

	public List<AgentElement> getAuthors() {
		List<AgentElement> l = new ArrayList<AgentElement>();
		if (nanopub != null) {
			for (URI uri : nanopub.getAuthors()) {
				l.add(new AgentElement(uri.stringValue()));
			}
		}
		return l;
	}

	public List<AgentElement> getCreators() {
		List<AgentElement> l = new ArrayList<AgentElement>();
		if (nanopub != null) {
			for (URI uri : nanopub.getCreators()) {
				l.add(new AgentElement(uri.stringValue()));
			}
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

	private static final String deleteGraphQuery =
		"clear graph <@>";
	
	public void delete() {
		if (nanopub != null) {
			for (URI g : nanopub.getGraphUris()) {
				TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", g.stringValue()));
			}
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
