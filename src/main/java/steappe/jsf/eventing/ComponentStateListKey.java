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
import java.util.stream.Stream;
import javax.faces.component.StateHelper;

/**
 * The key that refers to a list of values in a component's state.
 * 
 * @author Stéphane Appercel - creation.
 * @param <V> the generic type of the values stored in the list identified by this key.
 */
public interface ComponentStateListKey<V> {
    
    /**
     * Adds the specified value in the list referred to by this key in the component's state.
     * 
     * @param stateHelper the component's state helper.
     * @param value the value to add.
     */
    default void add(StateHelper stateHelper, V value) {
        String key = name();
        stateHelper.add(key, value);
    }
    
    /**
     * Removes the specified value from the list referred to by this key in the component's state.
     * 
     * @param stateHelper the component's state helper.
     * @param value the value to remove.
     */
    default void remove(StateHelper stateHelper, V value) {
        String key = name();
        stateHelper.remove(key, value);
    }
    
    /**
     * Gets all the values present in the list referred to by this key in the component's state.
     * 
     * @param stateHelper the component's state helper.
     * @return all the values.
     */
    default Stream<V> getValues(StateHelper stateHelper) {
        String key = name();
        Class<V> type = type();
        Optional<Collection<?>> values = Optional.ofNullable((Collection<?>) stateHelper.get(key));
        return values.map(Collection::stream).orElse(Stream.empty()).map(type::cast);
    }
    
    /**
     * Removes all the values present in the list referred to by this key in the component's state.
     * 
     * @param stateHelper the component's state helper.
     */
    default void removeAll(StateHelper stateHelper) {
        String key = name();
        stateHelper.remove(key);
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
