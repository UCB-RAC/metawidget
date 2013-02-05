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

'use strict';

/**
 * Metawidget module.
 */

angular.module( 'metawidget', [] )

/**
 * Angular directive to expose Metawidget.
 */

.directive( 'metawidget', [ '$compile', function( $compile ) {

	// Returns the Metawidget

	return {

		/**
		 * Metawidget is (E)lement level.
		 */

		restrict: 'E',

		/**
		 * Metawidget isolated scope.
		 */

		scope: {
			ngModel: '=',
			readOnly: '=',
			config: '='
		},

		/**
		 * Metawidget must transclude, so that transcluded DOM doesn't have to
		 * reference 'ng-model'.
		 */

		transclude: true,

		/**
		 * Angular compile function. Configures an Angular-specific Metawidget
		 * and invokes buildWidgets on it.
		 */

		compile: function compile( element, attrs, transclude ) {

			return function( scope, element, attrs ) {

				// Set up an AngularMetawidget

				var mw = new metawidget.angular.AngularMetawidget( element, attrs, transclude, scope, $compile );

				// Observe
				//
				// Do not observe primitive types, such as 'string',
				// otherwise every keypress will recreate the widget

				var typeofNgModel = typeof ( scope.$eval( 'ngModel' ) );

				if ( typeofNgModel === 'object' || typeofNgModel === 'undefined' ) {
					scope.$watch( 'ngModel', function( newValue, oldValue ) {

						// Cannot test against mw.toInspect, because is based on
						// the parent

						if ( newValue !== oldValue ) {
							mw.invalidateInspection();
							_buildWidgets();
						}
					} );
				}
				scope.$watch( 'readOnly', function( newValue, oldValue ) {

					// Test against mw.readOnly, not oldValue, because it may
					// have been reset already by _buildWidgets

					if ( newValue !== mw.readOnly ) {
						// Do not mw.invalidateInspection()
						_buildWidgets();
					}
				} );
				scope.$watch( 'config', function( newValue, oldValue ) {

					if ( newValue !== oldValue ) {
						mw.configure( newValue );
						_buildWidgets();
					}
				} );

				// Build

				_buildWidgets();

				function _buildWidgets() {

					// Rebuild the transcluded tree at the start of each build.
					//
					// Rebuilding only at the start of the <em>initial</em>
					// build was sufficient for {{...}} expressions, but not
					// 'ng-click' triggers.

					mw.overriddenNodes = [];
					transclude( scope.$parent, function( clone ) {

						for ( var loop = 0, length = clone.length; loop < length; loop++ ) {
							var cloneNode = clone[loop];

							// Must check nodeType *and* other attributes,
							// because Angular wraps everything (even text
							// nodes) with a 'span class='ng-scope'' tag
							//
							// https://github.com/angular/angular.js/issues/1059

							if ( cloneNode.nodeType === 1 && ( cloneNode.tagName !== 'SPAN' || cloneNode.attributes.length > 1 ) ) {
								mw.overriddenNodes.push( cloneNode );
							}
						}
					} );

					// Invoke Metawidget

					mw.path = attrs.ngModel;
					mw.toInspect = scope.$parent.$eval( metawidget.util.splitPath( mw.path ).type );
					mw.readOnly = scope.$eval( 'readOnly' );
					mw.buildWidgets();
				}
			};
		}
	};
} ] );

/**
 * @namespace Metawidget for AngularJS environments.
 */

metawidget.angular = metawidget.angular || {};

metawidget.angular.AngularMetawidget = function( element, attrs, transclude, scope, $compile ) {

	if ( ! ( this instanceof metawidget.angular.AngularMetawidget ) ) {
		throw new Error( "Constructor called as a function" );
	}

	// toInspect, path and readOnly set by _buildWidgets()

	var pipeline = new metawidget.Pipeline( element[0] );
	pipeline.buildNestedMetawidget = function( attributes, mw ) {

		var nestedMetawidget = document.createElement( 'metawidget' );
		nestedMetawidget.setAttribute( 'to-inspect', attrs.toInspect + '.' + attributes.name );
		if ( attributes.readOnly === 'true' ) {
			nestedMetawidget.setAttribute( 'read-only', 'true' );
		} else {
			nestedMetawidget.setAttribute( 'read-only', attrs.readOnly );
		}
		if ( attrs.config !== undefined ) {
			nestedMetawidget.setAttribute( 'config', attrs.config );
		}

		return nestedMetawidget;
	};

	// Configure defaults

	pipeline.inspector = new metawidget.inspector.PropertyTypeInspector();
	pipeline.inspectionResultProcessors = [ new metawidget.angular.inspectionresultprocessor.AngularInspectionResultProcessor( element, scope ) ];
	pipeline.widgetBuilder = new metawidget.widgetbuilder.CompositeWidgetBuilder( [ new metawidget.widgetbuilder.OverriddenWidgetBuilder(), new metawidget.widgetbuilder.ReadOnlyWidgetBuilder(),
			new metawidget.widgetbuilder.HtmlWidgetBuilder() ] );
	pipeline.widgetProcessors = [ new metawidget.widgetprocessor.IdProcessor(), new metawidget.angular.widgetprocessor.AngularWidgetProcessor( $compile, scope ) ];
	pipeline.layout = new metawidget.layout.HeadingTagLayoutDecorator( new metawidget.layout.TableLayout() );

	var lastInspectionResult = undefined;

	this.configure = function( config ) {

		pipeline.configure( config );
		lastInspectionResult = undefined;
	};

	this.configure( scope.$eval( 'config' ) );

	this.invalidateInspection = function() {

		lastInspectionResult = undefined;
	};

	this.buildWidgets = function( inspectionResult ) {

		if ( inspectionResult !== undefined ) {
			lastInspectionResult = inspectionResult;
		} else if ( lastInspectionResult === undefined ) {
			var splitPath = metawidget.util.splitPath( this.path );
			lastInspectionResult = pipeline.inspect( this.toInspect, splitPath.type, splitPath.names, this );
		}

		pipeline.buildWidgets( lastInspectionResult, this );
	};

	/**
	 * Overridden to inspect unused nodes by evaluating their 'ng-model'
	 * attribute.
	 */

	this.onEndBuild = function() {

		while ( this.overriddenNodes.length > 0 ) {

			var child = this.overriddenNodes[0];
			this.overriddenNodes.splice( 0, 1 );

			// Unused facets don't count

			if ( child.tagName === 'FACET' ) {
				continue;
			}

			var childAttributes;

			if ( child.hasAttribute( 'ng-model' ) ) {

				var splitPath = metawidget.util.splitPath( child.getAttribute( 'ng-model' ) );
				var toInspect = scope.$parent.$eval( splitPath.type );
				childAttributes = pipeline.inspect( toInspect, splitPath.type, splitPath.names, this )[0];

			} else {

				// Manually created components default to no section

				childAttributes = {
					section: ''
				};
			}

			pipeline.layoutWidget( child, childAttributes, pipeline.element, this );
		}
	};
};

