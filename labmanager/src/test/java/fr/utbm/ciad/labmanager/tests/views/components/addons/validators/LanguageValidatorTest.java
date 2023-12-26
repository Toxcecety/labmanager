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

package fr.utbm.ciad.labmanager.tests.views.components.addons.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.views.components.addons.validators.LanguageValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests for {@link LanguageValidator}.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@SuppressWarnings("all")
public class LanguageValidatorTest {

	@Test
	@DisplayName("apply(null)")
	public void apply_0() {
		LanguageValidator validator = new LanguageValidator("ERR");
		ValidationResult result = validator.apply(null, new ValueContext());
		assertNotNull(result);
		assertTrue(result.isError());
	}

	@Test
	@DisplayName("apply(e)")
	public void apply_1() {
		LanguageValidator validator = new LanguageValidator("ERR");
		for (final var value : CountryCode.values()) {
			ValidationResult result = validator.apply(value, new ValueContext());
			assertNotNull(result);
			if (value.isLanguageSource()) {
				assertFalse(result.isError(), "Error for " + value.name());
			} else {
				assertTrue(result.isError(), "Error for " + value.name());
			}
		}
	}

}
