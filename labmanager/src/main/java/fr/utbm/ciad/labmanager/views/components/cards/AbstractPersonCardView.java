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
    public AbstractPersonCardView(CardBuilder builder, T entity) {
        String imageUrl = builder.imageUrl;
        String name = builder.name;
        String role = builder.role;
        String email = builder.email;
        String mobilePhone = builder.mobilePhone;
        String officePhone = builder.officePhone;
        String officeRoom = builder.officeRoom;
        List<String> labels = builder.labels;

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
        divImage.setHeight("160px");
        Image image = new Image();
        image.setWidth("100%");
        image.setHeight("auto");
        if (imageUrl != null) {
            image.setSrc(imageUrl);
            image.setAlt("Profile image");
            divImage.add(image);
        } else {
            Icon userIcon = VaadinIcon.USER.create();
            userIcon.setSize("100px");
            divImage.add(userIcon);
        }


        // Setup the header
        Div headerDiv = new Div();
        headerDiv.setWidth("100%");
        headerDiv.addClassName(Margin.Bottom.NONE);
        Span header = new Span(name);
        header.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD);
        headerDiv.add(header);

        // Setup the subtitle
        Div subtitleDiv = new Div();
        subtitleDiv.setWidth("100%");
        subtitleDiv.addClassNames(Margin.Bottom.SMALL, Margin.Top.NONE);
        Span subtitle = new Span();
        if (role != null) {
            subtitle.setText(role);
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

    protected abstract void onClickEvent(T entity);

    private Div createInfoItem(VaadinIcon iconType, String info) {
        Div infoItem = new Div();
        infoItem.addClassNames(Display.FLEX, AlignItems.CENTER, Margin.Bottom.SMALL);

        Icon icon = iconType.create();
        icon.setSize("16px");
        icon.addClassNames(Margin.Right.SMALL);

        Span infoText = new Span(info);
        infoText.addClassNames(FontSize.SMALL);

        infoItem.add(icon, infoText);
        return infoItem;
    }

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
            this.name = name != null ? name : "";
            return this;
        }

        public CardBuilder setRole(String role) {
            this.role = role != null ? role : "";
            return this;
        }

        public CardBuilder setEmail(String email) {
            this.email = email != null ? email : "";
            return this;
        }

        public CardBuilder setOfficePhone(PhoneNumber phone) {
            this.officePhone = phone != null ? phone.toInternationalForm() : "";
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
