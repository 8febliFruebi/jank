(ns jank.core
  (:gen-class)
  (:require [instaparse.core :as insta]))

(def parse
  (insta/parser
    (clojure.java.io/resource "grammar")
    :auto-whitespace :standard))

(defn map-from [index func coll]
  (vec (concat (take index coll)
               (map func (drop index coll)))))

(defmulti handle
  (fn [current ast]
    (first current)))

(defmethod handle :lambda-definition [current ast]
  ; (λ (args) (rets) ...)
  (map-from 3 #(handle %1 ast) current))

(defmethod handle :macro-definition [current ast]
  ; (macro name (types) (args) ...)
  (map-from 4 #(handle %1 ast) current))

(defmethod handle :binding-definition [current ast]
  ; (bind name value)
  (map-from 2 #(handle %1 ast) current))

(defmethod handle :function-call [current ast]
  ; (foo ...)
  (map-from 1 #(handle %1 ast) current))

(defmethod handle :default [current ast]
  current)

(defmulti codegen-impl
  (fn [current]
    (first current)))

(defmethod codegen-impl :lambda-definition [current]
  "")

(defmethod codegen-impl :macro-definition [current]
  "")

(defmethod codegen-impl :binding-definition [current]
  "")

(defmethod codegen-impl :function-call [current]
  "")

(defmethod codegen-impl :default [current]
  "")

(defn codegen [ast]
  (doseq [current (:cells ast)]
    (println current)))

(defn -main
  [& args]
  (codegen
    (let [parsed (parse (slurp (first args)))]
      (when parsed
        (loop [current (first parsed)
               remaining (rest parsed)
               ast {:cells []}]
          (println current)
          (cond
            (nil? current) ast
            :else (recur (first remaining)
                         (rest remaining)
                         (update ast :cells conj (handle current ast)))))))))
