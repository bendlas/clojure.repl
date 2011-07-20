# clojure.repl.dir

A better version of clojure.repl/dir

## Usage

    > (require 'clojure.repl.dir)
    Replacing clojure.repl/dir
    > (use 'clojure.repl)

    > (dir *ns* ns-refers) ;; lists imports
    ;; a reaaaly long list    

    > (def x)
    > (dir)
    x : no doc
    
    > (dir clojure.repl.dir)
    *arglist-printlen* : How much of arglists is displayed
    *multiline-token*  : Marker when doc has multiple lines
    *terminal-width*   : Screen width in characters, that dir fills
    *truncated-token*  : Marker when first doc line was truncated
    NamespaceCoercion  : Defines a coercion to namespace used by dir
    dir :macro         [] [ns] [ns ns-fn]: Print listings of namespaces with doc sn…
    dir-fn             [ns] [ns ns-fn]: Returns a sorted seq of symbols naming publ…
    dir-line           [ns sym column]: Given a namespace and a sym, »
    to-namespace       [this]: Coerce to clojure.lang.Namespace

## License

Copyright (C) 2011 Herwig Hochleitner

Distributed under the Eclipse Public License, the same as Clojure.
