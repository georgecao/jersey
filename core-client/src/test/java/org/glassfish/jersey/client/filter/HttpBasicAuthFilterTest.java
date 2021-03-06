/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.client.filter;


import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientFactory;
import org.glassfish.jersey.client.JerseyInvocation;
import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.message.internal.Responses;
import org.glassfish.jersey.process.Inflector;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Martin Matula (martin.matula at oracle.com)
 */
public class HttpBasicAuthFilterTest {
    private JerseyInvocation.Builder invBuilder;

    @Before
    public void setUp() {
        JerseyClient client = JerseyClientFactory.clientBuilder().transport(new TestTransport()).build();
        client.configuration().register(new HttpBasicAuthFilter("Uzivatelske jmeno", "Heslo"));
        invBuilder = client.target(UriBuilder.fromUri("/").build()).request();
    }

    @Test
    public void testGet() {
        Response r = invBuilder.get();
        assertEquals("Basic " + Base64.encodeAsString("Uzivatelske jmeno:Heslo"), r.getHeaders().getHeader(HttpHeaders.AUTHORIZATION));
    }

    private static class TestTransport implements Inflector<Request, Response> {
        @Override
        public Response apply(Request request) {
            Response.ResponseBuilder rb = Responses.from(Response.Status.OK, request);
            final String headerValue = request.getHeaders().getHeader(HttpHeaders.AUTHORIZATION);
            if (headerValue != null) {
                rb.header(HttpHeaders.AUTHORIZATION, headerValue);
            }
            return rb.build();
        }
    }
}
