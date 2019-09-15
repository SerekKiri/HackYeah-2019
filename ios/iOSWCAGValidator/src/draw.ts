import PImage, { Bitmap } from 'pureimage'
import BufferStreamer from './BufferStreamer'
import BufferCollector from './BufferCollector'

interface Node {
  x: string
  y: string
  width: string
  height: string
}

export default async function draw (buffer: Buffer, node: Node) {
  const bitmap = await PImage.decodePNGFromStream(new BufferStreamer(buffer))

  const ctx = bitmap.getContext()
  ctx.fillStyle = 'rgba(255, 0, 0, 0.65)'
  ctx.fillRect(
    parseInt(node.x) * 2,
    parseInt(node.y) * 2,
    parseInt(node.width) * 2,
    parseInt(node.height) * 2
  )

  const stream = new BufferCollector()
  await PImage.encodePNGToStream(bitmap, stream)
  return stream.buffer
}
