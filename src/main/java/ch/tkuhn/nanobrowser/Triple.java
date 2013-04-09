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

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

public class Triple<S extends ThingElement,O> implements Serializable {

	private static final long serialVersionUID = 7919683922987907295L;

	private S subject;
	private ThingElement predicate;
	private O object;
	private NanopubElement nanopub;
	
	public Triple(S subject, ThingElement predicate, O object, NanopubElement nanopub) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
		this.nanopub = nanopub;
	}
	
	public Triple(S subject, ThingElement predicate, O object) {
		this(subject, predicate, object, null);
	}
	
	@SuppressWarnings("unchecked")
	public Triple(Statement statement, NanopubElement nanopub) {
		this.nanopub = nanopub;
		subject = (S) ThingElement.getThing(statement.getSubject().stringValue());
		predicate = ThingElement.getThing(statement.getPredicate().stringValue());
		Value ov = statement.getObject();
		if (ov instanceof Resource) {
			object = (O) ThingElement.getThing(((Resource) ov).stringValue());
		} else {
			object = (O) ov.stringValue();
		}
	}

	public Triple(Statement statement) {
		this(statement, null);
	}
	
	public S getSubject() {
		return subject;
	}
	
	public ThingElement getPredicate() {
		return predicate;
	}
	
	public O getObject() {
		return object;
	}
	
	public NanopubElement getNanopub() {
		return nanopub;
	}

}
