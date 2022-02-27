package com.example.vaadinChat;

import com.github.rjeschke.txtmark.Processor;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

@Route("")
@Push
public class MainView extends VerticalLayout { //Clasa main pentru aplicatia
    private final Storage storage;
    private Registration registration;
    private Grid<Storage.ChatMessage> grid;
    private VerticalLayout chat;
    private VerticalLayout login;
    private String user = "";

    public MainView(Storage storage) { //Apeleaza functiile pentru Logare si chat
        this.storage = storage;

        buildLogin();
        buildChat();
    }

    private void buildLogin() { //Logam un user nou
        login = new VerticalLayout() {{
            TextField field = new TextField();
            field.setPlaceholder("Your name"); //User trebuie de intriduce numele say orice simbol/
            add(
                    field,
                    new Button("Enter") {{  // Cand userul introduce parola noi ascundem login form si afisam chat
                        addClickListener(click -> {
                            login.setVisible(false);    //Ascundem Login form
                            chat.setVisible(true);      //Afisam Chat
                            user = field.getValue();
                            storage.addRecordJoined(user);
                        });
                        addClickShortcut(Key.ENTER);
                    }}
            );
        }};
        add(login);
    }

    private void buildChat() {  //Constuim chat
        chat = new VerticalLayout();
        add(chat);
        chat.setVisible(false);

        grid = new Grid<>();
        grid.setItems(storage.getMessages());
        grid.addColumn(new ComponentRenderer<>(message -> new Html(renderRow(message))))
                .setAutoWidth(true);

        TextField field = new TextField();

        chat.add(       //Pentru text
                new H3("Chat"),
                grid,
                new HorizontalLayout() {{
                    add(
                            field,
                            new Button("➡") {{
                                addClickListener(click -> {
                                    storage.addRecord(user, field.getValue());
                                    field.clear();
                                });
                                addClickShortcut(Key.ENTER); //Apasam ENTER si messajul este send
                            }}
                    );
                }}
        );
    }

    public void onMessage(Storage.ChatEvent event) {
        if (getUI().isPresent()) {
            UI ui = getUI().get();
            ui.getSession().lock();
            ui.beforeClientResponse(grid, ctx -> grid.scrollToEnd());
            ui.access(() -> grid.getDataProvider().refreshAll());
            ui.getSession().unlock();
        }
    }

    private String renderRow(Storage.ChatMessage message) { //Afisam date in chat
        if (message.getName().isEmpty()) {
            return Processor.process(String.format("_User **%s** joined the chat!_", message.getMessage())); //Afisam ca user a intarat in chat
        } else {
            return Processor.process(String.format("**%s**: %s", message.getName(), message.getMessage())); //Afisam mesaj care o timis user
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        registration = storage.attachListener(this::onMessage);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        registration.remove();
    }
}