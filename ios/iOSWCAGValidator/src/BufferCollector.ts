import { Writable } from 'stream'

export default class BufferCollector extends Writable {
  buffers: Buffer[] = []

  get buffer () {
    return Buffer.concat(this.buffers)
  }

  _write (chunk: any, encoding: string, callback: (error?: (Error | null)) => void): void {
    if (!(chunk instanceof Buffer)) {
      chunk = Buffer.from(chunk)
    }
    this.buffers.push(chunk)
    callback()
  }
}
