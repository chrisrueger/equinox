/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.security.auth.credentials;

import java.security.Principal;
import javax.security.auth.Subject;

/**
 * This interface describes public credentials added by the Equinox login modules.
 * <p>
 * This interface should not be extended by clients.
 * </p>
 * @see Subject#getPublicCredentials()
 */
public interface IPublicCredential extends Principal {

	/**
	 * Returns user's primary role, if set. Might return <code>null</code> if 
	 * primary role is not set.
	 * @return user's primary role. Returns <code>null</code> if there is no primary role.
	 */
	public Principal getPrimaryRole();

	/**
	 * Returns user's roles. Might return <code>null</code> if there are no roles.
	 * @return user's roles. Returns <code>null</code> if there are no roles.
	 */
	public Principal[] getRoles();

	/**
	 * ID of the provider of this public credential. 
	 * @return provider ID 
	 */
	public String getProviderID();
}
