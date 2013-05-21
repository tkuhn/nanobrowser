// This file is part of Nanobrowser.
// Copyright 2012, Tobias Kuhn, http://www.tkuhn.ch
//
// Nanobrowser is free software: you can redistribute it and/or modify it under the terms of the
// GNU Lesser General Public License as published by the Free Software Foundation, either version
// 3 of the License, or (at your option) any later version.
//
// Nanobrowser is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
// even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License along with Nanobrowser.
// If not, see http://www.gnu.org/licenses/.

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

	public ThingItem(String id, ThingElement th, int guiItemStyle) {
		super(id);
		
		PageParameters params = new PageParameters();
		String uri = th.getURI();
		params.add("uri", uri);
		BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("thinglink", ThingPage.class, params);
		add(link);
		String n = th.getShortName();
		if (guiItemStyle == ThingElement.PREDICATEFIRST_ITEM) {
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
	
	public AgentElement getUser() {
		return NanobrowserSession.get().getUser();
	}

}
