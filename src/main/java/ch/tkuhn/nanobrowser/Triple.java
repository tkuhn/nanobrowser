package ch.tkuhn.nanobrowser;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

public class Triple<S extends Thing,O> {
	
	private S subject;
	private Thing predicate;
	private O object;
	private Nanopub nanopub;
	
	public Triple(S subject, Thing predicate, O object, Nanopub nanopub) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
		this.nanopub = nanopub;
	}
	
	public Triple(S subject, Thing predicate, O object) {
		this(subject, predicate, object, null);
	}
	
	@SuppressWarnings("unchecked")
	public Triple(Statement statement, Nanopub nanopub) {
		this.nanopub = nanopub;
		subject = (S) Thing.getThing(statement.getSubject().stringValue());
		predicate = Thing.getThing(statement.getPredicate().stringValue());
		Value ov = statement.getObject();
		if (ov instanceof Resource) {
			object = (O) Thing.getThing(((Resource) ov).stringValue());
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
	
	public Thing getPredicate() {
		return predicate;
	}
	
	public O getObject() {
		return object;
	}
	
	public Nanopub getNanopub() {
		return nanopub;
	}

}
