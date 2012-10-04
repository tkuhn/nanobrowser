package ch.tkuhn.nanobrowser;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sparql.SPARQLRepository;

public class TripleStoreAccess {
	
	// No instances allowed:
	private TripleStoreAccess() {}
	
	private static String endpointURL = NanobrowserApplication.getProperty("sparql-endpoint-url");
	private static SPARQLRepository repo = new SPARQLRepository(endpointURL);
	private static QueryLanguage lang = QueryLanguage.SPARQL;
	private static String sparqlPrefixes = 
		"prefix ex: <http://krauthammerlab.med.yale.edu/nanopub/extensions/> " +
		"prefix np: <http://www.nanopub.org/nschema#> " +
		"prefix cl: <http://krauthammerlab.med.yale.edu/nanopub/claims/en/> " +
		"prefix pav: <http://swan.mindinformatics.org/ontologies/1.2/pav/> " +
		"prefix dc: <http://purl.org/dc/terms/> " +
		"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
		"prefix foaf: <http://xmlns.com/foaf/0.1/> ";

	public static boolean isTrue(String query) {
		boolean isTrue = false;
		try {
			RepositoryConnection connection = repo.getConnection();
			try {
				BooleanQuery booleanQuery = connection.prepareBooleanQuery(lang, sparqlPrefixes + query);
				isTrue = booleanQuery.evaluate();
			} finally {
				connection.close();
			}
		} catch (OpenRDFException ex) {
			ex.printStackTrace();
		}
		return isTrue;
	}
	
	public static List<BindingSet> getTuples(String query) {
		List<BindingSet> tuples = new ArrayList<BindingSet>();
		try {
			RepositoryConnection connection = repo.getConnection();
			try {
				TupleQuery tupleQuery = connection.prepareTupleQuery(lang, sparqlPrefixes + query);
				TupleQueryResult result = tupleQuery.evaluate();
				try {
					while (result.hasNext()) {
						tuples.add(result.next());
					}
				} finally {
					result.close();
				}
			} finally {
				connection.close();
			}
		} catch (OpenRDFException ex) {
			ex.printStackTrace();
		}
		return tuples;
	}

	public static List<Statement> getGraph(String query) {
		List<Statement> triples = new ArrayList<Statement>();
		try {
			RepositoryConnection connection = repo.getConnection();
			try {
				GraphQuery graphQuery = connection.prepareGraphQuery(lang, sparqlPrefixes + query);
				GraphQueryResult result = graphQuery.evaluate();
				try {
					while (result.hasNext()) {
						triples.add(result.next());
					}
				} finally {
					result.close();
				}
			} finally {
				connection.close();
			}
		} catch (OpenRDFException ex) {
			ex.printStackTrace();
		}
		return triples;
	}
	
	public static void runUpdateQuery(String query) {
		for (String qu : query.split("\n\n")) {
			// SPARQLRepository does not implement update queries
			try {
				URL url = new URL(endpointURL);
			    URLConnection connection = url.openConnection();
			    connection.setDoOutput(true);
			    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
			    wr.write("query=" + URLEncoder.encode(sparqlPrefixes + qu, "UTF8"));
			    wr.flush();
			    connection.getInputStream().close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static List<Statement> sortTriples(List<Statement> triples) {
		Collections.sort(triples, tripleComparator);
		return triples;
	}
	
	public static Comparator<Statement> tripleComparator = new Comparator<Statement>() {
		
		public int compare(Statement o1, Statement o2) {
			int d = o1.getSubject().stringValue().compareTo(o2.getSubject().stringValue());
			if (d == 0) {
				String p1 = o1.getPredicate().stringValue();
				String p2 = o2.getPredicate().stringValue();
				d = p1.compareTo(p2);
				if (d != 0) {
					if (p1.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) d = -1;
					if (p2.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) d = 1;
				}
			}
			if (d == 0) {
				d = o1.getObject().stringValue().compareTo(o2.getObject().stringValue());
			}
			return d;
		};
		
	};

}
