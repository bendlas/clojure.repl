# clojure.repl.dir

A better version of clojure.repl/dir

## Usage

    > (require 'clojure.repl.dir)
    Replacing clojure.repl/dir
    > (use 'clojure.repl)

The dir macro is replaced.
You can use it plain to examine the current namespace.

    > (def x)
    > (dir)
    x : no doc

You can give it a second argument to determine what kind of namespace
entries you want to see. One of ns-(publics|refers|map|interns|imports).

    > (dir *ns* ns-refers) ;; lists imports
    ;; a reaaaly long list    
    
Of course it takes just a namespace too. Notice, that it displays call
lists and macros.

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

When trying to examine a namespace, that isn't loaded, it tries to
require it.

    > (dir clojure.zip)
    ;; namespace clojure.zip not loaded; requiring!
    append-child [loc item]: Inserts the item as the rightmost child of the node at…
    branch?      [loc]: Returns true if the node at loc is a branch
    children     [loc]: Returns a seq of the children of node at loc, which must be…
    down         [loc]: Returns the loc of the leftmost child of the node at this l…
    edit         [loc f & args]: Replaces the node at this loc with the value of (f…
    ...

## License

Copyright (C) 2011 Herwig Hochleitner

Distributed under the Eclipse Public License, the same as Clojure.
