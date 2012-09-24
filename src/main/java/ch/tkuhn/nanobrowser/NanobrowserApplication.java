package ch.tkuhn.nanobrowser;

import java.io.InputStream;
import java.util.Properties;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

public class NanobrowserApplication extends WebApplication {
	
	private static Properties properties = new Properties();
	
	static {
		try {
			InputStream in = NanobrowserApplication.class.getResourceAsStream("/nanobrowser.properties");
			properties.load(in);
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}

	public Class<? extends Page> getHomePage() {
		return MainPage.class;
	}

}
