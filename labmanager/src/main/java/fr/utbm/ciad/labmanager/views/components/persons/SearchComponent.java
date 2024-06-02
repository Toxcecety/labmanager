package fr.utbm.ciad.labmanager.views.components.persons;

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
import fr.utbm.ciad.labmanager.views.ViewConstants;

public class SearchComponent extends Div {
    private ToggleButton restrictToOrganization;
    private Button searchButton;
    private TextField searchField;
    private Button resetButton;
    private Select<String> filterMenu;
    public SearchComponent() {
        createUI();
        add(searchField,searchButton, resetButton);
    }

    public SearchComponent(String[] filterOptions) {
        createUI();
        filterMenu = new Select<>();
        filterMenu.setLabel("Filter by");
        filterMenu.setItems(filterOptions);
        filterMenu.getStyle().set("margin-right", "10px");
        add(searchField, filterMenu, searchButton, resetButton);
    }

    private void createUI(){
        searchField = new TextField();
        searchField.setLabel("Search");
        searchField.getStyle().set("margin-right", "10px");

        searchButton = new Button("Search", click -> searchEvent());
        searchButton.getStyle().set("margin-right", "10px");

        resetButton = new Button("Reset", click -> resetEvent());
        resetButton.getStyle().set("margin-right", "10px");
    }

    private void searchEvent() {
        if (filterMenu.isEmpty()) {
            Notification notification = new Notification();
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setText("Please select a filter before searching.");
            notification.setDuration(3000);
            notification.open();
        } else {
            if (searchField.isEmpty()) {
                Notification notification = new Notification();
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setText("Please fill in the search field.");
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

    public String getSearchValue() {
        return searchField.getValue() == null ? "" : searchField.getValue();
    }

    public String getFilterValue() {
        return filterMenu.getValue() == null ? "" : filterMenu.getValue();
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
