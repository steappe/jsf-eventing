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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

/**
 * The renderer of the event observer UI component.
 * 
 * @author Stéphane Appercel - creation.
 */
@FacesRenderer(
        componentFamily = EventObserverComponent.COMPONENT_FAMILY,
        rendererType = EventObserverRenderer.RENDERER_TYPE
)
public class EventObserverRenderer extends Renderer {
    
    /**
     * The renderer type implemented by this renderer.
     */
    static public final String RENDERER_TYPE = "steappe.jsf.eventing.observer";
    
    static private final char QUOTE = '\'';
    
    static private final char COMMA = ',';
    
    /**
     * Converts relative client IDs into absolute client IDs.
     * 
     * @param context the faces context.
     * @param eventObserver the event observer.
     * @param relativeClientIds the relative client IDs.
     * @return the space separated list of absolute client IDs.
     */
    static private String toAbsoluteClientIds(
            FacesContext context,
            EventObserverComponent eventObserver,
            Stream<String> relativeClientIds) {
        Set<String> absoluteClientIds = relativeClientIds.map(relativeClientId ->
                toAbsoluteClientId(context, eventObserver, relativeClientId)
        ).collect(Collectors.toSet());
        
        StringBuilder builder = new StringBuilder(256);
        absoluteClientIds.forEach(clientId -> builder.append(clientId).append(' '));
        
        return builder.toString().trim();
    }
    
    /**
     * Converts a relative client ID into an absolute client ID.
     * 
     * @param context the faces context.
     * @param eventObserver the event observer.
     * @param relativeClientId the relative client ID.
     * @return the corresponding absolute client ID.
     */
    static private String toAbsoluteClientId(
            FacesContext context,
            EventObserverComponent eventObserver,
            String relativeClientId) {
        String absoluteClientId;
        
        if (relativeClientId.isEmpty() || relativeClientId.charAt(0) == '@') {
            absoluteClientId = relativeClientId;
        }
        else {
            UIComponent clientComponent = eventObserver.findComponent(relativeClientId);
            
            if (clientComponent == null) {
                throw new IllegalArgumentException("no such component: " + relativeClientId);
            }
            
            absoluteClientId = clientComponent.getClientId(context);
        }
        
        return absoluteClientId;
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        if (component instanceof EventObserverComponent) {
            EventObserverComponent eventObserver = (EventObserverComponent) component;
            
            String clientId = eventObserver.getClientId(context);
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement("span", eventObserver);
            writer.writeAttribute("id", clientId, "id");
            writer.startElement("script", eventObserver);
            writer.append("\n");
            
            ObservedEvent[] observedEvents = eventObserver.getObservedEvents().toArray(ObservedEvent[]::new);
            
            /**
             * encode the observed events
             */
            for (ObservedEvent observedEvent : observedEvents) {
                encodeEvent(context, eventObserver, observedEvent);
            }
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        if (component instanceof EventObserverComponent) {
            ResponseWriter writer = context.getResponseWriter();
            writer.endElement("script");
            writer.endElement("span");
        }
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (component instanceof EventObserverComponent) {
            EventObserverComponent eventObserver = (EventObserverComponent) component;
            
            String clientId = eventObserver.getClientId(context);
            String source = context.getExternalContext().getRequestParameterMap().get("javax.faces.source");

            if (clientId.equals(source)) {
                ActionEvent event = new ActionEvent(eventObserver);
                PhaseId phaseId = eventObserver.isImmediate() ?
                        PhaseId.APPLY_REQUEST_VALUES :
                        PhaseId.INVOKE_APPLICATION;
                event.setPhaseId(phaseId);
                eventObserver.queueEvent(event);
            }
        }
    }
    
    /**
     * Encodes the JavaScript instruction to register an observed event in the client-side event framework.
     * 
     * @param context the faces context.
     * @param eventObserver the event observer.
     * @param observedEvent the observed event.
     * @throws IOException if an I/O error occurred during the encoding.
     */
    private void encodeEvent(
            FacesContext context,
            EventObserverComponent eventObserver,
            ObservedEvent observedEvent) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = eventObserver.getClientId(context);
        String group = eventObserver.getGroup();
        String event = observedEvent.getEvent(context);
        String execute = toAbsoluteClientIds(context, eventObserver, observedEvent.getExecute(context));
        String render = toAbsoluteClientIds(context, eventObserver, observedEvent.getRender(context));
        
        writer.append("steappe.eventing.register(");
        writer.append(QUOTE).append(clientId).append(QUOTE);
        writer.append(COMMA);
        writer.append(QUOTE).append(group).append(QUOTE);
        writer.append(COMMA);
        writer.append(QUOTE).append(event).append(QUOTE);
        writer.append(COMMA);
        writer.append(QUOTE).append(execute).append(QUOTE);
        writer.append(COMMA);
        writer.append(QUOTE).append(render).append(QUOTE);
        writer.append(");");
        writer.append("\n");
    }
}
