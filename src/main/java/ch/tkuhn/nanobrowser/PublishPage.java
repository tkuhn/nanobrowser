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
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PublishPage extends NanobrowserWebPage {

	private static final long serialVersionUID = -4957600403501618983L;

	private TextField<String> sentenceField;

	public PublishPage(final PageParameters parameters) {
		
		add(new MenuBar("menubar"));

		Form<?> form = new Form<Void>("form") {

			private static final long serialVersionUID = -6839390607867733448L;

			protected void onSubmit() {
				String s = sentenceField.getModelObject();
				SentenceElement sentence = null;
				if (s != null) {
					if (SentenceElement.isSentenceURI(s)) {
						sentence = new SentenceElement(s);
					} else if (SentenceElement.isSentenceText(s)) {
						sentence = SentenceElement.withText(s);
					}
				}
				if (sentence != null) {
					// TODO: publish!
					//setResponsePage(SentencePage.class, getPageParameters());
				} else {
					// TODO
				}
			}
			
		};

		add(form);

		form.add(sentenceField = new TextField<String>("sentence", Model.of("")));
		form.add(new Label("author", NanobrowserSession.get().getUser().getName()));
		form.add(new Label("creator", NanobrowserSession.get().getUser().getName()));
		
	}

}
