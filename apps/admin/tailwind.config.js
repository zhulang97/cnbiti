/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        admin: {
          950: '#0d1117',
          900: '#161b22',
          800: '#1c2128',
          700: '#21262d',
          600: '#30363d',
          500: '#484f58',
          400: '#8b949e',
          300: '#b1bac4',
          200: '#c9d1d9',
          100: '#e6edf3',
        },
        accent: {
          600: '#0284c7',
          500: '#0ea5e9',
          400: '#38bdf8',
          300: '#7dd3fc',
        },
      },
    },
  },
  plugins: [],
}
