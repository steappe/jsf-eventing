/*
 * The MIT License
 *
 * Copyright 2016 Steappe Open Source.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package steappe.jsf.eventing;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.StateHelper;
import javax.faces.component.UICommand;
import static javax.faces.component.UICommand.COMPONENT_FAMILY;

/**
 * The event observer UI component.
 * 
 * @author Stéphane Appercel - creation.
 */
@FacesComponent(value = "steappe.jsf.eventing.EventObserver")
@ResourceDependencies({
    @ResourceDependency(library = "javax.faces", name = "jsf.js", target = "head"),
    @ResourceDependency(library = "steappe", name = "js/eventing.js", target = "head")
})
public class EventObserverComponent extends UICommand {
    
    /**
     * The helper to access this component's state.
     */
    private final StateHelper stateHelper;
    
    /**
     * The list of events observed by this observer - intentionally not saved in the component's state holder.
     */
    private final List<ObservedEvent> observedEvents = new LinkedList<>();

    /**
     * Constructs this UI component.
     */
    public EventObserverComponent() {
        super.setRendererType(EventObserverRenderer.RENDERER_TYPE);
        
        this.stateHelper = super.getStateHelper();
    }
    
    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    
    /**
     * Sets the name of of the event group.
     * 
     * @param group the name of the event group
     */
    public void setGroup(String group) {
        StringKeys.group.put(stateHelper, group);
    }
    
    /**
     * Gets the name of the event group. If none is specified, it defaults to "global".
     * 
     * @return the name of the event group.
     */
    public String getGroup() {
        return StringKeys.group.get(stateHelper, EventProducerHandler.DEFAULT_GROUP);
    }
    
    /**
     * Adds an observed event.
     * 
     * @param observedEvent the event to observe.
     */
    public void addObservedEvent(ObservedEvent observedEvent) {
        observedEvents.add(observedEvent);
    }
    
    /**
     * Gets the observed events.
     * 
     * @return the observed events.
     */
    public Stream<ObservedEvent> getObservedEvents() {
        return observedEvents.stream();
    }
    
    /**
     * The names of the tag attributes of type String used by this UI component.
     */
    private static enum StringKeys implements ComponentStateKey<String> {
        group;

        @Override
        public Class<String> type() {
            return String.class;
        }
    }
}
