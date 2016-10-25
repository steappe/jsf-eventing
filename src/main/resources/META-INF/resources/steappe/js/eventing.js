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

var steappe = steappe || {};
var jsf = jsf || {};


/**
 * The JSF eventing framework.
 * 
 * @author Stéphane Appercel - creation.
 */
steappe.eventing = (function() {
    
    /**
     * The event groups.
     */
    var eventGroups = {};
    var self = {};
    
    
    // ------------- PRIVATE CONSTRUCTORS ------------------
    
    
    /**
     * An event group.
     * 
     * @constructor
     */
    function EventGroup() {
        var self = this;
        
        /**
         * The identifiers of the JSF event observer components.
         * 
         * @type Array
         */
        var clientIds = [];

        /**
         * Registers an event observer.
         * 
         * @param {string} clientId The client identifier of the JSF event observer component.
         * @param {string} event The name of the event to observe.
         * @param {string} execute The space separated list of client IDs of JSF elements to execute during the AJAX request
         * @param {string} render The space separated list of client IDs of JSF element to render after the AJAX request
         */
        self.register = function(clientId, event, execute, render) {
            var client = document.getElementById(clientId);
            
            if (client) {
                var index = clientIds.indexOf(clientId);

                if (index === -1) {
                    clientIds.push(clientId);
                }
                
                /**
                 * attach an event observer to the DOM
                 */
                if (!client.eventObserver) {
                    client.eventObserver = new EventObserver();
                }
                
                /**
                 * register the event
                 */
                client.eventObserver.register(event, execute, render);
            }
        };
        
        /**
         * Dispatches a list of events.
         * 
         * @param {string} events The space separated list of event names to dispatch.
         */
        self.dispatch = function(events) {
            for (var i = 0; i < clientIds.length; i++) {
                var clientId = clientIds[i];
                var eventObserver = getEventObserver(clientId);
                
                if (eventObserver) {
                    eventObserver.dispatch(clientId, events);
                }
            }
        };
        
        /**
         * Gets a registered event observer.
         * 
         * @param {type} clientId The client identifier of the JSF event observer component.
         * @returns {EventObserver} The registered event observer, or null if none could be found.
         */
        function getEventObserver(clientId) {
            var eventObserver = null;
            var client = document.getElementById(clientId);
            
            if (client) {
                eventObserver = client.eventObserver;
            }
            
            return eventObserver;
        }
    }
    
    /**
     * An event observer.
     * 
     * @constructor
     */
    function EventObserver() {
        var self = this;
        
        /**
         * The registered event payloads.
         * 
         * @type Array 
         */
        var eventPayloads = [];
        
        /**
         * Registers an event to observe.
         * 
         * @param {string} event The name of the event to observe.
         * @param {string} execute The space separated list of client IDs of JSF elements to execute during the AJAX request.
         * @param {string} render The space separated list of client IDs of JSF element to render after the AJAX request.
         */
        self.register = function(event, execute, render) {
            var eventPayload = new EventPayload(execute, render);
            eventPayloads[event] = eventPayload;
        };
        
        /**
         * Dispatches a list of events to this event observer.
         * 
         * @param {string} clientId The client identifier of the JSF event observer component.
         * @param {string} events The space separated list of event names to dispatch.
         */
        self.dispatch = function(clientId, events) {
            var eventsToDispatch = events.split(/\s+/g) || [];
            var concernedByEvents = false;
            var toExecute = '';
            var toRender = '';
            
            /**
             * build the list of components to update after the AJAX request
             */
            for (var i = 0; i < eventsToDispatch.length; i++) {
                var event = eventsToDispatch[i];
                var eventPayload = eventPayloads[event];
                
                if (eventPayload) {
                    concernedByEvents = true;
                    toExecute += ' ' + eventPayload.getExecute();
                    toRender += ' ' + eventPayload.getRender();
                }
            }
            
            /**
             * if this event observer is concerned by any of the events to dispatch, then schedule the AJAX request
             */
            if (concernedByEvents) {
                var options = {};
                options['javax.faces.behavior.event'] = 'action';
                options.execute = toExecute;
                options.render = toRender;
                jsf.ajax.request(clientId, null, options);
            }
        };
    }
    
    /**
     * An event payload.
     * 
     * @param {string} execute The space separated list of client IDs of JSF elements to execute during the AJAX request.
     * @param {string} render The space separated list of client IDs of JSF element to render after the AJAX request.
     * @constructor
     */
    function EventPayload(execute, render) {
        var self = this;
        
        /**
         * Gets the space separated list of client IDs of JSF elements to execute during the AJAX request.
         * 
         * @returns {string} The space separated list of client IDs.
         */
        self.getExecute = function() {
            return execute;
        };
        
        /**
         * Gets the space separated list of client IDs of JSF element to render after the AJAX request.
         * 
         * @returns {string} The space separated list of client IDs.
         */
        self.getRender = function() {
            return render;
        };
    }

    
    // ---------------- PUBLIC METHODS -------------------
    
    
    /**
     * Registers an event observer.
     * 
     * @param {string} clientId The client ID of the JSF event observer component.
     * @param {string} group The name of the group of events.
     * @param {string} event the name of the event to observe.
     * @param {string} execute The space separated list of client IDs of JSF elements to execute during the AJAX request
     * @param {string} render The space separated list of client IDs of JSF element to render after the AJAX request
     */
    self.register = function(clientId, group, event, execute, render) {
        var eventGroup = getEventGroup(group, true);
        eventGroup.register(clientId, event, execute, render);
    };
    
    /**
     * Dispatches a list of events.
     * 
     * @param {string} group The name of the group of events.
     * @param {string} events The space separated list of event names to dispatch.
     */
    self.dispatch = function(group, events) {
        var eventGroup = getEventGroup(group, false);
        
        if (eventGroup) {
            eventGroup.dispatch(events);
        }
    };
    
    /**
     * Dispatches a message received by a Web Socket.
     * 
     * @param {string} message The message to dispatch.
     * @param {string} channel The name of the web socket channel at the origin of the event.
     */
    self.dispatchSocketMessage = function(message, channel) {
        if (typeof message === 'string') {
            self.dispatch(channel, message);
        }
        else {
            console.log("message must be a string: " + message);
        }
    };

    
    // ------------- PRIVATE METHODS ------------------
    

    /**
     * Gets or creates an event group.
     * 
     * @param {string} group The name of the group of events.
     * @param {boolean} createIfNotPresent Whether the event group shall be created if does not exist.
     * @returns {EventGroup} The event group, or null if none could be found.
     */
    function getEventGroup(group, createIfNotPresent) {
        var eventGroup = eventGroups[group];
        
        if (!eventGroup && createIfNotPresent) {
            eventGroup = new EventGroup();
            eventGroups[group] = eventGroup;
        }
        
        return eventGroup;
    }
    
    return self;
})();

