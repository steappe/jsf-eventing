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

import java.io.IOException;
import java.util.Optional;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

/**
 * The handler of the event producer tag.
 * 
 * @author Stéphane Appercel - creation.
 */
public class EventProducerHandler extends TagHandler {
    
    /**
     * The name of the default group of events if none is specified.
     */
    static public final String DEFAULT_GROUP = "global";
    
    /**
     * The name of the client behavior event that triggers this event producer.
     */
    private final TagAttribute on;
    
    /**
     * The name of event to produce. This event is dispatched to all the interested observers.
     */
    private final TagAttribute event;
    
    /**
     * The name of the group of events to which the event belongs. It's optional, and it defaults to 'global'.
     */
    private final Optional<TagAttribute> group;
    
    /**
     * Constructs the tag handler.
     * 
     * @param config specifies the definition of the tag.
     */
    public EventProducerHandler(TagConfig config) {
        super(config);
        
        this.on = getRequiredAttribute("on");
        this.group = Optional.ofNullable(getAttribute("group"));
        this.event = getRequiredAttribute("event");
    }
    
    @Override
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        if (ComponentHandler.isNew(parent)) {
            if (parent instanceof ClientBehaviorHolder) {
                ClientBehaviorHolder holder = (ClientBehaviorHolder) parent;
                
                String onValue = on.getValue(ctx);
                String groupValue = group.map(attribute -> attribute.getValue(ctx)).orElse(DEFAULT_GROUP);
                String eventValue = event.getValue(ctx);
                
                EventProducerBehavior behavior = new EventProducerBehavior();
                behavior.setGroup(groupValue);
                behavior.setEvent(eventValue);
                holder.addClientBehavior(onValue, behavior);
            }
            else {
                /**
                 * @todo add support for composite components.
                 */
            }
        }
    }
}
