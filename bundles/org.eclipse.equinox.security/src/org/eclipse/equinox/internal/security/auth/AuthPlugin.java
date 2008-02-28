/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.security.auth;

import java.net.URL;
import org.eclipse.core.runtime.*;
import org.eclipse.equinox.internal.security.storage.PasswordProviderSelector;
import org.eclipse.equinox.internal.security.storage.SecurePreferencesMapper;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.osgi.service.debug.DebugOptions;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;

// XXX general comment: how this bundle reacts to dynamic events (registry, OSGi) ?

public class AuthPlugin implements BundleActivator {

	/**
	 * The unique identifier constant of this plug-in.
	 */
	public static final String PI_AUTH = "org.eclipse.equinox.security"; //$NON-NLS-1$

	private static AuthPlugin singleton;

	private BundleContext bundleContext;
	private ServiceTracker debugTracker = null;
	private ServiceTracker configTracker = null;

	public static boolean DEBUG = false;
	public static boolean DEBUG_LOGIN_FRAMEWORK = false;

	/*
	 * Returns the singleton for this Activator. Callers should be aware that
	 * this will return null if the bundle is not active.
	 */
	public static AuthPlugin getDefault() {
		return singleton;
	}

	public AuthPlugin() {
		super();
	}

	public void start(BundleContext context) throws Exception {
		bundleContext = context;
		singleton = this;

		DEBUG = getBooleanOption(PI_AUTH + "/debug", false); //$NON-NLS-1$
		if (DEBUG)
			DEBUG_LOGIN_FRAMEWORK = getBooleanOption(PI_AUTH + "/debug/loginFramework", false); //$NON-NLS-1$

		// SecurePlatformInternal is started lazily when first SecureContext is created (this reduces 
		// time spend in the bundle activator).
	}

	public void stop(BundleContext context) throws Exception {

		PasswordProviderSelector.getInstance().stop();
		SecurePreferencesMapper.stop();
		SecurePlatformInternal.getInstance().stop();

		if (debugTracker != null) {
			debugTracker.close();
			debugTracker = null;
		}
		if (configTracker != null) {
			configTracker.close();
			configTracker = null;
		}
		bundleContext = null;
		singleton = null;
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}

	public void logError(String msg, Throwable e) {
		if (bundleContext == null) {
			System.err.println(msg);
			if (e != null)
				e.printStackTrace(System.err);
		} else {
			ILog log = Platform.getLog(bundleContext.getBundle());
			log.log(new Status(IStatus.ERROR, PI_AUTH, msg, e));
		}
	}

	public void logMessage(String msg) {
		if (bundleContext == null) {
			System.out.println(msg);
		} else {
			ILog log = Platform.getLog(bundleContext.getBundle());
			log.log(new Status(IStatus.INFO, PI_AUTH, msg, null));
		}
	}

	public boolean getBooleanOption(String option, boolean defaultValue) {
		if (debugTracker == null) {
			if (bundleContext == null)
				return defaultValue;
			debugTracker = new ServiceTracker(bundleContext, DebugOptions.class.getName(), null);
			debugTracker.open();
		}
		DebugOptions options = (DebugOptions) debugTracker.getService();
		if (options == null)
			return defaultValue;
		String value = options.getOption(option);
		if (value == null)
			return defaultValue;
		return value.equalsIgnoreCase("true"); //$NON-NLS-1$
	}

	public URL getConfigURL() {
		Filter filter = null;
		if (configTracker == null) {
			try {
				filter = bundleContext.createFilter(Location.CONFIGURATION_FILTER);
			} catch (InvalidSyntaxException e) {
				// should never happen
			}
			configTracker = new ServiceTracker(bundleContext, filter, null);
			configTracker.open();
		}
		Location location = (Location) configTracker.getService();
		if (location == null)
			return null;
		return location.getURL();
	}

}
