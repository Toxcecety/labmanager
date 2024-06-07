package fr.utbm.ciad.labmanager.views.components.persons;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

public class PaginationComponent extends Div {
    private int currentPage = 0;
    private Button nextButton;
    private Button prevButton;
    private Span pageLabel;
    private long totalPages;

    public PaginationComponent(long totalPages) {
        this.totalPages = totalPages;
        nextButton = new Button(getTranslation("views.pagination.next"), click -> nextPage());
        prevButton = new Button(getTranslation("views.pagination.previous"), click -> prevPage());
        pageLabel = new Span("Page: " + (currentPage + 1));
        if (totalPages <= 1) {
            setVisible(false);
        }
        if (currentPage == totalPages) {
            nextButton.setEnabled(false);
        }
        prevButton.setEnabled(false);
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

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int pageNumber) {
        this.currentPage = pageNumber;
        pageLabel.setText("Page: " + (currentPage + 1));
        nextButton.setEnabled(currentPage+1 != totalPages);
        prevButton.setEnabled(currentPage+1 != 1);
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
        if (totalPages <= 1) {
            setVisible(false);
        } else {
            setVisible(true);
        }
        nextButton.setEnabled(currentPage+1 != totalPages);
    }

    public void addPageChangeListener(ComponentEventListener<PageChangeEvent> listener) {
        addListener(PageChangeEvent.class, listener);
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
