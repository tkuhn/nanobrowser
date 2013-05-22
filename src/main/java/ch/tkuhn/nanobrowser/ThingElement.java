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

public class ThingElement implements Serializable {
	
	private static final long serialVersionUID = -6229734698248756258L;

	public static final String TYPE_URI = "http://www.w3.org/2002/07/owl#Thing";
	
	public static final int TINY_GUI_ITEM = 0;
	public static final int MEDIUM_GUI_ITEM = 1;
	public static final int LONG_GUI_ITEM = 2;
	public static final int PREDICATEFIRST_ITEM = 3;
	
	private final String uri;
	
	public static ThingElement getThing(String uri) {
		if (uri.startsWith(SentenceElement.AIDA_URI_BASE)) return new SentenceElement(uri);
		if (uri.startsWith(PaperElement.DOI_URI_BASE)) return new PaperElement(uri);
		List<String> types = getTypes(uri);
		if (types.contains(AgentElement.TYPE_URI)) return new AgentElement(uri);
		if (types.contains(SentenceElement.TYPE_URI)) return new SentenceElement(uri);
		if (types.contains(NanopubElement.TYPE_URI)) return new NanopubElement(uri);
		return new ThingElement(uri);
	}
	
	public ThingElement(String uri) {
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
	
	public static String getShortNameFromURI(String uri) {
		uri = uri.replaceFirst("[/#]$", "");
		uri = uri.replaceFirst("^.*[/#]([^/#]*)$", "$1");
		uri = uri.replaceFirst("((^|[^A-Za-z0-9\\-_])RA[A-Za-z0-9\\-_]{8})[A-Za-z0-9\\-_]{35}$", "$1");
		uri = uri.replaceFirst("(^|[^A-Za-z0-9\\-_])RA[A-Za-z0-9\\-_]{43}[^A-Za-z0-9\\-_](.+)$", "$2");
		return uri;
	}
	
	public String getShortName() {
		return getShortNameFromURI(uri);
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
		if (SentenceElement.isSentenceURI(uri)) types.add(SentenceElement.TYPE_URI);
		if (AgentElement.isAgent(uri)) types.add(AgentElement.TYPE_URI);
		if (NanopubElement.isNanopub(uri)) types.add(NanopubElement.TYPE_URI);
		return new ArrayList<String>(types);
	}
	
	public final String toString() {
		return getURI();
	}
	
	public boolean equals(Object other) {
		if (other instanceof ThingElement) {
			return getURI().equals(((ThingElement) other).getURI());
		} else {
			return false;
		}
	}

}
