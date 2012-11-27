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