metawidget.angular.inspectionresultprocessor = metawidget.angular.inspectionresultprocessor || {};

/**
 * @class InspectionResultProcessor to evaluate Angular expressions.
 * 
 * @param scope
 *            scope of the Metawidget directive
 * @param buildWidgets
 *            a function to use to rebuild the widgets following a $watch
 * @returns {metawidget.angular.AngularInspectionResultProcessor}
 */

metawidget.angular.inspectionresultprocessor.AngularInspectionResultProcessor = function( element, scope ) {

	if ( ! ( this instanceof metawidget.angular.inspectionresultprocessor.AngularInspectionResultProcessor ) ) {
		throw new Error( "Constructor called as a function" );
	}

	this.processInspectionResult = function( inspectionResult, mw ) {

		for ( var loop = 0, length = inspectionResult.length; loop < length; loop++ ) {

			// For each attribute in the inspection result...

			var attributes = inspectionResult[loop];

			for ( var attribute in attributes ) {

				// ...that looks like an expression...

				var expression = attributes[attribute];

				if ( expression.length < 4 || expression.slice( 0, 2 ) !== '{{' || expression.slice( expression.length - 2, expression.length ) !== '}}' ) {
					continue;
				}

				// ...evaluate it...

				expression = expression.slice( 2, expression.length - 2 );
				attributes[attribute] = scope.$parent.$eval( expression ) + '';

				// ...and watch it for future changes

				scope.$parent.$watch( expression, function( newValue, oldValue ) {

					if ( newValue !== oldValue ) {
						mw.buildWidgets();
					}
				} );
			}
		}

		return inspectionResult;
	};
};

metawidget.angular.widgetprocessor = metawidget.angular.widgetprocessor || {};

/**
 * @class WidgetProcessor to add Angular bindings and validation, and compile
 *        the widget.
 * 
 * @returns {metawidget.angular.AngularWidgetProcessor}
 */

metawidget.angular.widgetprocessor.AngularWidgetProcessor = function( $compile, scope ) {

	if ( ! ( this instanceof metawidget.angular.widgetprocessor.AngularWidgetProcessor ) ) {
		throw new Error( "Constructor called as a function" );
	}

	this.processWidget = function( widget, attributes, mw ) {

		// Ignore transcluded widgets. Compiling them again using $compile
		// seemed to trigger 'ng-click' listeners twice?

		if ( widget.overridden !== undefined ) {
			return widget;
		}

		// Binding
		//
		// Scope the binding to scope.$parent, not scope, so that bindings look
		// more 'natural' (eg. 'foo.bar' not 'toInspect.bar')

		var binding = mw.path;

		if ( attributes._root !== 'true' ) {
			binding += '.' + attributes.name;
		}

		if ( widget.tagName === 'OUTPUT' ) {
			widget.innerHTML = '{{' + binding + '}}';
		} else if ( widget.tagName === 'BUTTON' ) {
			widget.setAttribute( 'ng-click', binding + '()' );
		} else {
			widget.setAttribute( 'ng-model', binding );
		}

		// Validation

		if ( attributes.required !== undefined ) {
			widget.setAttribute( 'ng-required', attributes.required );
		}

		if ( attributes.minimumLength !== undefined ) {
			widget.setAttribute( 'ng-minlength', attributes.minimumLength );
		}

		if ( attributes.maximumLength !== undefined ) {
			widget.setAttribute( 'ng-maxlength', attributes.maximumLength );

			// (maxlength set by WidgetBuilder)

			widget.removeAttribute( 'maxlength' );
		}

		$compile( widget )( scope.$parent );

		return widget;
	};
};