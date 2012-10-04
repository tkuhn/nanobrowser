package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ThingItem extends Panel {
	
	private static final long serialVersionUID = -5109507637942030910L;
	
	protected ThingItem(String id) {
		super(id);
	}

	public ThingItem(String id, Thing th) {
		super(id);
		
		PageParameters params = new PageParameters();
		params.add("uri", th.getURI());
		BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("thinglink", ThingPage.class, params);
		add(link);
		link.add(new Label("thing", th.getLastPartOfURI()));
	}

}
