<template>
  <aside class="flex flex-1 bg-[#f8fafc] text-[#0f172a] lg:h-[100dvh] lg:overflow-hidden">
    <div class="flex min-h-full w-full flex-col px-4 pb-5 pt-4 sm:px-6 sm:pb-6 sm:pt-5 lg:px-11 lg:pb-4 lg:pt-0">
      <div class="flex-1 lg:flex lg:h-full lg:items-center">
        <div class="w-full">
          <div class="mx-auto w-full max-w-[430px]">
            <header class="mb-7 mt-2 lg:mb-9 lg:mt-0">
              <h2 class="text-[2.05rem] font-extrabold tracking-[-0.04em] text-[#031b4e] lg:text-[2.15rem]">Login</h2>
              <p class="mt-3 max-w-[355px] text-[13px] leading-6 text-[#475569]">
                Masukkan kredensial administratif Anda untuk mengelola skater dan jadwal
              </p>
            </header>

            <form class="space-y-5" novalidate @submit.prevent="handleSubmit">
              <FormInput
                id="email"
                v-model="email"
                label="Email"
                placeholder="Email"
                :icon="Mail"
                :error="errors.email"
              />

              <FormInput
                id="password"
                v-model="password"
                label="Kata Sandi"
                :type="showPassword ? 'text' : 'password'"
                placeholder="........"
                :icon="Lock"
                :error="errors.password"
              >
                <template #suffix>
                  <button
                    type="button"
                    class="ml-2 inline-flex h-7 w-7 items-center justify-center rounded-full text-slate-400 transition hover:bg-slate-100 hover:text-slate-600"
                    :aria-label="showPassword ? 'Sembunyikan password' : 'Tampilkan password'"
                    @click="showPassword = !showPassword"
                  >
                    <Eye v-if="!showPassword" class="h-4 w-4" />
                    <EyeOff v-else class="h-4 w-4" />
                  </button>
                </template>
              </FormInput>

              <button
                type="submit"
                :disabled="isSubmitting"
                class="inline-flex h-[42px] w-full items-center justify-center gap-2 rounded-[4px] bg-[#b35305] px-4 text-[15px] font-bold text-white transition hover:bg-[#944404] focus:outline-none focus:ring-4 focus:ring-orange-200"
                :class="isSubmitting ? 'cursor-not-allowed opacity-70' : ''"
              >
                <span>{{ isSubmitting ? 'Memproses...' : 'Masuk' }}</span>
                <LogIn v-if="!isSubmitting" class="h-[16px] w-[16px]" />
              </button>

              <p v-if="submitError" class="rounded-[6px] border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
                {{ submitError }}
              </p>

              <p v-if="submitSuccess" class="rounded-[6px] border border-emerald-200 bg-emerald-50 px-3 py-2 text-sm text-emerald-700">
                {{ submitSuccess }}
              </p>
            </form>
          </div>
        </div>
      </div>

      <footer class="pt-10 text-[9px] leading-5 tracking-[0.08em] text-slate-400 uppercase lg:pt-2">
        <div class="flex flex-wrap gap-x-6 gap-y-2">
          <a href="#" class="transition hover:text-slate-600">Kebijakan Privasi</a>
          <a href="#" class="transition hover:text-slate-600">Syarat Layanan</a>
          <a href="#" class="transition hover:text-slate-600">Portal Dukungan</a>
        </div>
        <p class="mt-2 max-w-[320px]">© 2026 Infinity Inline Skate Academy.</p>
      </footer>
    </div>
  </aside>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { Eye, EyeOff, Lock, LogIn, Mail } from 'lucide-vue-next'
import FormInput from './FormInput.vue'

const emit = defineEmits(['login-success'])

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/$/, '')

const email = ref('')
const password = ref('')
const showPassword = ref(false)
const isSubmitting = ref(false)
const submitError = ref('')
const submitSuccess = ref('')

const errors = reactive({
  email: '',
  password: ''
})

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const validate = () => {
  errors.email = ''
  errors.password = ''
  submitError.value = ''
  submitSuccess.value = ''

  if (!email.value.trim()) {
    errors.email = 'Email wajib diisi.'
  } else if (!emailPattern.test(email.value.trim())) {
    errors.email = 'Format email tidak valid.'
  }

  if (!password.value.trim()) {
    errors.password = 'Password wajib diisi.'
  }

  return !errors.email && !errors.password
}

const handleSubmit = async () => {
  if (!validate()) return

  const payload = {
    usernameAtauEmail: email.value.trim(),
    password: password.value
  }

  console.log('Login payload', payload)

  isSubmitting.value = true

  try {
    console.log('Login request URL', `${API_BASE_URL}/auth/login`)
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    })

    const data = await response.json().catch(() => null)
    console.log('Login status', response.status)

    if (!response.ok) {
      submitError.value = data?.message || 'Login gagal. Periksa koneksi API dan kredensial Anda.'
      return
    }

    submitSuccess.value = `Login berhasil untuk ${data?.fullName || data?.username || email.value.trim()}.`
    console.log('Login response', data)
    emit('login-success', data || payload)
  } catch (error) {
    submitError.value = `Tidak bisa terhubung ke REST API. ${error?.message || 'Pastikan backend berjalan dan URL API benar.'}`
    console.error(error)
  } finally {
    isSubmitting.value = false
  }
}
</script>
