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

	public ThingItem(String id, Thing th, int guiItemStyle) {
		super(id);
		
		PageParameters params = new PageParameters();
		String uri = th.getURI();
		params.add("uri", uri);
		BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("thinglink", ThingPage.class, params);
		add(link);
		String n = th.getLastPartOfURI();
		if (guiItemStyle == Thing.PREDICATEFIRST_ITEM) {
			if (SentenceRelation.get(uri) != null) {
				n = SentenceRelation.get(uri).getText();
			}
			n += ":";
		}
		link.add(new Label("thing", n));
	}
	
	public NanobrowserApplication getNanobrowserApp() {
		return (NanobrowserApplication) getApplication();
	}
	
	public Agent getUser() {
		return getNanobrowserApp().getUser();
	}

}
