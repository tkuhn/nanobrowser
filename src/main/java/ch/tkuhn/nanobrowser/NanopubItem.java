package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class NanopubItem extends ThingItem {
	
	private static final long serialVersionUID = -5109507637942030910L;

	public NanopubItem(String id, Nanopub n) {
		this(id, n, true, true);
	}
	
	public NanopubItem(String id, Nanopub n, boolean showLabelAndDate) {
		this(id, n, showLabelAndDate, showLabelAndDate);
	}

	public NanopubItem(String id, Nanopub n, boolean showLabel, boolean showDate) {
		super(id);
		
		PageParameters params = new PageParameters();
		params.add("uri", n.getURI());
		BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("nanopublink", NanopubPage.class, params);
		add(link);
		String date = n.getCreateDateString();
		Label dateLabel;
		if (date == null) {
			dateLabel = new Label("nanopubdate", "");
		} else {
			dateLabel = new Label("nanopubdate", n.getCreateDateString());
		}
		dateLabel.setVisible(showDate);
		add(dateLabel);
		Label nameLabel = new Label("nanopubname", " " + n.getLastPartOfURI() + " ");
		nameLabel.setVisible(showLabel);
		link.add(nameLabel);
	}

}
