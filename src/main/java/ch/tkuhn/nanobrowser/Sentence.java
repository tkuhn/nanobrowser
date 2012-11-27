package ch.tkuhn.nanobrowser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class Sentence extends Thing {
	
	private static final long serialVersionUID = -7967327315454171639L;
	
	public static final String TYPE_URI = "http://krauthammerlab.med.yale.edu/nanopub/AIDA-Sentence";
	public static final String AIDA_URI_BASE = "http://krauthammerlab.med.yale.edu/aida/";
	
	public Sentence(String uri) {
		super(uri);
	}
	
	public static Sentence withText(String sentenceText) {
		try {
			return new Sentence(AIDA_URI_BASE + URLEncoder.encode(sentenceText, "UTF8"));
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private static final String allSentencesQuery =
		"select distinct ?s where {?a npx:asSentence ?s}";
	
	public static List<Sentence> getAllSentences(int limit) {
		String lm = (limit >= 0) ? " limit " + limit : "";
		List<BindingSet> result = TripleStoreAccess.getTuples(allSentencesQuery + lm);
		List<Sentence> l = new ArrayList<Sentence>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("s");
			if (v instanceof BNode) continue;
			l.add(new Sentence(v.stringValue()));
		}
		return l;
	}
	
	public static boolean isSentenceURI(String uri) {
		return uri.startsWith(AIDA_URI_BASE);
	}
	
	public static boolean isSentenceText(String text) {
		if (text.indexOf("/") > -1) return false;
		if (text.indexOf(" ") == -1) return false;
		if (text.length() < 10 || text.length() > 500) return false;
		return text.substring(text.length()-1).equals(".");
	}
	
	private static final String nanopubsQuery =
		"select distinct ?p where { ?p np:hasAssertion ?a . ?a npx:asSentence <@> . ?p np:hasProvenance ?prov . " +
		"?prov np:hasAttribution ?att . graph ?att { ?p dc:created ?d } }";
	
	public List<Nanopub> getNanopubs() {
		String query = nanopubsQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		List<Nanopub> nanopubs = new ArrayList<Nanopub>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("p");
			if (v instanceof BNode) continue;
			nanopubs.add(new Nanopub(v.stringValue()));
		}
		return nanopubs;
	}

	private static final String opinionsQuery =
		"select ?p ?t ?pub where { " +
		"?pub np:hasAssertion ?ass . ?pub np:hasProvenance ?prov . " +
		"?prov np:hasAttribution ?att . graph ?att { ?pub dc:created ?d } . " +
		"graph ?ass { ?p npx:hasOpinion ?o . ?o rdf:type ?t . ?o npx:opinionOn ?s } ." +
		"{ ?ass2 npx:asSentence <@> . ?ass2 npx:asSentence ?s } union " +
		"{ <@> npx:hasSameMeaning ?s } union { ?s npx:hasSameMeaning <@> } " +
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
				Agent agent = new Agent(p.stringValue());
				Nanopub nanopub = new Nanopub(pub.stringValue());
				Opinion opinion = new Opinion(agent, t.stringValue(), this, nanopub);
				opinionMap.put(p.stringValue(), opinion);
			}
		}
		return new ArrayList<Opinion>(opinionMap.values());
	}
	
	private static final String sameMeaningSentencesQuery =
		"select ?s ?pub where { { " +
		"{ ?pub np:hasAssertion ?ass . ?ass npx:asSentence <@> . ?ass npx:asSentence ?s } union " +
		"{ ?pub np:hasAssertion ?ass . graph ?ass { <@> npx:hasSameMeaning ?s } } union " +
		"{ ?pub np:hasAssertion ?ass . graph ?ass { ?s npx:hasSameMeaning <@> } } " +
		"} . ?pub np:hasProvenance ?prov . ?prov np:hasAttribution ?att . graph ?att { ?pub dc:created ?d } . " +
		"} order by asc(?d)";
	
	public List<Pair<Sentence,Nanopub>> getSameMeaningSentences() {
		String query = sameMeaningSentencesQuery.replaceAll("@", getURI());
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		Map<String, Pair<Sentence,Nanopub>> sentencesMap = new HashMap<String, Pair<Sentence,Nanopub>>();
		for (BindingSet bs : result) {
			Value s = bs.getValue("s");
			Value pub = bs.getValue("pub");
			if (s instanceof BNode || pub instanceof BNode) continue;
			if (!s.stringValue().equals(getURI())) {
				Sentence sentence = new Sentence(s.stringValue());
				Nanopub nanopub = new Nanopub(pub.stringValue());
				sentencesMap.put(sentence.getURI(), new ImmutablePair<Sentence,Nanopub>(sentence, nanopub));
			}
		}
		return new ArrayList<Pair<Sentence,Nanopub>>(sentencesMap.values());
	}
	
	public void publishSameMeaning(Sentence other, Agent author) {
		String pubURI = "http://www.tkuhn.ch/nanobrowser/meta/" +
				(new Random()).nextInt(1000000000);
		String query = TripleStoreAccess.getNanopublishQueryTemplate("samemeaning")
				.replaceAll("@ROOT@", pubURI)
				.replaceAll("@AGENT@", author.getURI())
				.replaceAll("@SENTENCE1@", getURI())
				.replaceAll("@SENTENCE2@", other.getURI())
				.replaceAll("@DATETIME@", NanobrowserApplication.getTimestamp());
		TripleStoreAccess.runUpdateQuery(query);
	}
	
	private static final String getAllOpinionGraphsQuery =
		"select ?g ?ass ?att where { graph ?ass { ?a npx:hasSameMeaning ?c } . " +
		"graph ?g { ?pub np:hasAssertion ?ass . ?pub np:hasProvenance ?prov . ?prov np:hasAttribution ?att } }";
	private static final String deleteGraphQuery =
		"delete from graph identified by <@> { ?a ?b ?c } where  { ?a ?b ?c }";
	
	public static void deleteAllOpinionNanopubs() {
		for (BindingSet bs : TripleStoreAccess.getTuples(getAllOpinionGraphsQuery)) {
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("g").stringValue()));
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("ass").stringValue()));
			TripleStoreAccess.runUpdateQuery(deleteGraphQuery.replaceAll("@", bs.getValue("att").stringValue()));
		}
	}
	
	private static final String textSearchQuery =
		"select distinct ?s where { { ?a npx:asSentence ?s } union { ?x npx:hasSameMeaning ?s } . " +
		"filter regex(str(?s), \"@R\", \"i\") }";
	private static final String textSearchRegex =
		"^http://krauthammerlab.med.yale.edu/aida/.*@W";
	
	// TODO Use proper text indexing
	
	public static List<Sentence> search(String searchText, int limit) {
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
		List<Sentence> sentences = new ArrayList<Sentence>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("s");
			sentences.add(new Sentence(v.stringValue()));
		}
		return sentences;
	}
	
	public String getSentenceText() {
		try {
			return URLDecoder.decode(getLastPartOfURI(), "UTF8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public SentenceItem createGUIItem(String id) {
		return new SentenceItem(id, this);
	}

}
