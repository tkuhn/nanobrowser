package ch.tkuhn.nanobrowser;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class AgentItem extends ThingItem {
	
	private static final long serialVersionUID = -5109507637942030910L;

	public AgentItem(String id, Agent a) {
		super(id);
		
		PageParameters params = new PageParameters();
		params.add("uri", a.getURI());
		BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("agentlink", AgentPage.class, params);
		add(link);

		WebMarkupContainer icon = new WebMarkupContainer("icon");
		if (a.isBot()) {
			icon.add(new AttributeModifier("src", new Model<String>("icons/bot.svg")));
		}
		link.add(icon);
		
		link.add(new Label("agent", a.getName()));
	}

}
