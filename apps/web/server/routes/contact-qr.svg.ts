import QRCode from 'qrcode'

export default defineEventHandler(async (event) => {
  const query = getQuery(event)
  const data = String(query.data || '').trim()

  if (!data) {
    throw createError({ statusCode: 400, statusMessage: 'QR data is required' })
  }
  if (data.length > 2048) {
    throw createError({ statusCode: 400, statusMessage: 'QR data is too long' })
  }

  const svg = await QRCode.toString(data, {
    type: 'svg',
    width: 520,
    margin: 1,
    errorCorrectionLevel: 'M',
    color: {
      dark: '#020617',
      light: '#ffffff',
    },
  })

  setHeader(event, 'Content-Type', 'image/svg+xml; charset=utf-8')
  setHeader(event, 'Cache-Control', 'no-store, no-cache, must-revalidate, max-age=0')
  return svg
})
