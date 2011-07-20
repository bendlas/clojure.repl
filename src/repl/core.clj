(ns repl.core
  (:require [clojure.string :as str]))

;; ## This module implements a dir macro

;; It prints a listing of vars in a namespace
;; with arglist (if a given var has one in the metadata)
;; and snippets of documentation

;; # Example output
;;     *arglist-printlen* : How much of arglists is displayed
;;     *multiline-token*  : Marker when doc has multiple lines
;;     *terminal-width*   : Screen width in characters, that dir fills
;;     *truncated-token*  : Marker when first doc line was truncated
;;     NamespaceCoercion  : Defines a coercion to namespace used by dir
;;     dir :macro         [] [ns] [ns ns-fn]: Print listings of namespaces with doc sn…
;;     dir-fn             [ns] [ns ns-fn]: Returns a sorted seq of symbols naming publ…
;;     dir-line           [ns sym column]: Given a namespace and a sym, »
;;     resolve-ns         [ns-or-sym]: Like the-ns, but also looks in ns-aliases of cu…
;;     to-namespace       [this]: Coerce to clojure.lang.Namespace

;; # Dynamic configuration vars

(def ^{:doc "Screen width in characters, that dir fills"
       :dynamic true} *terminal-width* 80)

(def ^{:doc "Marker when doc has multiple lines"
       :dynamic true} *multiline-token* " »")

(def ^{:doc "Marker when first doc line was truncated"
       :dynamic true} *truncated-token* "…")

(def ^{:doc "How much of arglists is displayed"
       :dynamic true} *arglist-printlen* 24)

;; # Private helpers

(defn ^:private clamp
  "Numeric clamp"
  [n min- max+]
  (-> n (max min-) (min max+)))

(defn ^:private limit
  "String clamp"
  [^String s n]
  (.substring s 0 (clamp n 0 (count s))))

(defn ^:private append-token [s tok max-len]
  (str (limit s (- max-len (count tok)))
       tok))

(defn ^:private truncate [s tok max-len]
  (if (and (> (count s) max-len)
           (< (count tok) max-len))
    (append-token s tok max-len)
    (limit s max-len)))

(defn ^:private fill [s n ch]
  (apply str s (repeat (- n (count s)) ch)))

(defn ^:private line-of-doc
  "Extracts a line of documentation from a var
   inserting truncation/continuation markers"
  [var max-len]
  (let [max-len (max 0 max-len)
        doc (-> var meta :doc)
        doclines (when doc
                   (map str/trim
                        (str/split-lines doc)))
        line (first doclines)
        next-line? (-> doclines second count pos?)
        long-line? (> (count line) max-len)
        end-token (if long-line? *truncated-token* *multiline-token*)
        end-token? (and (or next-line? long-line?)
                        (> max-len (count end-token)))]
    (cond
     (str/blank? line) (limit "no doc" max-len)
     end-token? (append-token line end-token max-len)
     :else (limit line max-len))))

(defn ^:private get-separator
  "Return arglist or plain :"
  [var column]
  (let [alist (->> var meta :arglists (str/join \space))
        macro (-> var meta :macro)]
    (str " "
;         (when macro ":macro ")
         (when (> *terminal-width*
                  (+ column *arglist-printlen*))
           (truncate alist *truncated-token* *arglist-printlen*))
         ": ")))

;; # Public helpers (used in macro)

(defn dir-line
  "Given a namespace and a sym,
   returns string with the name and first line of doc
   name is padded with space to column"
  [ns sym column]
  (let [var (ns-resolve ns sym)
        sep (get-separator var column)
        ld (line-of-doc var (- *terminal-width*
                             column
                             (count sep)))]
    (limit (str (fill (str (name sym)
                           (when (-> var meta :macro) " :macro"))
                      column \space)
                sep ld)
           *terminal-width*)))

(defprotocol NamespaceCoercion
  "Defines a coercion to namespace used by dir"
  (to-namespace [this] "Coerce to clojure.lang.Namespace"))

(extend-protocol NamespaceCoercion
 
  ;; With this implementation, to-ns bhaves like the-ns, but also
  ;; looks in ns-aliases of current *ns* and supports the symbol '*ns*
  ;; as a special case input value.
  ;; This is to allow for `(dir *ns* ns-interns)` and such.
  
  ;; Tries to require as a last resort.

  nil
  (to-namespace [_]
    (throw (IllegalArgumentException.
            (format "Cannot coerce nil to clojure.lang.Namespace"))))
  
  java.lang.Object
  (to-namespace [o]
    (throw (IllegalArgumentException.
            (format "Cannot coerce %s to clojure.lang.Namespace"
                    (.getName (class o))))))
  
  clojure.lang.Namespace
  (to-namespace [ns] ns)
  
  clojure.lang.Symbol
  (to-namespace [sym]
    (or (find-ns sym)
        (get (ns-aliases *ns*) sym)
        (and (= sym '*ns*) *ns*)
        (do (require sym)
            (find-ns sym)))))

(defn dir-fn
  "Returns a sorted seq of symbols naming public vars in
   a namespace"
  ([ns] (dir-fn ns ns-publics))
  ([ns ns-fn]
     (sort (keys (ns-fn (the-ns ns))))))

;; # The macro

(defmacro dir
  "Print listings of namespaces with doc snippets
   ns: a symbol naming a namespace or '*ns*
   ns-fn: one of ns-(interns|publics|imports|...)
   default invocation: (dir *ns* ns-publics)"
  ([] `(dir ~*ns* ns-publics))
  ([ns] `(dir ~ns ns-publics))
  ([ns ns-fn]
     `(let [ns# (to-namespace '~ns)
            syms# (dir-fn ns# ~ns-fn)
            col# (->> syms#
                      (map (comp count name))
                      (reduce max 0))]
        (doseq [sym# syms#]
          (println
           (dir-line ns# sym# col#))))))
