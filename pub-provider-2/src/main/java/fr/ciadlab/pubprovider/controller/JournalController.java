package fr.ciadlab.pubprovider.controller;

import fr.ciadlab.pubprovider.entities.Author;
import fr.ciadlab.pubprovider.entities.Journal;
import fr.ciadlab.pubprovider.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@CrossOrigin
public class JournalController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private PublicationService pubServ;
    @Autowired
    private BookChapterService bookChapterServ;
    @Autowired
    private BookService bookServ;
    @Autowired
    private EngineeringActivityService engineeringActivityServ;
    @Autowired
    private JournalService journalServ;
    @Autowired
    private ProceedingsConferenceService proceedingsConferenceServ;
    @Autowired
    private ResearchOrganizationService researchOrganizationServ;
    @Autowired
    private SeminarPatentInvitedConferenceService seminarPatentInvitedConferenceServ;
    @Autowired
    private UniversityDocumentService universityDocumentServ;
    @Autowired
    private UserDocumentationService userDocumentationServ;
    @Autowired
    private ReadingCommitteeJournalPopularizationPaperService readingCommitteeJournalPopularizationPaperServ;

    @RequestMapping(value = "/getJournalData", method = RequestMethod.GET)
    public Journal getAuthorData(@RequestParam String name) {
        return journalServ.getJournal(journalServ.getJournalIdByName(name));
    }

    @RequestMapping(value = "/addJournal", method = RequestMethod.POST)
    public void createJournal(HttpServletResponse response,
                              @RequestParam String journalName,
                              @RequestParam String journalPublisher,
                              @RequestParam String journalElsevier,
                              @RequestParam String journalScimago,
                              @RequestParam String journalWos) throws IOException {
        try {
            journalServ.createJournal(journalName, journalPublisher, journalElsevier, journalScimago, journalWos);
            response.sendRedirect("/SpringRestHibernate/journalTool?success=" + journalName);
        } catch (Exception ex) {
            response.sendRedirect("/SpringRestHibernate/authorsTool?error=1&message=" + ex.getMessage()); // Redirect on the same page
        }

    }
}


	
