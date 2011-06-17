(ns clj-quickrepl.core
  (:refer-clojure :exclude [transient defn])
  (:require [clojure.string :as string])
  (:use [clojure.repl :only [source]]
        [clojure.core.incubator :only [-?>]]
        [clojure.java.io :only [reader]]
        [clojure.template :only [do-template]])
  (:import java.io.StringReader))

(clojure.core/defn line-of-doc "Extracts a line with documentation" [v]
  (if-let [lines (-?> v meta :doc StringReader. reader line-seq)]
    (str 
     (string/trim (first lines))
     (and (second lines) " [...]"))
    "<no doc>"))

(defmacro dir
  ([] `(dir *ns* ns-publics))
  ([ns] `(dir ~ns ns-publics))
  ([ns ns-fn]
     (let [ns (if (= `*ns* ns)
                *ns*
                (-> (find-ns ns)
                    (or (get (ns-aliases *ns*) ns))
                    (or (throw (IllegalArgumentException.
                                (format "%s: No such namespace" ns))))))]
       `(doseq [[s# v#] (~ns-fn ~ns)
                :let [ld# (line-of-doc v#)]]
          (println
           (format "%-18s" s#)
           (if (= "nil" ld#)
             ""
             (format " -- %s" ld#)))))))
