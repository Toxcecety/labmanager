/*
 * $Id$
 * 
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.utbm.ciad.labmanager.views.components.addons.converters;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

/** A converter from double to Number.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class DoubleToNumberConverter implements Converter<Double, Number> {

	private static final long serialVersionUID = 2868844832005910755L;

	@Override
	public Result<Number> convertToModel(Double value, ValueContext context) {
		if (value == null) {
			return Result.ok(Double.valueOf(0f));
		}
		return Result.ok(value);
	}

	@Override
	public Double convertToPresentation(Number value, ValueContext context) {
		if (value == null) {
			return Double.valueOf(0.);
		}
		return Double.valueOf(value.doubleValue());
	}

}
