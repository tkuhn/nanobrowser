package ch.tkuhn.nanobrowser;

import java.io.ByteArrayOutputStream;

import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.IResource;

import ch.tkuhn.nanopub.NanopubUtils;

public class RawNanopubPage implements IResource {

	private static final long serialVersionUID = 5368904845556181806L;

	private NanopubElement pub;

	@Override
	public void respond(Attributes attributes) {
		WebResponse resp = (WebResponse) attributes.getResponse();
		resp.setContentType("application/x-trig");
		try {
			pub = new NanopubElement(attributes.getParameters().get("uri").toString());
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			NanopubUtils.writeAsTrigFile(pub.getNanopub(), b);
			b.close();
			resp.write(b.toByteArray());
		} catch (Exception ex) {}
		resp.close();
	}

}
