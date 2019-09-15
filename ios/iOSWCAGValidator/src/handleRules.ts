export default function handleRules (node: any): string {
  // 1.1.1 Alternatywa w postaci tekstu: Zawartość nietekstowa
  if (node.type === 'XCUIElementTypeImage' && !node.label) {
    return `*1.1.1*: Image "${node.name}" does not have a label!`
  }

  // if (node.type === TextField && !node.placeholder)

  // TODO: add more rules here
  return ''
}
