export default {
  setStatus(arr) {
    arr.forEach(element => {
      element.status = element.status === 1
      if (element.children.length === 0) {
        return
      }
      this.setStatus(element.children)
    })
    return arr
  }
}

// this.tableData.forEach(item => {
//   item.status = item.status === 1
//   if (item.children.length !== 0) {
//     item.children.forEach(subItem => {
//       subItem.status = subItem.status === 1
//       if (subItem.children.length !== 0) {
//         subItem.children.forEach(node => {
//           node.status = node.status === 1
//         })
//       }
//     })
//   }
// })
