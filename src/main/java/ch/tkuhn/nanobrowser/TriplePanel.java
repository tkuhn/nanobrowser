package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.panel.Panel;
import org.openrdf.model.Statement;

public class TriplePanel extends Panel {
	
	private static final long serialVersionUID = -5109507637942030910L;

	public TriplePanel(String id, Statement triple) {
		super(id);
		
		Thing s = new Thing(triple.getSubject().stringValue());
		Thing p = new Thing(triple.getPredicate().stringValue());
		Thing o = new Thing(triple.getObject().stringValue());
		add(new ThingItem("subject", s));
		add(new ThingItem("predicate", p));
		add(new ThingItem("object", o));
	}

}
