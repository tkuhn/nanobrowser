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

import java.util.Random;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

public class NanobrowserSession extends WebSession {

	private static final long serialVersionUID = 1729283818728681884L;

	private static Random random = new Random();

	private AgentElement user;

	public NanobrowserSession(Request request) {
		super(request);
		String baseUri = NanobrowserApplication.getProperty("nanopub-server-baseuri");
		user = new AgentElement(baseUri + "user/anonymous-" + random.nextInt(100000000));
	}

	public AgentElement getUser() {
		return user;
	}

	public void setUser(AgentElement user) {
		this.user = user;
	}

	public static NanobrowserSession get() {
		return (NanobrowserSession)Session.get();
	}

}
