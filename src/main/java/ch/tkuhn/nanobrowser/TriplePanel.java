package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.openrdf.model.Statement;

public class TriplePanel extends Panel {
	
	private static final long serialVersionUID = -5109507637942030910L;

	public TriplePanel(String id, Statement triple) {
		super(id);
		
		add(new Label("subject", Thing.getLastPartOfURI(triple.getSubject().stringValue())));
		add(new Label("predicate", Thing.getLastPartOfURI(triple.getPredicate().stringValue())));
		add(new Label("object", Thing.getLastPartOfURI(triple.getObject().stringValue())));
	}

}
