package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PersonItem extends ThingItem {
	
	private static final long serialVersionUID = -5109507637942030910L;

	public PersonItem(String id, Person p) {
		super(id);
		
		PageParameters params = new PageParameters();
		params.add("uri", p.getURI());
		BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("personlink", PersonPage.class, params);
		add(link);
		link.add(new Label("person", p.getName()));
	}

}
