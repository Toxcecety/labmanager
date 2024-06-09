package fr.utbm.ciad.labmanager.views.components.journals;

import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;

/** Editor of journal information that may be embedded. This editor does not provide
 * the components for saving the information. It is the role of the component that
 * is embedding this editor to save the edited journal. It is a wizard
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public final class EmbeddedJournalEditorWizard extends JournalEditor {

    private static final long serialVersionUID = -8334616123986168541L;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedJournalEditor.class);

    /** Constructor.
     *
     * @param context the editing context for the conference.
     * @param authenticatedUser the connected user.
     * @param messages the accessor to the localized messages (Spring layer).
     */
    public EmbeddedJournalEditorWizard(AbstractEntityService.EntityEditingContext<Journal> context,
                                       AuthenticatedUser authenticatedUser, MessageSourceAccessor messages) {
        super(context, false, authenticatedUser, messages, LOGGER);
        createEditorContentAndLinkBeans();
    }

}
