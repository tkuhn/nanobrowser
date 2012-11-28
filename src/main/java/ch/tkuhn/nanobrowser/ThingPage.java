package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ThingPage extends NanobrowserWebPage {

	private static final long serialVersionUID = -4673886567380719848L;
	
	private Thing thing;

	public ThingPage(final PageParameters parameters) {
		
		thing = new Thing(parameters.get("uri").toString());
		
		add(new MenuBar("menubar"));
		
		add(new Label("title", thing.getLastPartOfURI()));

		add(new ExternalLink("uri", thing.getURI(), thing.getTruncatedURI()));

		add(new HList("typelist", thing.getTypes(), "Types"));
		
	}

}
