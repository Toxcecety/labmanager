/*
 * $Id$
 * 
 * Copyright (c) 2019-22, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of the CIAD laboratory and the Université de Technologie
 * de Belfort-Montbéliard ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with the CIAD-UTBM.
 * 
 * http://www.ciad-lab.fr/
 */

package fr.ciadlab.labmanager.controller.view.scientificaxis;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.aspose.slides.exceptions.IOException;
import fr.ciadlab.labmanager.configuration.Constants;
import fr.ciadlab.labmanager.controller.view.AbstractViewController;
import fr.ciadlab.labmanager.entities.scientificaxis.ScientificAxis;
import fr.ciadlab.labmanager.service.scientificaxis.ScientificAxisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/** REST Controller for scientific axis views.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.5
 */
@RestController
@CrossOrigin
public class ScientificAxisViewController extends AbstractViewController {

	private ScientificAxisService scientificAxisService;

	/** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param messages the accessor to the localized messages.
	 * @param constants the constants of the app.
	 * @param scientificAxisService the service for accessing the scientific axes.
	 * @param usernameKey the key string for encrypting the usernames.
	 */
	public ScientificAxisViewController(
			@Autowired MessageSourceAccessor messages,
			@Autowired Constants constants,
			@Autowired ScientificAxisService scientificAxisService,
			@Value("${labmanager.security.username-key}") String usernameKey) {
		super(messages, constants, usernameKey);
		this.scientificAxisService = scientificAxisService;
	}

	/** Replies the model-view component for managing the scientific axes.
	 *
	 * @param username the name of the logged-in user.
	 * @return the model-view component.
	 */
	@GetMapping("/" + Constants.SCIENTIFIC_AXIS_LIST_ENDPOINT)
	public ModelAndView scientificAxisList(
			@CookieValue(name = "labmanager-user-id", defaultValue = Constants.ANONYMOUS) byte[] username) {
		readCredentials(username, Constants.SCIENTIFIC_AXIS_LIST_ENDPOINT);
		final ModelAndView modelAndView = new ModelAndView(Constants.SCIENTIFIC_AXIS_LIST_ENDPOINT);
		initModelViewWithInternalProperties(modelAndView, false);
		initAdminTableButtons(modelAndView, endpoint(Constants.SCIENTIFIC_AXIS_EDITING_ENDPOINT, Constants.AXIS_ENDPOINT_PARAMETER));
		final List<ScientificAxis> axes = this.scientificAxisService.getAllScientificAxes();
		modelAndView.addObject("scientificAxes", axes); //$NON-NLS-1$
		return modelAndView;
	}

	/** Show the editor for a scientific axis. This editor permits to create or to edit a scientific axis.
	 *
	 * @param axis the identifier of the scientific axis to edit. If it is {@code null}, the endpoint
	 *     is dedicated to the creation of a scientific axis.
	 * @param username the name of the logged-in user.
	 * @return the model-view object.
	 * @throws IOException if there is some internal IO error when building the form's data.
	 */
	@GetMapping(value = "/" + Constants.SCIENTIFIC_AXIS_EDITING_ENDPOINT)
	public ModelAndView scientificAxisEditor(
			@RequestParam(required = false, name = Constants.AXIS_ENDPOINT_PARAMETER) Integer axis,
			@CookieValue(name = "labmanager-user-id", defaultValue = Constants.ANONYMOUS) byte[] username) throws IOException {
		ensureCredentials(username, Constants.SCIENTIFIC_AXIS_EDITING_ENDPOINT, axis);
		final ModelAndView modelAndView = new ModelAndView(Constants.SCIENTIFIC_AXIS_EDITING_ENDPOINT);
		initModelViewWithInternalProperties(modelAndView, false);
		//
		final ScientificAxis axisObj;
		if (axis != null && axis.intValue() != 0) {
			axisObj = this.scientificAxisService.getScientificAxisById(axis.intValue());
			if (axisObj == null) {
				throw new IllegalArgumentException("Scientific axis not found: " + axis); //$NON-NLS-1$
			}

			// Provide a YEAR-MONTH dates
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd"); //$NON-NLS-1$
			if (axisObj.getStartDate() != null) {
				modelAndView.addObject("formattedStartDate", axisObj.getStartDate().format(formatter)); //$NON-NLS-1$
			}
			if (axisObj.getEndDate() != null) {
				modelAndView.addObject("formattedEndDate", axisObj.getEndDate().format(formatter)); //$NON-NLS-1$
			}
		} else {
			axisObj = null;
		}
		//
		modelAndView.addObject("axis", axisObj); //$NON-NLS-1$
		modelAndView.addObject("formActionUrl", rooted(Constants.SCIENTIFIC_AXIS_SAVING_ENDPOINT)); //$NON-NLS-1$
		modelAndView.addObject("formRedirectUrl", rooted(Constants.SCIENTIFIC_AXIS_LIST_ENDPOINT)); //$NON-NLS-1$
		//
		return modelAndView;
	}

}
