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
package org.glassfish.jersey.server.model;

import java.lang.reflect.InvocationHandler;

import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.internal.ProcessingException;
import org.glassfish.jersey.server.spi.internal.ResourceMethodDispatcher;

import org.glassfish.hk2.scopes.Singleton;

import org.jvnet.hk2.annotations.Scoped;

/**
 * Specific resource method dispatcher for dispatching requests to a void
 * {@link java.lang.reflect.Method Java method} with no input arguments
 * using a supplied {@link InvocationHandler Java method invocation handler}.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
@Scoped(Singleton.class)
final class VoidVoidDispatcherProvider implements ResourceMethodDispatcher.Provider {

    private static class VoidToVoidDispatcher extends AbstractJavaResourceMethodDispatcher {

        private VoidToVoidDispatcher(Invocable resourceMethod, InvocationHandler handler) {
            super(resourceMethod, handler);
        }

        @Override
        public Response doDispatch(Object resource, Request request) throws ProcessingException {
            invoke(resource);
            return Response.noContent().build();
        }
    }

    @Override
    public ResourceMethodDispatcher create(Invocable resourceMethod, InvocationHandler handler) {
        if (resourceMethod.getHandlingMethod().getReturnType() != void.class || !resourceMethod.getParameters().isEmpty()) {
            return null;
        }

        return new VoidToVoidDispatcher(resourceMethod, handler);
    }
}
