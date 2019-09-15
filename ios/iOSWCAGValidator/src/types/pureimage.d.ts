declare module 'pureimage' {
  import { Stream } from 'stream'

  export function decodePNGFromStream (data: Stream): Promise<Bitmap>

  export function encodePNGToStream (bitmap: Bitmap, outstream: Stream): Promise<void>

  export interface Bitmap {
    getContext (): Context
  }

  export interface Context {
    fillStyle: string

    fillRect (x: number, y: number, w: number, h: number): void
  }
}
