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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openrdf.OpenRDFException;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sparql.SPARQLRepository;

import ch.tkuhn.nanopub.Nanopub;
import ch.tkuhn.nanopub.NanopubImpl;

public class TripleStoreAccess {
	
	// No instances allowed:
	private TripleStoreAccess() {}
	
	private static String endpointURL = NanobrowserApplication.getProperty("sparql-endpoint-url");
	private static SPARQLRepository repo;
	private static QueryLanguage lang = QueryLanguage.SPARQL;
	private static String sparqlPrefixes = 
		"prefix xsd: <http://www.w3.org/2001/XMLSchema#> " +
		"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
		"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		"prefix owl: <http://www.w3.org/2002/07/owl#> " +
		"prefix dc: <http://purl.org/dc/terms/> " +
		"prefix pav: <http://swan.mindinformatics.org/ontologies/1.2/pav/> " +
		"prefix foaf: <http://xmlns.com/foaf/0.1/> " +
		"prefix np: <http://www.nanopub.org/nschema#> " +
		"prefix npx: <http://purl.org/nanopub/x/> ";

	static {
		repo = new SPARQLRepository(endpointURL);
		try {
			repo.initialize();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Nanopub getNanopub(String uri) {
		try {
			return new NanopubImpl(repo, new URIImpl(uri));
		} catch (Exception ex) {}
		return null;
	}

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

	public static List<Triple<?,?>> getGraph(String query) {
		List<Triple<?,?>> triples = new ArrayList<Triple<?,?>>();
		try {
			RepositoryConnection connection = repo.getConnection();
			try {
				GraphQuery graphQuery = connection.prepareGraphQuery(lang, sparqlPrefixes + query);
				GraphQueryResult result = graphQuery.evaluate();
				try {
					while (result.hasNext()) {
						triples.add(new Triple<ThingElement,Object>(result.next()));
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
	
	public static List<Triple<?,?>> sortTriples(List<Triple<?,?>> triples) {
		Collections.sort(triples, tripleComparator);
		return triples;
	}
	
	public static Comparator<Triple<?,?>> tripleComparator = new Comparator<Triple<?,?>>() {
		
		public int compare(Triple<?,?> o1, Triple<?,?> o2) {
			int d = o1.getSubject().toString().compareTo(o2.getSubject().toString());
			if (d == 0) {
				String p1 = o1.getPredicate().getURI();
				String p2 = o2.getPredicate().getURI();
				d = p1.compareTo(p2);
				if (d != 0) {
					if (p1.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) d = -1;
					if (p2.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) d = 1;
				}
			}
			if (d == 0) {
				d = o1.getObject().toString().compareTo(o2.getObject().toString());
			}
			return d;
		};
		
	};
	
	public static String getNanopublishQuery(InputStream in) {
		String prefix = "";
		String query = "";
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			String l;
			String graph = null;
			while ((l = r.readLine()) != null) {
				l = l.replaceAll("\\s+", " ");
				if (l.matches(" *")) continue;
				if (l.matches(" *#.*")) continue;
				if (l.startsWith("@prefix")) {
					if (graph != null) throw new RuntimeException("Error parsing TriG line: " + l);
					l = l.replaceFirst("[. ]*$", "");
					l = l.substring(1);
					prefix += " " + l;
				} else if (l.matches("[^ ].* \\{ *")) {
					if (graph != null) throw new RuntimeException("Error parsing TriG line: " + l);
					graph = l;
				} else if (l.matches("\\} *")) {
					if (graph == null) throw new RuntimeException("Error parsing TriG line: " + l);
					query += prefix + " insert data into graph " + graph + " }\n\n";
					graph = null;
				} else {
					if (graph == null) throw new RuntimeException("Error parsing TriG line: " + l);
					graph += " " + l;
				}
			}
			if (graph != null) throw new RuntimeException("Error parsing TriG file");
			r.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		query = query.replaceAll(" +", " ");
		query = query.replaceFirst("^ ", "");
		query = query.replaceFirst(" $", "");
		return query;
	}

}
