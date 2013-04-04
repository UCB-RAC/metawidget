// Metawidget (licensed under LGPL)
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

package org.metawidget.inspectionresultprocessor.json;

import org.metawidget.inspectionresultprocessor.type.TypeMappingInspectionResultProcessorConfig;

/**
 * Configures a JsonTypeMappingProcessor with standard Java types.
 *
 * @author Ryan Bradley
 */

public class JsonTypeMappingProcessorConfig
	extends TypeMappingInspectionResultProcessorConfig {

	//
	// Constructor
	//

	public JsonTypeMappingProcessorConfig() {

		// Primitive datatypes as defined by section 3.2 of XML Schema Part 2: Datatypes Second
		// Edition

		setTypeMapping( String.class.getName(), "string" );
		setTypeMapping( Boolean.class.getName(), "boolean" );
		setTypeMapping( Character.class.getName(), "string" );

		setRemoveUnmappedTypes( true );
	}
}