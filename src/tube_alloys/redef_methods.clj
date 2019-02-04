(ns tube-alloys.redef-methods
  (:require [clojure.spec.alpha :as s]))

(defn wrap-dispatch-fn [multi-method mappings]
  (fn [& args]
    (let [not-found (Object.)
          dispatch-fn (.dispatchFn multi-method)
          dispatch-val (apply dispatch-fn args)
          ret (get mappings dispatch-val not-found)]
      (if (= not-found ret)
        (apply multi-method args)
        (apply ret args)))))

(defmacro with-redef-methods
  "Temporarily adds or overrides dispatch keys for the specified
  multi-methods while executing the body.

  This works in a similar manner to with-redefs by establishing a
  thread local binding over the multi-methods var.

  Unlike with-redefs it requires the right hand side of each binding
  to be a map of dispatch values to functions, of an arity compatible
  with the multimethod."
  [bindings & body]
  (let [rebindings (->> bindings
                        (partition 2)
                        (mapcat (fn [[sym mappings]]
                                  [sym `(wrap-dispatch-fn ~sym ~mappings)])))]
    `(with-redefs [~@rebindings]
       ~@body)))

(s/def ::override-map (s/map-of any? ifn?))

(s/def ::bindings (s/+ (s/cat :binding symbol? :map ::override-map)))

(s/fdef with-method-keys
  :args
  (s/cat :bindings (s/spec ::bindings) :body (s/* any?)))
