# lunch-clj

## Purpose of this app

This is an application I made for fun for some colleagues of mine. I wanted to try building a full stack clojure application. It was mainly a project to learn and also to try out some of the concurrency features of the language.

## Development Mode

### Compile css:

Compile css file once.

```
lein sass once
```

Automatically recompile css file on change.

```
lein sass watch
```

### Run development server from the REPL:

```
lein repl
```

Then call `(init)` and `(start)` to start the server.

If you want to restart the server call `(reset)`.

By default the server will start on port `3500`. You can configure the server and the database in the config file in `src/config/dev`.

### Run development ClojureScript compiler

```
lein with-profile +client figwheel
```
## Development Uberjar

```
lein with-profile +local uberjar
```

## Production Uberjar

```
lein uberjar
```

That should compile the clojurescript code first, and then create the standalone jar.

When you run the jar you need to set the port the ring server will use by setting the environment variable PORT.

You will also need to set the database configuration as environment variable:

```
EXPORT DB_ADAPTER=postgresql
EXPORT DB_USERNAME=name
EXPORT DB_PASSWORD=password
EXPORT DB_NAME=db_name
EXPORT DB_SERVER_NAME=localhost
EXPORT DB_PORT_NUMBER=5432
```


If you only want to compile the clojurescript code:

```
lein clean
lein cljsbuild once min
```

## TODO

- Refactor specs to live in a seperate folder
- Refactor some of the response objects to be protocols instead of maps
- Move out some of the logic that currently lives inside of the routes to be in a seperate controller folder
- Store sessions asynchonously in the database 
- Return an asynchronous result when querying the database, so that the thread is not blocked
- Write more tests for session route and model
