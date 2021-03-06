/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2012 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.message.internal;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request.RequestBuilder;
import javax.ws.rs.core.RequestHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;

/**
 * Adapter for {@link Request.Builder Jersey Request.Builder} to {@link javax.ws.rs.core.Request.RequestBuilder
 * JAX-RS Request.RequestBuilder}.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
final class JaxrsRequestBuilderView implements javax.ws.rs.core.Request.RequestBuilder {

    private Request.Builder wrapped;

    public JaxrsRequestBuilderView(Request.Builder wrapped) {
        this.wrapped = wrapped;
    }

    static Request.Builder unwrap(javax.ws.rs.core.Request.RequestBuilder builder) {
        if (builder instanceof JaxrsRequestBuilderView) {
            return ((JaxrsRequestBuilderView) builder).wrapped;
        }

        throw new IllegalArgumentException(String.format("Request builder class type '%s' not supported.", builder.getClass().getName()));
    }

    @Override
    public JaxrsRequestBuilderView redirect(String uri) {
        wrapped.uri(uri);
        return this;
    }

    @Override
    public JaxrsRequestBuilderView redirect(URI uri) {
        wrapped.uri(uri);
        return this;
    }

    @Override
    public JaxrsRequestBuilderView redirect(UriBuilder uri) {
        wrapped.uri(uri.build());
        return this;
    }

    @Override
    public JaxrsRequestBuilderView method(String httpMethod) {
        wrapped.method(httpMethod);
        return this;
    }

    @Override
    public JaxrsRequestBuilderView entity(Object entity) {
        wrapped.content(entity);
        return this;
    }

    @Override
    public RequestBuilder entity(Object entity, Annotation[] annotations) {
        wrapped.writeAnnotations(annotations).content(entity);
        return this;
    }

    @Override
    public JaxrsRequestBuilderView clone() {
        return new JaxrsRequestBuilderView(wrapped.clone());
    }

    @Override
    public javax.ws.rs.core.Request build() {
        return wrapped.build().toJaxrsRequest();
    }

    @Override
    public JaxrsRequestBuilderView accept(MediaType... types) {
        wrapped.headers(HttpHeaders.ACCEPT, (Object[]) types);
        return this;
    }

    @Override
    public JaxrsRequestBuilderView accept(String... types) {
        wrapped.headers(HttpHeaders.ACCEPT, types);
        return this;
    }

    @Override
    public JaxrsRequestBuilderView acceptLanguage(Locale... locales) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JaxrsRequestBuilderView acceptLanguage(String... locales) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JaxrsRequestBuilderView cookie(Cookie cookie) {
        wrapped.cookie(cookie);
        return this;
    }

    @Override
    public JaxrsRequestBuilderView allow(String... methods) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JaxrsRequestBuilderView allow(Set<String> methods) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JaxrsRequestBuilderView cacheControl(CacheControl cacheControl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JaxrsRequestBuilderView encoding(String encoding) {
        headerSingle(HttpHeaders.CONTENT_ENCODING, encoding);
        return this;
    }

    @Override
    public JaxrsRequestBuilderView header(String name, Object value) {
        return header(name, value, false);
    }

    public JaxrsRequestBuilderView headerSingle(String name, Object value) {
        return header(name, value, true);
    }

    public JaxrsRequestBuilderView header(String name, Object value, boolean single) {
        if (value != null) {
            if (single) {
                wrapped.replace(name, Collections.singleton(value));
            } else {
                wrapped.header(name, value);
            }
        } else {
            wrapped.remove(name);
        }
        return this;
    }

    @Override
    public JaxrsRequestBuilderView replaceAll(RequestHeaders headers) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JaxrsRequestBuilderView language(String language) {
        headerSingle(HttpHeaders.CONTENT_LANGUAGE, language);
        return this;
    }

    @Override
    public JaxrsRequestBuilderView language(Locale language) {
        headerSingle(HttpHeaders.CONTENT_LANGUAGE, language);
        return this;
    }

    @Override
    public JaxrsRequestBuilderView type(MediaType type) {
        headerSingle(HttpHeaders.CONTENT_TYPE, type);
        return this;
    }

    @Override
    public JaxrsRequestBuilderView type(String type) {
        return type(type == null ? null : MediaType.valueOf(type));
    }

    @Override
    public JaxrsRequestBuilderView variant(Variant variant) {
        if (variant == null) {
            type((MediaType) null);
            language((String) null);
            encoding(null);
            return this;
        }

        type(variant.getMediaType());
        // TODO set charset
        language(variant.getLanguage());
        encoding(variant.getEncoding());

        return this;
    }
}
