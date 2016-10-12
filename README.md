# jsf-eventing
A client-side eventing framework for JSF

It contains:
    - An event producer: it attaches a client behavior to a UI component, and in response to end user interaction it fires an event.
    - An event observer: it observes a particular event, and when this event is fired, it sends an AJAX request.
    - A JavaScript library: it implements the client-side eventing framework, building a bridge between event producers and event observers.

Example:
<h:commandButton value="Press me">
    <steappe:eventProducer on="click" event="press-me" group="example"/>
</h:commandButton>

<h:form>
    <steappe:eventObserver event="press-me" group="example" listener="#{bean.onPressMe()}" render="text"/>
    <h:outputText id="text" value="#{bean.text}/>
</h:form>

When the button is clicked, a client-side event named "press-me", belonging to the group of events named "example", is fired by the event producer.
The event observer reacts on this event, and sends an AJAX request to invoke a server-side operation and to update a component.

