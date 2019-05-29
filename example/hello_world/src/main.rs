use rpds::{List, Vector, HashTrieMap, HashTrieSet};
use ferris_says::{say};
use std::io::{stdout, BufWriter};
fn main() {
let stdout = stdout();
let out = b"Hello world";
let _v = Vector::new().push_back(1).push_back(2).push_back(3);
let _m = HashTrieMap::new().insert(b"a", b"b").insert(b"c", b"d");
let _s = HashTrieSet::new().insert(1).insert(3).insert(2);
let width = 24;
let mut writer = BufWriter::new(stdout.lock());
say(out, width, &mut writer).unwrap();
}
