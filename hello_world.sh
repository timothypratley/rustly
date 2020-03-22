#!/bin/sh
lein run --in example/hello_world/hello_world.clj --out example/hello_world/src/main.rs \
	&& cd example/hello_world && cargo run 
