<template>
  <div class="min-h-screen bg-[#edf2f7] text-[#0f172a] lg:h-[100dvh] lg:overflow-hidden">
    <div class="flex min-h-screen lg:h-[100dvh]">
      <Transition
        enter-active-class="transition-opacity duration-200"
        enter-from-class="opacity-0"
        enter-to-class="opacity-100"
        leave-active-class="transition-opacity duration-200"
        leave-from-class="opacity-100"
        leave-to-class="opacity-0"
      >
        <button
          v-if="isSidebarOpen"
          type="button"
          class="fixed inset-0 z-30 bg-slate-950/50 lg:hidden"
          aria-label="Tutup sidebar"
          @click="isSidebarOpen = false"
        />
      </Transition>

      <aside
        class="fixed inset-y-0 left-0 z-40 flex w-[280px] flex-col bg-[#081a2b] text-white shadow-2xl transition-transform duration-300 lg:static lg:h-[100dvh] lg:w-[250px] lg:translate-x-0 lg:shadow-none"
        :class="isSidebarOpen ? 'translate-x-0' : '-translate-x-full'"
      >
        <div class="border-b border-white/8 px-6 pb-6 pt-7">
          <div class="text-[1.55rem] font-extrabold tracking-[-0.04em] text-white">CRM Infinity</div>
          <div class="mt-1 text-[0.74rem] uppercase tracking-[0.28em] text-slate-400">Skate Academy</div>
        </div>

        <nav class="flex-1 overflow-y-auto px-3 py-5">
          <button
            v-for="item in navigationItems"
            :key="item.label"
            type="button"
            class="mb-1 flex w-full items-center gap-3 rounded-[4px] px-4 py-3 text-left text-[0.94rem] transition"
            :class="item.active ? 'bg-[#10263b] text-white' : 'text-slate-400 hover:bg-[#10263b]/70 hover:text-white'"
          >
            <component :is="item.icon" class="h-[17px] w-[17px] shrink-0" />
            <span>{{ item.label }}</span>
          </button>
        </nav>

        <div class="border-t border-white/8 px-3 py-5">
          <button
            v-for="item in footerItems"
            :key="item.label"
            type="button"
            class="mb-1 flex w-full items-center gap-3 rounded-[4px] px-4 py-3 text-left text-[0.94rem] text-slate-400 transition hover:bg-[#10263b]/70 hover:text-white"
            @click="item.action?.()"
          >
            <component :is="item.icon" class="h-[17px] w-[17px] shrink-0" />
            <span>{{ item.label }}</span>
          </button>
        </div>
      </aside>

      <div class="flex min-h-screen min-w-0 flex-1 flex-col lg:h-[100dvh]">
        <header class="border-b border-slate-200 bg-white px-4 py-3 shadow-[0_1px_0_rgba(15,23,42,0.04)] sm:px-5 lg:px-6">
          <div class="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
            <div class="flex items-center gap-3">
              <button
                type="button"
                class="inline-flex h-10 w-10 items-center justify-center rounded-[6px] border border-slate-200 text-slate-600 transition hover:bg-slate-50 lg:hidden"
                aria-label="Buka sidebar"
                @click="isSidebarOpen = true"
              >
                <Menu class="h-5 w-5" />
              </button>

              <div class="relative w-full max-w-[320px] flex-1">
                <Search class="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
                <input
                  type="text"
                  placeholder="Cari data..."
                  class="h-10 w-full rounded-[6px] border border-slate-200 bg-slate-50 pl-9 pr-3 text-sm text-slate-700 outline-none transition focus:border-slate-300 focus:bg-white focus:ring-2 focus:ring-slate-200"
                />
              </div>
            </div>

            <div class="flex items-center justify-between gap-4 md:justify-end">
              <div class="flex items-center gap-2 text-slate-500">
                <button type="button" class="inline-flex h-9 w-9 items-center justify-center rounded-full border border-slate-200 transition hover:bg-slate-50">
                  <Bell class="h-4 w-4" />
                </button>
                <button type="button" class="inline-flex h-9 w-9 items-center justify-center rounded-full border border-slate-200 transition hover:bg-slate-50">
                  <CircleHelp class="h-4 w-4" />
                </button>
              </div>

              <div class="flex items-center gap-3 rounded-[10px] border border-slate-200 bg-white px-3 py-2">
                <div class="text-right leading-tight">
                  <div class="text-[0.95rem] font-bold text-slate-900">{{ displayName }}</div>
                  <div class="text-[0.66rem] uppercase tracking-[0.18em] text-slate-400">{{ displayRole }}</div>
                </div>
                <div class="flex h-10 w-10 items-center justify-center rounded-full bg-[#0b1e2d] text-sm font-bold text-white">
                  {{ userInitials }}
                </div>
              </div>
            </div>
          </div>
        </header>

        <main class="flex-1 overflow-y-auto px-4 py-5 sm:px-5 lg:px-6 lg:py-6">
          <div class="mx-auto max-w-[1400px]">
            <section>
              <h1 class="text-[2rem] font-extrabold tracking-[-0.04em] text-[#031b4e] sm:text-[2.35rem]">
                Selamat Datang, {{ displayName }}
              </h1>
              <p class="mt-2 text-sm text-slate-600 sm:text-[1rem]">
                Berikut aktivitas yang terjadi di CRM Infinity hari ini.
              </p>
            </section>

            <section class="mt-6 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
              <article
                v-for="card in metricCards"
                :key="card.title"
                class="rounded-[6px] border border-slate-200 bg-white p-5 shadow-[0_6px_16px_rgba(15,23,42,0.04)]"
              >
                <div class="flex items-start justify-between gap-3">
                  <div>
                    <div class="text-[0.72rem] font-semibold uppercase tracking-[0.18em] text-slate-500">
                      {{ card.title }}
                    </div>
                    <div class="mt-2 text-[2rem] font-extrabold leading-none text-[#031b4e]">
                      {{ card.value }}
                    </div>
                  </div>
                  <div class="flex h-10 w-10 items-center justify-center rounded-full" :class="card.iconBackground">
                    <component :is="card.icon" class="h-5 w-5" :class="card.iconColor" />
                  </div>
                </div>
                <p class="mt-3 text-[0.92rem] leading-6" :class="card.noteColor">{{ card.note }}</p>
              </article>
            </section>

            <section class="mt-4 grid gap-4 xl:grid-cols-[minmax(0,2fr)_360px]">
              <article class="rounded-[6px] border border-slate-200 bg-white p-5 shadow-[0_6px_16px_rgba(15,23,42,0.04)]">
                <div class="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                  <div>
                    <h2 class="text-[1.35rem] font-bold text-[#0f172a]">Ringkasan Kehadiran</h2>
                    <p class="text-sm text-slate-500">Statistik partisipasi mingguan</p>
                  </div>
                  <div class="inline-flex h-9 items-center rounded-[4px] border border-slate-200 px-3 text-sm text-slate-500">
                    7 Hari Terakhir
                  </div>
                </div>

                <div class="mt-6 h-[280px] rounded-[6px] border border-slate-100 bg-[linear-gradient(180deg,#ffffff_0%,#f8fafc_100%)] p-4 sm:h-[320px]">
                  <div class="flex h-full items-end justify-between gap-2">
                    <div
                      v-for="bar in attendanceBars"
                      :key="bar.label"
                      class="flex flex-1 flex-col items-center justify-end gap-3"
                    >
                      <div class="flex w-full items-end justify-center">
                        <div class="w-full max-w-[46px] rounded-t-[6px] bg-[linear-gradient(180deg,#1d4ed8_0%,#60a5fa_100%)]" :style="{ height: `${bar.value}%` }" />
                      </div>
                      <span class="text-[0.72rem] font-semibold text-slate-500">{{ bar.label }}</span>
                    </div>
                  </div>
                </div>
              </article>

              <article class="rounded-[6px] border border-slate-200 bg-white p-5 shadow-[0_6px_16px_rgba(15,23,42,0.04)]">
                <h2 class="text-[1.35rem] font-bold text-[#0f172a]">Aktivitas Terbaru</h2>
                <div class="mt-5 space-y-4">
                  <div v-for="activity in recentActivities" :key="activity.title" class="flex items-start gap-3">
                    <div class="mt-0.5 flex h-9 w-9 shrink-0 items-center justify-center rounded-full" :class="activity.iconBackground">
                      <component :is="activity.icon" class="h-4 w-4" :class="activity.iconColor" />
                    </div>
                    <div>
                      <div class="text-[0.92rem] leading-5 text-slate-700">{{ activity.title }}</div>
                      <div class="mt-1 text-[0.77rem] text-slate-400">{{ activity.time }}</div>
                    </div>
                  </div>
                </div>
              </article>
            </section>

            <section class="mt-4">
              <article class="max-w-[760px] rounded-[6px] border border-slate-200 bg-white p-5 shadow-[0_6px_16px_rgba(15,23,42,0.04)]">
                <h2 class="text-[1.35rem] font-bold text-[#0f172a]">Ringkasan Pembayaran</h2>

                <div class="mt-5 overflow-x-auto">
                  <table class="min-w-full border-separate border-spacing-0 text-left">
                    <thead>
                      <tr class="text-[0.72rem] uppercase tracking-[0.16em] text-slate-500">
                        <th class="border-b border-slate-200 px-4 py-3">Nama Siswa</th>
                        <th class="border-b border-slate-200 px-4 py-3">Paket</th>
                        <th class="border-b border-slate-200 px-4 py-3">Jumlah</th>
                        <th class="border-b border-slate-200 px-4 py-3">Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="payment in payments" :key="payment.name" class="text-sm text-slate-700">
                        <td class="border-b border-slate-100 px-4 py-4 font-medium">{{ payment.name }}</td>
                        <td class="border-b border-slate-100 px-4 py-4">{{ payment.package }}</td>
                        <td class="border-b border-slate-100 px-4 py-4">{{ payment.amount }}</td>
                        <td class="border-b border-slate-100 px-4 py-4">
                          <span class="inline-flex rounded-[4px] px-2.5 py-1 text-[0.7rem] font-bold uppercase tracking-[0.12em] text-white" :class="payment.statusClass">
                            {{ payment.status }}
                          </span>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </article>
            </section>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import {
  BadgeDollarSign,
  Bell,
  BookCheck,
  CalendarCheck2,
  CircleHelp,
  CreditCard,
  LayoutDashboard,
  LogOut,
  Menu,
  Settings,
  Shield,
  ShieldAlert,
  Search,
  Star,
  UserCog,
  Users,
  Wrench
} from 'lucide-vue-next'

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['logout'])

