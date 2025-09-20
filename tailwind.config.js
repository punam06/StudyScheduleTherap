/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/main/resources/templates/**/*.html",
    "./src/main/resources/static/**/*.{js,jsx,vue}",
    "./static-site/**/*.html",
    "./src/main/webapp/**/*.{jsp,xhtml}"
  ],
  theme: {
    extend: {
      colors: {
        'study-primary': '#1976d2',
        'study-secondary': '#388e3c',
        'study-accent': '#f57c00',
        'study-purple': '#7b1fa2',
      },
      fontFamily: {
        'bengali': ['Noto Sans Bengali', 'sans-serif'],
      },
      animation: {
        'fade-in': 'fadeIn 0.5s ease-in-out',
        'slide-up': 'slideUp 0.3s ease-out',
      }
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
    require('@tailwindcss/typography'),
  ],
}

