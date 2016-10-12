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
     * The observers for each group of events.
     */
    var groupObservers = {};
    var self = {};
    
    
    // ------------- PRIVATE CONSTRUCTORS ------------------
    
    
    /**
     * An observer of a group of events.
     * 
     * @constructor
     */
    function GroupObserver() {
        
        /**
         * The observers of each event.
         */
        var eventObservers = {};
        var self = this;
        
        /**
         * Registers an event observer for a JSF client.
         * 
         * @param {string} event The event to observe.
         * @param {string} clientId The identifier of the JSF client interested by the event.
         * @param {function} observer The observer of the event.
         */
        self.register = function(event, clientId, observer) {
            var eventObserver = getEventObserver(event, true);
            eventObserver.register(clientId, observer);
        };
        
        /**
         * Dispatches an event to all the registered observers.
         * 
         * @param {string} event The event to dispatch.
         */
        self.dispatch = function(event) {
            var eventObserver = getEventObserver(event, false);
            
            if (eventObserver) {
                eventObserver.dispatch();
            }
            else {
                console.log("no observer for event " + event);
            }
        };
        
        /**
         * Gets or creates an event observer.
         * 
         * @param {string} event The event to observe.
         * @param {boolean} createIfNotPresent Whether the observer shall be created if it does not exist.
         * @returns {Eventing.EventObserver} The event observer, or null if none could be found.
         */
        function getEventObserver(event, createIfNotPresent) {
            var eventObserver = eventObservers[event];
            
            if (!eventObserver && createIfNotPresent) {
                eventObserver = new EventObserver();
                eventObservers[event] = eventObserver;
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
         * The identifiers of the JSF clients interested in the event.
         * 
         * @type Array
         */
        var clientIds = [];
        
        /**
         * Registers a JSF client interested in the event.
         * 
         * @param {string} clientId the identifier of the JSF client.
         * @param {function} observer The observer of the event.
         */
        self.register = function(clientId, observer) {
            var client = document.getElementById(clientId);
            
            if (client) {
                var index = clientIds.indexOf(clientId);

                if (index === -1) {
                    clientIds.push(clientId);
                }
                
                client.observer = observer;
            }
            else {
                console.log("no client in DOM for " + clientId);
            }
        };
        
        /**
         * Dispatches the event to all the registered JSF clients.
         */
        self.dispatch = function() {
            var count = clientIds.length;
            
            for (var i = 0; i < count; i++) {
                var clientId = clientIds[i];
                var client = document.getElementById(clientId);
                
                if (client) {
                    var observer = client.observer;

                    if (observer) {
                        observer();
                    }
                    else {
                        console.log("no observer for client " + clientId);
                    }
                }
                else {
                    console.log("no client in DOM for " + clientId);
                }
            }
        };
    }

    
    // ------------- PRIVATE METHODS ------------------
    

    /**
     * Gets or creates an observer for a group of events.
     * 
     * @param {string} group The name of the group of events.
     * @param {boolean} createIfNotPresent Whether the observer shall be created if not present.
     * @returns {Eventing.GroupObserver} The observer, or null if none could be found.
     */
    function getGroupObserver(group, createIfNotPresent) {
        var groupObserver = groupObservers[group];
        
        if (!groupObserver && createIfNotPresent) {
            groupObserver = new GroupObserver();
            groupObservers[group] = groupObserver;
        }
        
        return groupObserver;
    }

    
    // ---------------- PUBLIC METHODS -------------------
    
    
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
    
    /**
     * Dispatches an event.
     * 
     * @param {type} group the name of the group of events.
     * @param {type} event the name of the event to dispatch in that group.
     */
    self.dispatch = function(group, event) {
        var groupObserver = getGroupObserver(group, false);
        
        if (groupObserver) {
            groupObserver.dispatch(event);
        }
    };
    
    
    /**
     * Registers an observer for a specified event.
     * 
     * @param {string} group The name of the group of events.
     * @param {string} event The name of the observed event in that group.
     * @param {string} clientId The client ID of the JSF element that observes the event.
     * @param {string} execute The space separated list of client IDs of JSF elements to execute during the AJAX request
     * @param {string} render The space separated list of client IDs of JSF element to render after the AJAX request
     */
    self.register = function(group, event, clientId, execute, render) {
        console.log("register - group: " + group + ", event: " + event + ", clientId: " + clientId);
        
        var observer = function() {
            var options = {};
            options['javax.faces.behavior.event'] = 'action';
            options.execute = execute;
            options.render = render;
            
            jsf.ajax.request(clientId, null, options);
        };
        
        var groupObserver = getGroupObserver(group, true);
        groupObserver.register(event, clientId, observer);
    };
    
    return self;
})();

