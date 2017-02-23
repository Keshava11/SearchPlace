
http://www.oreilly.com/programming/free/files/rxjava-for-android-app-development.pdf
RxJava:
-> allows us to represent any operation as an asynchronous data stream.
-> The data stream that can be created on any thread
-> The data stream can be declaratively composed
-> the data stream can be consumed by multiple objects on any thread.


Concepts:
-> OBSERVABLES
-> OBSERVER
-> SCHEDULER
-> #create an observable
-> #subscribe an observer
-> #subscribeOn() specify thread
-> #observerOn() specify thread
-> #filter() filters the unwanted objects
-> #flatMap() maps a value to another value, which is returned as an Observable
-> #merge() merges the results of Observables
-> COLD Observables
-> HOT Observables
-> CONNECTABLE OBSERVABLES
-> #publish() Creates a Connectable Observable so that multiple objects can consume the result
-> #connect() Used by a Connectable Observable so the Observation Starts irrespective of the fact
 whether a subscriber is registered or not
