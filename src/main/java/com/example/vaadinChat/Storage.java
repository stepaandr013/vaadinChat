package com.example.vaadinChat;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class Storage {
    @Getter
    private Queue<ChatMessage> messages = new ConcurrentLinkedQueue<>();
    private ComponentEventBus eventBus = new ComponentEventBus(new Div());

    @Getter
    @AllArgsConstructor
    public static class ChatMessage { //Atribuim variabilele
        private String name;
        private String message;
    }

    public static class ChatEvent extends ComponentEvent<Div> { //Classa Chat Event
        public ChatEvent() {
            super(new Div(), false);
        }
    }

    public void addRecord(String user, String message) { // Pentru afisarea  user si message
        messages.add(new ChatMessage(user, message)); // Afisam messajul
        eventBus.fireEvent(new ChatEvent());
    }

    public void addRecordJoined(String user) { // Afisam message ca user a intat in Chat
        messages.add(new ChatMessage("", user));
        eventBus.fireEvent(new ChatEvent());
    }

    public Registration attachListener(ComponentEventListener<ChatEvent> messageListener) {
        return eventBus.addListener(ChatEvent.class, messageListener);
    }

    public int size() {
        return messages.size();
    }
}