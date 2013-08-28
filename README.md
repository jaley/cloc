# cloc

Cloc is a Clojure documentation tool, similar to PyDoc's local webserver
feature. It will use the excellent [Codox](http://github.com/weavejester/codox) library
to scan your source code and dependencies for docstrings, then server those docs through
a web UI, on a local web server. It also packs a fast, Lucene-powered search index, because
documentation tools that don't have a search are just wrong.

![Screenshot](http://i.imgur.com/uqYSdyz.png)

## Usage

The idea is that you add Cloc as a plugin to your `:user` profile for Leiningen. In
`~/.lein/profiles.clj`:

```
   {:user
    {:plugins [[cloc "0.1.0-SNAPSHOT"]}}
```

That will make the Leiningen plugin available in all your projects. So, from some Leiningen
project that you're working on, just run:

    $ lein cloc

You should see some logging, then after a few seconds it'll tell you the webserver is running.

## License

Copyright © 2013 James Aley

Distributed under the Eclipse Public License, the same as Clojure.
