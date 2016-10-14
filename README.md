# jsf.eventing
A client-side event framework for JSF.

**Description**

This framework adds support for the production and the observation of events in Web Pages designed with JSF.
It implements a light form of a CDI like event approach, where event producers and event observers don't have
dependencies to each other.

It contains:

* An event producer: it attaches a client behavior to a UI component, and in response to end user interaction it fires an event.
* An event observer: it observes a particular event, and when this event is fired, it sends an AJAX request.
* A JavaScript library: it implements the client-side event framework, building behind the scene a bridge between event producers and event observers.

<br/>
**Example:**
```html
<h:commandButton value="Press me">
    <steappe:eventProducer on="click" event="press-me" group="example"/>
</h:commandButton>

<!-- somewhere else in the page, possibly in a composite component -->
<h:form>
    <steappe:eventObserver event="press-me" group="example" listener="#{bean.onPressMe()}" render="text"/>
    <h:outputText id="text" value="#{bean.text}"/>
</h:form>
```

When the button is clicked, a client-side event named "press-me", belonging to the group of events named "example", is fired by the event producer.  
The event observer reacts on this event, and sends an AJAX request to invoke a server-side operation and to update a component.  
The observers of a particular event can be placed inside a composite component; the event producer does not know anything about these event observers: it just fires a 'semantic' event, and let the observers react on it.

<br/>
**Network traffic reduction**

A Web Page contains both 'static' HTML elements (they will never be updated) and 'dynamic' HTML elements, which are updated by some JavaScript code. And actually, the static part is often larger in size than the dynamic one. In server-side technologies like JSF, it's quite easy and convenient to let the server regenerate entire blocks of HTML elements (on an AJAX request), and while this can be done with no pain (and this is one of the reasons I love JSF), it's not always efficient because these blocks contain HTML elements that never change over time.  
<br/>
Well, with this event framework, it's possible in some circumstances to focus more on the dynamic HTML elements, and therefore to reduce both the network traffic between the client and the server, and the load of the server.

**Example:**
```html
<h:commandButton value="Press me">
    <!-- this one will update the entire component; not efficient -->
    <f:ajax event="click" listener="#{bean.onPressMe()}" render="component"/>
    
    <!-- alternate approach: let the component refresh what it has to refresh -->
    <steappe:eventProducer on="click" event="press-me" group="example"/>
</h:commandButton>

<!-- somewhere else in the page -->
<my:component id="component"/>

<!-- the component (in your component's library) -->
<cc:implementation>
    <div id="#{cc.clientId}">
        <h:form>
            <steappe:eventObserver event="press-me" group="example" listener="#{bean.onPressMe()}" render=":#{cc.clientId}:some-part"/>
        </h:form>
        
        <div>
            <!-- block of HTML elements that don't have to be updated -->
        </div>

        <div jsf:id="some-part">
            <!-- block of HTML elements that the component wants to update on the 'press-me' event -->
        </div>
    </div>
</cc:implementation>
```


Some tests were made in production, and the network traffic could be reduced by up to 70% in some cases.


**Areas of improvement**

* Support for list of events:
```html
<steappe:eventProducer on="click" events="event-one event-two" .../>
```

* Support for rendering hints - send only one AJAX request, and update several components.
```html
<steappe:eventObserver events="event-one event-two" listener="#{bean.myListener()}">
    <steappe:renderingHint event="event-one" render="component-one"/>
    <steappe:renderingHing event="event-two" render="component-two"/>
</steappe:eventObserver>
```