const isSidebarOpen = ref(false)

const displayName = computed(() => {
  return props.user?.fullName || props.user?.namaLengkap || props.user?.username || props.user?.email || 'Admin'
})

const displayRole = computed(() => {
  return props.user?.role?.name || props.user?.roleName || props.user?.role || 'Super Administrator'
})

const userInitials = computed(() => {
  const source = displayName.value.trim()
  if (!source) return 'AD'
  return source
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase() || '')
    .join('')
})

const navigationItems = [
  { label: 'Dashboard', icon: LayoutDashboard, active: true },
  { label: 'Manajemen User', icon: UserCog, active: false },
  { label: 'Manajemen Role', icon: Shield, active: false },
  { label: 'Level/Grade', icon: Star, active: false },
  { label: 'Absensi', icon: CalendarCheck2, active: false },
  { label: 'Pembayaran', icon: CreditCard, active: false },
  { label: 'Peralatan', icon: Wrench, active: false },
  { label: 'Checklist Progres', icon: BookCheck, active: false }
]

const footerItems = [
  { label: 'Pengaturan', icon: Settings },
  { label: 'Keluar', icon: LogOut, action: () => emit('logout') }
]

const metricCards = [
  {
    title: 'Total Siswa',
    value: '1,284',
    note: '+12% dari bulan lalu',
    icon: Users,
    iconBackground: 'bg-sky-100',
    iconColor: 'text-sky-600',
    noteColor: 'text-emerald-600'
  },
  {
    title: 'Pelatih Aktif',
    value: '42',
    note: 'Semua sedang di lokasi',
    icon: Shield,
    iconBackground: 'bg-amber-100',
    iconColor: 'text-amber-600',
    noteColor: 'text-slate-600'
  },
  {
    title: 'Pendapatan Bulanan',
    value: '$48.2k',
    note: 'Sesuai target',
    icon: BadgeDollarSign,
    iconBackground: 'bg-cyan-100',
    iconColor: 'text-cyan-600',
    noteColor: 'text-emerald-600'
  },
  {
    title: 'Absensi Tertunda',
    value: '14',
    note: 'Perlu tinjauan segera',
    icon: ShieldAlert,
    iconBackground: 'bg-rose-100',
    iconColor: 'text-rose-600',
    noteColor: 'text-rose-600'
  }
]

