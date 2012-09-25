package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ClaimPage extends WebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	public ClaimPage(final PageParameters parameters) {
		
		String uri = parameters.get("uri").toString();
		
		add(new Label("title", Utils.getSentenceFromURI(uri)));
		
		add(new Label("uri", uri));
		
	}

}
