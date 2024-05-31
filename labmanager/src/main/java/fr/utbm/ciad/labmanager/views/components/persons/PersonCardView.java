package fr.utbm.ciad.labmanager.views.components.persons;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.function.SerializableBiConsumer;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.member.ChronoMembershipComparator;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.cards.AbstractCardView;
import org.springframework.context.support.MessageSourceAccessor;

public class PersonCardView extends AbstractCardView<Person> {
    private final PersonService personService;
    private final UserService userService;
    private final AuthenticatedUser authenticatedUser;
    private final MessageSourceAccessor messages;

    public PersonCardView(Person person, PersonService personService, UserService userService, AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, MembershipService membershipService, ChronoMembershipComparator membershipComparator){
        super(new CardBuilder().setTitle(person.getFullName()).setSubtitleText(person.getEmail()).setDescriptionText(!person.getPrivateBiography() ? person.getBiography() : "Biography is marked as private")
                .setImageUrl(person.getPhotoURL() != null  ? person.getPhotoURL().toString() : "https://images.unsplash.com/photo-1615796153287-98eacf0abb13?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxleHBsb3JlLWZlZWR8Mnx8fGVufDB8fHx8fA%3D%3D")
                .setLabels(membershipService.getActiveMembershipsForPerson(person, membershipComparator)), person);

        this.personService = personService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.messages = messages;
    }

    protected void openPersonEditor(Person person, String title){
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
    }

    @Override
    protected void onClickEvent(Person entity) {
        openPersonEditor(entity, getTranslation("views.persons.edit_person", entity.getFullName()));
    }

    @Override
    protected void refreshItem(Person entity) {
    }

    @Override
    protected void refreshGrid() {

    }
}
