package fr.utbm.ciad.labmanager.views.components.persons;

import com.google.common.base.Strings;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinService;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.utils.io.filemanager.FileManager;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.avatars.AvatarItem;
import org.arakhne.afc.vmutil.FileSystem;

import java.util.function.Supplier;

public class SearchComponent extends Div {
    private static final boolean DEFAULT_ORGANIZATION_FILTERING = true;
    private final Supplier<ResearchOrganization> defaultOrganization;
    private final Supplier<FileManager> fileManager;
    private ToggleButton restrictToOrganization;
    private AvatarItem defaultOrganizationAvatar;
    private Button searchButton;
    private TextField searchField;
    private Button resetButton;
    private Select<String> filterMenu;
    public SearchComponent(Supplier<ResearchOrganization> defaultOrganization, Supplier<FileManager> fileManager) {
        this.defaultOrganization = defaultOrganization;
        this.fileManager = fileManager;
        createUI();
        add(restrictToOrganization, searchField, searchButton, resetButton);
    }

    public SearchComponent(String[] filterOptions, Supplier<ResearchOrganization> defaultOrganization, Supplier<FileManager> fileManager) {
        this.defaultOrganization = defaultOrganization;
        this.fileManager = fileManager;
        createUI();
        filterMenu = new Select<>();
        filterMenu.setLabel(getTranslation("views.filters"));
        filterMenu.setItems(filterOptions);
        filterMenu.getStyle().set("margin-right", "10px");
        add(restrictToOrganization, searchField, filterMenu, searchButton, resetButton);
    }

    private void createUI(){
        restrictToOrganization = new ToggleButton(DEFAULT_ORGANIZATION_FILTERING);
        defaultOrganizationAvatar = new AvatarItem();
        setDefaultOrganizationIcon(defaultOrganizationAvatar, defaultOrganization.get(), fileManager.get());
        defaultOrganizationAvatar.setHeading(getTranslation("views.filters.default_organization_filter"));
        restrictToOrganization.setLabelComponent(defaultOrganizationAvatar);
        restrictToOrganization.getStyle().set("margin-right", "30px");
        restrictToOrganization.setWidth("250px");
        restrictToOrganization.addValueChangeListener(event -> organizationFilterEvent());

        searchField = new TextField();
        searchField.setLabel(getTranslation("views.filters.keywords"));
        searchField.getStyle().set("margin-right", "10px");

        searchButton = new Button(getTranslation("views.filters.apply"), click -> searchEvent());
        searchButton.getStyle().set("margin-right", "10px");

        resetButton = new Button(getTranslation("views.filters.reset"), click -> resetEvent());
        resetButton.getStyle().set("margin-right", "10px");
    }

    private void setDefaultOrganizationIcon(AvatarItem organizationAvatar, ResearchOrganization defaultOrganization, FileManager fileManager) {
        final var logo = defaultOrganization.getPathToLogo();
        if (!Strings.isNullOrEmpty(logo)) {
            var logoFile = FileSystem.convertStringToFile(logo);
            if (logoFile != null) {
                logoFile = fileManager.normalizeForServerSide(logoFile);
                if (logoFile != null) {
                    organizationAvatar.setAvatarResource(ComponentFactory.newStreamImage(logoFile));
                }
            }
        }
    }

    private void searchEvent() {
        if (filterMenu.isEmpty()) {
            Notification notification = new Notification();
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setText(getTranslation("views.search.missing_filter"));
            notification.setDuration(3000);
            notification.open();
        } else {
            if (searchField.isEmpty()) {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText(getTranslation("views.search.missing_search"));
                notification.setDuration(3000);
                notification.open();
            } else {
                fireEvent(new SearchComponentEvent(this));
            }
        }
    }

    private void resetEvent() {
        searchField.clear();
        filterMenu.clear();
        fireEvent(new SearchComponentEvent(this));
    }

    private void organizationFilterEvent(){
        fireEvent(new SearchComponentEvent(this));
    }



    public String getSearchValue() {
        return searchField.getValue() == null ? "" : searchField.getValue();
    }

    public String getFilterValue() {
        return filterMenu.getValue() == null ? "" : filterMenu.getValue();
    }

    public boolean getRestrictToOrganizationValue() {
        return restrictToOrganization.getValue();
    }

    public void addSearchComponentListener(ComponentEventListener<SearchComponentEvent> listener) {
        addListener(SearchComponentEvent.class, listener);
    }
    public static class SearchComponentEvent extends ComponentEvent<SearchComponent> {
        public SearchComponentEvent(SearchComponent source) {
            super(source, false);
        }
    }
}
