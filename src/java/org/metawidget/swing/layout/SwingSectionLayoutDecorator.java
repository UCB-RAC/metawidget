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

package org.metawidget.swing.layout;

import javax.swing.JComponent;

import org.metawidget.layout.decorator.LayoutDecoratorConfig;
import org.metawidget.swing.Stub;
import org.metawidget.swing.SwingMetawidget;

/**
 * Convenience base class for LayoutDecorators wishing to decorate widgets based on changing
 * sections within Swing Layouts.
 *
 * @author Richard Kennard
 */

public abstract class SwingSectionLayoutDecorator
	extends org.metawidget.layout.decorator.SectionLayoutDecorator<JComponent, SwingMetawidget>
{
	//
	// Constructor
	//

	protected SwingSectionLayoutDecorator( LayoutDecoratorConfig<JComponent, SwingMetawidget> config )
	{
		super( config );
	}

	//
	// Public methods
	//

	@Override
	public void startLayout( JComponent container, SwingMetawidget metawidget )
	{
		super.startLayout( container, metawidget );
		container.putClientProperty( getClass(), null );
	}

	//
	// Protected methods
	//

	@Override
	protected State<JComponent> getState( JComponent container, SwingMetawidget metawidget )
	{
		@SuppressWarnings( "unchecked" )
		State<JComponent> state = (State<JComponent>) container.getClientProperty( getClass() );

		if ( state == null )
		{
			state = new State<JComponent>();
			container.putClientProperty( getClass(), state );
		}

		return state;
	}

	@Override
	protected boolean isEmptyStub( JComponent component )
	{
		return ( component instanceof Stub && component.getComponentCount() == 0 );
	}
}
