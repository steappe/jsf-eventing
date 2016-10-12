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

import java.util.regex.Pattern;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.StateHelper;
import javax.faces.component.UICommand;

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
     * The REGEX pattern used to split a whitespace separated list of client IDs.
     */
    static public final Pattern WHITESPACE_SEPARATED_LIST_PATTERN = Pattern.compile("\\s+");
    
    /**
     * The helper to access this component's state.
     */
    private final StateHelper stateHelper;

    /**
     * Constructs this faces component.
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
     * Gets the name of the event group. If none is specified, it defaults to "global".
     * 
     * @return the name of the event group.
     */
    public String getGroup() {
        return StringKeys.group.get(stateHelper, EventProducerHandler.DEFAULT_GROUP);
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
     * Gets the name of the event observed by this UI component. This name shall be unique in the event group.
     * 
     * @return the name of the observed event.
     */
    public String getEvent() {
        return StringKeys.event.get(stateHelper);
    }
    
    /**
     * Sets the name of the event observed by this UI component. This name shall be unique in the event group.
     * 
     * @param event the name of the observed event.
     */
    public void setEvent(String event) {
        StringKeys.event.put(stateHelper, event);
    }
    
    /**
     * Gets the space separated list of client IDs to render after the AJAX request has completed.
     * 
     * @return the space separated list of client IDs to render.
     */
    public String getRender() {
        return StringKeys.render.get(stateHelper, "@none");
    }
    
    /**
     * Sets the space separated list of client IDs to render after the AJAX request has completed.
     * 
     * @param render the space separated list of client IDs to render.
     */
    public void setRender(String render) {
        StringKeys.render.put(stateHelper, render);
    }
    
    /**
     * Gets the space separated list of client IDs to process during the execution of the AJAX request.
     * 
     * @return the space separated list of client IDs to process.
     */
    public String getExecute() {
        return StringKeys.execute.get(stateHelper, "@this");
    }
    
    /**
     * Sets the space separated list of client IDs to process during the execution of the AJAX request.
     * 
     * @param execute the space separated list of client IDs to process.
     */
    public void setExecute(String execute) {
        StringKeys.execute.put(stateHelper, execute);
    }
    
    /**
     * The names of the attributes used by this UI component.
     */
    private static enum StringKeys implements ComponentStateKey<String> {
        group, event, render, execute;

        @Override
        public Class<String> type() {
            return String.class;
        }
    }
    
}
