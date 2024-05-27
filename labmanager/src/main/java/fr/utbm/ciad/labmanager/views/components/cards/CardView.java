package fr.utbm.ciad.labmanager.views.components.cards;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

import java.util.List;

public abstract class CardView<T> extends ListItem {

    public CardView(CardBuilder builder, T entity) {
        String title = builder.title;
        String subtitleText = builder.subtitleText;
        String imageUrl = builder.imageUrl != null ? builder.imageUrl : "https://images.unsplash.com/photo-1615796153287-98eacf0abb13?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxleHBsb3JlLWZlZWR8Mnx8fGVufDB8fHx8fA%3D%3D";
        String descriptionText = builder.descriptionText;
        List<String> labels = builder.labels;

        this.getStyle().set("cursor", "pointer");
        this.addClassNames(Background.CONTRAST_5, Display.FLEX, FlexDirection.COLUMN, AlignItems.START, Padding.MEDIUM,
                BorderRadius.LARGE, LumoUtility.BoxShadow.SMALL);

        // Setup the image
        Div divImage = new Div();
        divImage.addClassNames(Background.CONTRAST, Display.FLEX, AlignItems.CENTER, JustifyContent.CENTER,
                Margin.Bottom.MEDIUM, Overflow.HIDDEN, BorderRadius.MEDIUM, Width.FULL);
        divImage.setHeight("160px");
        Image image = new Image();
        image.setWidth("100%");
        image.setHeight("auto");
        image.setSrc(imageUrl);
        image.setAlt("Profile image");
        divImage.add(image);

        // Setup the header
        Div headerDiv = new Div();
        headerDiv.setWidth("100%");
        Span header = new Span();
        header.addClassNames(FontSize.XLARGE, FontWeight.SEMIBOLD);
        header.setText(title);
        headerDiv.add(header);

        // Setup the subtitle
        Div subtitleDiv = new Div();
        subtitleDiv.setWidth("100%");
        subtitleDiv.getStyle().set("height", "20px"); // Set a minimum height to maintain space
        Span subtitle = new Span();
        if (subtitleText != null) {
            subtitle.setText(subtitleText);
        }
        subtitle.addClassNames(FontSize.SMALL, TextColor.SECONDARY, Margin.Bottom.SMALL);
        subtitleDiv.add(subtitle);

        // Setup the description
        Div descriptionDiv = new Div();
        descriptionDiv.setWidth("100%");
        descriptionDiv.getStyle().set("height", "136px");
        Paragraph description = new Paragraph(descriptionText);
        description.addClassNames(Margin.Vertical.MEDIUM, TextColor.TERTIARY);
        descriptionDiv.add(description);

        this.add(divImage, headerDiv, subtitleDiv, descriptionDiv);

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

    protected abstract void refreshItem(T entity);

    protected abstract void refreshGrid();

    public static class CardBuilder {
        private String title;
        private String subtitleText;
        private String imageUrl;
        private String descriptionText;
        private List<String> labels;

        public CardBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public CardBuilder setSubtitleText(String subtitleText) {
            this.subtitleText = subtitleText;
            return this;
        }

        public CardBuilder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public CardBuilder setDescriptionText(String descriptionText) {
            if (descriptionText != null) {
                this.descriptionText = descriptionText.length() > 100 ? descriptionText.substring(0, 100) + "..." : descriptionText;
            } else {
                this.descriptionText = "";
            }
            return this;
        }

        public CardBuilder setLabels(List<String> labels) {
            this.labels = labels;
            return this;
        }
    }
}
