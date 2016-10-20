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

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

/**
 * An observed event.
 * 
 * @author Stéphane Appercel - creation.
 */
public class ObservedEvent {

    /**
     * The REGEX pattern used to split a whitespace separated list of client IDs.
     */
    static private final Pattern WHITESPACE_SEPARATED_LIST_PATTERN = Pattern.compile("\\s+");
    
    /**
     * The default value for the render attribute, if none is specified.
     */
    static private final String DEFAULT_RENDER = "@none";
    
    /**
     * The default value for the execute attribute, if none is specified.
     */
    static private final String DEFAULT_EXECUTE = "@this";

    /**
     * The value expression that specifies the name of the event to observe.
     */
    private ValueExpression event;
    
    /**
     * The optional value expression that specifies the client IDs to execute during an AJAX request.
     */
    private Optional<ValueExpression> execute;
    
    /**
     * The optional value expression that specifies the client IDs to render during an AJAX request.
     */
    private Optional<ValueExpression> render;
    
    /**
     * Sets the value expression that specifies the name of the event to observe.
     * 
     * @param event the value expression for the event name.
     */
    public void setEvent(ValueExpression event) {
        this.event = event;
    }
    
    /**
     * Gets the value expression that specifies the name of the event to observe.
     * 
     * @return the value expression for the event name.
     */
    public ValueExpression getEvent() {
        return this.event;
    }
    
    /**
     * Gets the name of the observed event.
     * 
     * @param ctx the faces context.
     * @return the name of the observed event.
     */
    public String getEvent(FacesContext ctx) {
        ELContext elContext = ctx.getELContext();
        return (String) event.getValue(elContext);
    }
    
    /**
     * Sets the optional value expression that specifies the client IDs to execute during an AJAX request.
     * 
     * @param execute the optional value expression for the execute attribute.
     */
    public void setExecute(Optional<ValueExpression> execute) {
        this.execute = execute;
    }
    
    /**
     * Gets the optional value expression that specifies the client IDs to execute during an AJAX request.
     * 
     * @return the optional value expression for the execute attribute.
     */
    public Optional<ValueExpression> getExecute() {
        return this.execute;
    }
    
    /**
     * Gets the client IDs to execute during an AJAX request.
     * <p>
     * If the value expression of the execute attribute was not specified or if it evaluates to an empty collection
     * of client IDs, then the default value of '@this' will be returned.
     * 
     * @param ctx the faces context.
     * @return the client IDs to execute.
     */
    public Stream<String> getExecute(FacesContext ctx) {
        return evaluateStrings(ctx, execute, DEFAULT_EXECUTE);
    }
    
    /**
     * Sets the optional value expression that specifies the client IDs to render during an AJAX request.
     * 
     * @param render the optional value expression for the render attribute.
     */
    public void setRender(Optional<ValueExpression> render) {
        this.render = render;
    }
    
    /**
     * Gets the optional value expression that specifies the client IDs to render during an AJAX request.
     * 
     * @return the optional value expression for the render attribute.
     */
    public Optional<ValueExpression> getRender() {
        return this.render;
    }
    
    /**
     * Gets the client IDs to render during an AJAX request.
     * <p>
     * If the value expression of the render attribute was not specified or if it evaluates to an empty collection
     * of client IDs, then the default value of '@none' will be returned.
     * 
     * @param ctx the faces context.
     * @return the client IDs to render.
     */
    public Stream<String> getRender(FacesContext ctx) {
        return evaluateStrings(ctx, render, DEFAULT_RENDER);
    }
    
    /**
     * Evaluates an optional expression that specifies a collection of strings.
     * 
     * @param ctx the faces context.
     * @param optionalExpression the optional expression to evaluate.
     * @param defaultValue the default value to put in the collection in case the collection would be empty.
     * @return the result of the evaluation of the optional expression.
     */
    private Stream<String> evaluateStrings(
            FacesContext ctx,
            Optional<ValueExpression> optionalExpression,
            String defaultValue) {
        Stream<String> singleton = Stream.of(defaultValue);
        ELContext elContext = ctx.getELContext();
        
        return optionalExpression.map(expression -> {
            Stream<String> values;
            
            Object value = expression.getValue(elContext);
            
            if (value instanceof Collection) {
                Collection<?> collection = (Collection<?>) value;
                
                if (!collection.isEmpty()) {
                    values = collection.stream().map(String.class::cast);
                }
                else {
                    values = singleton;
                }
            }
            else {
                String list = (String) value;
                String[] array = WHITESPACE_SEPARATED_LIST_PATTERN.split(list);
                
                if (array.length != 0) {
                    values = Stream.of(array);
                }
                else {
                    values = singleton;
                }
            }
            
            return values;
        }).orElse(singleton);
    }
}
