package fr.utbm.ciad.labmanager.views.components.persons;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.function.SerializableBiConsumer;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.member.ChronoMembershipComparator;
import fr.utbm.ciad.labmanager.data.member.MembershipComparator;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.cards.AbstractPersonCardView;
import org.springframework.context.support.MessageSourceAccessor;

public class PersonCardView extends AbstractPersonCardView<Person> {
    private final PersonService personService;
    private final MembershipService membershipService;
    private final ChronoMembershipComparator membershipComparator;
    private final UserService userService;
    private final AuthenticatedUser authenticatedUser;
    private final MessageSourceAccessor messages;

    public PersonCardView(Person person, PersonService personService, UserService userService, AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, MembershipService membershipService, ChronoMembershipComparator membershipComparator){
        super(new CardBuilder().setImageUrl(person.getPhotoURL() != null  ? person.getPhotoURL().toString() : null)
                .setName(person.getFullName()).setEmail(person.getEmail())
                .setOfficePhone(person.getOfficePhone()).setMobilePhone(person.getMobilePhone())
                .setOfficeRoom(person.getOfficeRoom())
                .setLabels(membershipService.getActiveMembershipsForPerson(person, membershipComparator)), person);
        this.personService = personService;
        this.membershipService = membershipService;
        this.membershipComparator = membershipComparator;
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
        final SerializableBiConsumer<Dialog, Person> refreshAll = (dialog, entity) -> refreshPage();
        final SerializableBiConsumer<Dialog, Person> refreshOne = (dialog, entity) -> refreshCard(entity);
        ComponentFactory.openEditionModalDialog(title, editor, true,
                // Refresh the "old" item, even if it has been changed in the JPA database
                newEntity ? refreshAll : refreshOne,
                newEntity ? null : refreshAll);
    }

    public void updateCardData(Person person) {
        // Fetch the updated data for the person
        Person updatedPerson = personService.getPersonById(person.getId());

        // Use the CardBuilder to rebuild the card with the updated data
        CardBuilder cardBuilder = new CardBuilder()
                .setImageUrl(updatedPerson.getPhotoURL() != null  ? updatedPerson.getPhotoURL().toString() : null)
                .setName(updatedPerson.getFullName())
                .setEmail(updatedPerson.getEmail())
                .setOfficePhone(updatedPerson.getOfficePhone())
                .setMobilePhone(updatedPerson.getMobilePhone())
                .setOfficeRoom(updatedPerson.getOfficeRoom())
                .setLabels(membershipService.getActiveMembershipsForPerson(updatedPerson, membershipComparator));
    }

    @Override
    protected void onClickEvent(Person entity) {
        if (isAdminRole()){
            openPersonEditor(entity, getTranslation("views.persons.edit_person", entity.getFullName()));
        }
    }

    protected void refreshCard(Person entity) {
        updateCardData(entity);
    }

    protected void refreshPage() {
        UI.getCurrent().getPage().reload();
    }

    protected boolean isAdminRole() {
        return this.authenticatedUser != null && this.authenticatedUser.get().isPresent()
                && this.authenticatedUser.get().get().getRole().hasBaseAdministrationRights();
    }
}
