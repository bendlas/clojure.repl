(ns clojure.test.repl.dir
  (:use [clojure.repl.dir])
  (:require [clojure.repl.dir :as core])
  (:use [clojure.test]))

(deftest test-line-of-doc
  (let [ldvar #'clojure.repl.dir/line-of-doc
        ld @ldvar]
    (is (= "no doc" (ld #'test-line-of-doc 50)))
    (are [len res] (= (ld ldvar len) res)
         50   "Extracts a line of documentation from a var »"
         10   "Extracts …"
         1    "E")))

(deftest test-dir-line
  (binding [*ns* (find-ns 'clojure.test.repl.dir)
            *terminal-width* 60
            *multiline-token* "&"
            *truncated-token* "%"
            *arglist-printlen* 20]
    (are [ns               sym       col ap _  res] (= res (binding [*arglist-printlen* ap]
                                                      (dir-line (to-namespace ns) sym col)))
         'core             'dir-line 20  20 :> "dir-line             [ns sym column]: Given a namespace and%"
         *ns*              'dir-line 0   20 :> "dir-line [ns sym column]: Given a namespace and a sym,&"
         'clojure.repl.dir 'dir-line 20  10 :> "dir-line             [ns sym c%: Given a namespace and a sy%"
         '*ns*             'dir-line 50  20 :> "dir-line                                           : Given %"
         'core             'dir      12  20 :> "dir :macro   [] [ns] [ns ns-fn]: Print listings of namespac%")))

(deftest test-dir
  (let [empty (create-ns 'empty)]
    ;; testing empty
    (is (= "" (with-out-str
                (dir empty))))))
