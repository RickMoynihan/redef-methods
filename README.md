[![Clojars Project](https://img.shields.io/clojars/v/tube-alloys/redef-methods.svg)](https://clojars.org/tube-alloys/redef-methods)

# redef-methods

A tiny Clojure library consisting of a single macro
`with-redef-methods` that allows you to stub defmethod multi-method
bodies.  Like Clojure's `with-redefs` but for `defmethods`, not vars.

## When might I use this?

The main use case is for tests, where you want to stub out a specific
defmethod for a particular dispatch value.

In particular it was written to aid testing
[integrant](https://github.com/weavejester/integrant) allowing you to
selectively stub out small parts of a larger integrant system.

The macro though is fully generic and can be used in any clojure
(1.9.0+) code base.

You could for example also use it to introduce new thread local method
dispatches.  Not that I would recomend such a thing!

**WARNING**: You may run into some problems using this with `ig/load-namespaces`
and tools.namespace.

## Example

There exists a multi-method with one or more method bodies:

```clojure
(defmulti wibble (fn [key arg] key))   ;; dispatch on first argument

(defmethod wibble :foo [_ arg]
  :foo-body)

(defmethod wibble :bar [_ arg]
  :bar-body)


(wibble :foo 1) ;; => :foo-body

(wibble :bar 1) ;; => :bar-body
```

... and you want to stub them thread locally:

```clojure
(require '[tube-alloys.redef-methods :refer [with-redef-methods]])

(with-redef-methods [wibble {:foo (fn [key arg]
                                     :foo-override)}]

 ;; Within this thread local dynamic scope (wibble :foo xxx) will trigger our
 ;; override function and return `:foo-override` instead of the original `defmethod`
 ;; body.

 (wibble :foo 1) ;; => :foo-override
 )

;; Outside of the binding scope we get our original implementation:

(wibble :foo 1) ;; => :foo-body
```

## With special thanks to...

[Weavejester](https://github.com/weavejester) for coming up with the
idea [whilst discussing](https://clojurians-log.clojureverse.org/duct/2019-02-01/1549034715.119700)
approaches to testing integrant systems.

## License

Copyright © 2019 Rick Moynihan

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.
