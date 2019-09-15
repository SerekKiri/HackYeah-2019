import { parseString } from 'xml2js'

export default function parse (xml: string): Promise<any> {
  return new Promise((resolve, reject) => {
    parseString(xml, (err, result) => {
      if (err) {
        reject(err)
      } else {
        resolve(transform(result).children[0])
      }
    })
  })
}

// weird shit happens here
function transform (obj: any): any {
  return Object.entries(obj).reduce((acc: any, [key, value]: [string, any]) => {
    if (key === '$') return { ...acc, ...value }

    if (!value.$) value.$ = { type: key }

    if (!Array.isArray(value)) return { ...acc, children: acc.children.concat([transform(value)] as any[]) }
    return { ...acc, children: acc.children.concat(value.map(transform)) }
  }, { children: [] })
}
