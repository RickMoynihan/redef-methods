(ns tube-alloys.redef-methods-test
  (:require [tube-alloys.redef-methods :as sut]
            [clojure.test :refer :all]
            [clojure.spec.test.alpha :as st]))

(use-fixtures :once (fn [t]
                      (st/instrument)
                      (t)
                      (st/unstrument)))

(defmulti wibble (fn [dispatch-key another-arg]
                     dispatch-key))

(defmethod wibble :foo [_ another-arg]
  :defmethod-foo)

(defmethod wibble :bar [_ another-arg]
  :defmethod-bar)

(defmethod wibble :default [_ another-arg]
  :defmethod-default)

(defmulti wobble (fn [dispatch-key]
                     dispatch-key))

(defmethod wobble :foo [_]
  :defmethod-foo)

(deftest with-redef-methods-test
  (sut/with-redef-methods [wibble {:foo (fn [_ _arg]
                                          :overriden-foo)
                                   :baz (fn [_ arg]
                                          :new-dispatch-baz)
                                   1 1}
                           wobble {:foo (fn [_]
                                          :overriden-foo)}]

    (testing "Inside with-redef-methods"
      (is (= :overriden-foo (wibble :foo 1)))
      (is (= :overriden-foo (wobble :foo)))

      (is (= :new-dispatch-baz (wibble :baz 2)))
      (is (= :defmethod-default (wibble :quux 2)))))

  (testing "Normal behaviour restored outside of with-redef-methods"
    (is (= :defmethod-foo (wibble :foo 1)))
    (is (= :defmethod-bar (wibble :bar 1)))
    (is (= :defmethod-default (wibble :quux 1))))

  )
