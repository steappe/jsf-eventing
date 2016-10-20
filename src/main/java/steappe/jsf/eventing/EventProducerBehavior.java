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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.ClientBehaviorHint;
import javax.faces.component.behavior.FacesBehavior;

/**
 * The event producer client behavior.
 * 
 * @author Stéphane Appercel - creation.
 */
@FacesBehavior(value = EventProducerBehavior.BEHAVIOR_ID)
public class EventProducerBehavior extends ClientBehaviorBase {
    
    /**
     * The Faces Behavior ID of this component.
     */
    static public final String BEHAVIOR_ID = "steappe.jsf.eventing.producer";
    
    /**
     * Indication that this client behavior will perform a POST back to the server.
     */
    static private final Set<ClientBehaviorHint> HINTS = Collections.unmodifiableSet(
            EnumSet.of(ClientBehaviorHint.SUBMITTING)
    );

    /**
     * The name of the event group.
     */
    private String group;
    
    /**
     * The space separated list of event names fired by the event producer.
     */
    private String events;
    
    @Override
    public String getRendererType() {
        return EventProducerRenderer.RENDERER_TYPE;
    }

    @Override
    public Set<ClientBehaviorHint> getHints() {
        return HINTS;
    }

    /**
     * Sets the name of of the event group.
     * 
     * @param group the name of the event group.
     */
    public void setGroup(String group) {
        this.group = group;
    }
    
    /**
     * Gets the name of the event group. If none is specified, it defaults to "global".
     * 
     * @return the name of the event group.
     */
    public String getGroup() {
        return this.group;
    }
    
    /**
     * Sets the space separated liste of event names fired by the event producer.
     * 
     * @param events the space separated list of event names.
     */
    public void setEvents(String events) {
        this.events = events;
    }
    
    /**
     * Gets the space separated liste of event names fired by the event producer.
     * 
     * @return the space separated list of event names.
     */
    public String getEvents() {
        return this.events;
    }
}
