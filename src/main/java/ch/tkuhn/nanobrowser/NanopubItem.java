package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class NanopubItem extends Panel {
	
	private static final long serialVersionUID = -5109507637942030910L;

	public NanopubItem(String id, Nanopub n) {
		super(id);
		
		PageParameters params = new PageParameters();
		params.add("uri", n.getURI());
		BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("nanopublink", NanopubPage.class, params);
		add(link);
		link.add(new Label("nanopubname", n.getLastPartOfURI()));
	}

}
