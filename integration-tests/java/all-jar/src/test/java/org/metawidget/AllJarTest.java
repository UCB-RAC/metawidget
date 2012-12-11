// Metawidget
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package org.metawidget;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import junit.framework.TestCase;

/**
 * @author Richard Kennard
 */

public class AllJarTest
	extends TestCase {

	//
	// Private statics
	//

	private static final String	METAWIDGET_ALL_JAR	= "metawidget-all.jar";

	//
	// Public methods
	//

	/**
	 * Test metawidget-all.jar doesn't contain any incorrect files.
	 */

	public void testAllJar()
		throws Exception {

		JarFile jar = new JarFile( "../../../modules/java/all/target/" + METAWIDGET_ALL_JAR );

		boolean hadManifest = false;
		int hadTlds = 0;
		int hadXsds = 0;
		int hadXmls = 0;

		for ( Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements(); ) {

			String name = e.nextElement().getName();

			if ( name.equals( "org/" ) ) {
				continue;
			}

			if ( name.startsWith( "META-INF/" ) ) {

				if ( name.equals( "META-INF/" ) ) {
					continue;
				}

				if ( name.equals( "META-INF/MANIFEST.MF" ) ) {
					hadManifest = true;
					continue;
				}

				if ( name.equals( "META-INF/faces-config.xml" ) ) {
					continue;
				}

				if ( name.equals( "META-INF/metawidget-faces.taglib.xml" ) ) {
					continue;
				}

				if ( name.endsWith( ".tld" ) ) {
					hadTlds++;
					continue;
				}

			} else if ( name.startsWith( "org/metawidget/" ) ) {

				if ( name.endsWith( "/" ) ) {
					continue;
				}

				if ( name.endsWith( ".class" ) ) {
					continue;
				}

				if ( name.endsWith( "-1.0.xsd" ) ) {
					hadXsds++;
					continue;
				}

				if ( name.endsWith( "-default.xml" ) ) {
					hadXmls++;
					continue;
				}

				if ( name.startsWith( "org/metawidget/swing/icon" ) && name.endsWith( ".gif" ) ) {
					continue;
				}
			}

			assertTrue( "File should not be packaged in " + METAWIDGET_ALL_JAR + ": " + name, false );
		}

		assertTrue( hadManifest );
		assertEquals( 4, hadTlds );
		assertEquals( 2, hadXsds );
		assertEquals( 12, hadXmls );
	}
}