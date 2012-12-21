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

import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SearchPage extends NanobrowserWebPage {

	private static final long serialVersionUID = 6936478522592088856L;
	
	private String searchText;
	
	private ListModel<Sentence> sentenceModel = new ListModel<Sentence>();

	public SearchPage(final PageParameters parameters) {
		
		searchText = parameters.get("q").toString();
		
		update();
		
		add(new MenuBar("menubar"));
		
		add(new VList("sentencelist", sentenceModel, "Sentences"));
		
	}
	
	private void update() {
		sentenceModel.setObject(Sentence.search(searchText, 100));
	}

}
