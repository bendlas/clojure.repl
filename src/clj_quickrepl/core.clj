(ns clj-quickrepl.core
  (:import (java.io BufferedReader StringReader)))

(defn ^:private line-of-doc
  "Extracts a line of documentation from a var"
  [v]
  (let [doc (-> v meta :doc)
        lines (when doc
                (-> doc StringReader. BufferedReader. line-seq))
        docline (first lines)]
    (if docline
      (str (.trim docline)
           (and (second lines) " [...]"))
      "<no doc>")))

(defn dir-line
  "Given a namespace and a sym,
   returns string with the name and first line of doc"
  [ns sym]
  (let [v (ns-resolve ns sym)
        ld (line-of-doc v)]
    (if (= "nil" ld)
      sym
      (format "%-18s -- %s" sym ld))))

(defn resolve-ns
  "Like the-ns, but also looks in ns-aliases of current *ns*
   and supports '*ns* as a special case"
  [ns-or-sym]
  (if (instance? clojure.lang.Namespace ns-or-sym)
    ns-or-sym
    (or (find-ns ns-or-sym)
        (get (ns-aliases *ns*) ns-or-sym)
        (and (= ns-or-sym '*ns*) *ns*)
        (throw (IllegalArgumentException.
                (format "No namespace: %s found" ns-or-sym))))))

(defn dir-fn
  "Returns a sorted seq of symbols naming public vars in
   a namespace"
  ([ns] (dir-fn ns ns-publics))
  ([ns ns-fn]
     (sort (map first (ns-fn (the-ns ns))))))

(defmacro dir
  "Print listings of namespaces with doc snippets
   ns: a symbol naming a namespace or '*ns*
   ns-fn: one of ns-(interns|publics|imports|...)
   default invocation: (dir *ns* ns-publics)"
  ([] `(dir ~*ns* ns-publics))
  ([ns] `(dir ~ns ns-publics))
  ([ns ns-fn]
     (let [ns (resolve-ns ns)]
       `(doseq [sym# (dir-fn ~ns ~ns-fn)]
          (println
           (dir-line ~ns sym#))))))
