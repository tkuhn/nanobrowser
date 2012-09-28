package ch.tkuhn.nanobrowser;

import static ch.tkuhn.nanobrowser.NanopubAccess.getNanopubsForAuthor;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PersonPage extends WebPage {

	private static final long serialVersionUID = -4673886567380719848L;

	public PersonPage(final PageParameters parameters) {
		
		String uri = parameters.get("uri").toString();
		
		add(new Label("title", NanopubAccess.getPersonName(uri)));

		add(new ExternalLink("uri", uri, uri));
		
		add(new ListView<String>("nanopubs", getNanopubsForAuthor(uri)) {
			
			private static final long serialVersionUID = 3911519757128281636L;

			protected void populateItem(ListItem<String> item) {
				String uri = item.getModelObject();
				item.add(new PubItem("nanopub", uri));
			}
			
		});
		
	}

}
