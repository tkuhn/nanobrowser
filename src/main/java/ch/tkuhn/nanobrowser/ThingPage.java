package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ThingPage extends NanobrowserWebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	public ThingPage(final PageParameters parameters) {
		
		Thing th = new Thing(parameters.get("uri").toString());
		
		add(new MenuBar("menubar"));
		
		add(new Label("title", th.getLastPartOfURI()));

		add(new ExternalLink("uri", th.getURI(), th.getURI()));
		
	}

}
