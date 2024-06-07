package fr.utbm.ciad.labmanager.views.appviews.login;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Route(value = "login")
@AnonymousAllowed
public class LoginView extends Composite<HorizontalLayout> implements BeforeEnterObserver {

    private static final long serialVersionUID = 4873621745334362590L;

    private final AuthenticatedUser authenticatedUser;

    @Value("${labmanager.cas-servers.utbm.service}")
    private String casServerUtbmServiceUrl;

    @Value("${labmanager.cas-servers.ub.service}")
    private String casServerUdbServiceUrl;

    public LoginView(@Autowired AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        final VerticalLayout layoutColumn2 = new VerticalLayout();
        final Icon icon = new Icon();
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        layoutColumn2.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layoutColumn2.setAlignItems(FlexComponent.Alignment.CENTER);
        icon.getElement().setAttribute("icon", "lumo:user");
        //layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, loginForm);

        Button loginButtonUtbm = new Button("Login Utbm");
        loginButtonUtbm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButtonUtbm.addClickListener(event -> {
            UI.getCurrent().getPage().executeJs("window.location.href = '" + casServerUtbmServiceUrl + "'");
                });

        Button loginButtonUniv = new Button("Login Univ");
        loginButtonUniv.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButtonUniv.addClickListener(event -> {
            UI.getCurrent().getPage().executeJs("window.location.href = '" + casServerUdbServiceUrl + "'");
        });

        layoutColumn2.add(loginButtonUtbm);
        layoutColumn2.add(loginButtonUniv);
        getContent().add(layoutColumn2);

        layoutColumn2.add(icon);

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (this.authenticatedUser.get().isPresent()) {
            // Already logged in
            beforeEnterEvent.forwardTo(""); //$NON-NLS-1$
        }
    }
}
