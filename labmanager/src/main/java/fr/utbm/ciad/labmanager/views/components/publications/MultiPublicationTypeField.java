package fr.utbm.ciad.labmanager.views.components.publications;

import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class MultiPublicationTypeField extends AbstractMultiPublicationTypeField{
    public MultiPublicationTypeField(PublicationService publicationService, SerializableConsumer<MultiSelectComboBox<String>> comboConfigurator, SerializableConsumer<MultiSelectComboBox<String>> comboInitializer, SerializableConsumer<Consumer<String>> creationCallback) {
        super(combo -> {
                    combo.setItemLabelGenerator(it -> it);
                },
                combo->{
                    combo.setItems(query -> publicationService.getAllType().stream());

                }, creationCallback);
    }
}
