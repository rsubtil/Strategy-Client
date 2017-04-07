package gui;

import de.lessvoid.nifty.controls.MenuItemActivatedEvent;
import org.bushe.swing.event.EventTopicSubscriber;

public class MenuItemActivatedEventSubscriber implements EventTopicSubscriber<MenuItemActivatedEvent> {
    
    @Override
    public void onEvent(final String id, final MenuItemActivatedEvent event) {
        int option = (int)event.getItem();
        switch(option) {
            default:
                System.out.println("Choice not defined!");
        }
    }
}
