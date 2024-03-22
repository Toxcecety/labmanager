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

package fr.utbm.ciad.labmanager.views.components.jurys;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.jury.JuryMembership;
import fr.utbm.ciad.labmanager.data.member.Gender;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.jury.JuryMembershipService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.countryflag.CountryFlag;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListView;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/** List all the jury memberships.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardJuryMembershipListView extends AbstractEntityListView<JuryMembership> {

	private static final long serialVersionUID = 7493099024816373107L;

	private final JuryMembershipDataProvider dataProvider;

	private final PersonService personService;

	private final UserService userService;

	private JuryMembershipService membershipService;

	private Column<JuryMembership> candidateColumn;

	private Column<JuryMembership> defenseTypeColumn;

	private Column<JuryMembership> universityColumn;

	private Column<JuryMembership> countryColumn;

	private Column<JuryMembership> dateColumn;

	private Column<JuryMembership> participantColumn;

	private Column<JuryMembership> roleColumn;

	/** Constructor.
	 *
	 * @param authenticatedUser the connected user.
	 * @param messages the accessor to the localized messages (spring layer).
	 * @param membershipService the service for accessing the jury memberships.
	 * @param personService the service for accessing the JPA entities for persons.
	 * @param userService the service for accessing the JPA entities for users.
	 * @param logger the logger to use.
	 */
	public StandardJuryMembershipListView(
			AuthenticatedUser authenticatedUser, MessageSourceAccessor messages,
			JuryMembershipService membershipService, PersonService personService,
			UserService userService,Logger logger) {
		super(JuryMembership.class, authenticatedUser, messages, logger,
				"views.jury_membership.delete.title", //$NON-NLS-1$
				"views.jury_membership.delete.message", //$NON-NLS-1$
				"views.jury_memberships.delete_success", //$NON-NLS-1$
				"views.jury_memberships.delete_error"); //$NON-NLS-1$
		this.membershipService = membershipService;
		this.personService = personService;
		this.userService = userService;
		this.dataProvider = (ps, query, filters) -> ps.getAllJuryMemberships(query, filters, this::initializeEntityFromJPA);
		initializeDataInGrid(getGrid(), getFilters());
	}

	private void initializeEntityFromJPA(JuryMembership entity) {
		// Force the loaded of the lazy data that is needed for rendering the table
		Hibernate.initialize(entity.getCandidate());
		Hibernate.initialize(entity.getPerson());
	}

	@Override
	protected Filters<JuryMembership> createFilters() {
		return new JuryMembershipFilters(getAuthenticatedUser(), this::refreshGrid);
	}

	@Override
	protected boolean createGridColumns(Grid<JuryMembership> grid) {
		this.candidateColumn = grid.addColumn(new ComponentRenderer<>(this::createCandidateComponent))
				.setAutoWidth(true);
		this.defenseTypeColumn = grid.addColumn(this::getDefenseTypeLabel)
				.setAutoWidth(true)
				.setSortProperty("defenseType"); //$NON-NLS-1$
		this.universityColumn = grid.addColumn(membership -> membership.getUniversity())
				.setAutoWidth(true)
				.setSortProperty("university"); //$NON-NLS-1$
		this.countryColumn = grid.addColumn(new ComponentRenderer<>(this::createCountryComponent))
				.setAutoWidth(true)
				.setSortProperty("country"); //$NON-NLS-1$
		this.dateColumn = grid.addColumn(this::getDateLabel)
				.setAutoWidth(true)
				.setSortProperty("date"); //$NON-NLS-1$
		this.participantColumn = grid.addColumn(new ComponentRenderer<>(this::createParticipantComponent))
				.setAutoWidth(true);
		this.roleColumn = grid.addColumn(this::getRoleLabel)
				.setAutoWidth(true)
				.setSortProperty("type"); //$NON-NLS-1$
		return isAdminRole();
	}

	private Component createCandidateComponent(JuryMembership jury) {
		final var candidate = jury.getCandidate();
		if (candidate != null) {
			return ComponentFactory.newPersonAvatar(candidate);
		}
		return new Span();
	}

	private Component createParticipantComponent(JuryMembership jury) {
		final var participant = jury.getPerson();
		if (participant != null) {
			return ComponentFactory.newPersonAvatar(participant);
		}
		return new Span();
	}

	private String getRoleLabel(JuryMembership membership) {
		final var type = membership.getType();
		Gender gender = null;
		final var participant = membership.getPerson();
		if (participant != null) {
			gender = participant.getGender();
		}
		return type.getLabel(getMessageSourceAccessor(), gender, getLocale());
	}

	private Component createCountryComponent(JuryMembership membership) {
		final var country = membership.getCountry();
		if (country != null) {
			final var name = new Span(country.getDisplayCountry(getLocale()));
			name.getStyle().set("margin-left", "var(--lumo-space-s)"); //$NON-NLS-1$ //$NON-NLS-2$
			
			final var flag = new CountryFlag(country);
			flag.setSizeFromHeight(1, Unit.REM);
			
			final var layout = new HorizontalLayout(flag, name);
			layout.setSpacing(false);
			layout.setAlignItems(Alignment.CENTER);

			return layout;
		}
		return new Span();
	}

	private String getDefenseTypeLabel(JuryMembership membership) {
		return membership.getDefenseType().getLabel(getMessageSourceAccessor(), getLocale());
	}

	private String getDateLabel(JuryMembership membership) {
		return Integer.toString(membership.getDate().getYear());
	}

	@Override
	protected List<Column<JuryMembership>> getInitialSortingColumns() {
		return Arrays.asList(this.dateColumn, this.candidateColumn);
	}
	
	@Override
	protected SortDirection getInitialSortingDirection(Column<JuryMembership> column) {
		if (column == this.dateColumn) {
			return SortDirection.DESCENDING;
		}
		return SortDirection.ASCENDING;
	}

	@Override
	protected FetchCallback<JuryMembership, Void> getFetchCallback(Filters<JuryMembership> filters) {
		return query -> {
			return this.dataProvider.fetch(
					this.membershipService,
					VaadinSpringDataHelpers.toSpringPageRequest(query),
					filters).stream();
		};
	}

	@Override
	protected void addEntity() {
		openMembershipEditor(new JuryMembership(), getTranslation("views.jury_membership.add_membership")); //$NON-NLS-1$
	}

	@Override
	protected void edit(JuryMembership membership) {
		openMembershipEditor(membership, getTranslation("views.jury_membership.edit_membership", membership.getTitle())); //$NON-NLS-1$
	}

	/** Show the editor of a jury membership.
	 *
	 * @param membership the jury membership to edit.
	 * @param title the title of the editor.
	 */
	protected void openMembershipEditor(JuryMembership membership, String title) {
		final var editor = new EmbeddedJuryMembershipEditor(
				this.membershipService.startEditing(membership),
				this.personService, this.userService,
				getAuthenticatedUser(), getMessageSourceAccessor());
		final var newEntity = editor.isNewEntity();
		final SerializableBiConsumer<Dialog, JuryMembership> refreshAll = (dialog, entity) -> {
			// The person should be loaded because it was not loaded before
			this.membershipService.inSession(session -> {
				session.load(entity, Long.valueOf(entity.getId()));
				initializeEntityFromJPA(entity);
			});
			refreshGrid();
		};
		final SerializableBiConsumer<Dialog, JuryMembership> refreshOne = (dialog, entity) -> {
			// The person should be loaded because it was not loaded before
			this.membershipService.inSession(session -> {
				session.load(entity, Long.valueOf(entity.getId()));
				initializeEntityFromJPA(entity);
			});
			refreshItem(entity);
		};
		ComponentFactory.openEditionModalDialog(title, editor, false,
				// Refresh the "old" item, even if its has been changed in the JPA database
				newEntity ? refreshAll : refreshOne,
				newEntity ? null : refreshAll);
	}

	@Override
	protected EntityDeletingContext<JuryMembership> createDeletionContextFor(Set<JuryMembership> entities) {
		return this.membershipService.startDeletion(entities);
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		super.localeChange(event);
		this.candidateColumn.setHeader(getTranslation("views.candidates")); //$NON-NLS-1$
		this.defenseTypeColumn.setHeader(getTranslation("views.type")); //$NON-NLS-1$
		this.universityColumn.setHeader(getTranslation("views.university")); //$NON-NLS-1$
		this.countryColumn.setHeader(getTranslation("views.country")); //$NON-NLS-1$
		this.dateColumn.setHeader(getTranslation("views.date")); //$NON-NLS-1$
		this.participantColumn.setHeader(getTranslation("views.participant")); //$NON-NLS-1$
		this.roleColumn.setHeader(getTranslation("views.role")); //$NON-NLS-1$
	}

	/** UI and JPA filters for {@link StandardJuryMembershipListView}.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected static class JuryMembershipFilters extends Filters<JuryMembership> {

		private static final long serialVersionUID = 5727620980092385680L;

		private Checkbox includeCandidates;

		private Checkbox includeUniversities;

		private Checkbox includeParticipants;

		private Checkbox includeRoles;

		/** Constructor.
		 *
		 * @param user the connected user, or {@code null} if the filter does not care about a connected user.
		 * @param onSearch the callback function for running the filtering.
		 */
		public JuryMembershipFilters(AuthenticatedUser user, Runnable onSearch) {
			super(user, onSearch);
		}

		@Override
		protected void buildOptionsComponent(HorizontalLayout options) {
			this.includeCandidates = new Checkbox(true);
			this.includeUniversities = new Checkbox(true);
			this.includeParticipants = new Checkbox(true);
			this.includeRoles = new Checkbox(true);

			options.add(this.includeCandidates, this.includeUniversities, this.includeParticipants, this.includeRoles);
		}

		@Override
		protected void resetFilters() {
			this.includeCandidates.setValue(Boolean.TRUE);
			this.includeUniversities.setValue(Boolean.TRUE);
			this.includeParticipants.setValue(Boolean.TRUE);
			this.includeRoles.setValue(Boolean.TRUE);
		}

		@Override
		protected Predicate buildPredicateForAuthenticatedUser(Root<JuryMembership> root, CriteriaQuery<?> query,
				CriteriaBuilder criteriaBuilder, Person user) {
			final var crit1 = criteriaBuilder.equal(root.get("person"), user); //$NON-NLS-1$
			final var crit2 = criteriaBuilder.equal(root.get("candidate"), user); //$NON-NLS-1$
			return criteriaBuilder.or(crit1, crit2);
		}

		@Override
		protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<JuryMembership> root,
				CriteriaBuilder criteriaBuilder) {
			if (this.includeCandidates.getValue() == Boolean.TRUE) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("candidate")), keywords)); //$NON-NLS-1$
			}
			if (this.includeUniversities.getValue() == Boolean.TRUE) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("defenseType")), keywords)); //$NON-NLS-1$
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("university")), keywords)); //$NON-NLS-1$
			}
			if (this.includeParticipants.getValue() == Boolean.TRUE) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("person")), keywords)); //$NON-NLS-1$
			}
			if (this.includeRoles.getValue() == Boolean.TRUE) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("type")), keywords)); //$NON-NLS-1$
			}
		}

		@Override
		public void localeChange(LocaleChangeEvent event) {
			super.localeChange(event);
			this.includeCandidates.setLabel(getTranslation("views.filters.include_candidates")); //$NON-NLS-1$
			this.includeUniversities.setLabel(getTranslation("views.filters.include_universities")); //$NON-NLS-1$
			this.includeParticipants.setLabel(getTranslation("views.filters.include_participants")); //$NON-NLS-1$
			this.includeRoles.setLabel(getTranslation("views.filters.include_roles")); //$NON-NLS-1$
		}

	}

	/** Provider of data for jury memberships to be displayed in the list of memberships view.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	@FunctionalInterface
	protected interface JuryMembershipDataProvider {

		/** Fetch jury memberships data.
		 *
		 * @param membershipService the service to have access to the JPA.
		 * @param pageRequest the request for paging the data.
		 * @param filters the filters to apply for selecting the data.
		 * @return the lazy data page.
		 */
		Page<JuryMembership> fetch(JuryMembershipService membershipService, PageRequest pageRequest, Filters<JuryMembership> filters);

	}

}
