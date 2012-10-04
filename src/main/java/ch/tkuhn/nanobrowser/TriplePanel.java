package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

public class TriplePanel extends Panel {
	
	private static final long serialVersionUID = -5109507637942030910L;

	public TriplePanel(String id, Statement triple) {
		super(id);
		
		add(Thing.getThing(triple.getSubject()).createGUIItem("subject"));
		add(Thing.getThing(triple.getPredicate()).createGUIItem("predicate"));
		Value ov = triple.getObject();
		if (ov instanceof Resource) {
			add(Thing.getThing((Resource) ov).createGUIItem("object"));
		} else {
			add(new Label("object", ov.stringValue()));
		}
	}

}
