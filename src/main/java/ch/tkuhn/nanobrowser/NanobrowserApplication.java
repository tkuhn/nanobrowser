package ch.tkuhn.nanobrowser;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

public class NanobrowserApplication extends WebApplication {

	public Class<? extends Page> getHomePage() {
		return TestPage.class;
	}

}
