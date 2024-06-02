package fr.utbm.ciad.labmanager.views.appviews.persons;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.member.ChronoMembershipComparator;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
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
    private final MessageSourceAccessor messages;
    private final MembershipService membershipService;
    private final ChronoMembershipComparator chronoMembershipComparator;
    private String searchQuery = "";
    private String filterQuery = "";
    private long numberOfPages;
    private PaginationComponent paginationComponent;

    public PersonsCardView(@Autowired PersonService personService, @Autowired UserService userService, @Autowired AuthenticatedUser authenticatedUser, @Autowired MessageSourceAccessor messages, @Autowired MembershipService membershipService, @Autowired ChronoMembershipComparator chronoMembershipComparator) {
        this.personService = personService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.messages = messages;
        this.membershipService = membershipService;
        this.chronoMembershipComparator = chronoMembershipComparator;

        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("padding-left", "75px");
        getStyle().set("padding-right", "75px");

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.BETWEEN);

        SearchComponent searchComponent = new SearchComponent(new String[]{"Name", "ORCID", "Organizations"});

        imageContainer = new OrderedList();
        imageContainer.setWidthFull();
        imageContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.GRID, LumoUtility.ListStyleType.NONE, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
        String gridTemplateColumns = "repeat(" + cardsPerRow + ", 1fr)";
        imageContainer.getStyle().set("grid-template-columns", gridTemplateColumns);

        add(searchComponent);
        add(container, imageContainer);
        this.numberOfPages = personService.countAllPersons() / cardsPerRow*numberOfRows;
        paginationComponent = new PaginationComponent(numberOfPages);
        AtomicReference<Pageable> pageable = new AtomicReference<>(PageRequest.of(paginationComponent.getCurrentPage(), cardsPerRow*numberOfRows, Sort.by("lastName").and(Sort.by("firstName"))));
        add(paginationComponent);
        AtomicReference<Page<Person>> persons = new AtomicReference<>(personService.getAllPersons(pageable.get()));
        for (Person person : persons.get()) {
            imageContainer.add(new PersonCardView(person, personService, userService, authenticatedUser, messages, membershipService, chronoMembershipComparator));
        }

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
            updatePaginationComponent();
            fetchCards(0);
            paginationComponent.setCurrentPage(0);

        });
    }

    private void fetchCards(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, cardsPerRow*numberOfRows, Sort.by("lastName").and(Sort.by("firstName")));
        Page<Person> persons = switch (filterQuery) {
            case "Name" -> personService.getPersonsByName(searchQuery, pageable);
            case "ORCID" -> personService.getPersonsByOrcid(searchQuery, pageable);
            case "Organizations" -> personService.getPersonsByOrganization(searchQuery, pageable);
            default -> personService.getAllPersons(pageable);
        };
        imageContainer.removeAll();
        for (Person person : persons) {
            imageContainer.add(new PersonCardView(person, personService, userService, authenticatedUser, messages, membershipService, chronoMembershipComparator));
        }
        UI.getCurrent().getPage().executeJs("window.scrollTo(0, 0);");
    }

    private void updatePaginationComponent() {
        numberOfPages = switch (filterQuery) {
            case "Name" -> personService.countPersonsByName(searchQuery) / cardsPerRow*numberOfRows;
            case "ORCID" -> personService.countPersonsByOrcid(searchQuery) / cardsPerRow*numberOfRows;
            case "Organizations" -> personService.countPersonsByOrganization(searchQuery) / cardsPerRow*numberOfRows;
            default -> personService.countAllPersons() / cardsPerRow*numberOfRows;
        };
        paginationComponent.setTotalPages(numberOfPages);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("views.persons.list_title.all"); //$NON-NLS-1$
    }
}
