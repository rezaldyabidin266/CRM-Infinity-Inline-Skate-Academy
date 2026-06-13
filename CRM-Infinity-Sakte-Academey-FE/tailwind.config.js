/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#0F172A',
        muted: '#64748B',
        navy: '#071827',
        navySoft: '#0B1E2D',
        panel: '#F8FAFC',
        ember: '#AD4800'
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif']
      },
      boxShadow: {
        shell: '0 24px 80px rgba(2, 12, 27, 0.38)'
      },
      backgroundImage: {
        dots: 'radial-gradient(circle at 1px 1px, rgba(148, 163, 184, 0.16) 1px, transparent 0)'
      }
    }
  },
  plugins: []
}