const attendanceBars = [
  { label: 'Sen', value: 64 },
  { label: 'Sel', value: 80 },
  { label: 'Rab', value: 72 },
  { label: 'Kam', value: 88 },
  { label: 'Jum', value: 84 },
  { label: 'Sab', value: 58 },
  { label: 'Min', value: 45 }
]

const recentActivities = [
  {
    title: 'Siswa baru terdaftar: Mike Chen',
    time: '10 menit yang lalu',
    icon: Users,
    iconBackground: 'bg-emerald-100',
    iconColor: 'text-emerald-600'
  },
  {
    title: 'Pembayaran diterima untuk paket Skate Mastery',
    time: '45 menit yang lalu',
    icon: CreditCard,
    iconBackground: 'bg-amber-100',
    iconColor: 'text-amber-600'
  },
  {
    title: 'Pelatih Sarah menandai absensi',
    time: '2 jam yang lalu',
    icon: CalendarCheck2,
    iconBackground: 'bg-indigo-100',
    iconColor: 'text-indigo-600'
  },
  {
    title: 'Penilaian Level 2 menunggu tinjauan',
    time: 'Kemarin',
    icon: ShieldAlert,
    iconBackground: 'bg-rose-100',
    iconColor: 'text-rose-600'
  }
]

const payments = [
  { name: 'Alex Rivera', package: 'Elite Competitive', amount: '$240.00', status: 'Hadir', statusClass: 'bg-emerald-500' },
  { name: 'Emily Watson', package: 'Intermediate Pro', amount: '$180.00', status: 'Izin', statusClass: 'bg-blue-500' },
  { name: 'David Park', package: 'Beginner Foundation', amount: '$120.00', status: 'Alpha', statusClass: 'bg-rose-500' }
]
</script>
