package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class AssertionItem extends Panel {
	
	private static final long serialVersionUID = -5109507637942030910L;

	public AssertionItem(String id, String uri) {
		super(id);
		
		String s = Utils.getSentenceFromURI(uri);
		PageParameters params = new PageParameters();
		params.add("uri", uri);
		BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("assertionlink", AssertionPage.class, params);
		add(link);
		link.add(new Label("assertion", s));
	}

}
