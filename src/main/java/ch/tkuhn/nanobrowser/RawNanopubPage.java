package ch.tkuhn.nanobrowser;

import java.io.ByteArrayOutputStream;

import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.IResource;
import org.openrdf.rio.RDFFormat;

import ch.tkuhn.hashuri.rdf.RdfUtils;

public class RawNanopubPage implements IResource {

	private static final long serialVersionUID = 5368904845556181806L;

	private NanopubElement pub;
	private RDFFormat format;
	
	public RawNanopubPage(RDFFormat format) {
		this.format = format;
	}

	@Override
	public void respond(Attributes attributes) {
		WebResponse resp = (WebResponse) attributes.getResponse();
		resp.setContentType("application/x-trig");
		try {
			pub = new NanopubElement(attributes.getParameters().get("uri").toString());
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			RdfUtils.writeNanopub(pub.getNanopub(), b, format);
			b.close();
			resp.write(b.toByteArray());
		} catch (Exception ex) {}
		resp.close();
	}

}
