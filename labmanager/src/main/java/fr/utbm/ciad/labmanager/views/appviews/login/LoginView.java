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
        getContent().setWidth("fit-content");
        getContent().getStyle().set("margin", "auto"); // Center the layout
        getContent().getStyle().set("display", "flex");
        getContent().getStyle().set("justify-content", "center");
        getContent().getStyle().set("align-items", "center");
        getContent().getStyle().set("height", "100vh"); // Full viewport height

        final VerticalLayout layoutColumn2 = new VerticalLayout();
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        layoutColumn2.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layoutColumn2.setAlignItems(FlexComponent.Alignment.CENTER);
        layoutColumn2.getStyle().set("padding", "20px"); // Add padding
        layoutColumn2.getStyle().set("background-color", "#f9f9f9"); // Light background color
        layoutColumn2.getStyle().set("border-radius", "8px"); // Rounded corners
        layoutColumn2.getStyle().set("box-shadow", "0 4px 8px rgba(0, 0, 0, 0.1)"); // Subtle shadow

        final Icon icon = new Icon();
        icon.getElement().setAttribute("icon", "lumo:user");
        icon.getStyle().set("font-size", "48px"); // Larger icon size
        icon.getStyle().set("color", "#4CAF50"); // Icon color

        Button loginButtonUtbm = new Button("Login CAS UTBM");
        loginButtonUtbm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButtonUtbm.getStyle().set("margin", "10px 0"); // Margin for spacing
        loginButtonUtbm.getStyle().set("width", "200px"); // Button width
        loginButtonUtbm.addClickListener(event -> {
            UI.getCurrent().getPage().executeJs("window.location.href = '" + casServerUtbmServiceUrl + "'");
        });

        Button loginButtonUniv = new Button("Login CAS UB");
        loginButtonUniv.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButtonUniv.getStyle().set("margin", "10px 0"); // Margin for spacing
        loginButtonUniv.getStyle().set("width", "200px"); // Button width
        loginButtonUniv.addClickListener(event -> {
            UI.getCurrent().getPage().executeJs("window.location.href = '" + casServerUdbServiceUrl + "'");
        });

        layoutColumn2.add(icon);
        layoutColumn2.add(loginButtonUtbm);
        layoutColumn2.add(loginButtonUniv);

        getContent().add(layoutColumn2);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (this.authenticatedUser.get().isPresent()) {
            // Already logged in
            beforeEnterEvent.forwardTo(""); //$NON-NLS-1$
        }
    }
}
