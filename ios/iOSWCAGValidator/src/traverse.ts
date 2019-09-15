export default function traverse (node: any, callback: (node: any) => void) {
  // ignore the iOS status bar because it's useless
  if (node.type === 'XCUIElementTypeStatusBar') return

  for (let child of node.children) {
    if (!child.children.length) {
      // if a child doesn't have children, pass it to the callback with parent ref
      callback({ ...child, parent: node })
    } else {
      // if a child does have children, traverse further
      traverse(child, callback)
    }
  }
}
