import { Readable, ReadableOptions } from 'stream'

// https://stackoverflow.com/a/55983006
export default class BufferStreamer extends Readable {
  _object: any

  constructor (object: any, options?: ReadableOptions) {
    super(object instanceof Buffer || typeof object === 'string' ? options : { objectMode: true })
    this._object = object
  }

  _read () {
    this.push(this._object)
    this._object = null
  }
}
