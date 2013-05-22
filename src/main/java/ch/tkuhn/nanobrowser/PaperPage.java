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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PaperPage extends NanobrowserWebPage {

	private static final long serialVersionUID = -6918165090432972476L;

	private PaperElement paper;

	public PaperPage(final PageParameters parameters) {
		
		String uri = parameters.get("uri").toString();
		paper = new PaperElement(uri);
		
		update();
		
		add(new MenuBar("menubar"));
		
		add(new Label("title", paper.getDoiString()));
		
		add(new ExternalLink("uri", paper.getURI(), paper.getTruncatedURI()));

		WebMarkupContainer paperboxoverlay = new WebMarkupContainer("paperboxoverlay");
		paperboxoverlay.add(new AttributeModifier("onclick", new Model<String>("window.open('" + uri + "','_blank')")));
		add(paperboxoverlay);

		WebMarkupContainer paperbox = new WebMarkupContainer("paperbox");
		paperbox.add(new AttributeModifier("src", new Model<String>(uri)));
		add(paperbox);

	}
	
	private void update() {
	}

}
