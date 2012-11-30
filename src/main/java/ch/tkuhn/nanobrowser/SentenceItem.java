package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SentenceItem extends ThingItem {
	
	private static final long serialVersionUID = -5109507637942030910L;

	public SentenceItem(String id, Sentence s, int guiItemStyle) {
		super(id);
		
		PageParameters params = new PageParameters();
		params.add("uri", s.getURI());
		BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("sentencelink", SentencePage.class, params);
		add(link);
		
		Label nameLabel = new Label("sentence", s.getSentenceText());
		nameLabel.setVisible(guiItemStyle != Thing.TINY_GUI_ITEM);
		link.add(nameLabel);
	}

	public SentenceItem(String id, Sentence s) {
		this(id, s, Thing.MEDIUM_GUI_ITEM);
	}

}
