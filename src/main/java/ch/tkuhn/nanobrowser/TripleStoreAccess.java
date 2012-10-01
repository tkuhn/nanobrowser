package ch.tkuhn.nanobrowser;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
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
	
	private static String url = NanobrowserApplication.getProperty("sparql-endpoint-url");
	private static SPARQLRepository repo = new SPARQLRepository(url);
	private static QueryLanguage lang = QueryLanguage.SPARQL;
	
	public static List<BindingSet> getTuples(String sparqlQuery) {
		List<BindingSet> tuples = new ArrayList<BindingSet>();
		try {
			RepositoryConnection connection = repo.getConnection();
			try {
				TupleQuery tupleQuery = connection.prepareTupleQuery(lang, sparqlQuery);
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
				GraphQuery graphQuery = connection.prepareGraphQuery(lang, query);
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

}
