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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

import com.google.common.collect.ImmutableList;

import ch.tkuhn.hashuri.rdf.TransformNanopub;

public class SentenceElement extends ThingElement {
	
	private static final long serialVersionUID = -7967327315454171639L;
	
	public static final String TYPE_URI = "http://purl.org/nanopub/x/AIDA-Sentence";
	public static final String AIDA_URI_BASE = "http://purl.org/aida/";

	// TODO: express this in OWL
	private static final List<String> symmetricRelations = ImmutableList.of(
		"http://purl.org/nanopub/x/hasSameMeaning",
		"http://purl.org/nanopub/x/hasDifferentMeaning",
		"http://purl.org/nanopub/x/hasOppositeMeaning",
		"http://purl.org/nanopub/x/hasNonoppositeMeaning",
		"http://purl.org/nanopub/x/hasConflictingMeaning",
		"http://purl.org/nanopub/x/hasConsistentMeaning",
		"http://purl.org/nanopub/x/hasRelatedMeaning",
		"http://purl.org/nanopub/x/hasUnrelatedMeaning"
	);
	
	public SentenceElement(String uri) {
		super(uri);
	}
	
	public static SentenceElement withText(String text) throws AidaException {
		if (text == null) {
			throw new AidaException("Empty sentence.");
		}
		text = text.replaceAll("\\s+", " ").replaceAll("^ ", "").replaceAll(" $", "");
		if (text.isEmpty()) {
			throw new AidaException("Empty sentence.");
		} else if (text.matches(".*[a-z]+://.*")) {
			throw new AidaException("The sentence may not contain URIs.");
		} else if (text.indexOf(" ") == -1) {
			throw new AidaException("The sentence must consist of at least two words.");
		} else if (text.length() < 10) {
			throw new AidaException("The sentence is too short: it needs at least ten characters.");
		} else if (text.length() > 250) {
			throw new AidaException("The sentence is too long: at most 250 characters are allowed.");
		} else if (!text.substring(text.length()-1).equals(".")) {
			throw new AidaException("The sentence has to end with a full stop.");
		}
		try {
			return new SentenceElement(AIDA_URI_BASE + URLEncoder.encode(text, "UTF8"));
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private static final String allSentencesQuery =
		"select distinct ?s where { { {?s ?p ?e} union {?e ?p ?s} } " +
		"filter regex(str(?s), \"^http://purl.org/aida/\", \"i\") }";
	
	public static List<SentenceElement> getAllSentences(int limit) {
		String lm = (limit >= 0) ? " limit " + limit : "";
		List<BindingSet> result = TripleStoreAccess.getTuples(allSentencesQuery + lm);
		List<SentenceElement> l = new ArrayList<SentenceElement>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("s");
			if (v instanceof BNode) continue;
			l.add(new SentenceElement(v.stringValue()));
		}
		return l;
	}
	
	public static boolean isSentenceURI(String uri) {
		return uri.startsWith(AIDA_URI_BASE);
	}
	
	private static final String nanopubsQuery =
		"select distinct ?p where { ?p np:hasAssertion ?a . ?a npx:asSentence <@> . " +
		"?p np:hasPublicationInfo ?info . graph ?info { ?p dc:created ?d } }";
	
	public List<NanopubElement> getNanopubs() {
		String query = nanopubsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<NanopubElement> nanopubs = new ArrayList<NanopubElement>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			nanopubs.add(new NanopubElement(v.stringValue()));
		}
		return nanopubs;
	}

	private static final String opinionsQuery =
		"select ?p ?t ?pub where { " +
		"?pub np:hasAssertion ?ass . ?pub np:hasPublicationInfo ?info . " +
		"graph ?info { ?pub dc:created ?d } . " +
		"graph ?ass { ?p npx:hasOpinion ?o . ?o rdf:type ?t . ?o npx:opinionOn <@> } " +
		"} order by asc(?d)";
	
	public List<Opinion> getOpinions(boolean excludeNullOpinions) {
		String query = opinionsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		Map<String, Opinion> opinionMap = new HashMap<String, Opinion>();
		for (BindingSet bs : result) {
			Value p = bs.getValue("p");
			Value t = bs.getValue("t");
			Value pub = bs.getValue("pub");
			if (p instanceof BNode || t instanceof BNode || pub instanceof BNode) continue;
			if (excludeNullOpinions && t.stringValue().equals(Opinion.NULL_TYPE)) {
				opinionMap.remove(p.stringValue());
			} else {
				AgentElement agent = new AgentElement(p.stringValue());
				NanopubElement nanopub = new NanopubElement(pub.stringValue());
				Opinion opinion = new Opinion(agent, t.stringValue(), this, nanopub);
				opinionMap.put(p.stringValue(), opinion);
			}
		}
		return new ArrayList<Opinion>(opinionMap.values());
	}

	private static final String relatedSentencesQuery =
		"select ?s ?r ?o ?pub where { { " +
		"{ ?pub np:hasAssertion ?ass . graph ?ass { <@> ?r ?o } } union " +
		"{ ?pub np:hasAssertion ?ass . graph ?ass { ?s ?r <@> } } " +
		"} . ?pub np:hasPublicationInfo ?info. graph ?info { ?pub dc:created ?d } . " +
		"filter regex(str(?r), \"^http://purl.org/nanopub/x/(isImprovedVersionOf|has.*Meaning)\", \"i\") " +
		"} order by asc(?d)";
	
