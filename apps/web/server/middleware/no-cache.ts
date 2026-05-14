export default defineEventHandler((event) => {
  const pathname = getRequestURL(event).pathname
  if (pathname.startsWith('/_nuxt/') || pathname.startsWith('/favicon') || pathname.startsWith('/assets/')) return

  setHeader(event, 'Cache-Control', 'no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0')
  setHeader(event, 'Pragma', 'no-cache')
  setHeader(event, 'Expires', '0')
})
