package fr.utbm.ciad.labmanager.views.components.persons;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

public class PaginationComponent extends Div {
    private int currentPage = 0;
    private Button nextButton;
    private Button prevButton;
    private Span pageLabel;

    public PaginationComponent() {
        nextButton = new Button("Next", click -> nextPage());
        prevButton = new Button("Previous", click -> prevPage());
        pageLabel = new Span("Page: " + (currentPage + 1));
        add(prevButton, pageLabel, nextButton);
    }

    private void nextPage() {
        currentPage++;
        fireEvent(new PageChangeEvent(this, currentPage));
    }

    private void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            fireEvent(new PageChangeEvent(this, currentPage));
        }
    }

    public static class PageChangeEvent extends ComponentEvent<PaginationComponent> {
        private final int pageNumber;

        public PageChangeEvent(PaginationComponent source, int pageNumber) {
            super(source, false);
            this.pageNumber = pageNumber;
        }

        public int getPageNumber() {
            return pageNumber;
        }
    }
}
