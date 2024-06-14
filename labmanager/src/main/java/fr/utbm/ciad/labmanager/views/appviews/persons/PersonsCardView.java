package fr.utbm.ciad.labmanager.views.appviews.persons;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.member.ChronoMembershipComparator;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import fr.utbm.ciad.labmanager.views.components.persons.EmbeddedPersonEditor;
import fr.utbm.ciad.labmanager.views.components.persons.PaginationComponent;
import fr.utbm.ciad.labmanager.views.components.persons.PersonCardView;
import fr.utbm.ciad.labmanager.views.components.persons.SearchComponent;
import jakarta.annotation.security.RolesAllowed;
import org.apache.jena.base.Sys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Route(value = "persons_cards", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class PersonsCardView extends VerticalLayout implements HasDynamicTitle, HasComponents, HasStyle {
    private static final long serialVersionUID = 1616874715478139507L;
    private static final int cardsPerRow = 4;
    private static final int numberOfRows = 4;
    private final OrderedList imageContainer;
    private final PersonService personService;
    private final UserService userService;
    private final AuthenticatedUser authenticatedUser;
    private final ResearchOrganizationService organizationService;
    private final MessageSourceAccessor messages;
    private final MembershipService membershipService;
    private final ChronoMembershipComparator chronoMembershipComparator;
    private String searchQuery = "";
    private String filterQuery = "";
    private Boolean restrictToOrganization;
    private String organization;
    private long numberOfPages;
    private final PaginationComponent paginationComponent;


    public PersonsCardView(@Autowired PersonService personService, @Autowired UserService userService, @Autowired AuthenticatedUser authenticatedUser, @Autowired MessageSourceAccessor messages, @Autowired MembershipService membershipService, @Autowired ChronoMembershipComparator chronoMembershipComparator, @Autowired ResearchOrganizationService organizationService) {
        this.personService = personService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.organizationService = organizationService;
        this.messages = messages;
        this.membershipService = membershipService;
        this.chronoMembershipComparator = chronoMembershipComparator;
        this.organization = organizationService.getDefaultOrganization().getAcronym();

        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("padding-left", "75px");
        getStyle().set("padding-right", "75px");

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.BETWEEN);

        SearchComponent searchComponent = new SearchComponent(new String[]{getTranslation("views.filters.include_names"), getTranslation("views.filters.include_orcids"), getTranslation("views.filters.include_organizations")}, organizationService::getDefaultOrganization, this.organizationService::getFileManager);
        this.restrictToOrganization = searchComponent.getRestrictToOrganizationValue();

        MenuBar menu = createMenuBar();
        if (menu != null) {
            container.add(menu);
        }

        imageContainer = new OrderedList();
        imageContainer.setWidthFull();
        imageContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.GRID, LumoUtility.ListStyleType.NONE, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
        String gridTemplateColumns = "repeat(" + cardsPerRow + ", 1fr)";
        imageContainer.getStyle().set("grid-template-columns", gridTemplateColumns);

        add(searchComponent);
        add(container, imageContainer);
        this.numberOfPages = personService.countAllPersons() / cardsPerRow*numberOfRows;
        paginationComponent = new PaginationComponent(numberOfPages);
        add(paginationComponent);
        fetchCards(0);

        // Add a listener to the PaginationComponent
        paginationComponent.addPageChangeListener(event -> {
            int newPageNumber = event.getPageNumber();
            fetchCards(newPageNumber);
            paginationComponent.setCurrentPage(newPageNumber);
        });

        // Add a listener to the searchComponent
        searchComponent.addSearchComponentListener(event -> {
            searchQuery = searchComponent.getSearchValue();
            filterQuery = searchComponent.getFilterValue();
            restrictToOrganization = searchComponent.getRestrictToOrganizationValue();
            fetchCards(0);
            paginationComponent.setCurrentPage(0);
        });
    }

    private void fetchCards(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, cardsPerRow*numberOfRows, Sort.by("lastName").and(Sort.by("firstName")));
        Page<Person> persons;
        if (filterQuery.equals(getTranslation("views.filters.include_names"))){
            if (restrictToOrganization){
                numberOfPages = (long) Math.ceil((double)personService.countPersonsByName(searchQuery, organization) / (cardsPerRow*numberOfRows));
                persons = personService.getPersonsByName(searchQuery, organization, pageable);
            } else {
                numberOfPages = (long) Math.ceil((double)personService.countPersonsByName(searchQuery) / (cardsPerRow*numberOfRows));
                persons = personService.getPersonsByName(searchQuery, pageable);
            }
        } else if (filterQuery.equals(getTranslation("views.filters.include_orcids"))){
            if (restrictToOrganization){
                numberOfPages = (long) Math.ceil((double)personService.countPersonsByOrcid(searchQuery, organization) / (cardsPerRow*numberOfRows));
                persons = personService.getPersonsByOrcid(searchQuery, organization, pageable);
            } else {
                numberOfPages = (long) Math.ceil((double)personService.countPersonsByOrcid(searchQuery) / (cardsPerRow*numberOfRows));
                persons = personService.getPersonsByOrcid(searchQuery, pageable);
            }
        } else if (filterQuery.equals(getTranslation("views.filters.include_organizations"))){
            if (restrictToOrganization){
                numberOfPages = (long) Math.ceil((double)personService.countPersonsByOrganization(searchQuery, organization) / (cardsPerRow*numberOfRows));
                persons = personService.getPersonsByOrganization(searchQuery, organization, pageable);
            } else {
                numberOfPages = (long) Math.ceil((double) personService.countPersonsByOrganization(searchQuery) / (cardsPerRow*numberOfRows));
                persons = personService.getPersonsByOrganization(searchQuery, pageable);
            }
        } else {
            if (restrictToOrganization){
                numberOfPages = (long) Math.ceil((double)personService.countPersonsByOrganization(organization) / (cardsPerRow*numberOfRows));
                persons = personService.getPersonsByOrganization(organization, pageable);
            } else {
                numberOfPages = (long) Math.ceil((double)personService.countAllPersons() / (cardsPerRow*numberOfRows));
                persons = personService.getAllPersons(pageable);
            }
        }
        imageContainer.removeAll();
        for (Person person : persons) {
            imageContainer.add(new PersonCardView(person, personService, userService, authenticatedUser, messages, membershipService, chronoMembershipComparator));
        }
        paginationComponent.setTotalPages(numberOfPages);
        UI.getCurrent().getPage().executeJs("window.scrollTo(0, 0);");
    }

    private MenuBar createMenuBar(){
        final var menu = new MenuBar();
        menu.addThemeVariants(MenuBarVariant.LUMO_ICON);
        if (isAdminRole()) {
            ComponentFactory.addIconItem(menu, LineAwesomeIcon.PLUS_SOLID, getTranslation("views.persons.add_person"), null, it -> addEntity());
        }
        ComponentFactory.addIconItem(menu, LineAwesomeIcon.TH_LIST_SOLID, getTranslation("views.persons.switch_views"), null, it -> getUI().ifPresent(ui -> ui.navigate(PersonsListView.class)));
        return menu;
    }
    
    private void addEntity(){
        openPersonEditor(new Person(), getTranslation("views.persons.add_person")); //$NON-NLS-1$
    }

    protected void openPersonEditor(Person person, String title){
        final var personContext = this.personService.startEditing(person);
        final var user = this.userService.getUserFor(person);
        final var userContext = this.userService.startEditing(user, personContext);
        final var editor = new EmbeddedPersonEditor(
                userContext, this.personService, authenticatedUser, messages);
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, Person> refreshAll = (dialog, entity) -> refreshPage();
        ComponentFactory.openEditionModalDialog(title, editor, true,
                // Refresh the "old" item, even if it has been changed in the JPA database
                newEntity ? refreshAll : null,
                newEntity ? null : refreshAll);
    }

    private void refreshPage() {
        UI.getCurrent().getPage().reload();
    }

    protected boolean isAdminRole() {
        return this.authenticatedUser != null && this.authenticatedUser.get().isPresent()
                && this.authenticatedUser.get().get().getRole().hasBaseAdministrationRights();
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.persons.list_title.all"); //$NON-NLS-1$
    }
}
