import { remote } from 'webdriverio'
import parse from './parse'
import traverse from './traverse'
import fastify from 'fastify'
import handleRules from './handleRules'
import draw from './draw'
import * as path from 'path'

if (process.argv.length < 3) {
  console.log('.app/.ipa not specified!')
  console.log('Usage: ioswcag <path to .app/.ipa>')
  process.exit(1)
}

(async () => {
  const client = await remote({
    hostname: 'localhost',
    port: 4723,
    capabilities: {
      platformName: 'iOS',
      platformVersion: process.env.IOS_VERSION || '11.4',
      deviceName: 'iPhone Simulator',
      app: process.argv[2]
    },
    logLevel: 'warn'
  })

  const app = fastify()

  app.get('/', async (request, reply) => {
    console.log('Starting an audit...')

    const source = await client.getPageSource()
    let parsed = await parse(source)

    const b64 = await client.takeScreenshot()
    const buf = Buffer.from(b64, 'base64')
    const arr: Promise<{ image: Buffer, reason: string }>[] = []

    traverse(parsed, node => {
      let reason = handleRules(node)
      if (reason) {
        arr.push((async () => ({
          image: await draw(buf, node),
          reason: parseReason(reason)
        }))())
      }
    })

    try {
      await client.setOrientation('LANDSCAPE')
      await client.setOrientation('PORTRAIT')
    } catch (err) {
      arr.push((async () => ({
        image: Buffer.from('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAAAAAA6fptVAAAACklEQVR4nGP6DwABBQECz6AuzQAAAABJRU5ErkJggg==', 'base64'),
        reason: parseReason('*1.3.4*: Unable to rotate!')
      }))())
    }

    console.log('Finished the audit')
    const results = await Promise.all(arr)
    reply
      .type('text/html')
      .send(`
<!DOCTYPE html>
<html lang="en-US">
  <head>
    <title>WCAG Validation Report</title>
    <style>
      table {
        border-collapse: collapse;
      }
      
      table, th, td {
        border: 1px solid black;
      }
    </style>
  </head>
  <body>
    <h1>WCAG Report</h1>
    <p>Application: ${path.parse(process.argv[2]).base}</p>
    <p>Date: <script>document.write(new Date().toLocaleString())</script></p>
    <table>
      <tr>
        <th>Screenshot</th>
        <th>Violation</th>
      </tr>
      ${results.map(({ reason, image }) => `
        <tr>
          <td>
            <img src="data:image/png;base64,${image.toString('base64')}" alt="screenshot of a violated element">
          </td>
          <td>
            ${reason}
          </td>
        </tr>
      `).join('\n')}
    </table>
  </body>
</html>`)
  })

  const port = Math.floor(Math.random() * 65536 - 1024) + 1024

  app.listen(port)
    .then(() => {
      console.log(`Listening on http://localhost:${port}/`)
    })
    .catch(err => {
      console.error('Failed listening, error:')
      console.error(err)
    })

})()

function parseReason (reason: string) {
  console.log(reason.replace(/\*/g, ''))
  reason = reason.replace(/"(.*?)"/g, '<code>$1</code>')
  reason = reason.replace(/\*(.*?)\*/g, '<b>$1</b>')
  return reason
}
