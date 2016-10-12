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

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        if (component instanceof EventObserverComponent) {
            EventObserverComponent renderedComponent = (EventObserverComponent) component;
            
            String clientId = renderedComponent.getClientId(context);
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement("span", renderedComponent);
            writer.writeAttribute("id", clientId, "id");
            writer.startElement("script", renderedComponent);
            encodeFunction(context, renderedComponent);
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
            EventObserverComponent renderedComponent = (EventObserverComponent) component;
            
            String clientId = renderedComponent.getClientId(context);
            String source = context.getExternalContext().getRequestParameterMap().get("javax.faces.source");

            if (clientId.equals(source)) {
                ActionEvent event = new ActionEvent(renderedComponent);
                PhaseId phaseId = renderedComponent.isImmediate() ?
                        PhaseId.APPLY_REQUEST_VALUES :
                        PhaseId.INVOKE_APPLICATION;
                event.setPhaseId(phaseId);
                renderedComponent.queueEvent(event);
            }
        }
    }
    
    /**
     * Encodes the JavaScript function to execute after this UI component is loaded in the DOM.
     * 
     * @param context the faces context.
     * @param component the UI component being rendered.
     * @throws IOException if an I/O error occurred during the encoding.
     */
    private void encodeFunction(FacesContext context, EventObserverComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String group = component.getGroup();
        String event = component.getEvent();
        String clientId = component.getClientId(context);
        String execute = component.getExecute();
        String render = component.getRender();
        
        /**
         * the execute and render attributes have client IDs relative to their naming container
         * but for the AJAX request we need absolute client IDs
         */
        execute = toAbsoluteClientIds(context, component, execute);
        render = toAbsoluteClientIds(context, component, render);
        
        writer.append("\n");
        writer.append("steappe.eventing.register(");
        writer.append("'").append(group).append("'");
        writer.append(", '").append(event).append("'");
        writer.append(", '").append(clientId).append("'");
        writer.append(", '").append(execute).append("'");
        writer.append(", '").append(render).append("'");
        writer.append(");");
        writer.append("\n");
    }
    
    /**
     * Converts a space separated list of relative client IDs into a space separated list of absolute client IDs.
     * 
     * @param context the faces context.
     * @param component the UI component being rendered.
     * @param relativeClientIds the space separated list of relative client IDs to convert.
     * @return the corresponding space separated list of absolute client IDs.
     */
    private String toAbsoluteClientIds(FacesContext context, EventObserverComponent component, String relativeClientIds) {
        StringBuilder builder = new StringBuilder(256);
        
        EventObserverComponent.WHITESPACE_SEPARATED_LIST_PATTERN.splitAsStream(relativeClientIds).forEach(relativeClientId -> {
            String absoluteClientId = toAbsoluteClientId(context, component, relativeClientId);
            builder.append(absoluteClientId).append(' ');
        });
        
        return builder.toString().trim();
    }
    
    /**
     * Converts a relative client ID into an absolute client ID.
     * 
     * @param context the faces context.
     * @param component the UI component being rendered.
     * @param relativeClientId the relative client ID.
     * @return the corresponding absolute client ID.
     */
    private String toAbsoluteClientId(FacesContext context, EventObserverComponent component, String relativeClientId) {
        String absoluteClientId;
        
        if (relativeClientId.isEmpty() || relativeClientId.charAt(0) == '@') {
            absoluteClientId = relativeClientId;
        }
        else {
            UIComponent clientComponent = component.findComponent(relativeClientId);
            
            if (clientComponent == null) {
                throw new IllegalArgumentException("no such component: " + relativeClientId);
            }
            
            absoluteClientId = clientComponent.getClientId(context);
        }
        
        return absoluteClientId;
    }
    
}
