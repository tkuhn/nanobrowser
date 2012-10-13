package ch.tkuhn.nanobrowser;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	
	private Person user = new Person("http://www.example.org/somebody");
	
	public Person getUser() {
		return user;
	}
	
	public void setUser(Person user) {
		this.user = user;
	}
	
	protected void internalInit() {
		super.internalInit();
		this.mountPage("/nanopub", NanopubPage.class);
		this.mountPage("/sentence", SentencePage.class);
		this.mountPage("/person", PersonPage.class);
		this.mountPage("/thing", ThingPage.class);
		this.mountPage("/search", SearchPage.class);
	}

	public Class<? extends Page> getHomePage() {
		return MainPage.class;
	}
	
	public static String getTimestamp() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date()).replaceFirst("(..)$", ":$1");
	}

}
