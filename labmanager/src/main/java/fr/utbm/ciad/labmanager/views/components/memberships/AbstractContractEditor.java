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

package fr.utbm.ciad.labmanager.views.components.memberships;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.member.*;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotNullDateValidator;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

/** Abstract implementation for the editor of the information related to a membership.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public abstract class AbstractContractEditor extends AbstractEntityEditor<Membership> {

	private static final long serialVersionUID = -592763051466628801L;

	private static final String ANONYMOUS = "?"; //$NON-NLS-1$

	private DetailsWithErrorMark contractEndDate;

	private DatePicker to;

	/** Constructor.
	 *
	 * @param context the editing context for the membership.
	 * @param editAssociatedPerson indicates if the associated person could be edited or not.
	 * @param relinkEntityWhenSaving indicates if the editor must be relink to the edited entity when it is saved. This new link may
	 *     be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
	 * @param authenticatedUser the connected user.
	 * @param messages the accessor to the localized messages (Spring layer).
	 * @param logger the logger to be used by this view.
	 */
	public AbstractContractEditor(EntityEditingContext<Membership> context, boolean editAssociatedPerson,
								  boolean relinkEntityWhenSaving, AuthenticatedUser authenticatedUser,
								  MessageSourceAccessor messages, Logger logger) {
		super(Membership.class, authenticatedUser, messages, logger,
				"views.membership.administration_details", //$NON-NLS-1$
				null, context, relinkEntityWhenSaving);
	}

	/** Create the section for editing the contract end date.
	 *
	 * @param rootContainer the container.
	 */
	@Override
	protected void createEditorContent(VerticalLayout rootContainer) {
		final var content = ComponentFactory.newColumnForm(1);


		this.to = new DatePicker();
		this.to.setPrefixComponent(VaadinIcon.SIGN_OUT_ALT.create());
		this.to.setClearButtonVisible(true);
		content.add(this.to, 1);

		content.add(this.to, 2);

		this.contractEndDate = createDetailsWithErrorMark(rootContainer, content, "position"); //$NON-NLS-1$

		getEntityDataBinder().forField(this.to)
				.withValidator(new NotNullDateValidator(getTranslation("views.membership.since.error"))) //$NON-NLS-1$
				.withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.to, this.contractEndDate))
				.bind(Membership::getMemberToWhen, Membership::setMemberToWhen);
	}

	@Override
	protected String computeSavingSuccessMessage() {
		final var person = getEditedEntity().getPerson();
		final var name = person != null ? person.getFullName() : ANONYMOUS;
		return getTranslation("views.membership.save_success", name); //$NON-NLS-1$
	}

	@Override
	protected String computeValidationSuccessMessage() {
		final var person = getEditedEntity().getPerson();
		final var name = person != null ? person.getFullName() : ANONYMOUS;
		return getTranslation("views.membership.validation_success", name); //$NON-NLS-1$
	}

	@Override
	protected String computeDeletionSuccessMessage() {
		throw new java.lang.UnsupportedOperationException("Method meaningless in this class");
	}

	@Override
	protected String computeSavingErrorMessage(Throwable error) {
		final var person = getEditedEntity().getPerson();
		final var name = person != null ? person.getFullName() : ANONYMOUS;
		return getTranslation("views.membership.save_error", name); //$NON-NLS-1$
	}

	@Override
	protected String computeValidationErrorMessage(Throwable error) {
		final var person = getEditedEntity().getPerson();
		final var name = person != null ? person.getFullName() : ANONYMOUS;
		return getTranslation("views.membership.validation_error", name, error.getLocalizedMessage()); //$NON-NLS-1$
	}

	@Override
	protected String computeDeletionErrorMessage(Throwable error) {
		throw new java.lang.UnsupportedOperationException("Method meaningless in this class");
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		super.localeChange(event);

		this.contractEndDate.setSummaryText(getTranslation("views.date.end.contract")); //$NON-NLS-1$
		this.to.setLabel(getTranslation("views.membership.edit_end_date")); //$NON-NLS-1$
	}
}