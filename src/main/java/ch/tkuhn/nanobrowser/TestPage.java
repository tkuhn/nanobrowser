package ch.tkuhn.nanobrowser;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class TestPage extends WebPage {

	private static final long serialVersionUID = 6634220350799250923L;

	public TestPage(final PageParameters parameters) {

		add(new Label("message", "Works!"));

	}

}
