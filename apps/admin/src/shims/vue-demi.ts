export * from 'vue'

export const isVue2 = false
export const isVue3 = true

export function set<T extends object, K extends keyof T>(target: T, key: K, value: T[K]) {
  target[key] = value
  return value
}

export function del<T extends object, K extends keyof T>(target: T, key: K) {
  delete target[key]
}
