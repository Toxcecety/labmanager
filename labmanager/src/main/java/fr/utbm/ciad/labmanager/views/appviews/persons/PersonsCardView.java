package fr.utbm.ciad.labmanager.views.appviews.persons;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
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
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.persons.PersonCardView;
import fr.utbm.ciad.labmanager.views.components.persons.EmbeddedPersonEditor;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

@Route(value = "persons_cards", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class PersonsCardView extends Main implements HasDynamicTitle, HasComponents, HasStyle {
    private static final long serialVersionUID = 1616874715478139507L;
    private OrderedList imageContainer;

    public PersonsCardView(@Autowired PersonService personService, @Autowired UserService userService, @Autowired AuthenticatedUser authenticatedUser, @Autowired MessageSourceAccessor messages, @Autowired MembershipService membershipService, @Autowired ChronoMembershipComparator chronoMembershipComparator) {
        constructUI();
        Pageable pageable = PageRequest.of(0, 8);
        Page<Person> persons = personService.getAllPersons(pageable);
        for (Person person : persons) {
            imageContainer.add(new PersonCardView(person, personService, userService, authenticatedUser, messages, membershipService, chronoMembershipComparator));
        }
    }

    private void constructUI() {
        addClassNames("image-gallery-view");
        addClassNames(LumoUtility.MaxWidth.SCREEN_LARGE, LumoUtility.Margin.Horizontal.AUTO, LumoUtility.Padding.Bottom.LARGE, LumoUtility.Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.BETWEEN);

        Select<String> sortBy = new Select<>();
        sortBy.setLabel("Sort by");
        sortBy.setItems("Popularity", "Newest first", "Oldest first");
        sortBy.setValue("Popularity");

        imageContainer = new OrderedList();
        imageContainer.addClassNames(LumoUtility.Gap.MEDIUM, LumoUtility.Display.GRID, LumoUtility.ListStyleType.NONE, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
        imageContainer.getStyle().set("grid-template-columns", "repeat(4, 1fr)");

        container.add(sortBy);
        add(container, imageContainer);

    }


    @Override
    public String getPageTitle() {
        return getTranslation("views.persons.list_title.all"); //$NON-NLS-1$
    }
}
