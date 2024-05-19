package fr.utbm.ciad.labmanager.views.components.persons;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.member.ChronoMembershipComparator;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class PersonCardView extends ListItem {
    private String title;
    private String subtitleText;
    private String image_url;
    private String descriptionText;
    private List<String> label;
    private PersonService personService;
    private MembershipService membershipService;
    private final ChronoMembershipComparator membershipComparator;
    private UserService userService;
    private AuthenticatedUser authenticatedUser;
    private MessageSourceAccessor messages;

    public PersonCardView(Person person, PersonService personService, UserService userService, AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, MembershipService membershipService, ChronoMembershipComparator membershipComparator){
        this.title = person.getFullName();
        this.subtitleText = person.getEmail();
        this.descriptionText = person.getBiography();
        this.image_url = person.getPhotoURL() != null  ? person.getPhotoURL().toString() : "https://images.unsplash.com/photo-1615796153287-98eacf0abb13?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxleHBsb3JlLWZlZWR8Mnx8fGVufDB8fHx8fA%3D%3D";
        this.label = new ArrayList<>();
        this.personService = personService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.messages = messages;
        this.membershipComparator = membershipComparator;
        this.membershipService = membershipService;

        this.getStyle().set("cursor", "pointer");
        this.getElement().addEventListener("click", event -> {
            openPersonEditor(person, getTranslation("views.persons.edit_person", person.getFullName()));
        });
        addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
                BorderRadius.LARGE);

        //Setting up the image
        Div divImage = new Div();
        divImage.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
                Margin.Bottom.MEDIUM, Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);
        divImage.setHeight("160px");
        Image image = new Image();
        image.setWidth("100%");
        image.setSrc(image_url);
        image.setAlt("Photo de profil de l'utilisateur");
        divImage.add(image);

        Span header = new Span();
        header.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD);
        header.setText(title);
        add(divImage, header);

        if (subtitleText != null) {
            Span subtitle = new Span();
            subtitle.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
            subtitle.setText(subtitleText);
            add(new Span(subtitleText));
        }

        if (descriptionText != null) {
            String shortDescription = descriptionText.length() > 100 ? descriptionText.substring(0, 100) : descriptionText;
            Paragraph description = new Paragraph(shortDescription);
            description.addClassName(Margin.Vertical.MEDIUM);
            add(description);
        }

        Iterator<Membership> memberships = new MembershipIterator(person);
        while (memberships.hasNext()){
            final var mbr = memberships.next();
            final var organization = mbr.getDirectResearchOrganization();
            label.add(organization.getAcronymOrName());
        }

        if (label != null && !label.isEmpty()) {
            Span[] badges = new Span[label.size()];
            HorizontalLayout badgesLayout = new HorizontalLayout();
            for (int i = 0; i < label.size(); i++) {
                badges[i] = new Span();
                badges[i].getElement().setAttribute("theme", "badge");
                badges[i].setText(label.get(i));
                badgesLayout.add(badges[i]);
            }
            add(badgesLayout);
        }

    }

    protected DomEventListener openPersonEditor(Person person, String title) {
        final var personContext = this.personService.startEditing(person);
        final var user = this.userService.getUserFor(person);
        final var userContext = this.userService.startEditing(user, personContext);
        final var editor = new EmbeddedPersonEditor(
                userContext, authenticatedUser, messages);
        final var newEntity = editor.isNewEntity();
        final SerializableBiConsumer<Dialog, Person> refreshAll = (dialog, entity) -> refreshGrid();
        final SerializableBiConsumer<Dialog, Person> refreshOne = (dialog, entity) -> refreshItem(entity);
        ComponentFactory.openEditionModalDialog(title, editor, true,
                // Refresh the "old" item, even if it has been changed in the JPA database
                newEntity ? refreshAll : refreshOne,
                newEntity ? null : refreshAll);
        return null;
    }

    private class MembershipIterator implements Iterator<Membership> {

        private final Iterator<Membership> base;

        private boolean foundActive;

        private Membership next;

        private MembershipIterator(Person person) {
            this.base = PersonCardView.this.membershipService.getMembershipsForPerson(person.getId()).stream()
                    .filter(it -> !it.isFuture()).sorted(PersonCardView.this.membershipComparator).iterator();
            searchNext();
        }

        private void searchNext() {
            this.next = null;
            if (this.base.hasNext()) {
                final var mbr = this.base.next();
                if (!mbr.isFormer() || !this.foundActive) {
                    this.foundActive = true;
                    this.next = mbr;
                }
            }
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public Membership next() {
            final var currentNext = this.next;
            searchNext();
            return currentNext;
        }

    }

    private void refreshItem(Person entity) {
    }

    private void refreshGrid() {
    }

}
