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
package org.eclipse.equinox.internal.security.storage;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import org.eclipse.equinox.internal.security.auth.AuthPlugin;
import org.eclipse.equinox.internal.security.auth.nls.SecAuthMessages;

/**
 * PLEASE READ BEFORE CHANGING THIS FILE
 * 
 * At present most of the methods expect only file URLs. The API methods
 * take URLs for possible future expansion, and there is some code below
 * that would work with some other URL types, but the only supported URL
 * types at this time are file URLs. Also note that URL paths should not
 * be encoded (spaces should be spaces, not "%x20"). 
 *  
 * On encoding: Java documentation recommends using File.toURI().toURL().
 * However, in this process non-alphanumeric characters (including spaces)
 * get encoded and can not be used with the rest of Eclipse methods that
 * expect non-encoded strings.
 */
public class StorageUtils {

	/**
	 * Default name of the storage file
	 */
	final private static String propertiesFileName = "secure_preferences.equinox"; //$NON-NLS-1$

	/**
	 * Default locations:
	 * 1) user.home
	 * 2) Eclipse config location
	 */
	static public URL getDefaultLocation() throws IOException {
		String userHome = System.getProperty("user.home"); //$NON-NLS-1$
		if (userHome != null) {
			File file = new File(userHome, propertiesFileName);
			// NOTE: Don't use File.toURI().toURL() as it will escape space characters and such.
			// The escaped sequence will fail later when we try to open a stream on it.
			return file.toURL();
		}
		// use install location
		URL installLocation = AuthPlugin.getDefault().getConfigURL();
		if (installLocation != null && isFile(installLocation)) {
			File file = new File(installLocation.getPath(), propertiesFileName);
			// NOTE: Same thing about toURI() as above
			return file.toURL();
		}
		// practically, we never should reach this point but just in case:
		throw new IOException(SecAuthMessages.loginNoDefaultLocation);
	}

	static public OutputStream getOutputStream(URL url) throws IOException {
		if (isFile(url)) {
			File file = new File(url.getPath());
			return new FileOutputStream(file);
		}
		// note that code below does not work for File URLs - "by design" Java 
		// does not support creating output streams on file URLs. Code below should work 
		// for HTTP URLs; no idea as to the other types of URLs
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		return connection.getOutputStream();
	}

	static public InputStream getInputStream(URL url) throws IOException {
		if (url == null)
			return null;
		try {
			return url.openStream();
		} catch (FileNotFoundException e) {
			return null; // this is all right, means new file 
		}
	}

	static public boolean delete(URL url) {
		if (isFile(url)) {
			File file = new File(url.getPath());
			return file.delete();
		}
		return false;
	}

	static public boolean exists(URL url) {
		if (isFile(url)) {
			File file = new File(url.getPath());
			return file.exists();
		}
		return true;
	}

	static public boolean isFile(URL url) {
		return ("file".equals(url.getProtocol())); //$NON-NLS-1$
	}

}
