# Rustly

A Clojure to Rust transpiler.

![orange leaves](https://cdn.britannica.com/18/196818-131-84946E03.jpg)


## Usage

`lein run --in <src.clj> --out <out.rs>`


## Example

Run the example with `./hello_world.sh`

This shell script invokes the transpiler with input file
[example/hello_world/hello-world.clj](example/hello_world/hello_world.clj),
produces output file [example/hello_world/src/main.rs](example/hello_world/src/main.rs),
invokes `cargo run`,
producing the "Ferris says hello" message:

    ----------------------------
    | Hello world              |
    ----------------------------
              \
               \
                  _~^~^~_
              \) /  o o  \ (/
                '_   -   _'
                / '-----' \


## Status

This is a science project.
Rustly is alpha and subject to change.
Feedback on design and implementation is welcome.
Only a subset of Clojure is implemented.
The goal is to cover all the good parts.

No packaged builds are provided at this time.
To use this transpiler you must download the source code.

See [Kalai](https://github.com/echeran/kalai) for a more comprehensive Clojure transpiler.

## Rationale

A concise Lisp syntax, backed by Clojure tooling that targets Rust might be a good thing.


## Developing

Issues, pull requests, and suggestions are very welcome.

## License

Copyright © 2019 Timothy Pratley

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
