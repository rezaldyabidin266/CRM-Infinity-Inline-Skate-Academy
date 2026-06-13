<template>
  <AdminDashboard
    v-if="currentUser"
    :user="currentUser"
    @logout="handleLogout"
  />

  <main v-else class="min-h-screen bg-transparent lg:h-[100dvh] lg:min-h-[100dvh] lg:overflow-hidden">
    <section class="flex min-h-screen w-full flex-col lg:h-[100dvh] lg:min-h-[100dvh] lg:flex-row">
      <LoginHeroPanel class="lg:w-[60%]" />
      <LoginFormPanel class="lg:w-[40%]" @login-success="handleLoginSuccess" />
    </section>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import AdminDashboard from './components/AdminDashboard.vue'
import LoginFormPanel from './components/LoginFormPanel.vue'
import LoginHeroPanel from './components/LoginHeroPanel.vue'

const STORAGE_KEY = 'crm-infinity-user'

const currentUser = ref(loadStoredUser())

function loadStoredUser() {
  try {
    const storedValue = window.localStorage.getItem(STORAGE_KEY)
    return storedValue ? JSON.parse(storedValue) : null
  } catch (error) {
    console.error('Gagal membaca data user dari localStorage.', error)
    return null
  }
}

function handleLoginSuccess(user) {
  currentUser.value = user

  try {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(user))
  } catch (error) {
    console.error('Gagal menyimpan sesi login.', error)
  }
}

function handleLogout() {
  currentUser.value = null

  try {
    window.localStorage.removeItem(STORAGE_KEY)
  } catch (error) {
    console.error('Gagal menghapus sesi login.', error)
  }
}
</script>
