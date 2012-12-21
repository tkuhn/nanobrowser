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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;

public class Thing implements Serializable {
	
	private static final long serialVersionUID = -6229734698248756258L;

	public static final String TYPE_URI = "http://www.w3.org/2002/07/owl#Thing";
	
	public static final int TINY_GUI_ITEM = 0;
	public static final int MEDIUM_GUI_ITEM = 1;
	public static final int LONG_GUI_ITEM = 2;
	public static final int PREDICATEFIRST_ITEM = 3;
	
	private final String uri;
	
	public static Thing getThing(String uri) {
		List<String> types = getTypes(uri);
		if (types.contains(Agent.TYPE_URI)) return new Agent(uri);
		if (types.contains(Sentence.TYPE_URI)) return new Sentence(uri);
		if (types.contains(Nanopub.TYPE_URI)) return new Nanopub(uri);
		return new Thing(uri);
	}
	
	public Thing(String uri) {
		this.uri = uri.toString();  // throw exception when null
	}
	
	public String getURI() {
		return uri;
	}
	
	public String getTruncatedURI() {
		if (uri.length() > 120) {
			return uri.substring(0, 117) + "...";
		} else {
			return uri;
		}
	}
	
	public static String getLastPartOfURI(String uri) {
		return uri.replaceFirst("^.*[/#]([^/#]*)$", "$1");
	}
	
	public String getLastPartOfURI() {
		return getLastPartOfURI(uri);
	}
	
	private static final String labelsQuery =
		"select ?l where { <@> rdfs:label ?l }";
	
	public List<String> getLabels() {
		String query = labelsQuery.replaceAll("@", getURI());
		List<String> labels = new ArrayList<String>();
		for (BindingSet bs : TripleStoreAccess.getTuples(query)) {
			labels.add(bs.getValue("l").stringValue());
		}
		return labels;
	}
	
	public String getLabel() {
		List<String> labels = getLabels();
		if (labels.size() == 0) return null;
		return labels.get(0);
	}
	
	public ThingItem createGUIItem(String id, int guiItemStyle) {
		return new ThingItem(id, this, guiItemStyle);
	}
	
	public final ThingItem createGUIItem(String id) {
		return createGUIItem(id, MEDIUM_GUI_ITEM);
	}

	private static final String typesQuery =
		"select distinct ?t where { <@> a ?t }";
	
	public List<String> getTypes() {
		return getTypes(getURI());
	}
	
	public static List<String> getTypes(String uri) {
		// TODO improve this; should be just one SPARQL query
		String query = typesQuery.replaceAll("@", uri);
		List<BindingSet> result = TripleStoreAccess.getTuples(query);
		Set<String> types = new HashSet<String>();
		for (BindingSet bs : result) {
			Value v = bs.getValue("t");
			if (v instanceof BNode) continue;
			types.add(v.stringValue());
		}
		if (Sentence.isSentenceURI(uri)) types.add(Sentence.TYPE_URI);
		if (Agent.isAgent(uri)) types.add(Agent.TYPE_URI);
		if (Nanopub.isNanopub(uri)) types.add(Nanopub.TYPE_URI);
		return new ArrayList<String>(types);
	}
	
	public final String toString() {
		return getURI();
	}
	
	public boolean equals(Object other) {
		if (other instanceof Thing) {
			return getURI().equals(((Thing) other).getURI());
		} else {
			return false;
		}
	}

}
