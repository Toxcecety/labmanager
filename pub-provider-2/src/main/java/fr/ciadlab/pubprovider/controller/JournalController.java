/*
 * $Id$
 * 
 * Copyright (c) 2019-22, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of the Systems and Transportation Laboratory ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with the SeT.
 * 
 * http://www.ciad-lab.fr/
 */

package fr.ciadlab.pubprovider.controller;

import fr.ciadlab.pubprovider.entities.Author;
import fr.ciadlab.pubprovider.entities.CoreRanking;
import fr.ciadlab.pubprovider.entities.Journal;
import fr.ciadlab.pubprovider.entities.Quartile;
import fr.ciadlab.pubprovider.service.*;

import org.apache.catalina.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/journalTool")
    public ModelAndView showJournalTool() {
        final ModelAndView modelAndView = new ModelAndView("journalTool");
        modelAndView.addObject("journals", journalServ.getAllJournals());
        return modelAndView;
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
            response.sendRedirect("/SpringRestHibernate/authorsTool?error=1&message=" + ex.getMessage()); // Redirect on
                                                                                                          // the same
                                                                                                          // page
        }

    }

    @RequestMapping(value = "/getJournalData", method = RequestMethod.GET)
    public Journal getJournalData(@RequestParam String name) {
        return journalServ.getJournalByName(name);
    }

    @RequestMapping(value = "/getJournalQualityIndicators", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getJournalQualityIndicators(
            @RequestParam(value = "historyYear", required = true) int historyYear,
            @RequestParam(value = "journalName", required = true) String journalName) {
        Journal requestedJournal = journalServ.getJournalByName(journalName);
        if (requestedJournal != null && requestedJournal.hasJounralQualityIndicatorsHistoryForYear(historyYear)) {
            // creation of a JSON object containing each of the journal's quality indicators
            Map<String, String> journalIndicators = new HashMap<>();

            // recuperation of the indicators
            Quartile scimagoQuartile = requestedJournal.getScimagoQuartileByYear(historyYear);
            Quartile wosQuartile = requestedJournal.getWosQuartileByYear(historyYear);
            CoreRanking coreRanking = requestedJournal.getCoreRankingByYear(historyYear);
            int impactFactor = requestedJournal.getImpactFactorByYear(historyYear);

            // addition of the indicators in the json map
            if (scimagoQuartile != null)
                journalIndicators.put("scimagoQuartile",
                        scimagoQuartile.toString());
            if (wosQuartile != null)
                journalIndicators.put("wosQuartile",
                        wosQuartile.toString());
            if (coreRanking != null)
                journalIndicators.put("coreRanking",
                        coreRanking.toString());
            if (impactFactor != 0)
                journalIndicators.put("impactFactor",
                        Integer.toString(impactFactor));
            return journalIndicators;
        }
        return null;
    }

    @RequestMapping(value = "/editJournal", method = RequestMethod.POST)
    public void editJournal(HttpServletResponse response,
            @RequestParam("journal") String originalJournalName,
            @RequestParam String journalName,
            @RequestParam String journalPublisher,
            @RequestParam String journalElsevier,
            @RequestParam String journalScimago,
            @RequestParam String journalWos) throws IOException {
        try {
            final int journalId = journalServ.getJournalIdByName(originalJournalName);

            journalServ.updateJournal(journalId,
                    journalName,
                    journalPublisher,
                    journalElsevier,
                    journalScimago,
                    journalWos);

            response.sendRedirect("/SpringRestHibernate/journalTool?updated=true");
        } catch (Exception ex) {
            response.sendRedirect("/SpringRestHibernate/journalTool?error=1&message=" + ex.getMessage()); // Redirect on
                                                                                                          // the same
                                                                                                          // page
        }
    }
}
