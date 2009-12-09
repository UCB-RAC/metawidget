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

package android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout.LayoutParams;

/**
 * Dummy implementation for unit testing.
 *
 * @author Richard Kennard
 */

public class View
{
	//
	// Public statics
	//

	public static final int	VISIBLE		= 0;

	public static final int	INVISIBLE	= 4;

	public static final int	FOCUS_UP	= 33;

	//
	// Private members
	//

	private ViewParent		mParent;

	private Object			mTag;

	private int				mVisibility	= View.VISIBLE;

	//
	// Constructor
	//

	public View( Context context )
	{
		// Ignore context
	}

	public View( Context context, AttributeSet attributeSet )
	{
		// Ignore context
	}

	public View( Context context, AttributeSet attributeSet, int index )
	{
		// Ignore context
	}

	//
	// Public methods
	//

	public Context getContext()
	{
		return null;
	}

	public ViewParent getParent()
	{
		return mParent;
	}

	public void setParent( ViewParent parent )
	{
		mParent = parent;
	}

	public ViewGroup.LayoutParams getLayoutParams()
	{
		return null;
	}

	public void setId( int id )
	{
		// Do nothing
	}

	public Object getTag()
	{
		return mTag;
	}

	public void setTag( Object tag )
	{
		mTag = tag;
	}

	public void setLayoutParams( LayoutParams layoutParams )
	{
		// Do nothing
	}

	public void setVisibility( int visibility )
	{
		mVisibility = visibility;
	}

	public int getVisibility()
	{
		return mVisibility;
	}

	public View findViewWithTag( String string )
	{
		return null;
	}

	public void setFocusable( boolean b )
	{
		// Do nothing
	}

	public void setFocusableInTouchMode( boolean b )
	{
		// Do nothing
	}

	public void requestFocus()
	{
		// Do nothing
	}

	public void setPadding( int leftPadding, int topPadding, int rightPadding, int bottomPadding )
	{
		// Do nothing
	}

	//
	// Protected methods
	//

	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
	{
		// Do nothing
	}

	protected void postInvalidate()
	{
		// Do nothing
	}
}
