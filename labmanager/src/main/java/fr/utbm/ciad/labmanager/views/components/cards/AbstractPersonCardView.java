package fr.utbm.ciad.labmanager.views.components.cards;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import fr.utbm.ciad.labmanager.utils.phone.PhoneNumber;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public abstract class AbstractPersonCardView<T> extends ListItem {
    String imageUrl;
    String name;
    String role;
    String email;
    String mobilePhone;
    String officePhone;
    String officeRoom;
    List<String> labels;
    public AbstractPersonCardView(CardBuilder builder, T entity) {
        setData(builder);
        buildCard(entity);
    }

    protected void updateCard(CardBuilder builder){
        setData(builder);
        this.removeAll();
        buildCard(null);
    }

    /**
     * Set the data for the card
     *
     * @param builder The builder containing the data
     */
    private void setData(CardBuilder builder){
        imageUrl = builder.imageUrl;
        name = builder.name;
        role = builder.role;
        email = builder.email;
        mobilePhone = builder.mobilePhone;
        officePhone = builder.officePhone;
        officeRoom = builder.officeRoom;
        labels = builder.labels;
    }

    /**
     * Build the card with the provided data
     *
     * @param entity The entity associated with the card
     */
    private void buildCard(T entity) {
        this.getStyle().set("cursor", "pointer");
        this.addClassNames(
                Background.CONTRAST_5,
                Display.FLEX,
                FlexDirection.COLUMN,
                AlignItems.START,
                Padding.MEDIUM,
                BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL
        );

        // Setup the image
        Div divImage = new Div();
        divImage.addClassNames(
                Display.FLEX,
                AlignItems.CENTER,
                JustifyContent.CENTER,
                Margin.Bottom.MEDIUM,
                Overflow.HIDDEN,
                BorderRadius.MEDIUM,
                Width.FULL
        );
        divImage.setHeight("0");
        divImage.getStyle().set("padding-top", "56.25%");
        divImage.getStyle().set("position", "relative");

        if (imageUrl != null) {
            Image image = new Image();
            image.getStyle().set("display", "block");
            image.getStyle().set("position", "absolute");
            image.getStyle().set("top", "0");
            image.getStyle().set("left", "0");
            image.getStyle().set("width", "100%");
            image.getStyle().set("height", "100%");
            image.getStyle().set("object-fit", "cover");
            image.setSrc(imageUrl);
            image.setAlt("Profile image");
            divImage.add(image);
        } else {
            Icon userIcon = VaadinIcon.USER.create();
            userIcon.setSize("100px");
            userIcon.getStyle().set("position", "absolute");
            userIcon.getStyle().set("top", "50%");
            userIcon.getStyle().set("left", "50%");
            userIcon.getStyle().set("transform", "translate(-50%, -50%)");
            divImage.add(userIcon);
        }

        // Setup the header
        Div headerDiv = new Div();
        headerDiv.setWidth("100%");
        headerDiv.addClassName(Margin.Bottom.NONE);
        headerDiv.getStyle().set("max-height", "40px");
        headerDiv.getStyle().set("overflow", "hidden");
        headerDiv.getStyle().set("text-overflow", "ellipsis");
        Span header = new Span(name);
        header.addClassNames(FontSize.MEDIUM, FontWeight.SEMIBOLD);
        headerDiv.add(header);

        // Setup the subtitle
        Div subtitleDiv = new Div();
        subtitleDiv.setWidth("100%");
        subtitleDiv.addClassNames(Margin.Bottom.SMALL, Margin.Top.NONE);
        Span subtitle = new Span();
        if (role != null) {
            subtitle.setText(role);
        } else {
            subtitle.setText(getTranslation("views.persons.no_role"));
        }
        subtitle.addClassNames(FontSize.SMALL, TextColor.SECONDARY);
        subtitleDiv.add(subtitle);

        Div usefulInfoDiv = new Div();
        usefulInfoDiv.setWidth("100%");
        usefulInfoDiv.addClassNames(Margin.Bottom.MEDIUM);
        usefulInfoDiv.add(createInfoItem(VaadinIcon.ENVELOPE, email));
        usefulInfoDiv.add(createInfoItem(VaadinIcon.MOBILE, mobilePhone));
        usefulInfoDiv.add(createInfoItem(VaadinIcon.PHONE, officePhone));
        usefulInfoDiv.add(createInfoItem(VaadinIcon.OFFICE, officeRoom));

        this.add(divImage, headerDiv, subtitleDiv, usefulInfoDiv);

        // Setup the labels
        if (labels != null && !labels.isEmpty()) {
            HorizontalLayout badgesLayout = new HorizontalLayout();
            badgesLayout.addClassNames(Margin.Top.MEDIUM);
            for (String label : labels) {
                Span badge = new Span();
                badge.getElement().setAttribute("theme", "badge");
                badge.setText(label);
                badgesLayout.add(badge);
            }
            this.add(badgesLayout);
        }

        this.getElement().addEventListener("click", event -> onClickEvent(entity));
    }

    /**
     * Handle the click event on the card
     *
     * @param entity The entity associated with the card
     */
    protected abstract void onClickEvent(T entity);

    /**
     * Create a single info item with an icon and text
     *
     * @param iconType The icon to display
     * @param info     The text to display
     * @return A Div element containing the icon and text
     */
    private Div createInfoItem(VaadinIcon iconType, String info) {
        Div infoItem = new Div();
        infoItem.addClassNames(Display.FLEX, AlignItems.CENTER, Margin.Bottom.SMALL);
        infoItem.getStyle().set("height", "20px");

        Icon icon = iconType.create();
        icon.setSize("16px");
        icon.addClassNames(Margin.Right.SMALL);

        Span infoText = new Span(info != null ? info : " ");
        infoText.addClassNames(FontSize.SMALL);

        infoItem.add(icon, infoText);
        return infoItem;
    }

    /**
     * Builder class for the card
     */
    public static class CardBuilder {
        private String imageUrl;
        private String name;
        private String role;
        private String email;
        private String officePhone;
        private String mobilePhone;
        private String officeRoom;
        private List<String> labels;

        public CardBuilder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public CardBuilder setName(String name) {
            this.name = name != null ? name : " ";
            return this;
        }

        public CardBuilder setRole(String role) {
            this.role = role;
            return this;
        }

        public CardBuilder setEmail(String email) {
            this.email = email != null ? email : " ";
            return this;
        }

        public CardBuilder setOfficePhone(PhoneNumber phone) {
            this.officePhone = phone != null ? phone.toInternationalForm() : " ";
            return this;
        }

        public CardBuilder setMobilePhone(PhoneNumber phone) {
            this.mobilePhone = phone != null ? phone.toInternationalForm() : "";
            return this;
        }

        public CardBuilder setOfficeRoom(String officeRoom) {
            this.officeRoom = officeRoom != null ? officeRoom : "";
            return this;
        }


        public CardBuilder setLabels(List<String> labels) {
            this.labels = labels;
            return this;
        }
    }
}
