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

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.render.ClientBehaviorRenderer;
import javax.faces.render.FacesBehaviorRenderer;

/**
 * The renderer of the event producer behavior.
 * 
 * @author Stéphane Appercel - creation.
 */
@FacesBehaviorRenderer(rendererType = EventProducerRenderer.RENDERER_TYPE)
@ResourceDependencies({
    @ResourceDependency(library = "javax.faces", name = "jsf.js", target = "head"),
    @ResourceDependency(library = "steappe", name = "js/eventing.js", target = "head")
})
public class EventProducerRenderer extends ClientBehaviorRenderer {
    
    /**
     * The renderer type implemented by this renderer.
     */
    static public final String RENDERER_TYPE = "steappe.jsf.eventing.producer";

    @Override
    public String getScript(ClientBehaviorContext behaviorContext, ClientBehavior behavior) {
        String script;
        
        if (behavior instanceof EventProducerBehavior) {
            EventProducerBehavior dispatcherBehavior = (EventProducerBehavior) behavior;
            
            String group = dispatcherBehavior.getGroup();
            String event = dispatcherBehavior.getEvent();
            
            /**
             * the script dispatches the produced event to all the interested observers
             */
            StringBuilder builder = new StringBuilder(256);
            builder.append("steappe.eventing.dispatch(");
            builder.append('\'').append(group).append("', ");
            builder.append('\'').append(event).append("')");
            script = builder.toString();
        }
        else {
            script = super.getScript(behaviorContext, behavior);
        }
        
        return script;
    }
    
}
