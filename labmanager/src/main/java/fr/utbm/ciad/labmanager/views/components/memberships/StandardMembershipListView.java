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

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.organization.OrganizationAddressService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.FileManager;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractTwoLevelTreeListView;
import fr.utbm.ciad.labmanager.views.components.addons.entities.TreeListEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;
import org.vaadin.lineawesome.LineAwesomeIcon;

/** List all the organization memberships.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardMembershipListView extends AbstractTwoLevelTreeListView<Person, Membership> {

	private static final long serialVersionUID = 2320930215751419329L;

	private final MembershipService membershipService;
	
	private final PersonService personService;

	private final UserService userService;

	private final ResearchOrganizationService organizationService;

	private final OrganizationAddressService addressService;

	private final ScientificAxisService axisService;

	private MenuItem extendsContractButton;

	private Column<TreeListEntity<Person, Membership>> periodColumn;

	private Column<TreeListEntity<Person, Membership>> serviceColumn;

	private Column<TreeListEntity<Person, Membership>> employerColumn;

	/** Constructor.
	 *
	 * @param authenticatedUser the connected user.
	 * @param messages the accessor to the localized messages (spring layer).
	 * @param membershipService the service for accessing the memberships.
	 * @param personService the service for accessing the JPA entities for persons.
	 * @param userService the service for accessing the JPA entities for users.
	 * @param organizationService the service for accessing the JPA entities for research organizations.
	 * @param addressService the service for accessing the JPA entities for organization addresses.
	 * @param axisService the service for accessing the JPA entities for scientific axes.
	 * @param logger the logger to use.
	 */
	public StandardMembershipListView(
			AuthenticatedUser authenticatedUser, MessageSourceAccessor messages,
			MembershipService membershipService, PersonService personService,
			UserService userService, ResearchOrganizationService organizationService,
			OrganizationAddressService addressService, ScientificAxisService axisService,
			Logger logger) {
		super(Person.class, Membership.class, authenticatedUser, messages, logger,
				"views.memberships.delete.title", //$NON-NLS-1$
				"views.memberships.delete.message", //$NON-NLS-1$
				"views.membership.delete_success", //$NON-NLS-1$
				"views.membership.delete_error"); //$NON-NLS-1$
		this.membershipService = membershipService;
		this.personService = personService;
		this.userService = userService;
		this.organizationService = organizationService;
		this.addressService = addressService;
		this.axisService = axisService;
		setHoverMenu(isAdminRole());
		setRootEntityFetcher(
				(parentId, pageRequest, filters) -> {
					return this.membershipService.getAllPersonsWithMemberships(pageRequest, filters,
							it -> Hibernate.initialize(it.getMemberships()));
				},
				(rootEntity) -> {
					return rootEntity.getMemberships().size();
				});
		setChildEntityFetcher((parentId, pageRequest, filters) -> {
			return this.membershipService.getMembershipsForPerson(parentId, pageRequest, filters);
		});
		initializeDataInGrid(getGrid(), getFilters());
	}

	@Override
	protected List<Column<TreeListEntity<Person, Membership>>> getInitialSortingColumns() {
		return Arrays.asList(getFirstColumn(), this.periodColumn);
	}
	
	@Override
	protected SortDirection getInitialSortingDirection(Column<TreeListEntity<Person, Membership>> column) {
		if (column == this.periodColumn) {
			return SortDirection.DESCENDING;
		}
		return SortDirection.ASCENDING;
	}
	@Override
	protected MenuBar createMenuBar() {
		var menu = super.createMenuBar();
		if (menu == null) {
			menu = new MenuBar();
			menu.addThemeVariants(MenuBarVariant.LUMO_ICON);
		}

		this.extendsContractButton = ComponentFactory.addIconItem(menu, LineAwesomeIcon.EXPAND_SOLID, null, null, it -> extendContractSelection());
		this.extendsContractButton.setEnabled(false);

		return menu;
	}

	private void extendContractSelection() {
		final var selection = this.getGrid().getSelectionModel().getFirstSelectedItem();
		if (selection.isPresent()) {
			final TreeListEntity<Person, Membership> entity = selection.get();
			extendContract(entity);
		}
	}

	protected final void extendContract(TreeListEntity<Person, Membership> entity) {
		final var membership = entity.getChildEntity();
		// Membership may be null because the user has clicked on the person name (the root).
		if (membership != null) {
			editChildEntity(membership);
		}
	}

	protected void extendContractChildEntity(Membership membership) {
		openExtendContractEditor(membership, getTranslation("views.membership.extend_contract_membership", membership.getPerson().getFullName())); //$NON-NLS-1$
	}

	protected void openExtendContractEditor(Membership membership, String title) {

	}

	@Override
	protected void onSelectionChange(Set<?> selection) {
		final int size = selection.size();
		super.onSelectionChange(selection);
		this.extendsContractButton.setEnabled(size == 1);
	}

	@Override
	protected AbstractFilters<Membership> createFilters() {
		return new MembershipFilters(() -> this.organizationService.getDefaultOrganization(), () -> this.organizationService.getFileManager() , this::refreshGrid);
	}

	@Override
	protected void configureFirstColumn(Column<TreeListEntity<Person, Membership>> rootColumn) {
		super.configureFirstColumn(rootColumn);
	}

	@Override
	protected Component createRootEntityComponent(Person entity) {
		return ComponentFactory.newPersonAvatar(entity);
	}

	@Override
	protected Component createChildEntityComponent(Membership entity) {
		final var person = entity.getPerson();
		final var gender = person == null ? null : person.getGender();
		return new Span(entity.getMemberStatus().getLabel(getMessageSourceAccessor(), gender, false, getLocale()));
	}

	@Override
	protected void createAdditionalColumns(TreeGrid<TreeListEntity<Person, Membership>> grid) {
		this.periodColumn = grid.addColumn(new ComponentRenderer<>(this::getPeriodLabel))
				.setAutoWidth(false);
		this.serviceColumn = grid.addColumn(new ComponentRenderer<>(this::getServiceName))
				.setAutoWidth(true);
		this.employerColumn = grid.addColumn(new ComponentRenderer<>(this::getEmployerName))
				.setAutoWidth(true);
	}

	private Component getPeriodLabel(TreeListEntity<Person, Membership> treeEntity) {
		final var entity = treeEntity.getChildEntity();
		if (entity == null) {
			return new Span();
		}
		final var sdate = entity.getMemberSinceWhen();
		final var edate = entity.getMemberToWhen();
		String periodStr;
		if (sdate == null) {
			if (edate == null) {
				periodStr = ""; //$NON-NLS-1$
			} else {
				periodStr = getTranslation("views.membership.date.to", Integer.toString(edate.getYear())); //$NON-NLS-1$
			}
		} else if (edate == null) {
			periodStr = getTranslation("views.membership.date.since", Integer.toString(sdate.getYear())); //$NON-NLS-1$
		} else {
			final var year0 = sdate.getYear();
			final var year1 = edate.getYear();
			if (year0 == year1) {
				periodStr = getTranslation("views.membership.date.single", Integer.toString(year0)); //$NON-NLS-1$
			} else {
				periodStr = getTranslation("views.membership.date.since_to", Integer.toString(year0), Integer.toString(year1)); //$NON-NLS-1$
			}
		}

		final var period = new Span(periodStr);
		return period;
	}

	private static ResearchOrganization getService(Membership membership) {
		assert membership != null;
		final var employer = membership.getSuperResearchOrganization();
		if (employer != null) {
			return membership.getDirectResearchOrganization();
		}
		return employer;
	}

	private Component getServiceName(TreeListEntity<Person, Membership> entity) {
		if (entity != null) {
			final var membership = entity.getChildEntity();
			if (membership != null) {
				final var organization = getService(membership);
				if (organization != null) {
					return ComponentFactory.newOrganizationAvatar(organization, this.organizationService.getFileManager());
				}
			}
		}
		return new Span();
	}

	private static ResearchOrganization getEmployer(Membership membership) {
		assert membership != null;
		final var employer = membership.getSuperResearchOrganization();
		if (employer != null) {
			return employer;
		}
		return membership.getDirectResearchOrganization();
	}

	private Component getEmployerName(TreeListEntity<Person, Membership> entity) {
		if (entity != null) {
			final var membership = entity.getChildEntity();
			if (membership != null) {
				final var organization = getEmployer(membership);
				if (organization != null) {
					return ComponentFactory.newOrganizationAvatar(organization, this.organizationService.getFileManager());
				}
			}
		}
		return new Span();
	}

	@Override
	protected void addEntity() {
		openMembershipEditor(new Membership(), getTranslation("views.membership.add_membership")); //$NON-NLS-1$
	}

	@Override
	protected void editChildEntity(Membership membership) {
		openMembershipEditor(membership, getTranslation("views.membership.edit_membership", membership.getPerson().getFullName())); //$NON-NLS-1$
	}

	/** Show the editor of a membership.
	 *
	 * @param membership the membership to edit.
	 * @param title the title of the editor.
	 */
	protected void openMembershipEditor(Membership membership, String title) {
		final var editor = new EmbeddedMembershipEditor(
				this.membershipService.startEditing(membership),
				membership.getPerson() == null,
				this.personService, this.userService,
				this.organizationService, this.addressService,
				this.axisService,
				getAuthenticatedUser(), getMessageSourceAccessor());
		final var newEntity = editor.isNewEntity();
		final SerializableBiConsumer<Dialog, Membership> refreshAll = (dialog, entity) -> refreshGrid();
		final SerializableBiConsumer<Dialog, Membership> refreshOne = (dialog, entity) -> refreshItem(TreeListEntity.child(entity));
		ComponentFactory.openEditionModalDialog(title, editor, false,
				// Refresh the "old" item, even if its has been changed in the JPA database
				newEntity ? refreshAll : refreshOne,
				newEntity ? null : refreshAll);
	}

	@Override
	protected EntityDeletingContext<Membership> createDeletionContextForChildEntities(Set<Membership> entities) {
		return this.membershipService.startDeletion(entities);
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		super.localeChange(event);

		if (this.extendsContractButton != null) {
			ComponentFactory.setIconItemText(this.extendsContractButton, getTranslation("views.extend_contract")); //$NON-NLS-1$
		}

		getFirstColumn().setHeader(getTranslation("views.person")); //$NON-NLS-1$
		this.periodColumn.setHeader(getTranslation("views.period")); //$NON-NLS-1$
		this.serviceColumn.setHeader(getTranslation("views.service")); //$NON-NLS-1$
		this.employerColumn.setHeader(getTranslation("views.employer")); //$NON-NLS-1$
	}

	/** UI and JPA filters for {@link StandardMembershipListView}.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected static class MembershipFilters extends AbstractDefaultOrganizationDataFilters<Membership> {

		private static final long serialVersionUID = -7866307628748739653L;

		private Checkbox includeTypes;

		/** Constructor.
		 *
		 * @param defaultOrganizationSupplier the provider of the default organization.
		 * @param fileManager the manager of files on the server.
		 * @param onSearch the callback function for running the filtering.
		 */
		public MembershipFilters(Supplier<ResearchOrganization> defaultOrganizationSupplier, Supplier<FileManager> fileManager, Runnable onSearch) {
			super(defaultOrganizationSupplier, fileManager, onSearch);
		}

		@Override
		protected void buildOptionsComponent(HorizontalLayout options) {
			this.includeTypes = new Checkbox(true);

			options.add(this.includeTypes);
		}

		@Override
		protected void resetFilters() {
			this.includeTypes.setValue(Boolean.TRUE);
		}
		
		@Override
		protected Predicate buildPredicateForDefaultOrganization(Root<Membership> root, CriteriaQuery<?> query,
				CriteriaBuilder criteriaBuilder, ResearchOrganization defaultOrganization) {
			final var crit1 = criteriaBuilder.equal(root.get("researchOrganization"), defaultOrganization); //$NON-NLS-1$
			final var crit2 = criteriaBuilder.equal(root.get("superResearchOrganization"), defaultOrganization); //$NON-NLS-1$
			return criteriaBuilder.or(crit1, crit2);
		}

		@Override
		protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<Membership> root,
				CriteriaBuilder criteriaBuilder) {
			if (this.includeTypes.getValue() == Boolean.TRUE) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("memberStatus")), keywords)); //$NON-NLS-1$
			}
		}

		@Override
		public void localeChange(LocaleChangeEvent event) {
			super.localeChange(event);
			this.includeTypes.setLabel(getTranslation("views.filters.include_types")); //$NON-NLS-1$
		}

	}

}