	public List<Triple<SentenceElement,SentenceElement>> getRelatedSentences() {
		String query = relatedSentencesQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		Map<String, Triple<SentenceElement,SentenceElement>> sentencesMap = new HashMap<String, Triple<SentenceElement,SentenceElement>>();
		for (BindingSet bs : result) {
			Value s = bs.getValue("s");
			String r = bs.getValue("r").stringValue();
			if (s == null) {
				if (!symmetricRelations.contains(r)) continue;
				s = bs.getValue("o");
			}
			Value pub = bs.getValue("pub");
			if (s instanceof BNode || s instanceof BNode || pub instanceof BNode) continue;
			if (!s.stringValue().equals(getURI())) {
				SentenceElement sentence = new SentenceElement(s.stringValue());
				Triple<SentenceElement,SentenceElement> t = new Triple<SentenceElement,SentenceElement>(
						sentence,
						new ThingElement(r),
						this,
						new NanopubElement(pub.stringValue()));
				sentencesMap.put(sentence.getURI(), t);
			}
		}
		return new ArrayList<Triple<SentenceElement,SentenceElement>>(sentencesMap.values());
	}

	public URI publish(AgentElement author, boolean isExample, String provenance) {
		String types = "";
		URI uri = null;
		if (isExample) types = ", npx:ExampleNanopub";
		try {
			String pubURI = NanobrowserApplication.getProperty("nanopub-server-baseuri") + "pub/";
			String nanopubString = NanopubElement.getTemplate("publish")
					.replaceAll("@ROOT@", pubURI)
					.replaceAll("@TYPES@", types)
					.replaceAll("@AGENT@", author.getURI())
					.replaceAll("@SENTENCE@", getURI())
					.replaceAll("@DATETIME@", NanobrowserApplication.getTimestamp())
					.replaceAll("@PROV@", provenance);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			uri = TransformNanopub.transform(new ByteArrayInputStream(nanopubString.getBytes()), out, pubURI);
			String query = TripleStoreAccess.getNanopublishQuery(new ByteArrayInputStream(out.toByteArray()));
			TripleStoreAccess.runUpdateQuery(query);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return uri;
	}

	public void publishSentenceRelation(SentenceRelation rel, SentenceElement other, AgentElement author) {
		try {
			String pubURI = NanobrowserApplication.getProperty("nanopub-server-baseuri") + "meta/";
			String nanopubString = NanopubElement.getTemplate("sentencerel")
					.replaceAll("@ROOT@", pubURI)
					.replaceAll("@AGENT@", author.getURI())
					.replaceAll("@SENTENCE1@", other.getURI())
					.replaceAll("@RELATION@", rel.getURI())
					.replaceAll("@SENTENCE2@", getURI())
					.replaceAll("@DATETIME@", NanobrowserApplication.getTimestamp());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			TransformNanopub.transform(new ByteArrayInputStream(nanopubString.getBytes()), out, pubURI);
			String query = TripleStoreAccess.getNanopublishQuery(new ByteArrayInputStream(out.toByteArray()));
			TripleStoreAccess.runUpdateQuery(query);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static final String getAllOpinionGraphsQuery =
		"select ?g ?ass ?info where { graph ?ass { ?a npx:hasSameMeaning ?c } . " +
		"graph ?g { ?pub np:hasAssertion ?ass . ?pub np:hasPublicationInfo ?info } }";
	private static final String deleteGraphQuery =
		"delete from graph identified by <@> { ?a ?b ?c } where  { ?a ?b ?c }";
	
	public static void deleteAllOpinionNanopubs() {
		for (BindingSet bs : TripleStoreAccess.getTuples(getAllOpinionGraphsQuery)) {
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("g").stringValue()));
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("ass").stringValue()));
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("info").stringValue()));
		}
	}
	
	private static final String textSearchQuery =
		"select distinct ?s where { { ?a npx:asSentence ?s } union { ?x npx:hasSameMeaning ?s } . " +
		"filter regex(str(?s), \"@R\", \"i\") }";
	private static final String textSearchRegex =
		"^http://purl.org/aida/.*@W";
	
	// TODO Use proper text indexing
	
	public static List<SentenceElement> search(String searchText, int limit) {
		searchText = searchText
			.replaceAll("\\s+", " ")
			.replaceFirst("^ ", "")
			.replaceFirst(" $", "")
			.replaceAll("[^a-zA-Z0-9 ]", "")
			.replaceAll(" ", "(%20|~+)");
		String query = textSearchQuery
			.replaceAll("@R", textSearchRegex)
			.replaceAll("@W", searchText)
			.replaceAll("~", "\\\\\\\\")
			+ ((limit >= 0) ? " limit " + limit : "" );
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<SentenceElement> sentences = new ArrayList<SentenceElement>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("s");
			sentences.add(new SentenceElement(v.stringValue()));
		}
		return sentences;
	}
	
	public String getSentenceText() {
		try {
			return URLDecoder.decode(getShortName(), "UTF8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public SentenceItem createGUIItem(String id, int guiItemStyle) {
		return new SentenceItem(id, this, guiItemStyle);
	}

}
