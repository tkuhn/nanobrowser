package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SentenceItem extends ThingItem {
	
	private static final long serialVersionUID = -5109507637942030910L;

	public SentenceItem(String id, Sentence s) {
		super(id);
		
		PageParameters params = new PageParameters();
		params.add("uri", s.getURI());
		BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("sentencelink", SentencePage.class, params);
		add(link);
		link.add(new Label("sentence", s.getSentenceText()));
	}

}
