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
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.event.PhaseId;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

/**
 *
 * @author Stéphane Appercel
 */
public class OnEventHandler extends TagHandler {
    
    /**
     * The required event attribute.
     */
    private final TagAttribute event;
    
    /**
     * The optional execute attribute.
     */
    private final Optional<TagAttribute> execute;
    
    /**
     * The optional render attribute.
     */
    private final Optional<TagAttribute> render;

    /**
     * Constructs this tag handler.
     * 
     * @param config information about the tag.
     */
    public OnEventHandler(TagConfig config) {
        super(config);
        
        this.event = getRequiredAttribute("event");
        this.execute = Optional.ofNullable(getAttribute("execute"));
        this.render = Optional.ofNullable(getAttribute("render"));
    }
    
    @Override
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        if (parent instanceof EventObserverComponent) {
            EventObserverComponent eventObserver = (EventObserverComponent) parent;
            apply(ctx, eventObserver);
        }
    }
    
    /**
     * Applies the observed event to an event observer.
     * <p>
     * The event is applied to the event observer in the render response phase only, because we just want to change
     * the way the event observer is rendered. The event is not saved in the state holder of the event observer for
     * the same reason.
     * 
     * @param ctx the current <code>FaceletContext</code> instance.
     * @param eventObserver the event observer.
     */
    private void apply(FaceletContext ctx, EventObserverComponent eventObserver) {
        PhaseId phaseId = ctx.getFacesContext().getCurrentPhaseId();

        if (phaseId == PhaseId.RENDER_RESPONSE) {
            ValueExpression eventExpression = event.getValueExpression(ctx, String.class);
            Optional<ValueExpression> executeExpression = execute.map(attribute -> attribute.getValueExpression(ctx, Object.class));
            Optional<ValueExpression> renderExpression = render.map(attribute -> attribute.getValueExpression(ctx, Object.class));

            ObservedEvent observedEvent = new ObservedEvent();
            observedEvent.setEvent(eventExpression);
            observedEvent.setExecute(executeExpression);
            observedEvent.setRender(renderExpression);
            eventObserver.addObservedEvent(observedEvent);
        }
    }
}
