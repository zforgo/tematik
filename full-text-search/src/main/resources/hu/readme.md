# dictionary-hu

Hungarian spelling dictionary in UTF-8.

Useful with [hunspell][], [`nodehun`][nodehun], [`nspell`][nspell],
Open Office, LibreOffice, Firefox and Thunderbird, or [macOS][].

Generated by [`dictionaries`][dictionaries] from
[`laszlonemeth/magyarispell`][source].

## Install

[npm][]:

```sh
npm install dictionary-hu
```

## Use

```js
var hu = require('dictionary-hu')

hu(function (err, result) {
  console.log(err || result)
})
```

Yields:

```js
{dic: <Buffer>, aff: <Buffer>}
```

Where `dic` is a [`Buffer`][buffer] for the dictionary file at `index.dic` (in
UTF-8) and `aff` is a [`Buffer`][buffer] for the affix file at `index.aff` (in
UTF-8).

Or directly load the files, using something like:

```js
var path = require('path')
var base = require.resolve('dictionary-hu')

fs.readFileSync(path.join(base, 'index.dic'), 'utf-8')
fs.readFileSync(path.join(base, 'index.aff'), 'utf-8')
```

## License

Dictionary and affix file: [(GPL-2.0 OR LGPL-2.1 OR MPL-1.1)](https://github.com/wooorm/dictionaries/blob/main/dictionaries/hu/license).
Rest: [MIT][] © [Titus Wormer][home].

[hunspell]: https://hunspell.github.io

[nodehun]: https://github.com/nathanjsweet/nodehun

[nspell]: https://github.com/wooorm/nspell

[macos]: https://github.com/wooorm/dictionaries#macos

[source]: https://github.com/laszlonemeth/magyarispell

[npm]: https://docs.npmjs.com/cli/install

[dictionaries]: https://github.com/wooorm/dictionaries

[mit]: https://github.com/wooorm/dictionaries/blob/main/license

[buffer]: https://nodejs.org/api/buffer.html#buffer_buffer

[home]: https://wooorm.com