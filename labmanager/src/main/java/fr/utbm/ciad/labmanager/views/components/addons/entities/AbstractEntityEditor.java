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

package fr.utbm.ciad.labmanager.views.components.addons.entities;

import java.util.function.Consumer;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Binder.BindingBuilder;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinService;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.SimilarityError;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.users.UserIdentityChangedObserver;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

/** Abstract implementation for the editor of the information related to an entity.
 * 
 * @param <T> the type of the edited entity.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractEntityEditor<T extends IdentifiableEntity> extends Composite<VerticalLayout> implements LocaleChangeObserver {

	private static final long serialVersionUID = -9123030449423137764L;

	private final Class<T> entityType;

	private final Logger logger;

	private final MessageSourceAccessor messages;

	private final AuthenticatedUser authenticatedUser;

	private final Binder<T> entityBinder;

	private Details administrationDetails;

	private ToggleButton validatedEntity;
	
	private final boolean isBaseAdmin;

	private final boolean isAdvancedAdmin;

	private final String administationSectionTranslationKey;

	private final String validationTranslationKey;
	
	private final EntityEditingContext<T> editingContext;

	private boolean relinkEntityWhenSaving;

	/** Constructor.
	 *
	 * @param entityType the type of the edited entity.
	 * @param authenticatedUser the connected user.
	 * @param messages the accessor to the localized messages (Spring layer).
	 * @param logger the logger to be used by this view.
	 * @param administationSectionTranslationKey the key in the translation file that corresponds to
	 *     the label of the administration section.
	 * @param validationTranslationKey the key in the translation file that corresponds to the
	 *     label of the validation checkbox.
	 * @param editingContext the context that is used for representing the edited entity and all the associated files and entities.
	 * @param relinkEntityWhenSaving indicates if the editor must be relink to the edited entity when it is saved. This new link may
	 *     be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
	 */
	public AbstractEntityEditor(Class<T> entityType, AuthenticatedUser authenticatedUser,
			MessageSourceAccessor messages, Logger logger, String administationSectionTranslationKey,
			String validationTranslationKey,
			EntityEditingContext<T> editingContext,
			boolean relinkEntityWhenSaving) {
		this.administationSectionTranslationKey = administationSectionTranslationKey;
		this.validationTranslationKey = validationTranslationKey;
		this.entityType = entityType;
		this.messages = messages;
		this.logger = logger;
		this.entityBinder = createBinder(this.entityType);
		this.authenticatedUser = authenticatedUser;
		this.editingContext = editingContext;
		this.relinkEntityWhenSaving = relinkEntityWhenSaving;

		if (this.authenticatedUser != null && this.authenticatedUser.get().isPresent()) {
			final var role = this.authenticatedUser.get().get().getRole();
			this.isBaseAdmin = role.hasBaseAdministrationRights();
			this.isAdvancedAdmin = role.hasAdvancedAdministrationRights();
		} else {
			this.isBaseAdmin = false;
			this.isAdvancedAdmin = false;
		}
	}
	
	/** Create the instance of the binder for the provided type.
	 *
	 * @param type the type of the entities to be binded.
	 * @return the binder instance.
	 */
	protected Binder<T> createBinder(Class<T> type) {
		return new Binder<>(type);
	}

	/** Replies the context that represents the editied entity and the associated files and entities.
	 *
	 * @return the editing context.
	 */
	public EntityEditingContext<T> getEditingContext() {
		return this.editingContext;
	}

	/** Replies if the data inside the editor is valid.
	 *
	 * @return {@code true} if the fields have passed all the validation checks.
	 */
	public boolean isValidData() {
		return getEntityDataBinder().validate().isOk();
	}

	public SimilarityError isAlreadyInDatabase() {
		return SimilarityError.NO_ERROR;
	}
	
	/** Replies if the editor is launched by an user with base administration rights.
	 * 
	 * @return {@code true} if the user has base administration rights.
	 */
	public boolean isBaseAdmin() {
		return this.isBaseAdmin;
	}

	/** Replies if the editor is launched by an user with advanced administration rights.
	 * 
	 * @return {@code true} if the user has advanced administration rights.
	 */
	public boolean isAdvancedAdmin() {
		return this.isAdvancedAdmin;
	}

	/** Replies the accessor to the localized strings.
	 *
	 * @return the accessor.
	 */
	protected MessageSourceAccessor getMessageSourceAccessor() {
		return this.messages;
	}

	/** Replies the binder between the data provider and the fields.
	 *
	 * @return the entity binder.
	 */
	protected Binder<T> getEntityDataBinder() {
		return this.entityBinder;
	}

	/** Replies the authenticated user.
	 *
	 * @return the user.
	 */
	protected AuthenticatedUser getAuthenticatedUser() {
		return this.authenticatedUser;
	}

	/** Replies the edited entity.
	 *
	 * @return the edited entity, never {@code null}.
	 */
	public T getEditedEntity() {
		return this.editingContext.getEntity();
	}

	/** Replies if the edited entity is new for the JPA infrastructure.
	 *
	 * @return {@code true} if the entity has no JPA identifier.
	 */
	public boolean isNewEntity() {
		final var entity = getEditedEntity();
		return entity == null || entity.getId() == 0l;
	}

	/** Replies the logger.
	 *
	 * @return the logger.
	 */
	protected Logger getLogger() {
		return this.logger;
	}

	/** Replies the type of the edited entity.
	 * 
	 * @return the entity type.
	 */
	protected Class<T> getEntityType() {
		return this.entityType;
	}

	/** Create the editor content and links the beans
	 */
	protected final void createEditorContentAndLinkBeans() {
		final var rootContainer = getContent();
		rootContainer.setSpacing(false);
		createEditorContent(rootContainer);
		linkBeans();
	}

	/** Link the beans to the editor.
	 */
	protected void linkBeans() {
		getEntityDataBinder().setBean(getEditedEntity());
	}

	/** Unlink the beans to the editor.
	 *
	 * @param saveInEntity indicates if the field values must be written in the entity.
	 */
	protected void unlinkBeans(boolean saveInEntity) {
		final var binder = getEntityDataBinder();
		if (saveInEntity && binder.hasChanges()) {
			binder.writeBeanAsDraft(binder.getBean(), true);
		}
		binder.setBean(null);
	}

	/** Create the content of the editor.
	 * This function should invoke {@link #createAdministrationComponents(VerticalLayout, Consumer, Consumer)}.
	 *
	 * @param rootContainer the container.
	 * @see #createAdministrationComponents(VerticalLayout, Consumer, Consumer)
	 */
	protected abstract void createEditorContent(VerticalLayout rootContainer);

	/** Create the components for administrator.
	 *
	 * @param receiver the receiver of the component
	 * @param builderCallback the callback that is invoked to fill the administration form.
	 * @param validationBinder invoked to bind the validation attributes of the entity.
	 * @see #createAdministrationComponents(VerticalLayout, Consumer) 
	 */
	protected void createAdministrationComponents(VerticalLayout receiver,
			Consumer<FormLayout> builderCallback,
			Consumer<BindingBuilder<T, Boolean>> validationBinder) {
		final var content = ComponentFactory.newColumnForm(1);

		if (builderCallback != null) {
			builderCallback.accept(content);
		}

		if (validationBinder != null) {
			this.validatedEntity = new ToggleButton();
			content.add(this.validatedEntity);
			validationBinder.accept(this.entityBinder.forField(this.validatedEntity));
		}

		if (content.getElement().getChildCount() > 0) {
			this.administrationDetails = new Details("", content); //$NON-NLS-1$
			this.administrationDetails.setOpened(false);
			this.administrationDetails.addThemeVariants(DetailsVariant.FILLED);
			receiver.add(this.administrationDetails);
		}
	}

	/** Create the components for administrator with additional component.
	 *
	 * @param receiver the receiver of the component
	 * @param validationBinder invoked to bind the validation attributes of the entity.
	 * @see #createAdministrationComponents(VerticalLayout, Consumer, Consumer) 
	 */
	protected final void createAdministrationComponents(VerticalLayout receiver, Consumer<BindingBuilder<T, Boolean>> validationBinder) {
		createAdministrationComponents(receiver, null, validationBinder);
	}

	/** Invoked for validating the entity by an organizational structure manager. This function does not save.
	 * This function must be overridden by the child class that need to do a specific
	 * saving process.
	 * 
	 * @see #doValidate(IdentifiableEntity)
	 */
	public final void validateByOrganizationalStructureManager() {
		doValidate(getEditedEntity());
	}

	/** Invoked for validating the entity. This function does not save.
	 * This function must be overridden by the child class that need to do a specific
	 * saving process. 
	 *
	 * @param entity the entity to validate.
	 */
	protected void doValidate(T entity) {
		try {
			this.validatedEntity.setValue(Boolean.TRUE);
			notifyValidated();
		} catch (Throwable ex) {
			notifyValidationError(ex);
		}
	}

	/** Notify the user that the entity was validated.
	 */
	public final void notifyValidated() {
		notifyValidated(computeValidationSuccessMessage());
	}

	/** Compute the message for validation successs.
	 *
	 * @return the message.
	 */
	protected abstract String computeValidationSuccessMessage();

	/** Notify the user that the entity was validated.
	 *
	 * @param message the validation message to show to the user.
	 */
	@SuppressWarnings("static-method")
	protected void notifyValidated(String message) {
		ComponentFactory.showSuccessNotification(message);
	}

	/** Notify the user that its personal information has changed.
	 */
	protected void notifyAuthenticatedUserChange() {
		UserIdentityChangedObserver app = null;
		final var view = UI.getCurrent().getCurrentView();
		if (view != null) {
			app = view.findAncestor(UserIdentityChangedObserver.class);
		}
		if (app != null) {
			app.authenticatedUserIdentityChange();
		}
		ComponentFactory.showInfoNotification(getTranslation("views.authenticated_user_change")); //$NON-NLS-1$
	}

	/** Notify the user that the form contains invalid information.
	 */
	public void notifyInvalidity() {
		ComponentFactory.showWarningNotification(getTranslation("views.save_invalid_data")); //$NON-NLS-1$
	}

	public void notifySimilarityError(String message) {
		ComponentFactory.showErrorNotification(message); //$NON-NLS-1$
	}

	public void notifySimilarityWarning(String message) {
		ComponentFactory.showWarningNotification(message); //$NON-NLS-1$
	}

	/** Notify the user that the entity cannot be validated.
	 *
	 * @param error the error.
	 */
	public final void notifyValidationError(Throwable error) {
		notifyValidationError(error, computeValidationErrorMessage(error));
	}

	/** Compute the message for validation error.
	 *
	 * @param error the error.
	 * @return the message.
	 */
	protected abstract String computeValidationErrorMessage(Throwable error);

	/** Notify the user that the entity cannot be validated.
	 *
	 * @param message the message to show up to the user.
	 */
	protected void notifyValidationError(Throwable error, String message) {
		this.logger.warn("Error when validating the entity by " + AuthenticatedUser.getUserName(this.authenticatedUser) //$NON-NLS-1$
		+ ": " + message + "\n-> " + error.getLocalizedMessage(), error); //$NON-NLS-1$ //$NON-NLS-2$
		ComponentFactory.showErrorNotification(message);
	}

	/** Invoked for saving the entity. This function must be overridden by the child class that need to do a specific
	 * saving process. 
	 *
	 * @throws Exception if saving cannot be done.
	 */
	protected void doSave() throws Exception {
		getEditingContext().save();
	}
	
	/** Invoked for saving the entity.
	 *
	 * @return {@code true} if the saving was a success.
	 * @see #doSave()
	 */
	public final boolean save() {
		try {
			doSave();
			// Relink the beans for ensuring the editor has
			// the last instance of the entity instance
			// that may have changed due to the saving process
			if (this.relinkEntityWhenSaving) {
				linkBeans();
			}
			notifySaved();
			return true;
		} catch (Throwable ex) {
			notifySavingError(ex);
		}
		return false;
	}

	/** Notify the user that the entity was saved.
	 */
	public final void notifySaved() {
		notifySaved(computeSavingSuccessMessage());
	}

	/** Compute the message for saving success.
	 *
	 * @return the message.
	 */
	protected abstract String computeSavingSuccessMessage();

	/** Notify the user that the entity was saved.
	 *
	 * @param message the message to show up to the user.
	 */
	protected void notifySaved(String message) {
		this.logger.info("Data successfully saved by " + AuthenticatedUser.getUserName(this.authenticatedUser) //$NON-NLS-1$
		+ ": " + message); //$NON-NLS-1$
		ComponentFactory.showSuccessNotification(message);
	}

	/** Notify the user that the entity cannot be saved.
	 *
	 * @param error the error.
	 */
	public final void notifySavingError(Throwable error) {
		notifySavingError(error, computeSavingErrorMessage(error));
	}

	/** Compute the message for saving error.
	 *
	 * @param error the error.
	 * @return the message.
	 */
	protected abstract String computeSavingErrorMessage(Throwable error);

	/** Notify the user that the entity cannot be saved.
	 *
	 * @param message the message to show up to the user.
	 */
	protected void notifySavingError(Throwable error, String message) {
		this.logger.warn("Error when saving entity data by " + AuthenticatedUser.getUserName(this.authenticatedUser) //$NON-NLS-1$
		+ ": " + message + "\n-> " + error.getLocalizedMessage(), error); //$NON-NLS-1$ //$NON-NLS-2$
		ComponentFactory.showErrorNotification(message);
	}

	/** Invoked for preparing the deletion of the entity. This function must be overridden by the child class that need to do a specific
	 * deletion process. 
	 *
	 * @return the context for deleting the entity.
	 */
	protected EntityDeletingContext<T> prepareDeletion() {
		return getEditingContext().startDeletion();
	}

	/** Invoked for deleting the entity without querying the user.
	 *
	 * @return {@code true} if the deletion was a success.
	 * @see #doDelete(EntityDeletingContext) 
	 */
	public final boolean delete() {
		try {
			final var context = prepareDeletion();
			assert context != null;
			if (context.isDeletionPossible()) {
				doDelete(context);
				notifyDeleted();
				// Unlink after notifying for having the capability to get the name of the deleted entity
				unlinkBeans(false);
				return true;
			}
			var ex = context.getDeletionStatus().getException(getMessageSourceAccessor(), getLocale());
			if (ex == null) {
				ex = new IllegalStateException();
			}
			notifyDeletionError(ex);
		} catch (Throwable ex) {
			notifyDeletionError(ex);
		}
		return false;
	}

	/** Invoked for deleting the entity thourhg the provided context. This function must be overridden by the child class that need to do a specific
	 * deletion process. 
	 *
	 * @param deletionContext the context of deletion.
	 * @throws Exception if saving cannot be done.
	 */
	protected void doDelete(EntityDeletingContext<T> deletionContext) throws Exception {
		deletionContext.delete();
	}

	/** Notify the user that the entity was deleted.
	 */
	public final void notifyDeleted() {
		notifyDeleted(computeDeletionSuccessMessage());
	}

	/** Compute the message for deletion success.
	 *
	 * @return the message.
	 */
	protected abstract String computeDeletionSuccessMessage();

	/** Notify the user that the entity was deleted.
	 *
	 * @param message the message to show up to the user.
	 */
	protected void notifyDeleted(String message) {
		this.logger.info("Data successfully deleted by " + AuthenticatedUser.getUserName(this.authenticatedUser) //$NON-NLS-1$
		+ ": " + message); //$NON-NLS-1$
		ComponentFactory.showSuccessNotification(message);
	}

	/** Notify the user that the entity cannot be deleted.
	 *
	 * @param error the error.
	 */
	public final void notifyDeletionError(Throwable error) {
		notifyDeletionError(error, computeDeletionErrorMessage(error));
	}

	/** Compute the error message for deletion.
	 *
	 * @param error the error.
	 * @return the message.
	 */
	protected abstract String computeDeletionErrorMessage(Throwable error);

	/** Notify the user that the entity cannot be deleted.
	 *
	 * @param message the message to show up to the user.
	 */
	protected void notifyDeletionError(Throwable error, String message) {
		this.logger.warn("Error when deleting entity data by " + AuthenticatedUser.getUserName(this.authenticatedUser) //$NON-NLS-1$
			+ ": " + message + "\n-> " + error.getLocalizedMessage(), error); //$NON-NLS-1$ //$NON-NLS-2$
		ComponentFactory.showErrorNotification(message);
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		if (this.administrationDetails != null) {
			this.administrationDetails.setSummaryText(getTranslation(this.administationSectionTranslationKey));
		}
		if (this.validatedEntity != null) {
			this.validatedEntity.setLabel(getTranslation(this.validationTranslationKey));
		}
	}

	/** Create a "details" section that may be marked with error.
	 *
	 * @param container the container of the section.
	 * @param content the content of the section.
	 * @param sectionId the identifier of the section in the editor.
	 * @return the section container.
	 * @see #createDetails(HasComponents, Component, String)
	 * @see #initializeDetails(Details, String, boolean) 	
	 */
	protected DetailsWithErrorMark createDetailsWithErrorMark(HasComponents container, Component content, String sectionId) {
		return createDetailsWithErrorMark(container, content, sectionId, false);
	}

	/** Create a "details" section that may be marked with error.
	 *
	 * @param container the container of the section.
	 * @param content the content of the section.
	 * @param sectionId the identifier of the section in the editor.
	 * @param openByDefault indicates if the details must be open by default, i.e., when there is opening preference stored in the session.
	 * @return the section container.
	 * @see #createDetails(HasComponents, Component, String)
	 * @see #initializeDetails(Details, String, boolean)
	 */
	protected DetailsWithErrorMark createDetailsWithErrorMark(HasComponents container, Component content, String sectionId, boolean openByDefault) {
		final var details = new DetailsWithErrorMark(content);
		if (container != null) {
			container.add(details);
		}
		initializeDetails(details, sectionId, openByDefault);
		return details;
	}

	/** Create a "details" section.
	 *
	 * @param container the container of the section.
	 * @param content the content of the section.
	 * @param sectionId the identifier of the section in the editor.
	 * @return the section container.
	 * @see #createDetailsWithErrorMark(HasComponents, Component, String)
	 * @see #initializeDetails(Details, String, boolean) 
	 */
	protected Details createDetails(HasComponents container, Component content, String sectionId) {
		return createDetails(container, content, sectionId, false);
	}

	/** Create a "details" section.
	 *
	 * @param container the container of the section.
	 * @param content the content of the section.
	 * @param sectionId the identifier of the section in the editor.
	 * @param openByDefault indicates if the details must be open by default, i.e., when there is opening preference stored in the session.
	 * @return the section container.
	 * @see #createDetailsWithErrorMark(HasComponents, Component, String)
	 * @see #initializeDetails(Details, String, boolean) 
	 */
	protected Details createDetails(HasComponents container, Component content, String sectionId, boolean openByDefault) {
		final var details = new Details("", content); //$NON-NLS-1$
		if (container != null) {
			container.add(details);
		}
		initializeDetails(details, sectionId, openByDefault);
		return details;
	}

	/** Initialize a "details" section.
	 *
	 * @param details the component to initialize.
	 * @param sectionId the identifier of the section in the editor.
	 * @param openByDefault indicates if the details must be open by default, i.e., when there is opening preference stored in the session.
	 * @see #createDetailsWithErrorMark(HasComponents, Component, String)
	 * @see #createDetails(HasComponents, Component, String)
	 */
	protected void initializeDetails(Details details, String sectionId, boolean openByDefault) {
		final var key = new StringBuilder().append(ViewConstants.DETAILS_SECTION_OPENING_ROOT).append(getClass().getName());
		final var sectionKey = key.toString();
		final var attributeKey = key.append('.').append(sectionId).toString();
		
		final var request = VaadinService.getCurrentRequest();
		final var session = request != null ? request.getWrappedSession() : null;
		if (session == null || session.getAttribute(sectionKey) == null) {
			// Section was never defined. Use the default configuration.
			details.setOpened(openByDefault);
		} else {
			// Section was defined. Use the stored configuration.
			final var value = session.getAttribute(attributeKey);
			if (value instanceof Boolean bvalue) {
				details.setOpened(bvalue.booleanValue());
			} else if (value instanceof String svalue) {
					details.setOpened(Boolean.parseBoolean(svalue));
			} else {
				details.setOpened(openByDefault);
			}
		}

		details.addOpenedChangeListener(event -> {
			final var session0 = VaadinService.getCurrentRequest().getWrappedSession();
			session0.setAttribute(sectionKey, Boolean.TRUE.toString());
			session0.setAttribute(attributeKey, Boolean.toString(event.isOpened()));
		});
	}

}
