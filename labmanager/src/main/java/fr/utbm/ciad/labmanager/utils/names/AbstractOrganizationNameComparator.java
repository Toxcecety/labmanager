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

package fr.utbm.ciad.labmanager.utils.names;

import com.google.common.base.Strings;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;

/** Abstract implementation of utilities for comparing organization names.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
public abstract class AbstractOrganizationNameComparator implements OrganizationNameComparator {

	private double similaritylevel;

	@Override
	public double getSimilarityLevel() {
		return this.similaritylevel;
	}

	@Override
	public void setSimilarityLevel(double similarityLevel) {
		if (similarityLevel < 0.0) {
			this.similaritylevel = 0.0;
		} else {
			this.similaritylevel = Math.min(similarityLevel, 1.0);
		}
	}

	/** Create an instance of a string similarity computer.
	 * This is a factory method.
	 *
	 * @return the string similarity computer.
	 */
	protected abstract NormalizedStringSimilarity createStringSimilarityComputer();

	@Override
	public double getSimilarity(String acronym1, String name1, String acronym2, String name2) {
		final var similarityComputer = createStringSimilarityComputer();
		final var s1 = similarityComputer.similarity(acronym1, acronym2);
		final var s2 = similarityComputer.similarity(name1, name2);
		return Math.max(s1, s2);
	}

	/** Replies the similarity of the two strings.
	 *
	 * @param matcher the string similarity computer to be used.
	 * @param str1 the first string to compare.
	 * @param str2 the second string to compare.
	 * @return the level of similarity. {@code 0} means that the strings are not
	 *     similar, and {@code 1} means that they are totally equal.
	 */
	@SuppressWarnings("static-method")
	protected double getSimilarity(NormalizedStringSimilarity matcher, String str1, String str2) {
		if (Strings.isNullOrEmpty(str1) || Strings.isNullOrEmpty(str2)) {
			return 1.0;
		}
		return matcher.similarity(str1, str2);
	}

}
