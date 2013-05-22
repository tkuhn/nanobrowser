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
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PaperItem extends ThingItem {

	private static final long serialVersionUID = -4573142435385808966L;

	public PaperItem(String id, PaperElement p, int guiItemStyle) {
		super(id);
		
		PageParameters params = new PageParameters();
		params.add("uri", p.getURI());
		BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("doilink", PaperPage.class, params);
		add(link);
		
		Label nameLabel = new Label("doi", p.getDoiString());
		nameLabel.setVisible(guiItemStyle != ThingElement.TINY_GUI_ITEM);
		link.add(nameLabel);
	}

	public PaperItem(String id, PaperElement p) {
		this(id, p, ThingElement.MEDIUM_GUI_ITEM);
	}

}
