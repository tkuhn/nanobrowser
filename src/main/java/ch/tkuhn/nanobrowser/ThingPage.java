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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ThingPage extends NanobrowserWebPage {

	private static final long serialVersionUID = -4673886567380719848L;
	
	private Thing thing;

	public ThingPage(final PageParameters parameters) {
		
		thing = new Thing(parameters.get("uri").toString());
		
		add(new MenuBar("menubar"));
		
		add(new Label("title", thing.getLastPartOfURI()));

		add(new ExternalLink("uri", thing.getURI(), thing.getTruncatedURI()));

		add(new HList("typelist", thing.getTypes(), "Types"));
		
	}

}
