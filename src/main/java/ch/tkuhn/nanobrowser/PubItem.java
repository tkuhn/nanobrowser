package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PubItem extends Panel {
	
	private static final long serialVersionUID = -5109507637942030910L;

	public PubItem(String id, String uri) {
		super(id);
		
		String s = Utils.getLastPartOfURI(uri);
		PageParameters params = new PageParameters();
		params.add("uri", uri);
		BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("nanopublink", PubPage.class, params);
		add(link);
		link.add(new Label("nanopubname", s));
	}

}
