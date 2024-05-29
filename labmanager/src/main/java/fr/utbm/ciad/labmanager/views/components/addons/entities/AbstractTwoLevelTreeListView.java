/*
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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.IdentifiableEntity;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.DeletionStatus;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

/** Abstract implementation of a list of entities based on a tree grid view in which the root entity is of
 * a different type of the child entities. There is only two levels: root entities and child entities.
 * The tree grid is supposed to provide the child entities as the main set of entities.
 *
 * @param <R> the type of the root entities, that must be {@link IdentifiableEntity} to be able to provide their id.
 * @param <C> the type of the child entities, that must be {@link IdentifiableEntity} to be able to provide their id.
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 * @see #AbstractEntityListView
 */
public abstract class AbstractTwoLevelTreeListView<R extends IdentifiableEntity, C extends IdentifiableEntity>
			extends AbstractGridBaseEntityListView<TreeListEntity<R, C>, C, TreeGrid<TreeListEntity<R, C>>> {

	private static final long serialVersionUID = -4499965319594816679L;

	private final Class<R> rootEntityType;

	private final Class<C> childEntityType;

	private boolean hasHoverMenu;

	private EntityFecther<R, C> rootEntityFetcher;

	private EntityFecther<C, C> childEntityFetcher;

	private ChildEntityCounter<R> rootEntityChildCount;

	private Column<TreeListEntity<R, C>> firstColumn;

	/** Constructor.
	 *
	 * @param rootEntityType the type of the root entities.
	 * @param childEntityType the type of the child entities.
	 * @param authenticatedUser the connected user.
	 * @param messages the accessor to the localized messages (spring layer).
	 * @param logger the logger to be used by this view.
	 * @param deletionTitleMessageKey the key in the localized messages for the dialog box title related to a deletion.
	 * @param deletionMessageKey the key in the localized messages for the message related to a deletion.
	 * @param deletionSuccessMessageKey the key in the localized messages for the messages related to a deletion success.
	 * @param deletionErrorMessageKey the key in the localized messages for the messages related to an error of deletion.
	 */
	public AbstractTwoLevelTreeListView(
			Class<R> rootEntityType, Class<C> childEntityType,
			AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, Logger logger,
			String deletionTitleMessageKey, String deletionMessageKey,
			String deletionSuccessMessageKey, String deletionErrorMessageKey) {
		super(null, authenticatedUser, messages, logger,
				deletionTitleMessageKey, deletionMessageKey,
				deletionSuccessMessageKey, deletionErrorMessageKey);
		this.rootEntityType = rootEntityType;
		this.childEntityType = childEntityType;
	}

	@Override
	protected final void edit(TreeListEntity<R, C> entity) {
		final var membership = entity.getChildEntity();
		// Membership may be null because the user has clicked on the person name (the root).
		if (membership != null) {
			editChildEntity(membership);
		}
	}

	/** Edit the child entity.
	 *
	 * @param entity the entity to edit.
	 */
	protected abstract void editChildEntity(C entity);

	@Override
	protected final EntityDeletingContext<TreeListEntity<R, C>> createDeletionContextFor(Set<TreeListEntity<R, C>> entities) {
		final var input = entities.stream().map(it -> it.getChildEntity()).collect(Collectors.toSet());
		final var context = createDeletionContextForChildEntities(input);
		return DeletionContextWrapper.<R, C>of(context);
	}

	/** Create the deletion context for deleting all the pgiven child entities.
	 *
	 * @param entities the entities to delete.
	 * @return the deletion context.
	 */
	protected abstract EntityDeletingContext<C> createDeletionContextForChildEntities(Set<C> entities);

	/** Change the flag that indicates if the hover menu should be created.
	 *
	 * @param hasMenu {@code true} if the hover menu should be created.
	 */
	protected final void setHoverMenu(boolean hasMenu) {
		this.hasHoverMenu = hasMenu;
	}

	/** Change the fetcher for the root entities.
	 *
	 * @param rootEntityFetcher the tool for fetching the root entities from the JPA database.
	 * @param rootEntityChildCount the tool for obtaining the number of child entities from a given root entity.
	 */
	protected final void setRootEntityFetcher(EntityFecther<R, C> rootEntityFetcher,
			ChildEntityCounter<R> rootEntityChildCount) {
		this.rootEntityFetcher = rootEntityFetcher;
		this.rootEntityChildCount = rootEntityChildCount;
	}

	/** Change the fetcher for the child entities.
	 *
	 * @param childEntityFetcher the tool for fetching the child entities from the JPA database.
	 */
	protected final void setChildEntityFetcher(EntityFecther<C, C> childEntityFetcher) {
		this.childEntityFetcher = childEntityFetcher;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final Class<TreeListEntity<R, C>> getEntityType() {
		return (Class<TreeListEntity<R, C>>) (Object) TreeListEntity.class;
	}

	/** Replies the type of root entities  to be listed in this component.
	 *
	 * @return the root entity type.
	 */
	protected Class<R> getRootEntityType() {
		return this.rootEntityType;
	}

	/** Replies the type of root entities  to be listed in this component.
	 *
	 * @return the root entity type.
	 */
	protected Class<C> getChildEntityType() {
		return this.childEntityType;
	}

	/** Replies if the hover menu should be created.
	 *
	 * @return {@code true} if the hover menu should be created.
	 */
	protected final boolean hasHoverMenu() {
		return this.hasHoverMenu;
	}

	/** Replies the first column.
	 *
	 * @return the first column.
	 */
	protected Column<TreeListEntity<R, C>> getFirstColumn() {
		return this.firstColumn;
	}

	@Override
	protected final TreeGrid<TreeListEntity<R, C>> createGridInstance() {
		return new TreeGrid<>();
	}

	@Override
	protected List<Column<TreeListEntity<R, C>>> getInitialSortingColumns() {
		return Collections.singletonList(this.firstColumn);
	}

	@Override
	protected final boolean createGridColumns(TreeGrid<TreeListEntity<R, C>> grid) {
		this.firstColumn = grid.addComponentHierarchyColumn(it -> {
			if (it.isRootEntity()) {
				return createRootEntityComponent(it.getRootEntity());
			}
			return createChildEntityComponent(it.getChildEntity());
		});
		configureFirstColumn(this.firstColumn);
		createAdditionalColumns(grid);
		return hasHoverMenu();
	}
	
	/** Invoked for creating the additional columns.
	 * 
	 * @param grid the grid to populate.
	 */
	protected abstract void createAdditionalColumns(TreeGrid<TreeListEntity<R, C>> grid);

	/** Configure the first column.
	 * 
	 * @param firstColumn the column to configure.
	 */
	protected void configureFirstColumn(Column<TreeListEntity<R, C>> firstColumn) {
		firstColumn.setAutoWidth(true);
	}

	/** Create the component for the root entities.
	 *
	 * @param entity the entity for which the component should be created.
	 * @return the component for the root entity.
	 */
	protected abstract Component createRootEntityComponent(R entity);

	/** Create the component for the child entities.
	 *
	 * @param entity the entity for which the component should be created.
	 * @return the component for the child entity.
	 */
	protected abstract Component createChildEntityComponent(C entity);

	@Override
	protected final void initializeDataInGrid(TreeGrid<TreeListEntity<R, C>> grid, AbstractFilters<C> filters) {
		grid.setDataProvider(new LazyDataProvider(filters));
	}

	/** Lazy data provider for {@link AbstractTwoLevelTreeListView}.
	 *
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 * @see #AbstractEntityListView
	 */
	protected final class LazyDataProvider extends AbstractBackEndHierarchicalDataProvider<TreeListEntity<R, C>, AbstractFilters<C>> {

		private static final long serialVersionUID = -2633658073424798368L;

		private final AbstractFilters<C> filters;

		/** Constructor.
		 *
		 * @param filters the filters of the entities.
		 */
		public LazyDataProvider(AbstractFilters<C> filters) {
			this.filters = filters;
		}

		@Override
		public boolean hasChildren(TreeListEntity<R, C> item) {
			return item != null && item.isRootEntity();
		}

		@Override
		public int getChildCount(HierarchicalQuery<TreeListEntity<R, C>, AbstractFilters<C>> query) {
			final var parent = query.getParent();
			if (parent != null) {
				if (parent.isRootEntity()) {
					return AbstractTwoLevelTreeListView.this.rootEntityChildCount.count(parent.getRootEntity());
				}
				return 0;
			}
			return (int) fetchChildren(query).count();
		}


		@Override
		protected Stream<TreeListEntity<R, C>> fetchChildrenFromBackEnd(HierarchicalQuery<TreeListEntity<R, C>, AbstractFilters<C>> query) {
			if (query.getParent() == null) {
				final var originalStream = AbstractTwoLevelTreeListView.this.rootEntityFetcher.fetch(
						0,
						VaadinSpringDataHelpers.toSpringPageRequest(query),
						null).stream();
				return originalStream.map(it -> TreeListEntity.root(it));
			}
			final var originalStream = AbstractTwoLevelTreeListView.this.childEntityFetcher.fetch(
					query.getParent().getId(),
					VaadinSpringDataHelpers.toSpringPageRequest(query),
					null).stream();
			return originalStream.map(it -> TreeListEntity.child(it));
		}

	}

	/** Fetch entities from the JPA database.
	 *
	 * @param <T> the type of the returned entities, that must be {@link IdentifiableEntity} to be able to provide their id.
	 * @param <F> the type of the filtered entities, that must be {@link IdentifiableEntity} to be able to provide their id.
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	@FunctionalInterface
	protected interface EntityFecther<T extends IdentifiableEntity, F extends IdentifiableEntity> {

		/** Fetch entity data.
		 *
		 * @param parentId the identifier of the parent entity. It is {@code 0} if there is no parent.
		 * @param pageRequest the request for paging the data.
		 * @param filters the filters to apply for selecting the data.
		 * @return the lazy data page.
		 */
		Page<T> fetch(long parentId, PageRequest pageRequest, Specification<F> filters);

	}

	/** Count the child entities from a root entity.
	 *
	 * @param <T> the type of the returned entities, that must be {@link IdentifiableEntity} to be able to provide their id.
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	@FunctionalInterface
	protected interface ChildEntityCounter<T extends IdentifiableEntity> {

		/** Replies the number of child entities from the given root entity.
		 *
		 * @param parentEntity the entity to scan.
		 * @return the number of child entities in the given parent entity.
		 */
		int count(T parentEntity);

	}

	/** Deletion context for a grid entry.
	 *
	 * @param <R> the type of the root entities, that must be {@link IdentifiableEntity} to be able to provide their id.
	 * @param <C> the type of the child entities, that must be {@link IdentifiableEntity} to be able to provide their id.
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected static final class DeletionContextWrapper<R extends IdentifiableEntity, C extends IdentifiableEntity>
		implements EntityDeletingContext<TreeListEntity<R, C>> {

		private static final long serialVersionUID = 5230145754672876216L;

		private final EntityDeletingContext<C> original;

		/** Constructor.
		 *
		 * @param original the original deletion context.
		 */
		private DeletionContextWrapper(EntityDeletingContext<C> original) {
			this.original = original;
		}
		
		/** Create a wrapper for deletion context.
		 *
		 * @param <R> the type of the root entities, that must be {@link IdentifiableEntity} to be able to provide their id.
		 * @param <C> the type of the child entities, that must be {@link IdentifiableEntity} to be able to provide their id.
		 * @param original the original deletion context.
		 * @return the wrapper.
		 */
		public static <R extends IdentifiableEntity, C extends IdentifiableEntity>
				DeletionContextWrapper<R, C> of(EntityDeletingContext<C> original) {
			return new DeletionContextWrapper<>(original);
		}
		
		@Override
		public Set<TreeListEntity<R, C>> getEntities() {
			return this.original.getEntities().stream().map(it -> TreeListEntity.<R, C>child(it)).collect(Collectors.toSet());
		}

		@Override
		public boolean isDeletionPossible() {
			return this.original.isDeletionPossible();
		}

		@Override
		public DeletionStatus getDeletionStatus() {
			return this.original.getDeletionStatus();
		}

		@Override
		public void delete() throws Exception {
			this.original.delete();
		}

	}

}
