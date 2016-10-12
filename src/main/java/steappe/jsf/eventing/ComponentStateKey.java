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

import java.io.Serializable;
import javax.faces.component.StateHelper;

/**
 * The key that identifies a value in a UI component's state.
 * 
 * @author Stéphane Appercel - creation.
 * @param <V> the generic type of the value identified by this key.
 */
public interface ComponentStateKey<V extends Serializable> {
    
    /**
     * Gets a value associated to this key in the component's state.
     * 
     * @param stateHelper the component state helper.
     * @return the retrieved value, or null when no value is associated to this key.
     */
    default V get(StateHelper stateHelper) {
        String name = name();
        Class<V> type = type();
        return type.cast(stateHelper.eval(name));
    }
    
    /**
     * Gets a value associated to this key in the component's state.
     * 
     * @param stateHelper the component state helper.
     * @param defaultValue the default value to return when no value is associated to this key.
     * @return the retrieved value, or the specified default value when no value is associated to this key.
     */
    default V get(StateHelper stateHelper, V defaultValue) {
        String name = name();
        Class<V> type = type();
        return type.cast(stateHelper.eval(name, defaultValue));
    }
    
    /**
     * Puts the value associated to this key in the component's state.
     * 
     * @param stateHelper the component state helper.
     * @param value the value to associate to this key.
     */
    default void put(StateHelper stateHelper, V value) {
        String name = name();
        stateHelper.put(name, value);
    }
    
    /**
     * Gets the name of this key.
     * 
     * @return the key name.
     */
    String name();
    
    /**
     * Gets the type of the value associated to this key.
     * 
     * @return the value type.
     */
    Class<V> type();
}
