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

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.faces.component.StateHelper;

/**
 * The key that refers to a map of values in a component's state.
 * 
 * @author Stéphane Appercel - creation.
 * @param <V> the generic type of the values stored in the map identified by this key.
 */
public interface ComponentStateMapKey<V> {
    
    /**
     * Puts a value into the map referred to by this key in the component's state.
     * 
     * @param stateHelper the component's state helper.
     * @param mapKey the key of the value in the map.
     * @param value the value to put in the map.
     */
    default void put(StateHelper stateHelper, String mapKey, V value) {
        String key = name();
        stateHelper.put(key, mapKey, value);
    }
    
    /**
     * Removes a value from the map referred to by this key in the component's state.
     * 
     * @param stateHelper the component's state helper.
     * @param mapKey the key of the value in the map.
     */
    default void remove(StateHelper stateHelper, String mapKey) {
        String key = name();
        stateHelper.remove(key, mapKey);
    }
    
    /**
     * Gets a value in the map referred to by this key in the component's state.
     * 
     * @param stateHelper the component's state helper.
     * @param mapKey the key of the value in the map.
     * @return the optionally retrieved value.
     */
    default Optional<V> get(StateHelper stateHelper, String mapKey) {
        String key = name();
        Class<V> type = type();
        Optional<Map<?, ?>> pairs = Optional.ofNullable((Map<?, ?>) stateHelper.get(key));
        return pairs.map(map -> map.get(mapKey)).map(type::cast);
    }
    
    /**
     * Gets all the keys present in the map referred to by this key in the component's state.
     * 
     * @param stateHelper the component's state helper.
     * @return the keys present in the map.
     */
    default Stream<String> getMapKeys(StateHelper stateHelper) {
        String key = name();
        Optional<Map<?, ?>> pairs = Optional.ofNullable((Map<?, ?>) stateHelper.get(key));
        return pairs.map(Map::keySet).map(Set::stream).orElse(Stream.empty()).map(mapKey -> (String) mapKey);
    }
    
    /**
     * Removes all the values present in the map referred to by this key in the component's state.
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
