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
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.member.*;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddress;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxis;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxisComparator;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.organization.OrganizationAddressService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.utils.bap.FrenchBap;
import fr.utbm.ciad.labmanager.utils.cnu.CnuSection;
import fr.utbm.ciad.labmanager.utils.conrs.ConrsSection;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.validators.DisjointEntityValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotNullDateValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotNullEntityValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotNullEnumerationValidator;
import fr.utbm.ciad.labmanager.views.components.addons.value.ComboListField;
import fr.utbm.ciad.labmanager.views.components.organizations.SingleOrganizationNameField;
import fr.utbm.ciad.labmanager.views.components.persons.SinglePersonNameField;
import fr.utbm.ciad.labmanager.views.components.scientificaxes.EmbeddedScientificAxisEditor;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

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

	private static final long serialVersionUID = -592763051466628800L;

	private static final String ANONYMOUS = "?"; //$NON-NLS-1$

	private DetailsWithErrorMark positionDetails;

	private DatePicker to;

	private ToggleButton publicPosition;

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

	@Override
	protected void createEditorContent(VerticalLayout rootContainer) {
		createPositionDetails(rootContainer);
	}

	/** Create the section for editing the description of the position.
	 *
	 * @param rootContainer the container.
	 */
	protected void createPositionDetails(VerticalLayout rootContainer) {
		final var content = ComponentFactory.newColumnForm(1);

		
		this.to = new DatePicker();
		this.to.setPrefixComponent(VaadinIcon.SIGN_OUT_ALT.create());
		this.to.setClearButtonVisible(true);
		content.add(this.to, 1);

		content.add(this.to, 2);

		this.positionDetails = createDetailsWithErrorMark(rootContainer, content, "position"); //$NON-NLS-1$

		getEntityDataBinder().forField(this.to)
			.withValidator(new NotNullDateValidator(getTranslation("views.membership.since.error"))) //$NON-NLS-1$
			.withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.to, this.positionDetails))
			.bind(Membership::getMemberToWhen, Membership::setMemberToWhen);
	}

	/** Replies the gender of the person associated to the membership.
	 *
	 * @return the gender. It may be {@code null} if the gender cannot be determined.
	 */
	protected Gender getPersonGender() {
		final var person = getEditedEntity().getPerson();
		return person == null ? null : person.getGender();
	}

	private String getStatusLabel(MemberStatus status) {
		return status.getLabel(getMessageSourceAccessor(), getPersonGender(), false, getLocale());
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
		final var person = getEditedEntity().getPerson();
		final var name = person != null ? person.getFullName() : ANONYMOUS;
		return getTranslation("views.membership.delete_success2", name); //$NON-NLS-1$
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
		final var person = getEditedEntity().getPerson();
		final var name = person != null ? person.getFullName() : ANONYMOUS;
		return getTranslation("views.membership.deletion_error2", name, error.getLocalizedMessage()); //$NON-NLS-1$ 
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		super.localeChange(event);

		this.positionDetails.setSummaryText(getTranslation("views.date.end.contract")); //$NON-NLS-1$
		this.to.setLabel(getTranslation("views.membership.edit_end_date")); //$NON-NLS-1$
	}

	/** A validator for the super organization in organization memberships.
	 *
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class MembershipSuperOrganizationValidator implements Validator<ResearchOrganization> {

		private static final long serialVersionUID = -5928220978769779047L;

		/**
		 * Constructor.
		 */
		protected MembershipSuperOrganizationValidator() {
			//
		}

		@Override
		public String toString() {
			return "MembershipSuperOrganizationValidator"; //$NON-NLS-1$
		}

		@Override
		public ValidationResult apply(ResearchOrganization value, ValueContext context) {
			final var service = getEditedEntity().getDirectResearchOrganization();
			if (value != null) {
				if (service.getType().isEmployer()) {
					if (value.getType().isEmployer()) {
						return ValidationResult.error(getTranslation("views.membership.organization.error.both_employers")); //$NON-NLS-1$
					}
					return ValidationResult.error(getTranslation("views.membership.organization.error.invalid_employer")); //$NON-NLS-1$
				}
				if (!value.getType().isEmployer()) {
					return ValidationResult.error(getTranslation("views.membership.organization.error.no_employer")); //$NON-NLS-1$
				}
				if (!service.isSubOrganizationOf(value)) {
					return ValidationResult.error(getTranslation("views.membership.organization.error.no_sub_organization")); //$NON-NLS-1$
				}
			} else {
				if (service == null) {
					return ValidationResult.error(getTranslation("views.membership.organization.error.no_service")); //$NON-NLS-1$
				}
				if (!service.getType().isEmployer()) {
					return ValidationResult.error(getTranslation("views.membership.organization.error.no_employer")); //$NON-NLS-1$
				}
			}
			return ValidationResult.ok();
		}

	}

	/** A validator for the organization address in a membership.
	 *
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class OrganizationAddressValidator implements Validator<OrganizationAddress> {

		private static final long serialVersionUID = -7982111295740316957L;

		/**
		 * Constructor.
		 */
		protected OrganizationAddressValidator() {
			//
		}

		@Override
		public String toString() {
			return "OrganizationAddressValidator"; //$NON-NLS-1$
		}

		@Override
		public ValidationResult apply(OrganizationAddress value, ValueContext context) {
			if (value != null) {
				final var service = getEditedEntity().getDirectResearchOrganization();
				final var employer = getEditedEntity().getSuperResearchOrganization();
				if (service != null && employer != null) {
					if (!service.getAddresses().contains(value) && !employer.getAddresses().contains(value)) {
						ValidationResult.error(getTranslation("views.membership.address.error.invalid_address2")); //$NON-NLS-1$
					}
				} else if (service != null) {
					if (!service.getAddresses().contains(value)) {
						ValidationResult.error(getTranslation("views.membership.address.error.invalid_address0")); //$NON-NLS-1$
					}
				} else if (employer != null) {
					if (!employer.getAddresses().contains(value)) {
						ValidationResult.error(getTranslation("views.membership.address.error.invalid_address1")); //$NON-NLS-1$
					}
				} else {
					ValidationResult.error(getTranslation("views.membership.address.error.no_orgnaization")); //$NON-NLS-1$
				}
			}
			return ValidationResult.ok();
		}

	}

}
