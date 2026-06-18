export default defineEventHandler((event) => {
  const url = getRequestURL(event)
  if (!/^\/en(?:\/|$)/i.test(url.pathname)) return

  const targetPath = url.pathname.replace(/^\/en(?=\/|$)/i, '') || '/'
  return sendRedirect(event, `${targetPath}${url.search}`, 301)
})
