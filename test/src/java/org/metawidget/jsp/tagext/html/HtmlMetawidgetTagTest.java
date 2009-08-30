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

package org.metawidget.jsp.tagext.html;

import java.lang.reflect.Field;

import junit.framework.TestCase;

import org.metawidget.jsp.tagext.MetawidgetTag;
import org.metawidget.jsp.tagext.FacetTag.FacetContent;
import org.metawidget.jsp.tagext.StubTag.StubContent;

/**
 * HtmlMetawidgetTag test cases.
 * <p>
 * These are just some fringe-case tests. Most of the testing is done by WebTest.
 *
 * @author Richard Kennard
 */

public class HtmlMetawidgetTagTest
	extends TestCase
{
	//
	// Public methods
	//

	public void testMetawidget()
		throws Exception
	{
		HtmlMetawidgetTag metawidget = new HtmlMetawidgetTag();

		// Value without prefix

		metawidget.setValue( "foo" );
		assertTrue( "foo".equals( metawidget.getPath() ));
		assertTrue( null == metawidget.getPathPrefix() );

		// Value with prefix

		metawidget.setValue( "foo.bar" );
		assertTrue( "foo.bar".equals( metawidget.getPath() ));
		assertTrue( "foo.".equals( metawidget.getPathPrefix() ));

		metawidget.setValue( "foo.bar.baz" );
		assertTrue( "foo.bar.baz".equals( metawidget.getPath() ));
		assertTrue( "foo.bar.".equals( metawidget.getPathPrefix() ));
	}

	public void testLifecyle()
		throws Exception
	{
		// We must use doStartTag(), not rely on super.release()

		HtmlMetawidgetTag metawidget = new HtmlMetawidgetTag();
		metawidget.setFacet( "foo", new FacetContent( "abc", null ) );
		metawidget.setParameter( "bar", "def" );
		metawidget.setStub( "baz", new StubContent( "ghi", null ) );

		Field facets = MetawidgetTag.class.getDeclaredField( "mFacets" );
		facets.setAccessible( true );
		Field parameters = MetawidgetTag.class.getDeclaredField( "mParameters" );
		parameters.setAccessible( true );
		Field stubs = MetawidgetTag.class.getDeclaredField( "mStubs" );
		stubs.setAccessible( true );
		Field needsConfiguring = MetawidgetTag.class.getDeclaredField( "mNeedsConfiguring" );
		needsConfiguring.setAccessible( true );
		needsConfiguring.set( metawidget, false );

		assertTrue( null != facets.get( metawidget ));
		assertTrue( null != parameters.get( metawidget ));
		assertTrue( null != stubs.get( metawidget ));
		assertTrue( false == (Boolean) needsConfiguring.get( metawidget ));

		metawidget.doStartTag();

		// Should have reset

		assertTrue( null == facets.get( metawidget ));
		assertTrue( null == parameters.get( metawidget ));
		assertTrue( null == stubs.get( metawidget ));
		assertTrue( false != (Boolean) needsConfiguring.get( metawidget ));
	}
}