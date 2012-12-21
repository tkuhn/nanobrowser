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

public class NanobrowserWebPage extends WebPage {
	
	private static final long serialVersionUID = -4339409513715269264L;

	public NanobrowserApplication getNanobrowserApp() {
		return (NanobrowserApplication) getApplication();
	}
	
	public Agent getUser() {
		return getNanobrowserApp().getUser();
	}

}
